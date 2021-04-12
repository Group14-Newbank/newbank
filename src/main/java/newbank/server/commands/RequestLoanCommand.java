package newbank.server.commands;

import newbank.server.Customer;
import newbank.server.CustomerID;
import newbank.server.NewBank;
import newbank.server.microloans.LoanRequest;
import org.javamoney.moneta.Money;

import java.util.ArrayList;
import java.util.Optional;

import static newbank.Configuration.*;

public class RequestLoanCommand extends Command {
  Money amount;
  int repaymentTerm;
  Customer customer;

  public RequestLoanCommand(final NewBank bank, final String[] tokens, final CustomerID customer) {
    super(bank, tokens, customer);
    responsibilityChain = new ArrayList<>();
    responsibilityChain.add(this::requestingHelp);
    responsibilityChain.add(this::mustLogIn);
    responsibilityChain.add(this::incorrectUsage);
    responsibilityChain.add(this::setCustomer);
    responsibilityChain.add(this::alreadyRequestedLoan);
    responsibilityChain.add(this::invalidAmount);
    responsibilityChain.add(this::invalidRepaymentTerm);
  }

  @Override
  protected String getSyntax() {
    return "REQUESTLOAN <amount> <repayment-term-days>";
  }

  @Override
  public String execute() {
    Optional<String> message = applyResponsibilityChain();
    if (message.isPresent()) return message.get();

    customer.getLoanHistory().addLoanRequest(
        new LoanRequest(amount, customer, repaymentTerm)
    );
    return "SUCCESS: Loan request submitted.";
  }

  protected Optional<String> setCustomer() {
    Optional<Customer> optCustomer = bank.getCustomer(customerID.getKey());
    if (!optCustomer.isPresent()) return Optional.of(
        "Customer information could not be found. Please try again later."
    );
    customer = optCustomer.get();
    return Optional.empty();
  }

  private Optional<String> alreadyRequestedLoan() {
    if (!customer.getLoanHistory().hasCurrentLoanRequest()) return Optional.empty();
    return Optional.of("FAIL: You already have a current loan request");
  }

  /**
   * Raises a failure message if the requested amount is inappropriate and sets
   * the `amount` field
   */
  private Optional<String> invalidAmount() {
    double rawAmount;
    try {
      rawAmount = Double.parseDouble(tokens[1]);
    } catch (NumberFormatException ex) {
      return Optional.of(String.format("FAIL: Loan amount [%s] invalid.", tokens[1]));
    }
    if (rawAmount <= 0)
      return Optional.of(String.format("FAIL: Loan amount [%s] invalid.", tokens[1]));
    amount = Money.of(rawAmount, DEFAULT_CURRENCY);
    if (amount.isGreaterThan(MAX_MICROLOAN))
      return Optional.of(String.format(
          "FAIL: Requested loan of [%s] (%s) exceeds maximum of %s",
          tokens[1], DEFAULT_CURRENCY, MAX_MICROLOAN.toString()
      ));
    return Optional.empty();
  }

  /**
   * Raises a failure message if the requested amount is inappropriate and
   * otherwise sets the `amount` field
   */
  private Optional<String> invalidRepaymentTerm() {
    try {
      repaymentTerm = Integer.parseInt(tokens[2]);
    } catch (NumberFormatException ex) {
      return Optional.of(String.format(
          "FAIL: Repayment term [%s] is invalid.", tokens[2]
      ));
    }

    if (repaymentTerm <= 0)
      return Optional.of(String.format(
          "FAIL: Repayment term of [%d] days invalid.", repaymentTerm
      ));

    if (repaymentTerm > MAX_REPAYMENT_PERIOD_DAYS)
      return Optional.of(String.format(
          "FAIL: Repayment term [%d] exceeds maximum of %d days",
          repaymentTerm, MAX_REPAYMENT_PERIOD_DAYS
      ));
    return Optional.empty();
  }
}
