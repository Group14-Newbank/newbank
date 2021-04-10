package newbank.server.commands;

import java.math.BigDecimal;

import org.javamoney.moneta.Money;

import newbank.server.CustomerID;
import newbank.server.NewBank;
import newbank.server.exceptions.AccountBalanceInsufficientException;
import newbank.server.exceptions.AccountInvalidException;
import newbank.server.exceptions.CommandInvalidSyntaxException;
import newbank.server.exceptions.RequestNotAllowedException;

import static newbank.utils.Config.DEFAULT_CURRENCY;

public class MoveMoneyCommand extends Command {
  private final NewBank bank;
  private final String[] tokens;
  private final CustomerID customer;

  public MoveMoneyCommand(final NewBank bank, final String[] tokens, final CustomerID customer) {
    this.bank = bank;
    this.tokens = tokens;
    this.customer = customer;
  }

  public String getSyntax() {
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

    if (accountNameFrom.equals(accountNameTo)) {
      return "FAIL: The accounts must be different to complete a transfer.";
    }

    try {
      final BigDecimal amount = new BigDecimal(amountString);

      if (amount.compareTo(BigDecimal.ZERO) <= 0) {
        return String.format("FAIL: The amount must be a positive number: [%s].", amountString);
      }

      bank.moveMoney(
          customer, accountNameFrom, accountNameTo, Money.of(amount, DEFAULT_CURRENCY));

      return String.format(
          "SUCCESS: Money transferred from [%s] to [%s] successfully.",
          accountNameFrom, accountNameTo);
    } catch (NumberFormatException ex) {
      return String.format("FAIL: The specified amount is invalid: [%s].", amountString);
    } catch (AccountInvalidException ex) {
      return String.format("FAIL: Account [%s] does not exist.", ex.getAccountName());
    } catch (AccountBalanceInsufficientException ex) {
      return String.format(
          "FAIL: Insufficient balance in [%s], missing: [%s].",
          accountNameFrom, ex.getMissingBalance());
    }
  }
}
