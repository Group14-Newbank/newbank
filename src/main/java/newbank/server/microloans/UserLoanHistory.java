package newbank.server.microloans;

import newbank.server.Customer;

public class UserLoanHistory {
  private final LoanProposals loanProposals;
  private final Loans loans;
  private final Customer customer;

  public UserLoanHistory(Customer customer) {
    this.customer = customer;
    loanProposals = new LoanProposals();
    loans = new Loans();
  }

  public boolean hasDefaultedOnADebt() {
    return loans.getDebts(customer).anyMatch(MicroLoan::hasDefaulted);
  }

  public int currentDebtCount() {
    return (int) loans.getDebts(customer).filter(MicroLoan::isCurrent).count();
  }
  
  public boolean hasCurrentLoanRequest() {
    return loanProposals.getRequests(customer)
        .anyMatch(LoanProposal::isCurrent);
  }

  public void addLoanRequest(LoanRequest loanRequest) {
    loanProposals.addProposal(loanRequest);
  }
  
  public void addLoan(MicroLoan loan) {
    loans.addLoan(loan);
  }
}
