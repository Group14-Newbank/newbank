package newbank.server;

import java.util.ArrayList;

import newbank.server.exceptions.CustomerMaxAccountsException;

public class Customer {
  private String username;
  private String password;
  private final ArrayList<Account> accounts;

  public static final int MAX_ACCOUNTS = 5;

  public Customer(final String username, final String password) {
    this.username = username;
    this.password = password;
    accounts = new ArrayList<>();
  }

  public String accountsToString() {
    StringBuilder s = new StringBuilder();
    for (Account a : accounts) {
      s.append(a.toString());
    }
    return s.toString();
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
  
  @Override
  public String toString() {
	  StringBuilder sb = new StringBuilder();
	  sb.append(this.getClass().getName() + "[");
	  sb.append("username=" + username + ", ");
	  sb.append("password=" + password + ", ");
	  sb.append("accounts=" + this.accountsToString());
	  sb.append("]");
	  return sb.toString();
  }
}
