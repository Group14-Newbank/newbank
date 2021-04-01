package newbank.server.commands;

import newbank.server.CustomerID;
import newbank.server.NewBank;

public class UnknownCommand extends Command {
  public UnknownCommand(final NewBank bank, final String[] tokens, CustomerID customer) {
  }

  @Override
  public String execute() {
    return "FAIL: Unknown command.";
  }
}
