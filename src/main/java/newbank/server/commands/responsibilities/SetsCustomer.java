package newbank.server.commands.responsibilities;

import newbank.server.Customer;
import newbank.server.CustomerID;
import newbank.server.NewBank;

import java.util.Optional;

public interface SetsCustomer {
  void setCustomer(Customer customer);
  CustomerID getCustomerID();
  NewBank getBank();

  default String retrieveCustomer() {
    Optional<Customer> optCustomer = getBank().getCustomer(getCustomerID().getKey());
    if (!optCustomer.isPresent())
      return "Customer information could not be found. Please try again later.";

    setCustomer(optCustomer.get());
    return "";
  }
}
