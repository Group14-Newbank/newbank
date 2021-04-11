package newbank.server;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import newbank.server.exceptions.DuplicateCustomerException;
import newbank.server.exceptions.PasswordInvalidException;
import newbank.server.exceptions.UsernameInvalidException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class TestNewBank {
  private static NewBank bank;

  @BeforeAll
  public static void setupBank() {
    bank = NewBank.getBank();
  }

  @Test
  public void canRegisterNewCustomer()
      throws DuplicateCustomerException, PasswordInvalidException, UsernameInvalidException {
    assertThat(bank.getCustomer("customer2").isPresent(), equalTo(false));
    bank.addCustomer("customer2", "123456");
    assertThat(bank.getCustomer("customer2").isPresent(), equalTo(true));
  }

  private static Stream<Arguments> provideParamsForRegisterExceptions() {
    return Stream.of(
        Arguments.of(UsernameInvalidException.class, "12345", "123456"),
        Arguments.of(PasswordInvalidException.class, "customer4", ""),
        Arguments.of(DuplicateCustomerException.class, "customer3", "123456")
    );
  }

  @ParameterizedTest
  @MethodSource("provideParamsForRegisterExceptions")
  public void throwsOnInvalidRegistration(Class<Throwable> e, String username, String password) {
    assertThrows(e, () -> {
      bank.addCustomer(username, password);
      bank.addCustomer(username, password);
    });
  }
  
  @Test
  public void canLogIn()
      throws DuplicateCustomerException, PasswordInvalidException, UsernameInvalidException {
    bank.addCustomer("customer1", "123456");
    assertThat(bank.checkLogInDetails("customer1", "123"), nullValue());
    assertThat(bank.checkLogInDetails("customer1", "123456"), not(equalTo(nullValue())));
  }
}
