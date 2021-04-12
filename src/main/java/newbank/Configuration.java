package newbank;

import org.javamoney.moneta.Money;

import java.math.BigDecimal;

public class Configuration {
    public static final int DEFAULT_PORT = 14002;
    public static final String DEFAULT_IP = "localhost";

    public static final String DEFAULT_CURRENCY = "GBP";
    public static final int MAX_ACCOUNTS = 5;

    public static final Money MAX_MICROLOAN = Money.of(1000, "GBP");
    public static final BigDecimal ACCRUAL_RATE = BigDecimal.valueOf(0.01);
    public static final int MICROLOAN_OFFER_EXPIRY_DAYS = 7;
    public static final int MICROLOAN_REQUEST_EXPIRY_DAYS = 7;
    public static final int MAX_REPAYMENT_PERIOD_DAYS = 730;
}
