package newbank.server.commands;

import newbank.server.CustomerID;
import newbank.server.NewBank;

public class QuitCommand extends Command {
  public QuitCommand(final NewBank bank, final String[] tokens, CustomerID customer) {
      super(bank, tokens, customer);
  }

  @Override
  protected String getSyntax() {
    return "QUIT";
  }

  @Override
  public String execute() {
    return "SUCCESS: Good bye.";
  }
}
