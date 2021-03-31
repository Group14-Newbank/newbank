package newbank.server;

public interface CommandSupplier {
  public Command makeCommand(final NewBank bank, final String[] tokens, final CustomerID customer);
}
