package newbank.server.commands;

import newbank.server.CustomerID;
import newbank.server.NewBank;
import newbank.server.exceptions.RequestNotAllowedException;

public class ShowAccountsCommand extends Command {
  private final NewBank bank;
  private final CustomerID customer;

  public ShowAccountsCommand(final NewBank bank, final String[] tokens, CustomerID customer) {
    this.bank = bank;
    this.customer = customer;
  }

  @Override
  public String execute() {
    try {
      checkLoggedIn(customer);
      return bank.showAccountsFor(customer);
    } catch (RequestNotAllowedException ex) {
      return String.format("FAIL: %s", ex.getMessage());
    }
  }
}
