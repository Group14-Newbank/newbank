package newbank.server.microloans;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RepaymentPlan {
  private final LocalDateTime creationDate;
  private final LocalDateTime repaymentDeadline;
  private final MicroLoan loan;
  // TODO variable-rates. Probably introduce an abstract parent class.
  private final BigDecimal interestRate;
  private final List<LoanBalanceChange> balanceChanges;
  private boolean closed = false;

  public RepaymentPlan(MicroLoan microLoan, BigDecimal interestRate, int repaymentPeriod) {
    this.loan = microLoan;
    this.interestRate = interestRate;
    this.creationDate = LocalDateTime.now();
    this.repaymentDeadline = creationDate.plusDays(repaymentPeriod);
    this.balanceChanges = new ArrayList<>();
  }

  public boolean hasDefaulted() {
    return isCurrent() && repaymentDeadline.isBefore(LocalDateTime.now());
  }
  
  public boolean isCurrent() {
    return !closed;
  }
}
