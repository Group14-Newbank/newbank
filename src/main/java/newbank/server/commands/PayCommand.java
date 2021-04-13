package newbank.server.commands;

import org.javamoney.moneta.Money;

import newbank.server.CustomerID;
import newbank.server.NewBank;
import newbank.server.exceptions.AccountInvalidException;
import newbank.server.exceptions.CustomerInvalidException;
import newbank.server.exceptions.InsufficientFundsException;
import newbank.server.exceptions.RequestNotAllowedException;

public class PayCommand extends Command {
  private final NewBank bank;
  private final String[] tokens;
  private final CustomerID customer;
  private static final String DEFAULT_CURRENCY = "GBP";

  public PayCommand(final NewBank bank, final String[] tokens, final CustomerID customer) {
    this.bank = bank;
    this.tokens = tokens;
    this.customer = customer;
  }

  protected String getSyntax() {
    return "PAY <person> <amount>";
  }

  @Override
  public String execute() {
    if ((tokens.length >= 2 && tokens[1].equalsIgnoreCase("HELP"))) {
      return String.format("SUCCESS: Usage: %s", getSyntax());
    }

    if (tokens.length != 3) {
      return String.format("FAIL: Usage: %s", getSyntax());
    }

    try {
      checkLoggedIn(customer);

      final Double amount = Double.parseDouble(tokens[2]);

      if (!(amount > 0)) {
        return String.format("FAIL: Credit amount [%s] invalid.", tokens[2]);
      }

      bank.payCustomer(customer, tokens[1], Money.of(amount, DEFAULT_CURRENCY));

      return String.format("Default account for customer [%s] credited successfully.", tokens[1]);
    } catch (RequestNotAllowedException ex) {
      return String.format("FAIL: %s", ex.getMessage());
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
