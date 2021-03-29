package newbank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import newbank.server.exceptions.DuplicateCustomerException;
import newbank.server.exceptions.PasswordInvalidException;
import newbank.server.exceptions.RequestNotAllowedException;

/** The NewBankClientHandler handles all clients requests. */
public class NewBankClientHandler extends Thread {

  private NewBank bank;
  private BufferedReader in;
  private PrintWriter out;
  private CustomerID customer;

  public NewBankClientHandler(Socket s) throws IOException {
    bank = NewBank.getBank();
    in = new BufferedReader(new InputStreamReader(s.getInputStream()));
    out = new PrintWriter(s.getOutputStream(), true);
  }

  private void handleLogin(String[] tokens) {
    String username = "";
    String password = "";

    if (tokens.length >= 2) {
      username = tokens[1];
    }
    if (tokens.length >= 3) {
      password = tokens[2];
    }

    customer = bank.checkLogInDetails(username, password);

    if (customer != null) {
      out.println("SUCCESS: Log In Successful");
    } else {
      out.println("FAIL: Log In Failed");
    }
  }

  private void handleRegisterCustomer(String[] tokens) {
    String username = "";
    String password = "";

    if (tokens.length >= 2) {
      username = tokens[1];
    }
    if (tokens.length >= 3) {
      password = tokens[2];
    }

    try {
      bank.addCustomer(username, password);

      out.println("SUCCESS: Customer created successfully.");
    } catch (DuplicateCustomerException ex) {
      out.println("FAIL: " + String.format("Customer name [%s] already exists.", username));
    } catch (PasswordInvalidException e) {
      out.println("FAIL: Specified password does not meet the security requirements.");
    }
  }

  private void handleShowAccounts() throws RequestNotAllowedException {
    checkLoggedIn();
    out.println(bank.showAccountsFor(customer));
  }

  private void checkLoggedIn() throws RequestNotAllowedException {
    if (customer == null) {
      throw new RequestNotAllowedException();
    }
  }

  private boolean processRequest(final String request) {
    final String[] tokens = request.split("\\s+");

    assert (tokens.length > 0);

    try {
      switch (tokens[0].toUpperCase()) {
        case "LOGIN":
          handleLogin(tokens);
          break;
        case "REGISTER":
          handleRegisterCustomer(tokens);
          break;
        case "SHOWMYACCOUNTS":
          handleShowAccounts();
          break;
        case "NEWACCOUNT":
          handleNewAccount(tokens);
          break;
        case "PAYSOMEONE":
          handlePayAccount();
          break;
        case "QUIT":
          out.println("SUCCESS: Good bye.");
          return false;
        default:
          out.println("FAIL: Unknown command.");
      }
    } catch (RequestNotAllowedException ex) {
      out.println("FAIL: Customer not logged in.");
    }

    return true;
  }

  private void handlePayAccount() throws RequestNotAllowedException {
    checkLoggedIn();
    out.println(bank.showAccountsFor(customer));
    out.println("Who would you like to pay?");
    Scanner scannerObj = new Scanner(System.in);  // Create a Scanner object
    System.out.println("Who would you like to pay?");

    String payName = scannerObj.nextLine();  // Read user input
    System.out.println("How much would you like to send: " + payName);  // Output user

    String payAmount = scannerObj.nextLine();
    System.out.println(bank.showAccountsFor(customer) + payAmount);
  }

  private void handleNewAccount(String[] tokens) throws RequestNotAllowedException {
    checkLoggedIn();

    if (tokens.length != 2) {
      out.println("FAIL: The proper syntax is: NEWACCOUNT <Name>");
      return;
    }

    out.println(bank.newAccount(customer, tokens[1]));
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
