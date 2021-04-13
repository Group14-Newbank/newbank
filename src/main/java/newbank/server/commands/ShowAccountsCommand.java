package newbank.server.commands;

import newbank.server.CustomerID;
import newbank.server.NewBank;
import newbank.server.exceptions.RequestNotAllowedException;
import newbank.utils.Config;

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
      return String.format("SUCCESS: %s%s", Config.MULTILINE_INFO_SEPARATOR,bank.showAccountsFor(customer));
    } catch (RequestNotAllowedException ex) {
      return String.format("FAIL: %s", ex.getMessage());
    }
  }
}
