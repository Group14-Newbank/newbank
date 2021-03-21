package newbank;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import newbank.client.ExampleClient;
import newbank.server.NewBankServer;
import newbank.utils.Display;
import newbank.utils.QueueDisplay;

public class TestApp {
  private NewBankServer server;
  private PipedReader reader;
  private PipedWriter writer;
  private Display display;
  private ExampleClient client;

  @Before
  public void setup() throws IOException, InterruptedException {
    server = new NewBankServer(NewBankServer.DEFAULT_SERVER_PORT);
    server.start();

    reader = new PipedReader();
    writer = new PipedWriter(reader);
    display = new QueueDisplay();

    client = new ExampleClient("localhost", NewBankServer.DEFAULT_SERVER_PORT, reader);
    client.setDisplay(display);
    client.start();
  }

  @After
  public void tearDown() throws IOException, InterruptedException {
    writer.close();
    client.join();
  }

  @Test
  public void canDisplayBalance() throws IOException {
    display.discardLinesUntil("Username");
    writer.write("Bhagy\n");
    display.discardLinesUntil("Password");
    writer.write("bhagy\n");
    display.discardLinesUntil("Successful");

    writer.write("SHOWMYACCOUNTS\n");
    display.discardLinesUntil("request");
    String[] response = display.getLine().split(":");

    assertThat(response[0].trim(), equalTo("Main"));
    assertThat(response[1].trim(), equalTo("1000.0"));
  }

  @Test
  public void canCreateNewAccount() throws IOException {
    display.discardLinesUntil("Username");
    writer.write("Bhagy\n");
    display.discardLinesUntil("Password");
    writer.write("bhagy\n");
    display.discardLinesUntil("Successful");

    writer.write("NEWACCOUNT 123\n");
    display.discardLinesUntil("request");
    String response = display.getLine();

    assertThat(response, equalTo("The account has been created successfully."));
  }
}
