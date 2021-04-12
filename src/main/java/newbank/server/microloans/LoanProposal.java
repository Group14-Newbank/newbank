package newbank.server.microloans;

import org.javamoney.moneta.Money;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static newbank.Configuration.ACCRUAL_RATE;

/**
 * Parent class of loan-offers and -requests
 */
abstract class LoanProposal {
  protected final LocalDateTime creationDate;
  private int repaymentPeriod;
  protected Money proposedAmount;
  protected BigDecimal accrualRate;
  protected LocalDateTime expiryDate;

  protected LoanProposal(Money proposedAmount, int repaymentPeriod) {
    this.proposedAmount = proposedAmount;
    this.accrualRate = ACCRUAL_RATE;
    this.creationDate = LocalDateTime.now();
    this.repaymentPeriod = repaymentPeriod;
  }

  public Money getProposedAmount() {
    return proposedAmount;
  }

  public BigDecimal getAccrualRate() {
    return accrualRate;
  }

  public void setAccrualRate(BigDecimal accrualRate) {
    this.accrualRate = accrualRate;
  }

  public int getRepaymentPeriod(){
    return repaymentPeriod;
  }

  public boolean isCurrent() {
    return LocalDateTime.now().isBefore(expiryDate);
  }
}
