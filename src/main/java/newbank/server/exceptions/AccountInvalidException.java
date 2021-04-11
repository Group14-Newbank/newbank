package newbank.server.exceptions;

public class AccountInvalidException extends Exception {
  private static final long serialVersionUID = 627849707939479489L;
  private final String owner;

  public AccountInvalidException(final String owner) {
    super();
    this.owner = owner;
  }

  public String getOwner() {
    return owner;
  }
}
