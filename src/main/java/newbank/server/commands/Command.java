package newbank.server.commands;

import newbank.server.CustomerID;
import newbank.server.exceptions.CommandInvalidSyntaxException;
import newbank.server.exceptions.RequestNotAllowedException;

/** Abstract representation of a command. */
public abstract class Command {
  public abstract String execute() throws CommandInvalidSyntaxException;

  /**
   * @return the command syntax
   */
  protected String getSyntax() {
    return "";
  }

  public String getUsage() {
    String syntax = getSyntax();
    if (syntax.isEmpty()) {
      return "No help instruction available";
    }

    return String.format("SUCCESS: Usage: %s", syntax);
  }

  public String getUsageInvalidSyntax() {
    String syntax = getSyntax();
    if (syntax.isEmpty()) {
      return "FAIL: unknown syntax.";
    }

    return String.format("FAIL: Usage: %s", syntax);
  }

  protected void checkLoggedIn(CustomerID customer) throws RequestNotAllowedException {
    if (!isLoggedIn(customer)) {
      throw new RequestNotAllowedException();
    }
  }

  protected boolean isLoggedIn(CustomerID customer) {
    return !customer.getKey().isEmpty();
  }
}
