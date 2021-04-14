package newbank.server.microloans;

import newbank.server.Customer;

public class UserLoanHistory {
  private final LoanProposals loanProposals;
  private final Customer customer;

  public UserLoanHistory(Customer customer) {
    this.customer = customer;
    loanProposals = new LoanProposals();
  }
  
  public boolean hasCurrentLoanRequest() {
    return loanProposals.getRequests(customer)
        .anyMatch(LoanProposal::isCurrent);
  }

  public void addLoanRequest(LoanRequest loanRequest) {
    loanProposals.addProposal(loanRequest);
  }
}
