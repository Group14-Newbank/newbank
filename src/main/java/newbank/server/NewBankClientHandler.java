package newbank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import newbank.server.commands.*;

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

    initialiseSupportedCommands();
  }

  // add supported commands here
  private void initialiseSupportedCommands() {
    commands.put("DEPOSIT", DepositCommand::new);
    commands.put("LOGIN", LoginCommand::new);
    commands.put("LOGOUT", LogoutCommand::new);
    commands.put("NEWACCOUNT", NewAccountCommand::new);
    commands.put("QUIT", QuitCommand::new);
    commands.put("REGISTER", RegisterCommand::new);
    commands.put("SHOWMYACCOUNTS", ShowAccountsCommand::new);
    commands.put("MOVE", MoveMoneyCommand::new);
    commands.put("DEFAULT", DefaultCommand::new);
    commands.put("PAY", PayCommand::new);
    commands.put("REQUESTLOAN", RequestLoanCommand::new);
    commands.put("GRANTLOAN", GrantLoanCommand::new);
    commands.put("UNKNOWN", UnknownCommand::new);
    commands.put("HELP", HelpCommand::new);
  }

  private Command getCommand(final String name, final String[] tokens) {
    return commands.getOrDefault(name, UnknownCommand::new).makeCommand(bank, tokens, customer);
  }

  private boolean processRequest(final String request) {
    final String[] tokens = request.trim().split("\\s+");

    assert (tokens.length > 0);

    final String commandName = tokens[0].toUpperCase();
    final Command command = getCommand(commandName, tokens);

    out.println(command.execute());

    return !request.equals("QUIT");
  }

  public void run() {
    boolean hasMore = true;
    try {
      while (hasMore) {
        String request = in.readLine();
        if (NewBankServer.VERBOSE_MODE) {
          // useful for debugging/testing, not meant for production
          out.println(String.format("Received request [%s]", request));
        }

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
