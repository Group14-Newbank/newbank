package newbank.server.exceptions;

public class AccountInvalidException extends Exception {
  private static final long serialVersionUID = 627849707939479489L;

  private String owner;
  private String accountName;

  public AccountInvalidException(final String owner, final String accountName) {
    super();

    this.owner = owner;
    this.accountName = accountName;
  }

  public String getAccountName() {
    return accountName;
  }

  public String getOwner() {
    return owner;
  }
}
