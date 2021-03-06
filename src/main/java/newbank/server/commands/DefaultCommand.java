package newbank.server.commands;

import newbank.server.CustomerID;
import newbank.server.NewBank;
import newbank.server.exceptions.AccountInvalidException;
import newbank.server.exceptions.AccountTypeInvalidException;

import java.util.ArrayList;

public class DefaultCommand extends Command {

  public DefaultCommand(final NewBank bank, final String[] tokens, final CustomerID customerID) {
    super(bank, tokens, customerID);
    responsibilityChain = new ArrayList<>();
    responsibilityChain.add(this::requestingHelp);
    responsibilityChain.add(this::mustLogIn);
    responsibilityChain.add(this::incorrectUsage);
  }

  public String getSyntax() {
    return "DEFAULT <Name>";
  }

  @Override
  public String execute() {
    String message = applyResponsibilityChain();
    if (!message.isEmpty()) return message;
    final String accountName = tokens[1];
    try {
      bank.setDefaultAccount(customerID, accountName);
    } catch (AccountInvalidException ex) {
      return String.format("FAIL: Account [%s] does not exist.", tokens[1]);
    } catch (AccountTypeInvalidException ex) {
      return String.format("FAIL: Account [%s] cannot be default.", tokens[1]);
    }
    return String.format("SUCCESS: Account [%s] set as default.", accountName);
  }
}
