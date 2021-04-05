package newbank.server;

import org.javamoney.moneta.Money;

import newbank.server.exceptions.AccountNameInvalidException;

public class Account {
  private String accountName;
  private Money balance;

  public static final int MIN_NAME_LENGTH = 4;
  public static final int MAX_NAME_LENGTH = 12;

  public Account(String accountName, Money openingBalance) throws AccountNameInvalidException {
    this.validate(accountName, openingBalance);

    this.accountName = accountName;
    this.balance = openingBalance;
  }

  private void validate(String accountName, Money openingBalance) throws AccountNameInvalidException {
    if (accountName.length() < MIN_NAME_LENGTH || accountName.length() > MAX_NAME_LENGTH) {
      throw new AccountNameInvalidException(
          "Length must be between " + MIN_NAME_LENGTH + " and " + MAX_NAME_LENGTH + " characters.");
    }

    if (!accountName.matches("[a-zA-Z]+")) {
      throw new AccountNameInvalidException("Only letters are allowed.");
    }
  }

  public String toString() {
    return String.format("%s: %.2f %s", accountName, balance.getNumberStripped(), balance.getCurrency());
  }
}
