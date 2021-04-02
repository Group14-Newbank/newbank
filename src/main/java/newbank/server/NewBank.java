package newbank.server;

import java.util.HashMap;
import java.util.Optional;

import org.javamoney.moneta.Money;

import newbank.server.exceptions.AccountNameInvalidException;
import newbank.server.exceptions.CustomerMaxAccountsException;
import newbank.server.exceptions.DuplicateCustomerException;
import newbank.server.exceptions.PasswordInvalidException;
import newbank.server.exceptions.UsernameInvalidException;

public class NewBank {
  private static final NewBank bank = new NewBank();
  private final HashMap<String, Customer> customers;

  private NewBank() {
    customers = new HashMap<>();
    addTestData();
  }

  private void addTestData() {
    try {
      Customer bhagy = new Customer("Bhagy", "bhagy");
      bhagy.addAccount(new Account("Main", Money.of(1000, "GBP")));
      customers.put("Bhagy", bhagy);

      Customer christina = new Customer("Christina", "christina");
      christina.addAccount(new Account("Savings", Money.of(1500, "GBP")));
      customers.put("Christina", christina);

      Customer john = new Customer("John", "john");
      john.addAccount(new Account("Checking", Money.of(250, "GBP")));
      customers.put("John", john);
    } catch (CustomerMaxAccountsException e) {
      System.err.println("FAIL: Maximum number of accounts is: " + Customer.MAX_ACCOUNTS);
      System.exit(1);
    } catch (AccountNameInvalidException e) {
      System.err.println("FAIL: Invalid account name: " + e.getMessage());
      System.exit(1);
    }
  }

  // simple algorithm to validate user name:
  // must start with a letter and only contain letters and digits
  // we allow UNICODE letters
  private void validateUsername(final String username) throws UsernameInvalidException {
    if (!username.matches("^\\p{L}(?:\\p{L}|\\p{N})+")) {
      throw new UsernameInvalidException();
    }
  }

  // Simple algorithm to check that the password meets the security requirements
  // must be non-empty
  private void validatePassword(final String password) throws PasswordInvalidException {
    if (password.isEmpty()) {
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
   * Check customer's credentials
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
   * Retrieve and display account information for a given customer
   *
   * @param customerID The customer identifier
   * @return account information
   */
  public synchronized String showAccountsFor(final CustomerID customerID) {
    Customer customer = customers.get(customerID.getKey());

    return showAccountsFor(customer);
  }

  private String showAccountsFor(final Customer customer) {
    return customer.accountsToString();
  }

  private boolean credentialsAreValid(final String username, final String password) {
    assert (customers.containsKey(username));

    return customers.get(username).getPassword().equals(password);
  }

  /**
   * Create a new account for a given customer
   *
   * @param customerID The customer identifier
   * @param accountName the account name
   * @return a success indicator if the operation was successful, otherwise an error message
   */
  public synchronized String newAccount(final CustomerID customerID, final String accountName) {
    Customer customer = customers.get(customerID.getKey());

    return newAccount(customer, accountName);
  }

  private String newAccount(final Customer customer, final String accountName) {
    try {
      Money openingBalance = Money.of(0, "GBP");

      Account account = new Account(accountName, openingBalance);

      customer.addAccount(account);

      return "SUCCESS: The account has been created successfully.";
    } catch (CustomerMaxAccountsException e) {
      return "FAIL: Maximum number of accounts is: " + Customer.MAX_ACCOUNTS;
    } catch (AccountNameInvalidException e) {
      return "FAIL: Invalid account name: " + e.getMessage();
    }
  }

  /**
   * Retrieves the customer with a given name
   *
   * @param customerName
   * @return
   */
  public synchronized Optional<Customer> getCustomer(final String customerName) {
    return customers
        .entrySet()
        .stream()
        .filter(e -> e.getValue().getUsername().equals(customerName))
        .findFirst()
        .map(e -> e.getValue());
  }
}
