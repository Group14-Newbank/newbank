package newbank.server.commands;

import java.util.ArrayList;

import org.javamoney.moneta.Money;

import newbank.server.CustomerID;
import newbank.server.NewBank;
import newbank.server.exceptions.AccountBalanceInsufficientException;
import newbank.server.exceptions.AccountInvalidException;
import newbank.server.commands.responsibilities.SetsAmount;

public class MoveMoneyCommand extends Command implements SetsAmount {
  private Money amount;

  public MoveMoneyCommand(final NewBank bank, final String[] tokens, final CustomerID customerID) {
    super(bank, tokens, customerID);
    responsibilityChain = new ArrayList<>();
    responsibilityChain.add(this::requestingHelp);
    responsibilityChain.add(this::mustLogIn);
    responsibilityChain.add(this::incorrectUsage);
    responsibilityChain.add(this::invalidAmount);
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

    if (accountNameFrom.equals(accountNameTo)) {
      return "FAIL: The accounts must be different to complete a transfer.";
    }

    try {
      bank.moveMoney(customerID, accountNameFrom, accountNameTo, amount);

      return String.format(
          "SUCCESS: Money transferred from [%s] to [%s] successfully.",
          accountNameFrom, accountNameTo);
    } catch (AccountInvalidException ex) {
      return String.format("FAIL: Account [%s] does not exist.", ex.getAccountName());
    } catch (AccountBalanceInsufficientException ex) {
      return String.format(
          "FAIL: Insufficient balance in [%s], missing: [%s].",
          accountNameFrom, ex.getMissingBalance());
    }
  }

  //////////////////////////// SetsAmount overrides ////////////////////////////
  @Override
  public void setAmount(Money amount) {
    this.amount = amount;
  }

  @Override
  public String getAmountInput() {
    return tokens[3];
  }

  @Override
  public String getAmountName() {
    return "Transfer";
  }
}
