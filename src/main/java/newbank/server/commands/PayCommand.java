package newbank.server.commands;

import org.javamoney.moneta.Money;

import newbank.server.CustomerID;
import newbank.server.NewBank;
import newbank.server.exceptions.AccountInvalidException;
import newbank.server.exceptions.CustomerInvalidException;
import newbank.server.exceptions.InsufficientFundsException;

import java.util.ArrayList;

import static newbank.utils.Config.DEFAULT_CURRENCY;

public class PayCommand extends Command {
  public PayCommand(final NewBank bank, final String[] tokens, final CustomerID customerID) {
    super(bank, tokens, customerID);
    responsibilityChain = new ArrayList<>();
    responsibilityChain.add(this::requestingHelp);
    responsibilityChain.add(this::mustLogIn);
    responsibilityChain.add(this::incorrectUsage);
  }

  public String getSyntax() {
    return "PAY <person> <amount>";
  }

  @Override
  public String execute() {
    String message = applyResponsibilityChain();
    if (!message.isEmpty()) return message;

    try {
      final Double amount = Double.parseDouble(tokens[2]);

      if (!(amount > 0)) {
        return String.format("FAIL: Credit amount [%s] invalid.", tokens[2]);
      }

      bank.payCustomer(customerID, tokens[1], Money.of(amount, DEFAULT_CURRENCY));

      return String.format("Default account for customer [%s] credited successfully.", tokens[1]);
    } catch (AccountInvalidException e) {
      return String.format(
          "FAIL: No default current account found for customer [%s].", e.getOwner());
    } catch (CustomerInvalidException e) {
      return String.format("FAIL: Customer [%s] does not exist.", tokens[1]);
    } catch (InsufficientFundsException e) {
      return String.format("FAIL: Insufficient funds to perform transaction.");
    } catch (NumberFormatException e) {
      return String.format("FAIL: Credit amount [%s] invalid.", tokens[2]);
    }
  }
}
