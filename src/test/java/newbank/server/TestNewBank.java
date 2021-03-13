package newbank.server;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestNewBank {
	private static NewBank bank;
	
	@BeforeClass
	public static void setupBank() {
		bank = NewBank.getBank();
		bank.addCustomer( "customer1", "123456" );
	}

	@Test
	public void checkCanLogIn() {
		assertThat( bank.checkLogInDetails( "customer1", "123" ), nullValue() );
		assertThat( bank.checkLogInDetails( "customer1", "123456" ), not( equalTo( nullValue() ) ) );
	}
	
}
