package newbank.server;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;

import newbank.server.exceptions.DuplicateCustomerException;
import newbank.server.exceptions.PasswordInvalidException;
import newbank.server.exceptions.UsernameInvalidException;

public class TestNewBank {
  private static NewBank bank;

  @BeforeClass
  public static void setupBank() throws DuplicateCustomerException, PasswordInvalidException {
    bank = NewBank.getBank();
  }

  @Test
  public void canRegisterNewCustomer()
      throws DuplicateCustomerException, PasswordInvalidException, UsernameInvalidException {
    assertThat(bank.getCustomer("customer2").isPresent(), equalTo(false));
    bank.addCustomer("customer2", "Abc123");
    assertThat(bank.getCustomer("customer2").isPresent(), equalTo(true));
  }

  @Test(expected = DuplicateCustomerException.class)
  public void throwsWhenAddingDuplicateCustomer()
      throws DuplicateCustomerException, PasswordInvalidException, UsernameInvalidException {
    bank.addCustomer("customer3", "Abc123");
    bank.addCustomer("customer3", "Abc123");
  }

  @Test(expected = UsernameInvalidException.class)
  public void throwsWhenRegisteringWithInvalidName()
      throws DuplicateCustomerException, PasswordInvalidException, UsernameInvalidException {
    bank.addCustomer("12345", "Abc123");
  }

  @Test(expected = PasswordInvalidException.class)
  public void throwsWhenRegisteringWithInvalidPassword() throws DuplicateCustomerException, PasswordInvalidException, UsernameInvalidException {
	  bank.addCustomer("customer4", "");
  }
  
  @Test
  public void canLogIn()
      throws DuplicateCustomerException, PasswordInvalidException, UsernameInvalidException {
    bank.addCustomer("customer1", "Abc123456");
    assertThat(bank.checkLogInDetails("customer1", "Abc123"), nullValue());
    assertThat(bank.checkLogInDetails("customer1", "Abc123456"), not(equalTo(nullValue())));
  }
}
