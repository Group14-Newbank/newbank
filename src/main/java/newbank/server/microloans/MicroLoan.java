package newbank.server.microloans;

import newbank.server.Customer;
import org.javamoney.moneta.Money;

import java.math.BigDecimal;

public class MicroLoan {
  static private double ACCRUAL_RATE;
  private final Money originalAmount;
  private Money outstandingAmount;
  private final Customer lender;
  private final Customer borrower;
  private final RepaymentPlan plan;

  public MicroLoan(
      Customer lender,
      Customer borrower,
      Money amount,
      BigDecimal interestRate,
      int repaymentPeriod
  ) {
    this.originalAmount = this.outstandingAmount = amount;
    this.lender = lender;
    this.borrower = borrower;
    this.plan = new RepaymentPlan(this, interestRate, repaymentPeriod);
  }

  public MicroLoan(Customer lender, LoanRequest acceptedRequest) {
    this(
        lender, 
        acceptedRequest.getBorrower(), 
        acceptedRequest.getProposedAmount(), 
        acceptedRequest.getAccrualRate(),
        acceptedRequest.getRepaymentPeriod()
    );
  }
  
  public Customer getBorrower() {
    return borrower;
  }

  public Customer getLender() {
    return lender;
  }

  public boolean hasDefaulted() {
    return plan.hasDefaulted();
  }
  
  public boolean isCurrent() {
    return plan.isCurrent();
  }
}
