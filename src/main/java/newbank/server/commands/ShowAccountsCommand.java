package newbank.server.commands;

import newbank.server.CustomerID;
import newbank.server.NewBank;
import newbank.utils.Config;

import java.util.ArrayList;

public class ShowAccountsCommand extends Command {

  public ShowAccountsCommand(final NewBank bank, final String[] tokens, CustomerID customerID) {
      super(bank, tokens, customerID);
    responsibilityChain = new ArrayList<>();
    responsibilityChain.add(this::requestingHelp);
    responsibilityChain.add(this::mustLogIn);
  }

  @Override
  public String getSyntax() {
      return "SHOWMYACCOUNTS";
  }

  @Override
  public String execute() {
    String message = applyResponsibilityChain();
    if (!message.isEmpty()) return message;

    return String.format(
        "SUCCESS: %s%s", Config.MULTILINE_INFO_SEPARATOR, bank.showAccountsFor(customerID)
    );
  }
}
