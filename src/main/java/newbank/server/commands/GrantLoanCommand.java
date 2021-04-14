package newbank.server.commands;

import newbank.server.Customer;
import newbank.server.CustomerID;
import newbank.server.NewBank;
import newbank.server.commands.responsibilities.SetsCustomer;
import newbank.server.exceptions.AccountBalanceInsufficientException;
import newbank.server.exceptions.AccountInvalidException;
import newbank.server.exceptions.CustomerInvalidException;
import newbank.server.microloans.LoanProposals;
import newbank.server.microloans.LoanRequest;
import newbank.server.microloans.MicroLoan;

import java.util.ArrayList;
import java.util.Optional;

public class GrantLoanCommand extends Command implements SetsCustomer {
  Customer customer;
  private LoanRequest loanRequest;

  public GrantLoanCommand(final NewBank bank, final String[] tokens, final CustomerID customer) {
    super(bank, tokens, customer);
    responsibilityChain = new ArrayList<>();
    responsibilityChain.add(this::requestingHelp);
    responsibilityChain.add(this::mustLogIn);
    responsibilityChain.add(this::incorrectUsage);
    responsibilityChain.add(this::retrieveCustomer);
    responsibilityChain.add(this::setLoanRequest);
    responsibilityChain.add(this::noDefaultAccount);
  }

  @Override
  public String getSyntax() {
    return "GRANTLOAN <loan-request-id>";
  }

  @Override
  public String execute() {
    String message = applyResponsibilityChain();
    if (!message.isEmpty()) return message;

    try {
      NewBank.getBank().payCustomer(
          customerID, loanRequest.getBorrower().getUsername(), loanRequest.getProposedAmount()
      );
    } catch (AccountInvalidException | CustomerInvalidException | AccountBalanceInsufficientException e) {
      // The first 2 exceptions can't be thrown in practice because we've validated the arguments already
      return e.getMessage();
    }

    customer.getLoanHistory().addLoan(new MicroLoan(customer, loanRequest));
    loanRequest.accept();
    return "SUCCESS: Loan request accepted.";
  }

  private String setLoanRequest() {
    Optional<LoanRequest> possRequest = new LoanProposals().getCurrentRequest(tokens[1]);
    if (!possRequest.isPresent())
      return String.format("FAIL: loan-request with id [%s] not found.", tokens[1]);
    
    loanRequest = possRequest.get();
    if (!loanRequest.getBorrower().equals(customer)) return "";

    return "FAIL: you cannot accept your own loan-request";
  }

  ///////////////////////// SetsCustomer overrides ////////////////////////////
  @Override
  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  @Override
  public CustomerID getCustomerID() {
    return customerID;
  }

  @Override
  public NewBank getBank() {
    return bank;
  }
}
