package newbank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/** The NewBankClientHandler handles all clients requests. */
public class NewBankClientHandler extends Thread {

  private NewBank bank;
  private BufferedReader in;
  private PrintWriter out;
  private CustomerID customer = new CustomerID();
  private Map<String, CommandSupplier> commands = new HashMap<>();

  public NewBankClientHandler(Socket s) throws IOException {
    bank = NewBank.getBank();
    in = new BufferedReader(new InputStreamReader(s.getInputStream()));
    out = new PrintWriter(s.getOutputStream(), true);

    initialiseSupportedComamnds();
  }

  // add supported commands here
  private void initialiseSupportedComamnds() {
    commands.put("LOGIN", (bank, tokens, customer) -> new LoginCommand(bank, tokens, customer));
    commands.put(
        "NEWACCOUNT", (bank, tokens, customer) -> new NewAccountCommand(bank, tokens, customer));
    commands.put(
        "REGISTER", (bank, tokens, customer) -> new RegisterCommand(bank, tokens, customer));
    commands.put(
        "SHOWMYACCOUNTS",
        (bank, tokens, customer) -> new ShowAccountsCommand(bank, tokens, customer));
  }

  private Command getCommand(final String[] tokens) {
    return commands.get(tokens[0].toUpperCase()).makeCommand(bank, tokens, customer);
  }

  private boolean processRequest(final String request) {
    final String[] tokens = request.split("\\s+");

    assert (tokens.length > 0);

    if (tokens[0].toUpperCase().equals("QUIT")) {
      out.println("SUCCESS: Good bye.");
      return false;
    }

    out.println(getCommand(tokens).execute());

    return true;
  }

  public void run() {
    boolean hasMore = true;
    try {
      while (hasMore) {
        String request = in.readLine();
        out.println(String.format("Received request [%s]", request));

        hasMore = processRequest(request);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        in.close();
        out.close();
      } catch (IOException e) {
        e.printStackTrace();
        Thread.currentThread().interrupt();
      }
    }
  }
}
