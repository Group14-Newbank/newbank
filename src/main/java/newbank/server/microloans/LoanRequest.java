package newbank.server.microloans;

import newbank.server.Customer;
import org.javamoney.moneta.Money;

import static newbank.utils.Config.MICROLOAN_REQUEST_EXPIRY_DAYS;

public class LoanRequest extends LoanProposal {
    public LoanRequest(
        Money proposedAmount, 
        Customer borrower, 
        int repaymentPeriod
    ) {
        super(proposedAmount, borrower, repaymentPeriod);
        this.expiryDate = creationDate.plusDays(MICROLOAN_REQUEST_EXPIRY_DAYS);
    }

    public Customer getBorrower() {
        return proposer;
    }
}
