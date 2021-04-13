package newbank.server.commands;

import newbank.server.CustomerID;
import newbank.server.NewBank;
import newbank.server.exceptions.CommandInvalidSyntaxException;
import newbank.server.exceptions.RequestNotAllowedException;

public class LogoutCommand extends Command {
  private final String[] tokens;
  private final CustomerID customer;

  public LogoutCommand(final NewBank bank, final String[] tokens, final CustomerID customer) {
    this.tokens = tokens;
    this.customer = customer;
  }

  @Override
  public String getSyntax() {
    return "LOGOUT";
  }

  @Override
  public String execute() throws CommandInvalidSyntaxException {

    if (!(tokens.length == 1)) {
      throw new CommandInvalidSyntaxException();
    }

    try {
      checkLoggedIn(customer);
    } catch (RequestNotAllowedException e) {
      return String.format("FAIL: %s", e.getMessage());
    }

    customer.setKey("");
    return "SUCCESS: You have been logged out successfully.";
  }
}
