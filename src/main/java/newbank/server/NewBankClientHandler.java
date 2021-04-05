package newbank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NewBankClientHandler extends Thread {

  private final NewBank bank;
  private final BufferedReader in;
  private final PrintWriter out;

  public NewBankClientHandler(Socket s) throws IOException {
    bank = NewBank.getBank();
    in = new BufferedReader(new InputStreamReader(s.getInputStream()));
    out = new PrintWriter(s.getOutputStream(), true);
  }

  public void run() {
    try {
      CustomerID customer;
      clearConsole();
      launchHeader();

      while (true) {
        customer = logIn();
        if (customer == null) {
          out.println("Log In Failed");
        } else break;
      }

      // The user is authenticated. Get requests from the user and process them.
      mainMenu();
      
      handleRequests(customer);
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

  private void handleRequests(CustomerID customer) throws IOException {
    // keep getting requests from the client and processing them
    while (true) {
      String request = in.readLine();
      out.printf("Received request [%s] from %s\n", request, customer.getKey());
      if (request.equals("QUIT")) return;

      String response = bank.processRequest(customer, request);
      out.println(response);
    }
  }

  private CustomerID logIn() throws IOException {
    // ask for username
    out.println("Enter Username");
    String userName = in.readLine();
    // ask for password
    out.println("Enter Password");
    String password = in.readLine();
    out.println("Checking Details...");
    // authenticate user and get customer ID token from bank for use in subsequent requests
    return bank.checkLogInDetails(userName, password);
  }

  private void clearConsole() {
    out.println("\033[H\033[2J");
  }

  private void launchHeader() {
    out.println("--------------------------------------------");
    out.println("Welcome to New Bank Terminal. Please log in.");
    out.println("--------------------------------------------");
  }

  private void mainMenu() {
    clearConsole();
    out.println("Log In Successful");
    out.println("You have the following options:");
    out.println("OPTION 1. SHOWMYACCOUNTS — see your accounts and current balance");
    out.println("OPTION 2. NEWACCOUNT <Name> - open a new account (COMING SOON)"); 
    out.println("OPTION 3. MOVE <Amount> <From> <To> - move money from one account to another (COMING SOON)");
    out.println("OPTION 4. PAY <Person/Company> <Ammount> - pay another person (COMING SOON)");
    out.println("OPTION 5. QUIT - quit the menu"
  }

}