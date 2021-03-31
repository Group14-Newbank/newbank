package newbank.server;

import newbank.server.exceptions.RequestNotAllowedException;
import newbank.server.exceptions.SyntaxInvalidException;

public class NewAccountCommand extends Command {
  private final NewBank bank;
  private final String[] tokens;
  private final CustomerID customer;

  public NewAccountCommand(final NewBank bank, final String[] tokens, final CustomerID customer) {
    this.bank = bank;
    this.tokens = tokens;
    this.customer = customer;
  }

  private void validateSyntax() throws SyntaxInvalidException {
    if (tokens.length != 2) {
      throw new SyntaxInvalidException("The proper syntax is: NEWACCOUNT <Name>");
    }
  }

  @Override
  public String execute() {
    try {
      checkLoggedIn(customer);

      validateSyntax();

      return bank.newAccount(customer, tokens[1]);
    } catch (RequestNotAllowedException | SyntaxInvalidException ex) {
      return String.format("FAIL: %s", ex.getMessage());
    }
  }
}
