package newbank.server.microloans;

import newbank.server.Customer;
import org.javamoney.moneta.Money;

import static newbank.Configuration.MICROLOAN_REQUEST_EXPIRY_DAYS;

public class LoanRequest extends LoanProposal {
    private final Customer borrower;

    public LoanRequest(
        Money proposedAmount, 
        Customer borrower, 
        int repaymentPeriod
    ) {
        super(proposedAmount, repaymentPeriod);
        this.expiryDate = creationDate.plusDays(MICROLOAN_REQUEST_EXPIRY_DAYS);
        this.borrower = borrower;
    }

    public Customer getBorrower() {
        return borrower;
    }

    @Override
    public int getRepaymentPeriod() {
        return 0;
    }
}
