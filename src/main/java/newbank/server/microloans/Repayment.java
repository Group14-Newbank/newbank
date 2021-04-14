package newbank.server.microloans;

/**
 * The event of a loan (or part of a loan) being repaid
 */
public class Repayment implements LoanBalanceChange {
  private RepaymentPlan plan;
  // TODO use whatever class is introduced in the 22-pay-someone branch
  private long transaction;

  @Override
  public RepaymentPlan getRepaymentPlan() {
    return plan;
  }
}
