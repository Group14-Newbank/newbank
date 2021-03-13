package newbank.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class NewBank {
	
	private static final NewBank bank = new NewBank();
	private HashMap<String,Customer> customers;
	
	private NewBank() {
		customers = new HashMap<>();
		addTestData();
	}
	
	private void addTestData() {
		Customer bhagy = new Customer();
		bhagy.addAccount(new Account("Main", 1000.0));
		customers.put("Bhagy", bhagy);
		
		Customer christina = new Customer();
		christina.addAccount(new Account("Savings", 1500.0));
		customers.put("Christina", christina);
		
		Customer john = new Customer();
		john.addAccount(new Account("Checking", 250.0));
		customers.put("John", john);
	}
	
	/**
	 * Add a new customer to the bank with the supplied credentials.
	 * @param username The customer's username
	 * @param password The customer's password 
	 */
	public void addCustomer( final String username, final String password ) {
		customers.put( username , new Customer( username, password ) );
	}
	
	public static NewBank getBank() {
		return bank;
	}

	/**
	 * Check customer's credentials
	 * 
	 * @param username The customer's username
	 * @param password The customer's password
	 * @return A derived ID identifying the customer on success, or null otherwise
	 */
	public synchronized CustomerID checkLogInDetails( final String username, final String password) {
		if(customers.containsKey(username)) {
			if( authenticateCustomer( username, password ) ) {
				return new CustomerID( username );	
			}
		}
		return null;
	}
	
	private boolean authenticateCustomer( final String username, final String password ) {
		assert( customers.containsKey( username) );
		
		Optional<Map.Entry<String, Customer>> customer = customers.entrySet().stream()
		.filter( entry -> password.equals( entry.getValue().getPassword() ) )
		.findFirst();
		
		return customer.isPresent();
	}

	// commands from the NewBank customer are processed in this method
	public synchronized String processRequest(CustomerID customer, String request) {
		if(customers.containsKey(customer.getKey())) {
			switch(request) {
			case "SHOWMYACCOUNTS" : return showMyAccounts(customer);
			default : return "FAIL";
			}
		}
		return "FAIL";
	}
	
	private String showMyAccounts(CustomerID customer) {
		return (customers.get(customer.getKey())).accountsToString();
	}

}
