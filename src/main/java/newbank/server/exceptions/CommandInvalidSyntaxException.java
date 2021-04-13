package newbank.server.exceptions;

public class CommandInvalidSyntaxException extends Exception {
  private static final long serialVersionUID = 12345656L;

  public CommandInvalidSyntaxException() {

  }

  public CommandInvalidSyntaxException(String message) {
    super(message);
  }
}
