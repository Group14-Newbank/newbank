package newbank.server.commands;

import newbank.server.CustomerID;
import newbank.server.NewBank;
import newbank.server.exceptions.DuplicateCustomerException;
import newbank.server.exceptions.PasswordInvalidException;
import newbank.server.exceptions.UsernameInvalidException;

import java.util.ArrayList;

public class RegisterCommand extends Command {

  public RegisterCommand(final NewBank bank, final String[] tokens, final CustomerID customerID) {
    super(bank, tokens, customerID);
    responsibilityChain = new ArrayList<>();
    responsibilityChain.add(this::requestingHelp);
    responsibilityChain.add(this::incorrectUsage);
    responsibilityChain.add(this::mustLogOut);
  }

  @Override
  public String getSyntax() {
    return "REGISTER <username> <password>";
  }

  @Override
  public String execute() {
    String message = applyResponsibilityChain();
    if (!message.isEmpty()) return message;

    String username = tokens[1];
    String password = tokens[2];
    try {
      bank.addCustomer(username, password);

      return "SUCCESS: Customer created successfully.";
    } catch (DuplicateCustomerException ex) {
      return "FAIL: " + String.format("Customer name [%s] already exists.", username);
    } catch (PasswordInvalidException e) {
      return "FAIL: Password must contain a mixture of uppercase and lowercase letters and at least one number.";
    } catch (UsernameInvalidException e) {
      return String.format(
          "FAIL: Username [%s] invalid. Username must start with a letter and contain only letters and digits.",
          username);
    }
  }

  private String mustLogOut() {
    return isLoggedIn() ? "FAIL: Request not allowed, please log out first." : "";
  }
}
