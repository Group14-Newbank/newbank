package newbank;

import static newbank.utils.Config.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.javamoney.moneta.Money;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import newbank.client.ConfigurationException;
import newbank.client.TestClient;
import newbank.server.NewBank;
import newbank.server.NewBankServer;
import newbank.utils.Display;
import newbank.utils.QueueDisplay;
import newbank.server.Customer;
import newbank.server.microloans.Loans;

public class TestApp {
  private static NewBankServer server;

  private PipedReader reader;
  private PipedWriter writer;
  private Display display;
  private TestClient client;

  @Disabled
  private String testCommand(String command) throws IOException {
    writer.write(command);
    display.discardLinesUntil("request");

    return display.getLine();
  }

  @BeforeAll
  public static void beforeAll() throws IOException {
    server = new NewBankServer(DEFAULT_PORT);
    NewBankServer.VERBOSE_MODE = true;
    server.start();
  }

  @BeforeEach
  public void setup() throws IOException, ConfigurationException {
    reader = new PipedReader();
    writer = new PipedWriter(reader);
    display = new QueueDisplay();

    client = new TestClient("localhost", DEFAULT_PORT, reader);
    client.setDisplay(display);
    client.start();
  }

  @AfterEach
  public void tearDown() throws IOException {
    writer.close();
    client.interrupt();
  }

  private String logIn(final String username, final String password) throws IOException {
    writer.write(String.format("LOGIN %s %s \n", username, password));
    display.discardLinesUntil("request");

    return display.getLine();
  }

  private void checkAccountBalance(final String account, final String balance) throws IOException {
    String result = testCommand("SHOWMYACCOUNTS\n");
    String[] info = result.split(MULTILINE_INFO_SEPARATOR);
    assertThat(info.length, equalTo(2));

    result = info[1];
    String[] output = result.split(":");
    assertThat(output[0].trim(), equalTo(account));
    assertThat(output[1].trim(), equalTo(balance));
  }

  private <T> T getAndAssertOptional(Supplier<Optional<T>> supplier) {
    Optional<T> opt = supplier.get();
    assertThat(opt.isPresent(), equalTo(true));
    return opt.get();
  }
  
  private Customer getCustomer(final String username) {
    return getAndAssertOptional(() -> NewBank.getBank().getCustomer(username));
  }

  void testShowMyAccountsOutput(String[] patterns) throws IOException {
    String accountSummary = testCommand("SHOWMYACCOUNTS\n");
    String[] outputLines = accountSummary.split(MULTILINE_INFO_SEPARATOR);
    assertThat(outputLines.length, equalTo(patterns.length + 1));

    for (int i = 0; i < patterns.length; i++) {
      assertThat(outputLines[i+1], matchesPattern(patterns[i]));
    }
  }

  @Test
  public void cannotSendCommandsIfLoggedOut() throws IOException {
    writer.write("SHOWMYACCOUNTS\n");
    assertThat(display.getLine(), not(matchesPattern("request")));
  }

  @Test
  public void canCreateNewAccount() throws IOException {
    String response = logIn("John", "John123");
    assertThat(response, containsString("SUCCESS"));

    response = testCommand("NEWACCOUNT\n");
    assertThat(response, equalTo("FAIL: Usage: NEWACCOUNT <Name> [Default]"));

    response = testCommand("NEWACCOUNT abc\n");
    assertThat(
        response,
        equalTo("FAIL: Invalid account name: Length must be between 4 and 12 characters."));

    response = testCommand("NEWACCOUNT abcdefghijklmnopqr\n");
    assertThat(
        response,
        equalTo("FAIL: Invalid account name: Length must be between 4 and 12 characters."));

    response = testCommand("NEWACCOUNT 123456\n");
    assertThat(response, equalTo("FAIL: Invalid account name: Only letters are allowed."));

    response = testCommand("NEWACCOUNT accountB\n");
    assertThat(response, equalTo("SUCCESS: The account has been created successfully."));

    response = testCommand("NEWACCOUNT accountC\n");
    assertThat(response, equalTo("SUCCESS: The account has been created successfully."));
    response = testCommand("NEWACCOUNT accountD\n");
    assertThat(response, equalTo("SUCCESS: The account has been created successfully."));
    response = testCommand("NEWACCOUNT accountE\n");
    assertThat(response, equalTo("SUCCESS: The account has been created successfully."));

    response = testCommand("NEWACCOUNT accountF\n");
    assertThat(response, equalTo("FAIL: Maximum number of accounts is: 5"));
    response = testCommand("NEWACCOUNT accountG\n");
    assertThat(response, equalTo("FAIL: Maximum number of accounts is: 5"));
  }

