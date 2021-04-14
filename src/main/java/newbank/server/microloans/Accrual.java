package newbank.server.microloans;

/**
 * The event of a loan's amount changing because of the accrual of interest
 */
public class Accrual implements LoanBalanceChange {
  private RepaymentPlan plan;

  @Override
  public RepaymentPlan getRepaymentPlan() {
    return plan;
  }
}
