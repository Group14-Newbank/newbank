package newbank.server.commands.responsibilities;

import org.javamoney.moneta.Money;

import javax.money.Monetary;
import java.math.BigDecimal;

import static newbank.utils.Config.DEFAULT_CURRENCY;

public interface SetsAmount {
  void setAmount(Money amount);

  String getAmountInput();

  /**
   * The word that appears in error messages. i.e. "FAIL: [NAME] amount is invalid"
   */
  String getAmountName();

  default BigDecimal getMinimumAmount() {
    return BigDecimal.ZERO;
  }

  /**
   * Raises a failure message if the requested amount is inappropriate and sets the `amount` field
   */
  default String invalidAmount() {
    String amountInput = getAmountInput();
    BigDecimal rawAmount;
    try {
      rawAmount = new BigDecimal(amountInput);
    } catch (NumberFormatException ex) {
      return String.format("FAIL: %s amount [%s] is invalid.", getAmountName(), amountInput);
    }

    if (rawAmount.compareTo(getMinimumAmount()) <= 0)
      return String.format(
          "FAIL: %s amount [%s] must be greater than %s.",
          getAmountName(), amountInput, getMinimumAmount().toString()
      );

    if(rawAmount.stripTrailingZeros().scale() > Monetary.getCurrency(DEFAULT_CURRENCY).getDefaultFractionDigits())
      return String.format(
          "FAIL: %s amount [%s] has more decimal places than allowed in currency %s",
          getAmountName(), amountInput, DEFAULT_CURRENCY
      );

    Money amount = Money.of(rawAmount, DEFAULT_CURRENCY);
    setAmount(amount);
    return "";
  }
}
