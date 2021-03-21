package newbank.server;

import java.util.ArrayList;

import newbank.server.exceptions.CustomerMaxAccountsException;

public class Customer {
  private String username;
  private String password;
  private ArrayList<Account> accounts;

  public static final int MAX_ACCOUNTS = 5;

  public Customer(final String username, final String password) {
    this.username = username;
    this.password = password;
    accounts = new ArrayList<>();
  }

  public String accountsToString() {
    String s = "";
    for (Account a : accounts) {
      s += a.toString();
    }
    return s;
  }

  public void addAccount(Account account) throws CustomerMaxAccountsException {
    if (accounts.size() >= MAX_ACCOUNTS) {
      throw new CustomerMaxAccountsException();
    }

    accounts.add(account);
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }
}
