package newbank.server.commands;

import java.math.BigDecimal;

import org.javamoney.moneta.Money;

import newbank.server.CustomerID;
import newbank.server.NewBank;
import newbank.server.exceptions.RequestNotAllowedException;
import newbank.server.exceptions.SyntaxInvalidException;

public class MoveMoneyCommand extends Command {
  private final NewBank bank;
  private final String[] tokens;
  private final CustomerID customer;

  public MoveMoneyCommand(final NewBank bank, final String[] tokens, final CustomerID customer) {
    this.bank = bank;
    this.tokens = tokens;
    this.customer = customer;
  }

  private void validateSyntax() throws SyntaxInvalidException {
    if (tokens.length != 3) {
      throw new SyntaxInvalidException("The proper syntax is: MoveMoney <Amount> <From> <To>.");
    }
  }

  @Override
  public String execute() {
    try {
      BigDecimal money = new BigDecimal(amounts[0]);
      if (money <= 0) {

      }

      Money money = Money.of(amount, "GBP");

      if (money < 0)
    } catch (RequestNotAllowedException | SyntaxInvalidException ex) {
      return String.format("FAIL: %s", ex.getMessage());
    }
  }
}
