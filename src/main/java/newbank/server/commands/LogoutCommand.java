package newbank.server.commands;

import newbank.server.CustomerID;
import newbank.server.NewBank;

import java.util.ArrayList;

public class LogoutCommand extends Command {

  public LogoutCommand(final NewBank bank, final String[] tokens, final CustomerID customerID) {
    super(bank, tokens, customerID);
    responsibilityChain = new ArrayList<>();
    responsibilityChain.add(this::requestingHelp);
    responsibilityChain.add(this::mustLogIn);
    responsibilityChain.add(this::incorrectUsage);
  }

  @Override
  public String getSyntax() {
    return "LOGOUT";
  }

  @Override
  public String execute() {
    String message = applyResponsibilityChain();
    if (!message.isEmpty()) return message;

    customerID.setKey("");
    return "SUCCESS: You have been logged out successfully.";
  }
}
