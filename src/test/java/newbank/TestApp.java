package newbank;

import static newbank.Configuration.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.stream.Stream;

import newbank.server.NewBank;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import newbank.client.ConfigurationException;
import newbank.client.ExampleClient;
import newbank.server.NewBankServer;
import newbank.utils.Display;
import newbank.utils.QueueDisplay;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.money.MonetaryAmount;

public class TestApp {
  private static NewBankServer server;

  private PipedReader reader;
  private PipedWriter writer;
  private Display display;
  private ExampleClient client;

  @Disabled
  private String testCommand(String command) throws IOException {
    writer.write(command);
    display.discardLinesUntil("request");

    return display.getLine();
  }

  @BeforeAll
  public static void beforeAll() throws IOException {
    server = new NewBankServer(DEFAULT_PORT);
    server.start();
  }

  @BeforeEach
  public void setup() throws IOException, ConfigurationException {
    reader = new PipedReader();
    writer = new PipedWriter(reader);
    display = new QueueDisplay();

    client = new ExampleClient("localhost", DEFAULT_PORT, reader);
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

    String[] output = result.split(":");
    assertThat(output[0].trim(), equalTo(account));
    assertThat(output[1].trim(), equalTo(balance));
  }

  @Test
  public void cannotSendCommandsIfLoggedOut() throws IOException {
    writer.write("SHOWMYACCOUNTS\n");
    assertThat(display.getLine(), not(matchesPattern("request")));
  }

  @Test
  public void canDisplayBalance() throws IOException {
    String response = logIn("Bhagy", "bhagy");
    assertThat(response, containsString("SUCCESS"));

    String accountSummary = testCommand("SHOWMYACCOUNTS\n");
    assertThat(accountSummary, matchesPattern("[*]Main:\\s+1000.00\\s+GBP"));

    accountSummary = display.getLine();
    assertThat(accountSummary, matchesPattern("Savings:\\s+201.19\\s+GBP"));
    assertThat(display.getLine(), equalTo(""));
  }

  @Test
  public void canCreateNewAccount() throws IOException {
    String response = logIn("John", "john");
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
    addCustomer("TestCustomer1", "password1");

    String response = logIn("TestCustomer1", "password1");
    assertThat(response, containsString("SUCCESS"));
  }

  private void setupCustomerWithAccount(final String username, final String password)
      throws IOException {
    addCustomer(username, password);

    String response = logIn(username, password);
    assertThat(response, containsString("SUCCESS"));

    response = testCommand("NEWACCOUNT Savings\n");
    assertThat(response, containsString("SUCCESS"));
  }

  @Test
  public void canDepositMoney() throws IOException {
    setupCustomerWithAccount("TestCustomer2", "password2");

    String response = testCommand("DEPOSIT Savings 1000.0\n");
    assertThat(response, containsString("SUCCESS"));

    checkAccountBalance("Savings", "1000.00 GBP");

    response = testCommand("DEPOSIT Savings 250.0\n");
    assertThat(response, containsString("SUCCESS"));

    checkAccountBalance("Savings", "1250.00 GBP");
  }

  @Test
  public void canHandleInvalidDepositAccountOrAmount() throws IOException {
    setupCustomerWithAccount("TestCustomer3", "password3");

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
    setupCustomerWithAccount("TestCustomer4", "password4");

    String response = testCommand("NEWACCOUNT Main DEFAULT\n");
    assertThat(response, containsString("SUCCESS"));

    response = testCommand("NEWACCOUNT Checking\n");
    assertThat(response, containsString("SUCCESS"));

    response = testCommand("DEFAULT Checking\n");
    assertThat(response, containsString("SUCCESS"));

    String result = testCommand("SHOWMYACCOUNTS\n");
    assertThat(result, matchesPattern("Savings:\\s+0.00\\s+GBP"));

    result = display.getLine();
    assertThat(result, matchesPattern("Main:\\s+0.00\\s+GBP"));

    result = display.getLine();
    assertThat(result, matchesPattern("[*]Checking:\\s+0.00\\s+GBP"));
  }

