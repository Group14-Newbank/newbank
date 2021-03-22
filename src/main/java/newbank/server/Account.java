package newbank.server;

import newbank.server.exceptions.AccountInvalidNameException;

public class Account {
  private String accountName;
  private double openingBalance;

  public static final int MIN_NAME_LENGTH = 4;
  public static final int MAX_NAME_LENGTH = 12;

  public Account(String accountName, double openingBalance) throws AccountInvalidNameException {
    this.validate(accountName, openingBalance);

    this.accountName = accountName;
    this.openingBalance = openingBalance;
  }

  private void validate(String accountName, double openingBalance) throws AccountInvalidNameException {
    if (accountName.length() < MIN_NAME_LENGTH || accountName.length() > MAX_NAME_LENGTH) {
      throw new AccountInvalidNameException(
          "Length must be between " + MIN_NAME_LENGTH + " and " + MAX_NAME_LENGTH + " characters.");
    }

    if (!accountName.matches("[a-zA-Z]+")) {
      throw new AccountInvalidNameException("Only letters are allowed.");
    }
  }

  public String toString() {
    return (accountName + ": " + openingBalance);
  }
}
