package newbank.server.exceptions;

import newbank.server.Account;
import org.javamoney.moneta.Money;

public class AccountBalanceInsufficientException extends Exception {
  private static final long serialVersionUID = 8234423235864929105L;

  private Money requiredBalance;
  private Money currentBalance;
  private final String accountName;

  public AccountBalanceInsufficientException(Money requiredBalance, Account account) {
    this.requiredBalance = requiredBalance;
    this.currentBalance = account.getBalance();
    this.accountName = account.getName();
  }
  
  public String getMessage() {
    return String.format(
        "FAIL: Insufficient funds in [%s], missing: [%s].",
        accountName, getMissingBalance()
    );
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
