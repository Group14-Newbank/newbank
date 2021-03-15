package newbank.server.exceptions;

public class CustomerMaxAccountsException extends Exception {
  private int maxAccounts;

  public CustomerMaxAccountsException(int maxAccounts) {
    this.maxAccounts = maxAccounts;
  }

  public int getMaxAccounts() {
    return maxAccounts;
  }
}
