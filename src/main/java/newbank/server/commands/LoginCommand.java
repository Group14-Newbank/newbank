package newbank.server.commands;

import newbank.server.CustomerID;
import newbank.server.NewBank;

public class LoginCommand extends Command {
  private final NewBank bank;
  private final String[] tokens;
  private final CustomerID customer;

  public LoginCommand(final NewBank bank, final String[] tokens, final CustomerID customer) {
    this.bank = bank;
    this.tokens = tokens;
    this.customer = customer;
  }

  @Override
  public String execute() {
    String username = "";
    String password = "";

    if (tokens.length >= 2) {
      username = tokens[1];
    }
    if (tokens.length >= 3) {
      password = tokens[2];
    }

    CustomerID tempCustomer = bank.checkLogInDetails(username, password);

    if (tempCustomer != null) {
      // store customerID
      customer.setKey(tempCustomer.getKey());

      return "SUCCESS: Log In Successful";
    } else {
      return "FAIL: Log In Failed";
    }
  }
}
