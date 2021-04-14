package newbank.server;

import java.util.ArrayList;
import java.util.Optional;

import newbank.server.exceptions.AccountInvalidException;
import newbank.server.exceptions.AccountTypeInvalidException;
import newbank.server.exceptions.CustomerMaxAccountsException;

public class Customer {
  private String username;
  private String password;
  private final ArrayList<Account> accounts;
  private Optional<Account> defaultAccount;

  public static final int MAX_ACCOUNTS = 5;

  public Customer(final String username, final String password) {
    this.username = username;
    this.password = password;
    accounts = new ArrayList<>();
    defaultAccount = Optional.empty();
  }

  public String accountsToString() {
    StringBuilder s = new StringBuilder();
    for (Account a : accounts) {
      if (defaultAccount.isPresent() && a == defaultAccount.get()) {
        s.append('*');
      }

      s.append(a.toString());
      s.append("\n");
    }
    return s.toString();
  }

  public void addAccount(Account account)
      throws CustomerMaxAccountsException, AccountInvalidException, AccountTypeInvalidException {
    if (accounts.size() >= MAX_ACCOUNTS) {
      throw new CustomerMaxAccountsException();
    }

    accounts.add(account);

    if (!hasDefaultAccount() && !Account.isSavingsAccount(account.getName())) {
      setDefaultAccount(account.getName());
    }
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public Optional<Account> getAccount(final String accountName) {
    return accounts.stream().filter(a -> a.getName().equalsIgnoreCase(accountName)).findFirst();
  }

  public Optional<Account> getDefaultAccount() {
    return defaultAccount;
  }

  /**
   * Set the customer's default current account.
   *
   * @param accountName The account name
   * @throws AccountInvalidException if the specified account does not exist.
   * @throws AccountTypeInvalidException if the the given account type cannot be set as the default
   *     account.
   */
  public void setDefaultAccount(final String accountName)
      throws AccountInvalidException, AccountTypeInvalidException {
    Optional<Account> newDefault =
        accounts.stream().filter(e -> e.getName().equalsIgnoreCase(accountName)).findFirst();

    Account acc = newDefault.orElseThrow(() -> new AccountInvalidException(username, accountName));

    if (Account.isSavingsAccount(accountName)) {
      throw new AccountTypeInvalidException();
    }

    // update default account
    defaultAccount = Optional.of(acc);
  }

  /**
   * Determine if the account supplied is the customer's default current account.
   *
   * @param accountName The account name
   * @return true if the account is the default current account, false otherwise.
   */
  public boolean isDefaultAccount(final String accountName) {
    if (defaultAccount.isPresent()) {
      return defaultAccount.get().getName().equalsIgnoreCase(accountName);
    }

    return false;
  }

  /** @return true if the customer has a default current account, false otherwise. */
  public boolean hasDefaultAccount() {
    return defaultAccount.isPresent();
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
