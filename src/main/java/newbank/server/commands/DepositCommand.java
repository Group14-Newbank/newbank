package newbank.server.commands;

import org.javamoney.moneta.Money;

import newbank.server.CustomerID;
import newbank.server.NewBank;
import newbank.server.exceptions.AccountInvalidException;
import newbank.server.commands.responsibilities.SetsAmount;

import java.util.ArrayList;

public class DepositCommand extends Command implements SetsAmount {
  Money amount;

  public DepositCommand(final NewBank bank, final String[] tokens, final CustomerID customerID) {
    super(bank, tokens, customerID);
    responsibilityChain = new ArrayList<>();
    responsibilityChain.add(this::requestingHelp);
    responsibilityChain.add(this::mustLogIn);
    responsibilityChain.add(this::incorrectUsage);
    responsibilityChain.add(this::invalidAmount);
  }

  public String getSyntax() {
    return "DEPOSIT <account_name> <amount>";
  }

  @Override
  public String execute() {
    String message = applyResponsibilityChain();
    if (!message.isEmpty()) return message;

    final String accountName = tokens[1];

    try {
      bank.depositMoney(customerID, accountName, amount);
    } catch (AccountInvalidException ex) {
      return String.format("FAIL: Account [%s] does not exist.", accountName);
    }

    return "SUCCESS: Account credited successfully.";
  }

  //////////////////////////// SetsAmount overrides ////////////////////////////
  @Override
  public void setAmount(Money amount) {
    this.amount = amount;
  }

  @Override
  public String getAmountInput() {
    return tokens[2];
  }

  @Override
  public String getAmountName() {
    return "Deposit";
  }
}
