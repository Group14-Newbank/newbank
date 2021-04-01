package newbank.server.exceptions;

public class RequestNotAllowedException extends Exception {
  private static final long serialVersionUID = 1234563L;

  public RequestNotAllowedException() {
    super("Request not allowed, please log in first.");
  }
}
