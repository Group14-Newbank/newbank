package newbank.server;

import java.util.HashMap;

import newbank.server.exceptions.AccountInvalidNameException;
import newbank.server.exceptions.CustomerMaxAccountsException;

public class NewBank {

  private static final NewBank bank = new NewBank();
  private HashMap<String, Customer> customers;

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
    } catch (Exception e) {

    }
  }

  /**
   * Add a new customer to the bank with the supplied credentials.
   *
   * @param username The customer's username
   * @param password The customer's password
   */
  public void addCustomer(final String username, final String password) {
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

  private boolean credentialsAreValid(final String username, final String password) {
    assert (customers.containsKey(username));

    return customers.get(username).getPassword().equals(password);
  }

  // commands from the NewBank customer are processed in this method
  public synchronized String processRequest(CustomerID customerID, String request) {
    Customer customer = customers.get(customerID.getKey());
    if (customer == null) {
      return "FAIL: Customer not found!";
    }

    String[] args = request.split("\\s+");
    if (args.length == 0) {
      return "FAIL: Invalid command!";
    }

    String command = args[0];
    switch (command) {
    case "SHOWMYACCOUNTS":
      return showMyAccounts(customer);

    case "NEWACCOUNT":
      if (args.length != 2) {
        return "FAIL: The proper syntax is: NEWACCOUNT <Name>";
      }

      return newAccount(customer, args[1]);

    default:
      return "FAIL: Unknown command!";
    }
  }

  private String showMyAccounts(Customer customer) {
    return customer.accountsToString();
  }

  private String newAccount(Customer customer, String accountName) {
    try {
      Account account = new Account(accountName, 0);

      customer.addAccount(account);

      return "The account has been created successfully.";
    } catch (CustomerMaxAccountsException e) {
      return "FAIL: Maximum number of accounts is: " + e.getMaxAccounts();
    } catch (AccountInvalidNameException e) {
      return "FAIL: Invalid account name: " + e.getMessage();
    }
  }
}
