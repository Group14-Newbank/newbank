package newbank.server.exceptions;

public class AccountInvalidNameException extends Exception {
  public AccountInvalidNameException(String message) {
    super(message);
  }
}
