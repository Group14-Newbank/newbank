package newbank.server.commands;

import org.javamoney.moneta.Money;

import newbank.server.CustomerID;
import newbank.server.NewBank;
import newbank.server.exceptions.AccountInvalidException;

import java.util.ArrayList;

import static newbank.utils.Config.DEFAULT_CURRENCY;

public class DepositCommand extends Command {
  double amount;

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
      bank.depositMoney(customerID, accountName, Money.of(amount, DEFAULT_CURRENCY));
    } catch (AccountInvalidException ex) {
      return String.format("FAIL: Account [%s] does not exist.", accountName);
    }

    return "SUCCESS: Account credited successfully.";
  }

  /**
   * Raises a failure message if the requested amount is inappropriate and sets the `amount` field
   */
  private String invalidAmount() {
    try {
      amount = Double.parseDouble(tokens[2]);
    } catch (NumberFormatException ex) {
      return String.format("FAIL: Deposit amount [%s] invalid.", tokens[2]);
    }

    if (amount > 0) return "";

    return String.format("FAIL: Deposit amount [%s] invalid.", tokens[2]);
  }
}
