package newbank.server.exceptions;

public class SyntaxInvalidException extends Exception {
  private static final long serialVersionUID = 7564364169006200062L;

  public SyntaxInvalidException(final String message) {
    super(message);
  }
}
