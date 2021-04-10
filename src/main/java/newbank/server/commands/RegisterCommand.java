package newbank.server.commands;

import newbank.server.CustomerID;
import newbank.server.NewBank;
import newbank.server.exceptions.DuplicateCustomerException;
import newbank.server.exceptions.PasswordInvalidException;
import newbank.server.exceptions.UsernameInvalidException;

import java.util.ArrayList;
import java.util.Optional;

public class RegisterCommand extends Command {

  public RegisterCommand(final NewBank bank, final String[] tokens, final CustomerID customer) {
    super(bank, tokens, customer);
    responsibilityChain = new ArrayList<>();
    responsibilityChain.add(this::requestingHelp);
    responsibilityChain.add(this::incorrectUsage);
    responsibilityChain.add(this::mustLogOut);
  }

  @Override
  protected String getSyntax() {
    return "REGISTER <username> <password>";
  }

  @Override
  public String execute() {
    Optional<String> message = applyResponsibilityChain();
    if (message.isPresent()) return message.get();

    String username = tokens[1];
    String password = tokens[2];
    try {
      bank.addCustomer(username, password);

      return "SUCCESS: Customer created successfully.";
    } catch (DuplicateCustomerException ex) {
      return "FAIL: " + String.format("Customer name [%s] already exists.", username);
    } catch (PasswordInvalidException e) {
      return "FAIL: Specified password does not meet the security requirements.";
    } catch (UsernameInvalidException e) {
      return String.format(
          "FAIL: Username [%s] invalid. Username must start with a letter and contain only letters and digits.",
          username);
    }
  }

  private Optional<String> mustLogOut() {
    if (isLoggedIn())
      return Optional.of("FAIL: Request not allowed, please log out first.");
    return Optional.empty();
  }
}
