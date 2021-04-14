package newbank.server.microloans;

import newbank.server.Customer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class LoanProposals {
  private final AllProposals proposalSource;

  public LoanProposals() {
    proposalSource = AllProposals.getInstance();
  }
  
  public Stream<LoanRequest> getRequests() {
    return proposalSource.getRequests();
  }

  public Stream<LoanRequest> getRequests(Customer customer) {
    return proposalSource.getRequests(customer);
  }
  
  public void addProposal(LoanProposal proposal) {
    proposalSource.addProposal(proposal);
  }
}

enum AllProposals {
  INSTANCE;

  private final List<LoanRequest> loanRequests;
  
  public static AllProposals getInstance() {
    return INSTANCE;
  }

  AllProposals() {
    loanRequests = new ArrayList<>();
  }

  public Stream<LoanRequest> getRequests() {
    return loanRequests.stream();
  }

  public Stream<LoanRequest> getRequests(Customer customer) {
    return loanRequests
        .stream()
        .filter(loanRequest -> loanRequest.getBorrower().equals(customer));
  }

  public void addProposal(LoanProposal request) {
    loanRequests.add((LoanRequest) request);
    // TODO handle LoanOffer
  }
}
