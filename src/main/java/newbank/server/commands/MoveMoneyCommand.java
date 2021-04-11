package newbank.server.commands;

import newbank.server.CustomerID;
import newbank.server.NewBank;
import newbank.server.exceptions.RequestNotAllowedException;
import newbank.server.exceptions.SyntaxInvalidException;

public class MoveMoneyCommand extends Command {
  private final NewBank bank;
  private final String[] tokens;
  private final CustomerID customer;

  public MoveMoneyCommand(final NewBank bank, final String[] tokens, final CustomerID customer) {
    this.bank = bank;
    this.tokens = tokens;
    this.customer = customer;
  }

  private void validateSyntax() throws SyntaxInvalidException {
    if (tokens.length != 2) {
      throw new SyntaxInvalidException("The proper syntax is: MoveMoney <Name>");
    }
  }

  @Override
  public String execute() {
    try {

    } catch (RequestNotAllowedException | SyntaxInvalidException ex) {
      return String.format("FAIL: %s", ex.getMessage());
    }
  }
}
