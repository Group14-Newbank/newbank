package newbank.server.commands;

import newbank.server.commands.responsibilities.SetsAmount;
import newbank.server.exceptions.AccountBalanceInsufficientException;
import org.javamoney.moneta.Money;

import newbank.server.CustomerID;
import newbank.server.NewBank;
import newbank.server.exceptions.AccountInvalidException;
import newbank.server.exceptions.CustomerInvalidException;

import java.util.ArrayList;

public class PayCommand extends Command implements SetsAmount {
  private Money amount;

  public PayCommand(final NewBank bank, final String[] tokens, final CustomerID customerID) {
    super(bank, tokens, customerID);
    responsibilityChain = new ArrayList<>();
    responsibilityChain.add(this::requestingHelp);
    responsibilityChain.add(this::mustLogIn);
    responsibilityChain.add(this::incorrectUsage);
    responsibilityChain.add(this::invalidAmount);
  }

  public String getSyntax() {
    return "PAY <person> <amount>";
  }

  @Override
  public String execute() {
    String message = applyResponsibilityChain();
    if (!message.isEmpty()) return message;

    try {
      bank.payCustomer(customerID, tokens[1], amount);

      return String.format("Default account for customer [%s] credited successfully.", tokens[1]);
    } catch (AccountInvalidException e) {
      return String.format(
          "FAIL: No default current account found for customer [%s].", e.getOwner());
    } catch (CustomerInvalidException e) {
      return String.format("FAIL: Customer [%s] does not exist.", tokens[1]);
    } catch (AccountBalanceInsufficientException e) {
      return e.getMessage();
    }
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
    return "Credit";
  }
}
