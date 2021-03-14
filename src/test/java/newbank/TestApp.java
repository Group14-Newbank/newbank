package newbank;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;

import org.junit.Test;

import newbank.client.ExampleClient;
import newbank.server.NewBankServer;
import newbank.utils.Display;
import newbank.utils.QueueDisplay;

public class TestApp {

  @Test
  public void canDisplayBalance() throws IOException, InterruptedException {
    NewBankServer server = new NewBankServer(NewBankServer.DEFAULT_SERVER_PORT);
    server.start();

    PipedReader reader = new PipedReader();
    PipedWriter writer = new PipedWriter( reader );
    Display display = new QueueDisplay();
        
    ExampleClient client = new ExampleClient("localhost", NewBankServer.DEFAULT_SERVER_PORT, reader );
    client.setDisplay( display );
    client.start();
    
    display.discardLinesUntil("Username");
    writer.write("Bhagy\n");
    display.discardLinesUntil("Password");
    writer.write("bhagy\n");
    display.discardLinesUntil("Successful");
    writer.write("SHOWMYACCOUNTS\n");
    display.discardLinesUntil("request");
    String[] response = display.getLine().split(":");
    
    assertThat( response[0].trim(), equalTo("Main"));
    assertThat( response[1].trim(), equalTo("1000.0"));
    
    // terminate
    writer.close();
    client.join();
  }
}
