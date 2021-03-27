package newbank.server;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;

import newbank.server.exceptions.DuplicateCustomerException;
import newbank.server.exceptions.PasswordInvalidException;

public class TestNewBank {
  private static NewBank bank;

  @BeforeClass
  public static void setupBank() throws DuplicateCustomerException, PasswordInvalidException {
    bank = NewBank.getBank();
  }

  @Test
  public void canRegisterNewCustomer() throws DuplicateCustomerException, PasswordInvalidException {
    assertThat(bank.getCustomer("customer2").isPresent(), equalTo(false));
    bank.addCustomer("customer2", "123456");
    assertThat(bank.getCustomer("customer2").isPresent(), equalTo(true));
  }

  @Test(expected = DuplicateCustomerException.class)
  public void throwsDuplicateCustomerWhenAccountAlreadyExists()
      throws DuplicateCustomerException, PasswordInvalidException {
    bank.addCustomer("customer3", "123456");
    bank.addCustomer("customer3", "123456");
  }

  @Test
  public void canLogIn() throws DuplicateCustomerException, PasswordInvalidException {
    bank.addCustomer("customer1", "123456");
    assertThat(bank.checkLogInDetails("customer1", "123"), nullValue());
    assertThat(bank.checkLogInDetails("customer1", "123456"), not(equalTo(nullValue())));
  }
}
