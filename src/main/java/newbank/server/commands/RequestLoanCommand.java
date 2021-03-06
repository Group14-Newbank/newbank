package newbank.server.commands;

import newbank.server.Customer;
import newbank.server.CustomerID;
import newbank.server.NewBank;
import newbank.server.commands.responsibilities.SetsAmount;
import newbank.server.commands.responsibilities.SetsCustomer;
import newbank.server.microloans.LoanRequest;
import org.javamoney.moneta.Money;

import java.util.ArrayList;

import static newbank.utils.Config.*;

public class RequestLoanCommand extends Command implements SetsAmount, SetsCustomer {
  Money amount;
  int repaymentTerm;
  Customer customer;

  public RequestLoanCommand(final NewBank bank, final String[] tokens, final CustomerID customer) {
    super(bank, tokens, customer);
    responsibilityChain = new ArrayList<>();
    responsibilityChain.add(this::requestingHelp);
    responsibilityChain.add(this::mustLogIn);
    responsibilityChain.add(this::incorrectUsage);
    responsibilityChain.add(this::retrieveCustomer);
    responsibilityChain.add(this::hasDefaultedPreviously);
    responsibilityChain.add(this::alreadyHas3Loans);
    responsibilityChain.add(this::alreadyRequestedLoan);
    responsibilityChain.add(this::invalidAmount);
    responsibilityChain.add(this::amountTooLarge);
    responsibilityChain.add(this::invalidRepaymentTerm);
    responsibilityChain.add(this::noDefaultAccount);
  }

  @Override
  public String getSyntax() {
    return "REQUESTLOAN <amount> <repayment-term-days>";
  }

  @Override
  public String execute() {
    String message = applyResponsibilityChain();
    if (!message.isEmpty()) return message;

    customer.getLoanHistory().addLoanRequest(
        new LoanRequest(amount, customer, repaymentTerm)
    );
    return "SUCCESS: Loan request submitted.";
  }

  private String hasDefaultedPreviously() {
    if (!customer.getLoanHistory().hasDefaultedOnADebt()) return "";
    return "FAIL: You may not request or accept a microloan because you have defaulted on one in the past";
  }

  private String alreadyHas3Loans() {
    if (customer.getLoanHistory().currentDebtCount() < 3) return "";
    return "FAIL: You may not request of accept a microloan because you may not be the recipient of more than 3.";
  }

  private String alreadyRequestedLoan() {
    if (!customer.getLoanHistory().hasCurrentLoanRequest()) return "";

    return "FAIL: You already have a current loan request";
  }

  /**
   * Raises a failure message if the requested repayment term is invalid or
   * inappropriate and otherwise sets the `repaymentTerm` field
   */
  private String invalidRepaymentTerm() {
    try {
      repaymentTerm = Integer.parseInt(tokens[2]);
    } catch (NumberFormatException ex) {
      return String.format(
          "FAIL: Repayment term [%s] is invalid.", tokens[2]
      );
    }

    if (repaymentTerm <= 0)
      return String.format(
          "FAIL: Repayment term of [%d] days invalid.", repaymentTerm
      );

    if (repaymentTerm > MAX_REPAYMENT_PERIOD_DAYS)
      return String.format(
          "FAIL: Repayment term [%d] exceeds maximum of %d days",
          repaymentTerm, MAX_REPAYMENT_PERIOD_DAYS
      );
    return "";
  }
  
  private String amountTooLarge() {
    if (amount.isGreaterThan(MAX_MICROLOAN))
      return String.format(
          "FAIL: Requested loan of [%s] (%s) exceeds maximum of %s",
          getAmountInput(), DEFAULT_CURRENCY, MAX_MICROLOAN.toString()
      );
    return "";
  }

  //////////////////////////// SetsAmount overrides ////////////////////////////
  @Override
  public void setAmount(Money amount) {
    this.amount = amount;
  }

  @Override
  public String getAmountInput() {
    return tokens[1];
  }

  @Override
  public String getAmountName() {
    return "Loan";
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
