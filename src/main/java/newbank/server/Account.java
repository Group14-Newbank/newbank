package newbank.server;

import org.javamoney.moneta.Money;

import newbank.server.exceptions.AccountBalanceInsufficientException;
import newbank.server.exceptions.AccountBalanceInvalidException;
import newbank.server.exceptions.AccountNameInvalidException;

/**
 * Representation of a customer's account.
 *
 * <p>
 * An account is identified by a name and has a balance. An account can be the
 * default account for a customer which means that it will be used (by default)
 * in various transactions. Note that a Savings account cannot be used as the
 * default current account.
 */
public class Account {
  private String accountName;
  private Money balance;

  public static final int MIN_NAME_LENGTH = 4;
  public static final int MAX_NAME_LENGTH = 12;
  public static final String DEFAULT_CURRENCY = "GBP";

  public Account(final String accountName, final Money openingBalance)
      throws AccountBalanceInvalidException, AccountNameInvalidException {
    validateName(accountName);
    validateBalance(openingBalance);

    this.accountName = accountName;
    this.balance = openingBalance;
  }

  public static boolean isSavingsAccount(final String accountName) {
    return accountName.equalsIgnoreCase("Savings");
  }

  private static void validateName(final String accountName) throws AccountNameInvalidException {
    if (accountName.length() < MIN_NAME_LENGTH || accountName.length() > MAX_NAME_LENGTH) {
      throw new AccountNameInvalidException(
          "Length must be between " + MIN_NAME_LENGTH + " and " + MAX_NAME_LENGTH + " characters.");
    }

    if (!accountName.matches("[a-zA-Z]+")) {
      throw new AccountNameInvalidException("Only letters are allowed.");
    }
  }

  private static void validateBalance(final Money openingBalance) throws AccountBalanceInvalidException {
    if (openingBalance.isNegative()) {
      throw new AccountBalanceInvalidException();
    }
  }

  public String toString() {
    return String.format("%s: %.2f %s", accountName, balance.getNumberStripped(), balance.getCurrency());
  }

  public String getName() {
    return accountName;
  }

  public Money getBalance() {
    return balance;
  }

  public void moveBalanceToAccount(Account destination, Money amount) throws AccountBalanceInsufficientException {
    if (balance.isLessThan(amount)) {
      throw new AccountBalanceInsufficientException(amount, balance);
    }

    balance = balance.subtract(amount);
    destination.addMoney(amount);
  }

  public void addMoney(Money amount) {
    balance = balance.add(amount);
  }
}
