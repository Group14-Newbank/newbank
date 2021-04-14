package newbank.server.microloans;

import newbank.server.Customer;
import org.javamoney.moneta.Money;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static newbank.utils.Config.ACCRUAL_RATE;

/**
 * Parent class of loan-offers and -requests
 */
abstract class LoanProposal {
  protected final Customer proposer;
  protected final LocalDateTime creationDate;
  private int repaymentPeriod;
  protected Money proposedAmount;
  protected BigDecimal accrualRate;
  protected LocalDateTime expiryDate;
  protected boolean accepted = false;

  protected LoanProposal(Money proposedAmount, Customer proposer, int repaymentPeriod) {
    this.proposedAmount = proposedAmount;
    this.proposer = proposer;
    this.repaymentPeriod = repaymentPeriod;
    this.accrualRate = ACCRUAL_RATE;
    this.creationDate = LocalDateTime.now();
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
    return !accepted && LocalDateTime.now().isBefore(expiryDate);
  }

  public String getID() {
    return proposer.getUsername();
  }
  
  public void accept() {
    accepted = true;
  }
}