  @Test
  public void canMoveMoney() throws IOException {
    String response = logIn("Bhagy", "Bhagy123");
    assertThat(response, containsString("SUCCESS"));

    testShowMyAccountsOutput(new String[] {
        "[*]Main:\\s+1000.00\\s+GBP",
        "Savings:\\s+201.19\\s+GBP"
    });

    response = testCommand("MOVE money please\n");
    assertThat(
        response, equalTo("FAIL: Usage: MOVE <account_name_from> <account_name_to> <amount>"));

    response = testCommand("MOVE Savings 100\n");
    assertThat(
        response, equalTo("FAIL: Usage: MOVE <account_name_from> <account_name_to> <amount>"));

    response = testCommand("MOVE Savings Savings 100\n");
    assertThat(response, equalTo("FAIL: The accounts must be different to complete a transfer."));

    response = testCommand("MOVE Savings Main 201.20\n");
    assertThat(response, equalTo("FAIL: Insufficient balance in [Savings], missing: [GBP 0.01]."));

    response = testCommand("MOVE Savings Main -120.23\n");
    assertThat(response, equalTo("FAIL: Transfer amount [-120.23] must be greater than 0."));

    response = testCommand("MOVE Savings Main 0\n");
    assertThat(response, equalTo("FAIL: Transfer amount [0] must be greater than 0."));

    response = testCommand("MOVE Savings Main t123\n");
    assertThat(response, equalTo("FAIL: Transfer amount [t123] is invalid."));

    response = testCommand("MOVE Savings Main 100.001\n");
    assertThat(response, containsString("FAIL: Transfer amount [100.001] has more decimal places"));

    response = testCommand("MOVE Savings MyMain 99\n");
    assertThat(response, equalTo("FAIL: Account [MyMain] does not exist."));

    response = testCommand("MOVE Savings Main 101.19\n");
    assertThat(
        response, equalTo("SUCCESS: Money transferred from [Savings] to [Main] successfully."));

    // The balances are updated
    testShowMyAccountsOutput(new String[] {
        "[*]Main:\\s+1101.19\\s+GBP",
        "Savings:\\s+100.00\\s+GBP"
    });

    response = testCommand("MOVE Main Savings 1100\n");
    assertThat(
        response, equalTo("SUCCESS: Money transferred from [Main] to [Savings] successfully."));

    // The balances are updated
    testShowMyAccountsOutput(new String[] {
        "[*]Main:\\s+1.19\\s+GBP",
        "Savings:\\s+1200.00\\s+GBP"
    });

    response = testCommand("MOVE Main Savings 2\n");
    assertThat(response, equalTo("FAIL: Insufficient balance in [Main], missing: [GBP 0.81]."));

    // The balances remain the same
    testShowMyAccountsOutput(new String[] {
        "[*]Main:\\s+1.19\\s+GBP",
        "Savings:\\s+1200.00\\s+GBP"
    });
  }

  @Test
  public void canHandleEmptyRequest() throws IOException {
    String response = testCommand("\n");
    assertThat(response, equalTo("FAIL: Unknown command."));
  }

  @Test
  public void canHandleUnknownCommands() throws IOException {
    String response = testCommand("INVALID command\n");
    assertThat(response, equalTo("FAIL: Unknown command."));
  }

  private void addCustomer(final String name, final String password) throws IOException {
    String response = testCommand(String.format("REGISTER %s %s\n", name, password));
    assertThat(response, containsString("SUCCESS"));
  }

  @Test
  public void canRegisterCustomer() throws IOException {
    addCustomer("TestCustomer1", "Password1");

    String response = logIn("TestCustomer1", "Password1");
    assertThat(response, containsString("SUCCESS"));
  }

