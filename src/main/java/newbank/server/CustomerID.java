package newbank.server;

public class CustomerID {
  private String key;

  public CustomerID() {
    this.key = new String();
  }

  public CustomerID(final String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public void setKey(final String key) {
    this.key = key;
  }
}
