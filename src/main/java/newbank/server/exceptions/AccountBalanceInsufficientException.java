package newbank.server.exceptions;

import org.javamoney.moneta.Money;

public class AccountBalanceInsufficientException extends Exception {
  private static final long serialVersionUID = 8234423235864929105L;

  private Money requiredBalance;
  private Money currentBalance;

  public AccountBalanceInsufficientException(Money requiredBalance, Money currentBalance) {
    this.requiredBalance = requiredBalance;
    this.currentBalance = currentBalance;
  }

  public Money getRequiredBalance() {
    return requiredBalance;
  }

  public Money getCurrentBalance() {
    return currentBalance;
  }

  public Money getMissingBalance() {
    return requiredBalance.subtract(currentBalance);
  }
}
