package newbank.server.commands;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.javamoney.moneta.Money;

import newbank.server.CustomerID;
import newbank.server.NewBank;
import newbank.server.exceptions.AccountBalanceInsufficientException;
import newbank.server.exceptions.AccountInvalidException;

import static newbank.utils.Config.DEFAULT_CURRENCY;

public class MoveMoneyCommand extends Command {

  public MoveMoneyCommand(final NewBank bank, final String[] tokens, final CustomerID customerID) {
    super(bank, tokens, customerID);
    responsibilityChain = new ArrayList<>();
    responsibilityChain.add(this::requestingHelp);
    responsibilityChain.add(this::mustLogIn);
    responsibilityChain.add(this::incorrectUsage);
  }

  @Override
  public String getSyntax() {
    return "MOVE <account_name_from> <account_name_to> <amount>";
  }

  @Override
  public String execute() {
    String message = applyResponsibilityChain();
    if (!message.isEmpty()) return message;

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
          customerID, accountNameFrom, accountNameTo, Money.of(amount, DEFAULT_CURRENCY)
      );

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
