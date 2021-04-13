package newbank.server.exceptions;

public class AccountInvalidException extends Exception {
  private static final long serialVersionUID = 627849707939479489L;

  private String accountName;

  public AccountInvalidException(String accountName) {
    this.accountName = accountName;
  }

  public String getAccountName() {
    return accountName;
  }
}
