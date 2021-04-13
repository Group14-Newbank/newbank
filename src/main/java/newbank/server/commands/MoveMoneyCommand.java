package newbank.server.commands;

import java.math.BigDecimal;

import org.javamoney.moneta.Money;

import newbank.server.Account;
import newbank.server.CustomerID;
import newbank.server.NewBank;
import newbank.server.exceptions.AccountBalanceInsufficientException;
import newbank.server.exceptions.AccountInvalidException;
import newbank.server.exceptions.CommandInvalidSyntaxException;
import newbank.server.exceptions.RequestNotAllowedException;

public class MoveMoneyCommand extends Command {
  private final NewBank bank;
  private final String[] tokens;
  private final CustomerID customer;

  public MoveMoneyCommand(final NewBank bank, final String[] tokens, final CustomerID customer) {
    this.bank = bank;
    this.tokens = tokens;
    this.customer = customer;
  }

  protected String getSyntax() {
    return "MOVE <account_name_from> <account_name_to> <amount>";
  }

  @Override
  public String execute() throws CommandInvalidSyntaxException {
    try {
      checkLoggedIn(customer);
    } catch (RequestNotAllowedException ex) {
      return ex.getMessage();
    }

    if (tokens.length != 4) {
      throw new CommandInvalidSyntaxException();
    }

    final String accountNameFrom = tokens[1];
    final String accountNameTo = tokens[2];
    final String amountString = tokens[3];

    try {
      final BigDecimal amount = new BigDecimal(amountString);

      if (amount.compareTo(BigDecimal.ZERO) <= 0) {
        return String.format("FAIL: The amount must be a positive number: [%s].", amountString);
      }

      bank.moveMoney(customer, accountNameFrom, accountNameTo, Money.of(amount, Account.DEFAULT_CURRENCY));

      return "SUCCESS: Account credited successfully.";
    } catch (NumberFormatException ex) {
      return String.format("FAIL: Specified amount [%s] invalid.", amountString);
    } catch (AccountInvalidException ex) {
      return String.format("FAIL: Account [%s] does not exist.", ex.getAccountName());
    } catch (AccountBalanceInsufficientException ex) {
      return String.format("FAIL: Insufficient balance on the account, missing: [%s]", ex.getMissingBalance());
    }
  }
}
