package newbank.server.commands;

import newbank.server.CustomerID;
import newbank.server.NewBank;

import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract representation of a command.
 */
public abstract class Command {
  protected final NewBank bank;
  protected final String[] tokens;
  protected final CustomerID customerID;
  protected List<Supplier<String>> responsibilityChain;

  protected Command(NewBank bank, String[] tokens, CustomerID customerID) {
    this.bank = bank;
    this.tokens = tokens;
    this.customerID = customerID;
  }

  public abstract String execute();

  /**
   * @return the command syntax
   */
  public abstract String getSyntax();

  /**
   * Apply each of the functions in the responsibility chain. If any of them
   * return a failure message, stop processing.
   *
   * @return An empty string if success. Otherwise a failure message.
   */
  protected String applyResponsibilityChain() {
    return responsibilityChain.stream()
        .map(Supplier::get)
        .filter(message -> !message.isEmpty())
        .findFirst().orElse("");
  }

  protected String requestingHelp() {
    if (tokens.length >= 2 && tokens[1].equalsIgnoreCase("HELP"))
      return String.format("SUCCESS: Usage: %s", getSyntax());
    return "";
  }

  protected String mustLogIn() {
    if (isLoggedIn()) return "";
    return "FAIL: Request not allowed, please log in first.";
  }

  protected String incorrectUsage() {
    String[] expectedTokens = getSyntax().split("\\s");
    if (tokens.length > expectedTokens.length || incorrectOptionalLiterals(expectedTokens))
      return String.format("FAIL: Usage: %s", getSyntax());

    return "";
  }

  protected String noDefaultAccount() {
    return bank.hasDefaultAccount(customerID) ? "" : "FAIL: You do not have a default account.";
  }

  /**
   * <p>Determines whether a command is incorrect, taking into account any
   * optional-literal arguments (of the form "[OPT-LIT]") in the command syntax.
   * Optional-literal arguments are assumed to be at the end of the command.</p>
   * 
   * <p>Here is an example using the command <code>ADDACCOUNT &lt;name&gt; [DEFAULT]</code>:</p>
   * <code>ADDACCOUNT Main DEFAULT</code>
   * <p>Here "DEFAULT" is the optional-literal argument. If it is supplied, we
   * must confirm that it has the expected value, "DEFAULT", which it does, so
   * the command succeeds.</p>
   *
   * @return true if the user omits a non-optional argument, or if they supply
   * an optional-literal argument other than the one specified. Otherwise 
   * returns false.
   * 
   */
  private boolean incorrectOptionalLiterals(String[] expectedTokens) {
    // Find any optional-literal command arguments like [DEFAULT]
    Pattern optionalLiteralRegex = Pattern.compile("^\\[.+]$");
    int tokenCount = tokens.length;
    for (int i = expectedTokens.length - 1; i >= 0; i--) {
      Matcher match = optionalLiteralRegex.matcher(expectedTokens[i]);
      // No (more) opt-lit-args, so usage is incorrect only if a mandatory token is missing: 
      if (!match.matches()) return tokenCount <= i;

      if (tokenCount > i && match.group().equalsIgnoreCase(tokens[i]))
        return true;
    }
    return false;
  }

  protected boolean isLoggedIn() {
    return !customerID.getKey().isEmpty();
  }
}