  private void setupCustomerWithAccount(
      final String username, final String password, final String accountName
  ) throws IOException {
    addCustomer(username, password);

    String response = logIn(username, password);
    assertThat(response, containsString("SUCCESS"));

    response = testCommand(String.format("NEWACCOUNT %s\n", accountName));
    assertThat(response, containsString("SUCCESS"));
  }

  private void setupCustomerWithAccount(final String username, final String password) throws IOException {
    setupCustomerWithAccount(username, password, "Savings");
  }

  @Test
  public void canDepositMoney() throws IOException {
    setupCustomerWithAccount("TestCustomer2", "Password2");

    String response = testCommand("DEPOSIT Savings 1000.0\n");
    assertThat(response, containsString("SUCCESS"));

    checkAccountBalance("Savings", "1000.00 GBP");

    response = testCommand("DEPOSIT Savings 250.0\n");
    assertThat(response, containsString("SUCCESS"));

    checkAccountBalance("Savings", "1250.00 GBP");
  }

  @Test
  public void canHandleInvalidDepositAccountOrAmount() throws IOException {
    setupCustomerWithAccount("TestCustomer3", "Password3");

    String response = testCommand("DEPOSIT Main 1000.0\n");
    assertThat(response, containsString("FAIL"));

    response = testCommand("DEPOSIT Main -500.0\n");
    assertThat(response, containsString("FAIL"));
  }

  @Test
  public void canExitApplication() throws IOException, InterruptedException {
    String response = testCommand("QUIT\n");
    assertThat(response, containsString("SUCCESS"));
    client.join();
  }

  @Test
  public void canSetDefaultAccount() throws IOException {
    setupCustomerWithAccount("TestCustomer4", "Password4");

    String response = testCommand("NEWACCOUNT Main DEFAULT\n");
    assertThat(response, containsString("SUCCESS"));

    response = testCommand("NEWACCOUNT Checking\n");
    assertThat(response, containsString("SUCCESS"));

    response = testCommand("DEFAULT Checking\n");
    assertThat(response, containsString("SUCCESS"));

    testShowMyAccountsOutput(new String[] {
        "Savings:\\s+0.00\\s+GBP",
        "Main:\\s+0.00\\s+GBP",
        "[*]Checking:\\s+0.00\\s+GBP"
    });
  }

  @Test
  public void canHandleSettingSavingsAsDefault() throws IOException {
    setupCustomerWithAccount("TestCustomer5", "Password5");

    String response = testCommand("DEFAULT Savings\n");
    assertThat(response, containsString("FAIL: Account [Savings] cannot be default."));
  }

  @Test
  public void canHandleSettingNonExistingAccountAsDefault() throws IOException {
    setupCustomerWithAccount("TestCustomer7", "Password7");

    String response = testCommand("DEFAULT Main\n");
    assertThat(response, equalTo("FAIL: Account [Main] does not exist."));
  }

  @Test
  public void checkThatFirstNonSavingsAccountIsDefault() throws IOException {
    setupCustomerWithAccount("TestCustomer6", "Password6");

    String response = testCommand("NEWACCOUNT Main\n");
    assertThat(response, containsString("SUCCESS"));

    testShowMyAccountsOutput(new String[]{
        "Savings:\\s+0.00\\s+GBP",
        "[*]Main:\\s+0.00\\s+GBP"
    });
  }

  @Test
  public void canPayCustomer() throws IOException {
    setupCustomerWithAccount("TestCustomer8", "Password8");
    String response = testCommand("NEWACCOUNT Main DEFAULT\n");
    assertThat(response, containsString("SUCCESS"));

    response = testCommand("DEPOSIT Main 1000.0\n");
    assertThat(response, containsString("SUCCESS"));

    response = testCommand("PAY John 1000.0\n");
    assertThat(response, equalTo("Default account for customer [John] credited successfully."));
  }

  @Test
  public void canHandleInsufficientFunds() throws IOException {
    setupCustomerWithAccount("TestCustomer9", "Password9");
    String response = testCommand("NEWACCOUNT Main DEFAULT\n");
    assertThat(response, containsString("SUCCESS"));

    response = testCommand("PAY John 1000.0\n");
    assertThat(response, containsString("FAIL: Insufficient funds"));
  }