  @Test
  public void canHandleSettingSavingsAsDefault() throws IOException {
    setupCustomerWithAccount("TestCustomer5", "password5");

    String response = testCommand("DEFAULT Savings\n");
    assertThat(response, containsString("FAIL: Account [Savings] cannot be default."));
  }

  @Test
  public void canHandleSettingNonExistingAccountAsDefault() throws IOException {
    setupCustomerWithAccount("TestCustomer7", "password7");

    String response = testCommand("DEFAULT Main\n");
    assertThat(response, equalTo("FAIL: Account [Main] does not exist."));
  }

  @Test
  public void checkThatFirstNonSavingsAccountIsDefault() throws IOException {
    setupCustomerWithAccount("TestCustomer6", "password6");

    String response = testCommand("NEWACCOUNT Main\n");
    assertThat(response, containsString("SUCCESS"));

    String result = testCommand("SHOWMYACCOUNTS\n");
    assertThat(result, matchesPattern("Savings:\\s+0.00\\s+GBP"));

    result = display.getLine();
    assertThat(result, matchesPattern("[*]Main:\\s+0.00\\s+GBP"));
  }

  @Test
  public void canPayCustomer() throws IOException {
    setupCustomerWithAccount("TestCustomer8", "password8");
    String response = testCommand("NEWACCOUNT Main DEFAULT\n");
    assertThat(response, containsString("SUCCESS"));

    response = testCommand("DEPOSIT Main 1000.0\n");
    assertThat(response, containsString("SUCCESS"));

    response = testCommand("PAY John 1000.0\n");
    assertThat(response, equalTo("Default account for customer [John] credited successfully."));
  }

  @Test
  public void canHandleInsufficientFunds() throws IOException {
    setupCustomerWithAccount("TestCustomer9", "password9");
    String response = testCommand("NEWACCOUNT Main DEFAULT\n");
    assertThat(response, containsString("SUCCESS"));

    response = testCommand("PAY John 1000.0\n");
    assertThat(response, containsString("FAIL: Insufficient funds to perform transaction."));
  }

  @Test
  public void canHandleInvalidPayRecipient() throws IOException {
    setupCustomerWithAccount("TestCustomer10", "password10");
    String response = testCommand("NEWACCOUNT Main DEFAULT\n");
    assertThat(response, containsString("SUCCESS"));

    response = testCommand("PAY Jason 1000.0\n");
    assertThat(response, containsString("FAIL: Customer [Jason] does not exist."));
  }

  @Test
  public void canHandleInvalidCreditAmount() throws IOException {
    setupCustomerWithAccount("TestCustomer11", "password11");
    String response = testCommand("NEWACCOUNT Main DEFAULT\n");
    assertThat(response, containsString("SUCCESS"));

    response = testCommand("PAY John -100.0\n");
    assertThat(response, containsString("FAIL: Credit amount [-100.0] invalid."));
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
    setupCustomerWithAccount(username, "password0");

    String response = testCommand(String.format(
        "REQUESTLOAN %s %s\n", MAX_MICROLOAN.getNumber().toString(), MAX_REPAYMENT_PERIOD_DAYS
    ));
    assertThat(response, containsString("SUCCESS"));
    assertThat("The user now has 1 loan-request", 
        NewBank.getBank().getCustomer(username).get()
            .getLoanHistory().hasCurrentLoanRequest()
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
        Arguments.of("loan.+invalid", "0", "365"),
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
        String.format("password7%d", hash)
    );

    String response = testCommand(String.format("REQUESTLOAN %s %s\n", amount, term)).toLowerCase();
    assertThat(response, matchesPattern(String.format("fail:.+%s.+", failReason)));
  }
  
  // TODO cannotRequestMicroloanWhenAlreadyHave3
  // TODO cannotRequestMicroloanWhenDefaultedInPast
}
