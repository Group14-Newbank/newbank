package newbank.server.commands;

import newbank.server.CustomerID;
import newbank.server.NewBank;

import java.util.ArrayList;
import java.util.Optional;

public class ShowAccountsCommand extends Command {

  public ShowAccountsCommand(final NewBank bank, final String[] tokens, CustomerID customer) {
      super(bank, tokens, customer);
    responsibilityChain = new ArrayList<>();
    responsibilityChain.add(this::requestingHelp);
    responsibilityChain.add(this::mustLogIn);
  }

  @Override
  protected String getSyntax() {
      return "SHOWMYACCOUNTS";
  }

  @Override
  public String execute() {
    Optional<String> message = applyResponsibilityChain();
    if (message.isPresent()) return message.get();

    return bank.showAccountsFor(customerID);
  }
}
