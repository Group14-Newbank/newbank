package newbank.server.commands;

import newbank.server.CustomerID;
import newbank.server.NewBank;

import java.util.ArrayList;

public class LoginCommand extends Command {

  public LoginCommand(final NewBank bank, final String[] tokens, final CustomerID customerID) {
    super(bank, tokens, customerID);
    responsibilityChain = new ArrayList<>();
    responsibilityChain.add(this::requestingHelp);
    responsibilityChain.add(this::incorrectUsage);
  }

  @Override
  public String execute() {
    String message = applyResponsibilityChain();
    if (!message.isEmpty()) return message;

    String username = tokens[1];
    String password = tokens[2];
    CustomerID tempCustomer = bank.checkLogInDetails(username, password);

    if (tempCustomer == null) return "FAIL: Log In Failed";

    // store customerID
    customerID.setKey(tempCustomer.getKey());
    return "SUCCESS: Log In Successful";
  }
  
  @Override
  public String getSyntax() {
    return "LOGIN <username> <password>";
  }
}
