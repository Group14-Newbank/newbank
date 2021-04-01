package newbank.server.commands;

import newbank.server.CustomerID;
import newbank.server.NewBank;

public interface CommandSupplier {
  public Command makeCommand(final NewBank bank, final String[] tokens, final CustomerID customer);
}
