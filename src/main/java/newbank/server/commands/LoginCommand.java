package newbank.server.commands;

import newbank.server.CustomerID;
import newbank.server.NewBank;

import java.util.ArrayList;
import java.util.Optional;

public class LoginCommand extends Command {

  public LoginCommand(final NewBank bank, final String[] tokens, final CustomerID customer) {
    super(bank, tokens, customer);
    responsibilityChain = new ArrayList<>();
    responsibilityChain.add(this::requestingHelp);
    responsibilityChain.add(this::incorrectUsage);
  }

  @Override
  public String execute() {
    Optional<String> message = applyResponsibilityChain();
    if (message.isPresent()) return message.get();

    String username = tokens[1];
    String password = tokens[2];
    CustomerID tempCustomer = bank.checkLogInDetails(username, password);

    if (tempCustomer == null) return "FAIL: Log In Failed";

    // store customerID
    customerID.setKey(tempCustomer.getKey());
    return "SUCCESS: Log In Successful";
  }
  
  @Override
  protected String getSyntax() {
    return "LOGIN <username> <password>";
  }
}
