package newbank.server;

import newbank.server.exceptions.RequestNotAllowedException;

/** Abstract representation of a command. */
public abstract class Command {
  public abstract String execute();

  protected void checkLoggedIn(CustomerID customer) throws RequestNotAllowedException {
    if (customer.getKey().isEmpty()) {
      throw new RequestNotAllowedException();
    }
  }
}
