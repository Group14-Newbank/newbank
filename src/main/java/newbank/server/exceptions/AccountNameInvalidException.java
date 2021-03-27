package newbank.server.exceptions;

public class AccountNameInvalidException extends Exception {

  private static final long serialVersionUID = 1234565L;

  public AccountNameInvalidException(String message) {
    super(message);
  }
}
