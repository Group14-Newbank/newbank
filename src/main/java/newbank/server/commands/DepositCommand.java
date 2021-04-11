package newbank.server.commands;

import org.javamoney.moneta.Money;

import newbank.server.CustomerID;
import newbank.server.NewBank;
import newbank.server.exceptions.AccountInvalidException;
import newbank.server.exceptions.RequestNotAllowedException;

public class DepositCommand extends Command {
  private final NewBank bank;
  private final String[] tokens;
  private final CustomerID customer;
  private static final String DEFAULT_CURRENCY = "GBP";

  public DepositCommand(final NewBank bank, final String[] tokens, final CustomerID customer) {
    this.bank = bank;
    this.tokens = tokens;
    this.customer = customer;
  }

  protected String getSyntax() {
    return "DEPOSIT <account_name> <amount>";
  }

  @Override
  public String execute() {
    if ((tokens.length >= 2 && tokens[1].equalsIgnoreCase("HELP"))) {
      return String.format("SUCCESS: Usage: %s", getSyntax());
    }

    try {
      checkLoggedIn(customer);
    } catch (RequestNotAllowedException ex) {
      return String.format("FAIL: %s", ex.getMessage());
    }

    if (tokens.length != 3) {
      return String.format("FAIL: Usage: %s", getSyntax());
    }

    final String accountName = tokens[1];

    try {
      final Double amount = Double.parseDouble(tokens[2]);

      if (!(amount > 0)) {
        return String.format("FAIL: Deposit amount [%s] invalid.", tokens[2]);
      }

      bank.depositMoney(customer, accountName, Money.of(amount, DEFAULT_CURRENCY));

      return "SUCCESS: Account credited successfully.";
    } catch (NumberFormatException ex) {
      return String.format("FAIL: Deposit amount [%s] invalid.", tokens[2]);
    } catch (AccountInvalidException ex) {
      return String.format("FAIL: Account [%s] does not exist.", accountName);
    }
  }
}
