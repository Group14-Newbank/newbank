package newbank.server.commands;

import org.javamoney.moneta.Money;

import newbank.server.Account;
import newbank.server.CustomerID;
import newbank.server.NewBank;
import newbank.server.exceptions.AccountInvalidException;
import newbank.server.exceptions.CommandInvalidSyntaxException;
import newbank.server.exceptions.RequestNotAllowedException;

public class DepositCommand extends Command {
  private final NewBank bank;
  private final String[] tokens;
  private final CustomerID customer;

  public DepositCommand(final NewBank bank, final String[] tokens, final CustomerID customer) {
    this.bank = bank;
    this.tokens = tokens;
    this.customer = customer;
  }

  public String getSyntax() {
    return "DEPOSIT <account_name> <amount>";
  }

  @Override
  public String execute() throws CommandInvalidSyntaxException {
    try {
      checkLoggedIn(customer);
    } catch (RequestNotAllowedException ex) {
      return String.format("FAIL: %s", ex.getMessage());
    }

    if (tokens.length != 3) {
      throw new CommandInvalidSyntaxException();
    }

    final String accountName = tokens[1];

    try {
      final Double amount = Double.parseDouble(tokens[2]);

      if (!(amount > 0)) {
        return String.format("FAIL: Deposit amount [%s] invalid.", tokens[2]);
      }

      bank.depositMoney(customer, accountName, Money.of(amount, Account.DEFAULT_CURRENCY));

      return "SUCCESS: Account credited successfully.";
    } catch (NumberFormatException ex) {
      return String.format("FAIL: Deposit amount [%s] invalid.", tokens[2]);
    } catch (AccountInvalidException ex) {
      return String.format("FAIL: Account [%s] does not exist.", accountName);
    }
  }
}
