package newbank.server.commands;

import newbank.server.CustomerID;
import newbank.server.NewBank;
import newbank.utils.Config;

public class HelpCommand extends Command {
  public HelpCommand(final NewBank bank, final String[] tokens, final CustomerID customer) {}

  @Override
  public String execute() {
    return new StringBuilder()
        .append("SUCCESS: ")
        .append(Config.MULTILINE_INFO_SEPARATOR)
        .append("You have the following commands available:")
        .append(Config.MULTILINE_INFO_SEPARATOR)
        .append(Config.MULTILINE_INFO_SEPARATOR)
        .append("...................GENERAL...................")
        .append(Config.MULTILINE_INFO_SEPARATOR)
        .append(Config.MULTILINE_INFO_SEPARATOR)
        .append(String.format("%-15s%s", "LOGIN", "Log into the application"))
        .append(Config.MULTILINE_INFO_SEPARATOR)
        .append(String.format("%-15s%s", "HELP", "Display this help"))
        .append(Config.MULTILINE_INFO_SEPARATOR)
        .append(String.format("%-15s%s", "QUIT", "Exit the application"))
        .append(Config.MULTILINE_INFO_SEPARATOR)
        .append(Config.MULTILINE_INFO_SEPARATOR)
        .append("............ACCOUNT ADMINISTRATION............")
        .append(Config.MULTILINE_INFO_SEPARATOR)
        .append(Config.MULTILINE_INFO_SEPARATOR)
        .append(String.format("%-15s%s", "DEFAULT", "Specify the default current account"))
        .append(Config.MULTILINE_INFO_SEPARATOR)
        .append(String.format("%-15s%s", "NEWACCOUNT", "Creates a new account for a customer"))
        .append(Config.MULTILINE_INFO_SEPARATOR)
        .append(String.format("%-15s%s", "REGISTER", "Adds a new customer to the application"))
        .append(Config.MULTILINE_INFO_SEPARATOR)
        .append(Config.MULTILINE_INFO_SEPARATOR)
        .append(".................TRANSACTIONS.................")
        .append(Config.MULTILINE_INFO_SEPARATOR)
        .append(Config.MULTILINE_INFO_SEPARATOR)
        .append(
            String.format(
                "%-15s%s",
                "SHOWMYACCOUNTS",
                "Retrieves and displays a list of all the customers’ accounts along with their current balance"))
        .append(Config.MULTILINE_INFO_SEPARATOR)
        .append(
            String.format("%-15s%s", "DEPOSIT", "Add money into one of the customer's own accounts"))
        .append(Config.MULTILINE_INFO_SEPARATOR)
        .append(
            String.format(
                "%-15s%s", "PAY", "Credit a specified customer's default current account"))
        .append(Config.MULTILINE_INFO_SEPARATOR)
        .append(Config.MULTILINE_INFO_SEPARATOR)
        .append("Use '<commnad> help' for information on a specific command.")
        .append(Config.MULTILINE_INFO_SEPARATOR)
        .toString();
  }
}
