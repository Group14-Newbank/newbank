package newbank.server.microloans;

import newbank.server.Customer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Loans {
  private final AllLoans loanSource;

  public Loans() {
    loanSource = AllLoans.getInstance();
  }

  public Stream<MicroLoan> getCredits(Customer customer) {
    return loanSource.getCredits(customer);
  }

  public Stream<MicroLoan> getDebts(Customer customer) {
    return loanSource.getDebts(customer);
  }

  public void addLoan(MicroLoan loan) {
    loanSource.addLoan(loan);
  }
}

enum AllLoans {
  INSTANCE;

  private final List<MicroLoan> loans;

  public static AllLoans getInstance() {
    return INSTANCE;
  }

  AllLoans() {
    loans = new ArrayList<>();
  }

  public Stream<MicroLoan> getCredits(Customer customer) {
    return loans
        .stream()
        .filter(loan -> loan.getLender().equals(customer));
  }

  public Stream<MicroLoan> getDebts(Customer customer) {
    return loans
        .stream()
        .filter(loan -> loan.getBorrower().equals(customer));
  }

  public void addLoan(MicroLoan loan) {
    loans.add(loan);
  }
}
