package newbank.client;

import java.io.IOException;
import java.io.Reader;

public class TestClient extends ExampleClient {

  public TestClient(String ip, int port, Reader reader)
      throws IOException, IllegalThreadStateException, ConfigurationException {
    super(ip, port, reader);
  }

  protected void displayPrompt() {
    // NOT NEEDED FOR TEST CLIENT
  }

  protected void processResponse(final String response) {
    display.writeLine(response);
  }
}
