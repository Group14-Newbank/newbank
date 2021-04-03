package newbank.server.commands;

import newbank.server.CustomerID;
import newbank.server.NewBank;
import newbank.server.exceptions.DuplicateCustomerException;
import newbank.server.exceptions.PasswordInvalidException;
import newbank.server.exceptions.UsernameInvalidException;

public class RegisterCommand extends Command {
  private final NewBank bank;
  private final String[] tokens;
  private final CustomerID customer;

  public RegisterCommand(final NewBank bank, final String[] tokens, final CustomerID customer) {
    this.bank = bank;
    this.tokens = tokens;
    this.customer = customer;
  }

  @Override
  public String execute() {
    if (isLoggedIn(customer)) {
      return "FAIL: Request not allowed, please log out first.";
    }

    String username = "";
    String password = "";

    if (tokens.length >= 2) {
      username = tokens[1];
    }
    if (tokens.length >= 3) {
      password = tokens[2];
    }

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
}