  @Test
  public void canHandleInvalidPayRecipient() throws IOException {
    setupCustomerWithAccount("TestCustomer10", "Password10");
    String response = testCommand("NEWACCOUNT Main DEFAULT\n");
    assertThat(response, containsString("SUCCESS"));

    response = testCommand("PAY Jason 1000.0\n");
    assertThat(response, containsString("FAIL: Customer [Jason] does not exist."));
  }

  @Test
  public void canHandleInvalidCreditAmount() throws IOException {
    setupCustomerWithAccount("TestCustomer11", "Password11");
    String response = testCommand("NEWACCOUNT Main DEFAULT\n");
    assertThat(response, containsString("SUCCESS"));

    response = testCommand("PAY John -100.0\n");
    assertThat(response, containsString("FAIL: Credit amount [-100.0] must be greater than 0"));

    response = testCommand("PAY John 100.001\n");
    assertThat(response, containsString("FAIL: Credit amount [100.001] has more decimal places"));
  }

  @Test
  public void canHandleInvalidCommandsBeforeLoggingIn() throws IOException {
    String response = testCommand("NEWACCOUNT accountB\n");
    assertThat(response, equalTo("FAIL: Request not allowed, please log in first."));

    response = testCommand("DEPOSIT Savings 1000.0\n");
    assertThat(response, equalTo("FAIL: Request not allowed, please log in first."));

    response = testCommand("SHOWMYACCOUNTS\n");
    assertThat(response, equalTo("FAIL: Request not allowed, please log in first."));

    response = testCommand("PAY Jason 1000.0\n");
    assertThat(response, equalTo("FAIL: Request not allowed, please log in first."));

    response = testCommand("DEFAULT Savings\n");
    assertThat(response, equalTo("FAIL: Request not allowed, please log in first."));
  }

  @Test
  public void canRequestExactlyOneMicroloan() throws IOException {
    final String username = "canRequestExactlyOneMicroloan";
    setupCustomerWithAccount(username, "Password0", "Main");

    String response = testCommand(String.format(
        "REQUESTLOAN %s %s\n", MAX_MICROLOAN.getNumber().toString(), MAX_REPAYMENT_PERIOD_DAYS
    ));
    assertThat(response, containsString("SUCCESS"));
    assertThat("The user now has 1 loan-request", 
        getCustomer(username).getLoanHistory().hasCurrentLoanRequest()
    );
    
    response = testCommand("REQUESTLOAN 100 60\n");
    assertThat(response, matchesPattern("FAIL:.+already.+request"));
  }

  private static Stream<Arguments> badLoanRequestParams() {
    return Stream.of(
        Arguments.of("term.+invalid", "100", "years?"),
        Arguments.of("term.+invalid", "100", "0"),
        Arguments.of("term.+exceeds maximum", "100", Integer.toString(MAX_REPAYMENT_PERIOD_DAYS + 1)),
        Arguments.of("loan.+invalid", "money", "365"),
        Arguments.of("loan.+decimal places", "100.001", "365"),
        Arguments.of("loan.+0", "0", "365"),
        Arguments.of(
            "loan.+exceeds maximum",
            MAX_MICROLOAN.add(Money.of(1, MAX_MICROLOAN.getCurrency())).getNumber().toString(),
            "365"
        )
    );
  }

  @ParameterizedTest
  @MethodSource("badLoanRequestParams")
  public void invalidMicroloanRequestFails(String failReason, String amount, String term) throws IOException {
    int hash = Math.abs(String.format("%s#%s#%s", failReason, amount, term).hashCode());
    setupCustomerWithAccount(
        String.format("TestCustomer%d", hash),
        String.format("Password%d", hash),
        "Main"
    );

    String response = testCommand(String.format("REQUESTLOAN %s %s\n", amount, term)).toLowerCase();
    assertThat(response, matchesPattern(String.format("fail:.+%s.+", failReason)));
  }

