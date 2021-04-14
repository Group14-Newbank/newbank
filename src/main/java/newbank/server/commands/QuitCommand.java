package newbank.server.commands;

import newbank.server.CustomerID;
import newbank.server.NewBank;

import java.util.ArrayList;

public class QuitCommand extends Command {
  public QuitCommand(final NewBank bank, final String[] tokens, CustomerID customerID) {
      super(bank, tokens, customerID);
    responsibilityChain = new ArrayList<>();
    responsibilityChain.add(this::requestingHelp);
    responsibilityChain.add(this::incorrectUsage);
  }

  @Override
  public String getSyntax() {
    return "QUIT";
  }

  @Override
  public String execute() {
    String message = applyResponsibilityChain();
    return message.isEmpty() ? "SUCCESS: Good bye." : message;
  }
}
