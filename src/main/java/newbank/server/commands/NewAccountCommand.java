package newbank.server.commands;

import newbank.server.Account;
import newbank.server.CustomerID;
import newbank.server.NewBank;

import java.util.ArrayList;

public class NewAccountCommand extends Command {

  public NewAccountCommand(final NewBank bank, final String[] tokens, final CustomerID customerID) {
    super(bank, tokens, customerID);
    responsibilityChain = new ArrayList<>();
    responsibilityChain.add(this::requestingHelp);
    responsibilityChain.add(this::mustLogIn);
    responsibilityChain.add(this::incorrectUsage);
  }

  @Override
  public String getSyntax() {
    return "NEWACCOUNT <Name> [Default]";
  }

  @Override
  public String execute() {
    String message = applyResponsibilityChain();
    if (!message.isEmpty()) return message;

    final String accountName = tokens[1];
    boolean isDefault = tokens.length == 3 || isFirstNonSavingsAccount(accountName);

    return bank.newAccount(customerID, accountName, isDefault);
  }

  private boolean isFirstNonSavingsAccount(final String accountName) {
    return !Account.isSavingsAccount(accountName) && !bank.hasDefaultAccount(customerID);
  }
}
