package newbank.server;

import java.util.HashMap;
import java.util.Optional;

import newbank.server.exceptions.AccountNameInvalidException;
import newbank.server.exceptions.CustomerMaxAccountsException;
import newbank.server.exceptions.DuplicateCustomerException;
import newbank.server.exceptions.PasswordInvalidException;

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
      bhagy.addAccount(new Account("Main", 1000.0));
      customers.put("Bhagy", bhagy);

      Customer christina = new Customer("Christina", "christina");
      christina.addAccount(new Account("Savings", 1500.0));
      customers.put("Christina", christina);

      Customer john = new Customer("John", "john");
      john.addAccount(new Account("Checking", 250.0));
      customers.put("John", john);
    } catch (CustomerMaxAccountsException e) {
      System.err.println("FAIL: Maximum number of accounts is: " + Customer.MAX_ACCOUNTS);
      System.exit(1);
    } catch (AccountNameInvalidException e) {
      System.err.println("FAIL: Invalid account name: " + e.getMessage());
      System.exit(1);
    }
  }

  /**
   * Add a new customer to the bank with the supplied credentials.
   *
   * @param username The customer's username
   * @param password The customer's password
   */
  public void addCustomer(final String username, final String password)
      throws DuplicateCustomerException, PasswordInvalidException {
    if (customers.containsKey(username)) {
      throw new DuplicateCustomerException();
    }

    validatePassword(password);

    customers.put(username, new Customer(username, password));
  }

  // Simple algorithm to check that the password meets the security requirements
  private void validatePassword(final String password) throws PasswordInvalidException {
    if (password.isEmpty()) {
      throw new PasswordInvalidException();
    }
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
      Account account = new Account(accountName, 0.0);

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
