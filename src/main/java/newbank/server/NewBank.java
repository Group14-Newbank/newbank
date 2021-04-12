package newbank.server;

import java.util.HashMap;
import java.util.Optional;

import org.javamoney.moneta.Money;

import newbank.server.exceptions.AccountBalanceInvalidException;
import newbank.server.exceptions.AccountInvalidException;
import newbank.server.exceptions.AccountNameInvalidException;
import newbank.server.exceptions.AccountTypeInvalidException;
import newbank.server.exceptions.CustomerInvalidException;
import newbank.server.exceptions.CustomerMaxAccountsException;
import newbank.server.exceptions.DuplicateCustomerException;
import newbank.server.exceptions.InsufficientFundsException;
import newbank.server.exceptions.PasswordInvalidException;
import newbank.server.exceptions.UsernameInvalidException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewBank {
  private static final NewBank bank = new NewBank();
  private final HashMap<String, Customer> customers;

  private NewBank() {
    customers = new HashMap<>();
    addTestData();
  }

  private void addTestData() {
    try {
      Customer bhagy = new Customer("Bhagy", "Bhagy123");

      bhagy.addAccount(new Account("Main", Money.of(1000, "GBP")));
      bhagy.addAccount(new Account("Savings", Money.of(201.19, "GBP")));

      customers.put("Bhagy", bhagy);

      Customer christina = new Customer("Christina", "Christina123");

      christina.addAccount(new Account("Savings", Money.of(1500, "GBP")));

      customers.put("Christina", christina);

      Customer john = new Customer("John", "John123");

      john.addAccount(new Account("Checking", Money.of(250, "GBP")));

      customers.put("John", john);
    } catch (CustomerMaxAccountsException e) {
      System.err.println("FAIL: Maximum number of accounts is: " + Customer.MAX_ACCOUNTS);
      System.exit(1);
    } catch (AccountNameInvalidException e) {
      System.err.println("FAIL: Invalid account name: " + e.getMessage());
      System.exit(1);
    } catch (AccountBalanceInvalidException e) {
      System.err.println("FAIL: Invalid balance specified.");
      System.exit(1);
    } catch (AccountInvalidException e) {
      System.err.println("FAIL: Invalid account.");
      System.exit(1);
    } catch (AccountTypeInvalidException e) {
      System.err.println("FAIL: Invalid default account.");
      System.exit(1);
    }
  }

  // simple algorithm to validate user name:
  // must start with a letter and only contain letters and digits
  // we allow UNICODE letters
  private void validateUsername(final String username) throws UsernameInvalidException {
    if (!username.matches("\\p{L}(?:\\p{L}|\\p{N})+")) {
      throw new UsernameInvalidException();
    }
  }

  // Simple algorithm to check that the password meets the security requirements
  // must be non-empty
  private void validatePassword(final String password) throws PasswordInvalidException {
    if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")){
      if (password.isEmpty());
      throw new PasswordInvalidException();
    }
  }

  /**
   * Add a new customer to the bank with the supplied credentials.
   *
   * @param username The customer's username
   * @param password The customer's password
   */
  public void addCustomer(final String username, final String password)
      throws DuplicateCustomerException, PasswordInvalidException, UsernameInvalidException {
    if (customers.containsKey(username)) {
      throw new DuplicateCustomerException();
    }

    validateUsername(username);
    validatePassword(password);

    customers.put(username, new Customer(username, password));
  }

  public static NewBank getBank() {
    return bank;
  }

  /**
   * Check customer's credentials.
   *
   * @param username The customer's username
   * @param password The customer's password
   * @return A derived ID identifying the customer on success, or null otherwise
   */
  public synchronized CustomerID checkLogInDetails(final String username, final String password) {
    if (customers.containsKey(username)) {
      if (credentialsAreValid(username, password)) {
        return new CustomerID(username);
      }
    }
    return null;
  }

  /**
   * Retrieve and display account information for a given customer.
   *
   * @param customerID The customer identifier
   * @return account information
   */
  public synchronized String showAccountsFor(final CustomerID customerID) {
    Customer customer = customers.get(customerID.getKey());

    return customer.accountsToString();
  }

  private boolean credentialsAreValid(final String username, final String password) {
    assert (customers.containsKey(username));

    return customers.get(username).getPassword().equals(password);
  }

  /**
   * Create a new account for a given customer.
   *
   * @param customerID The customer identifier
   * @param accountName The account name
   * @param isDefault Indicates whether this is the customer's default account
   * @return A success indicator if the operation was successful, otherwise an error message
   * @throws AccountInvalidException
   */
  public synchronized String newAccount(
      final CustomerID customerID, final String accountName, final boolean isDefault) {
    Customer customer = customers.get(customerID.getKey());

    try {
      customer.addAccount(new Account(accountName, Money.of(0, "GBP")));

      if (isDefault) {
        customer.setDefaultAccount(accountName);
      }

      return "SUCCESS: The account has been created successfully.";
    } catch (CustomerMaxAccountsException e) {
      return "FAIL: Maximum number of accounts is: " + Customer.MAX_ACCOUNTS;
    } catch (AccountNameInvalidException e) {
      return "FAIL: Invalid account name: " + e.getMessage();
    } catch (AccountBalanceInvalidException e) {
      return "FAIL: Account starting balance cannot be negative.";
    } catch (AccountTypeInvalidException e) {
      return String.format("FAIL: Account [%s] cannot be default.", accountName);
    } catch (AccountInvalidException e) {
      return String.format("FAIL: Account [%s] does not exist.", accountName);
    }
  }

  /**
   * Retrieves the customer with a given name.
   *
   * @param customerName
   * @return
   */
  public synchronized Optional<Customer> getCustomer(final String customerName) {
    return customers
        .entrySet()
        .stream()
        .filter(e -> e.getValue().getUsername().equalsIgnoreCase(customerName))
        .findFirst()
        .map(e -> e.getValue());
  }

  private Account getAccount(final CustomerID customerID, final String accountName)
      throws AccountInvalidException {
    Customer customer = customers.get(customerID.getKey());
    Optional<Account> account = customer.getAccount(accountName);

    return account.orElseThrow(() -> new AccountInvalidException(customer.getUsername()));
  }

  /**
   * Deposit some money into a specified customer's account.
   *
   * @param customerID The customer identifier
   * @param accountName The account name
   * @param money The amount to deposit
   */
  public synchronized void depositMoney(
      final CustomerID customerID, final String accountName, final Money money)
      throws AccountInvalidException {

    getAccount(customerID, accountName).credit(money);
  }

  /**
   * Set the default customer's account. This also clears any previously set default account.
   *
   * @param customerID The customer identifier
   * @param accountName The account name
   * @throws AccountInvalidException if the customer's account does not exist.
   * @throws AccountTypeInvalidException
   */
  public synchronized void setDefaultAccount(final CustomerID customerID, final String accountName)
      throws AccountInvalidException, AccountTypeInvalidException {
    Customer customer = customers.get(customerID.getKey());

    customer.setDefaultAccount(accountName);
  }

  /**
   * Determine whether a customer has a default current account set.
   *
   * @param customerID The customer identifier
   * @return true if so, false otherwise.
   */
  public synchronized boolean hasDefaultAccount(final CustomerID customerID) {
    Customer customer = customers.get(customerID.getKey());

    return customer.hasDefaultAccount();
  }

  /**
   * Credit default current account for customer identified by recipientName with the amount of
   * money supplied.
   *
   * @param customerID The customer initiating the transaction
   * @param recipientName The customer receiving the funds
   * @param money The amount of money to credit
   * @throws AccountInvalidException If the recipient has no default current account.
   * @throws CustomerInvalidException If the recipient does not exist.
   * @throws InsufficientFundsException
   */
  public synchronized void payCustomer(
      final CustomerID customerID, final String recipientName, final Money money)
      throws AccountInvalidException, CustomerInvalidException, InsufficientFundsException {
    Optional<Customer> recipient = getCustomer(recipientName);

    if (!recipient.isPresent()) {
      throw new CustomerInvalidException();
    }

    Optional<Account> destinatorAccount = recipient.get().getDefaultAccount();

    // check recipient's account
    if (!destinatorAccount.isPresent()) {
      throw new AccountInvalidException(recipient.get().getUsername());
    }

    Customer originator = customers.get(customerID.getKey());

    Optional<Account> originatorAccount = originator.getDefaultAccount();

    // check originator's account
    if (!originatorAccount.isPresent()) {
      throw new AccountInvalidException(originator.getUsername());
    }

    // check originator's funds
    if (originatorAccount.get().getBalance().isLessThan(money)) {
      throw new InsufficientFundsException();
    }

    // perform transaction
    originatorAccount.get().debit(money);
    destinatorAccount.get().credit(money);
  }
}
