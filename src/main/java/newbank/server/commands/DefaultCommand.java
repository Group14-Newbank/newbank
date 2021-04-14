package newbank.server.commands;

import newbank.server.CustomerID;
import newbank.server.NewBank;
import newbank.server.exceptions.AccountInvalidException;
import newbank.server.exceptions.AccountTypeInvalidException;
import newbank.server.exceptions.CommandInvalidSyntaxException;
import newbank.server.exceptions.RequestNotAllowedException;

public class DefaultCommand extends Command {
  private final NewBank bank;
  private final String[] tokens;
  private final CustomerID customer;

  public DefaultCommand(final NewBank bank, final String[] tokens, final CustomerID customer) {
    this.bank = bank;
    this.tokens = tokens;
    this.customer = customer;
  }

  public String getSyntax() {
    return "DEFAULT <Name>";
  }

  @Override
  public String execute() throws CommandInvalidSyntaxException {
    try {
      checkLoggedIn(customer);

      if (!(tokens.length == 2)) {
        throw new CommandInvalidSyntaxException();
      }

      final String accountName = tokens[1];
      bank.setDefaultAccount(customer, accountName);

      return String.format("SUCCESS: Account [%s] set as default.", accountName);

    } catch (RequestNotAllowedException ex) {
      return String.format("FAIL: %s", ex.getMessage());
    } catch (AccountInvalidException ex) {
      return String.format("FAIL: Account [%s] does not exist.", tokens[1]);
    } catch (AccountTypeInvalidException ex) {
      return String.format("FAIL: Account [%s] cannot be default.", tokens[1]);
    }
  }
}