  @Test
  public void cannotRequestLoanWithoutDefaultAccount() throws IOException {
    setupCustomerWithAccount("cannotRequestLoanWithoutDefaultAccount", "Pw1");

    String response = testCommand("REQUESTLOAN 100 365\n").toLowerCase();
    assertThat(response, matchesPattern("fail:.+default.+"));
  }
  
  // TODO cannotRequestMicroloanWhenAlreadyHave3
  // TODO cannotRequestMicroloanWhenDefaultedInPast

  private String createLoanRequest(String uniqueUsername) throws IOException {
    setupCustomerWithAccount(uniqueUsername, "Password0", "Main");
    String response = testCommand(String.format(
        "REQUESTLOAN %s %s\n", MAX_MICROLOAN.getNumber().toString(), MAX_REPAYMENT_PERIOD_DAYS
    ));
    assertThat(response, containsString("SUCCESS"));
    return uniqueUsername;
  }

  @Test
  public void canGrantLoanRequestExactlyOnce() throws IOException {
    String borrowerName = "canGrantLoanRequestBorrower";
    String loanID = createLoanRequest(borrowerName);

    String response = testCommand("LOGOUT\n");
    assertThat(response, containsString("SUCCESS"));

    String lenderName = "canGrantLoanRequestLender";
    setupCustomerWithAccount(lenderName, "Password0", "Main");
    response = testCommand("DEPOSIT main 1000\n");
    assertThat(response, containsString("SUCCESS"));

    Customer borrower = getCustomer(borrowerName);
    Customer lender = getCustomer(lenderName);
    Money borrowerBalanceBefore =
        getAndAssertOptional(borrower::getDefaultAccount).getBalance();
    Money lenderBalanceBefore =
        getAndAssertOptional(lender::getDefaultAccount).getBalance();

    response = testCommand(String.format("GRANTLOAN %s\n", loanID));
    assertThat(response, containsString("SUCCESS"));

    assertThat(new Loans().getCredits(lender).count(), equalTo(1L));
    assertThat(borrower.getLoanHistory().currentDebtCount(), equalTo(1));
    assertThat(borrower.getLoanHistory().hasCurrentLoanRequest(), equalTo(false));

    // Accounts have been credited and debited
    assertThat(getAndAssertOptional(borrower::getDefaultAccount).getBalance(),
        equalTo(borrowerBalanceBefore.add(MAX_MICROLOAN)));
    assertThat(getAndAssertOptional(lender::getDefaultAccount).getBalance(),
        equalTo(lenderBalanceBefore.subtract(MAX_MICROLOAN)));

    // Cannot grant a request that's already been accepted
    response = testCommand(String.format("GRANTLOAN %s\n", loanID));
    assertThat(response, matchesPattern("FAIL:.+not found.*"));
  }

  @Test
  public void cannotGrantMyOwnLoanRequest() throws IOException {
    String loanID = createLoanRequest("cantGrantOwn");
    String response = testCommand(String.format("GRANTLOAN %s\n", loanID));
    assertThat(response, matchesPattern("FAIL:.+your own.*"));
  }

  @Test
  public void cannotGrantNonexistentLoanRequest() throws IOException {
    setupCustomerWithAccount("cantGrantNon", "Password0");
    String response = testCommand("GRANTLOAN nonsenseID\n");
    assertThat(response, matchesPattern("FAIL:.+not found.*"));
  }

  @Test void cannotGrantRequestWithoutDefaultAccount() throws IOException {
    String loanID = createLoanRequest("cannotGrantRequestWithoutDefaultAccount");
    String response = logIn("Christina", "Christina123");
    assertThat(response, containsString("SUCCESS"));

    response = testCommand(String.format("GRANTLOAN %s\n", loanID)).toLowerCase();
    assertThat(response, matchesPattern("fail:.+default.+"));
  }

  @Test void cannotGrantRequestWithInsufficientFunds() throws IOException {
    String loanID = createLoanRequest("cannotGrantRequestWithInsufficientFunds");
    String response = logIn("John", "John123");
    assertThat(response, containsString("SUCCESS"));

    response = testCommand(String.format("GRANTLOAN %s\n", loanID)).toLowerCase();
    assertThat(response, matchesPattern("fail:.+insufficient funds.+"));
  }
}
