package newbank.server.commands;

import newbank.server.CustomerID;
import newbank.server.NewBank;

import java.util.List;
import java.util.Optional;
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
  protected List<Supplier<Optional<String>>> responsibilityChain;

  protected Command(NewBank bank, String[] tokens, CustomerID customerID) {
    this.bank = bank;
    this.tokens = tokens;
    this.customerID = customerID;
  }

  public abstract String execute();

  /**
   * @return the command syntax
   */
  protected abstract String getSyntax();

  /**
   * Apply each of the functions in the responsibility chain. If any of them
   * return a String (containing a failure message), stop processing.
   *
   * @return `Optional`ly either a string containing a failure message, or null.
   */
  protected Optional<String> applyResponsibilityChain() {
    return responsibilityChain.stream()
        .map(Supplier::get)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst();
  }

  protected Optional<String> requestingHelp() {
    if (tokens.length >= 2 && tokens[1].equalsIgnoreCase("HELP"))
      return Optional.of(String.format("SUCCESS: Usage: %s", getSyntax()));
    return Optional.empty();
  }

  protected Optional<String> mustLogIn() {
    if (isLoggedIn()) return Optional.empty();
    return Optional.of("FAIL: Request not allowed, please log in first.");
  }

  protected Optional<String> incorrectUsage() {
    String[] expectedTokens = getSyntax().split("\\s");
    if (tokens.length > expectedTokens.length || incorrectOptionalLiterals(expectedTokens))
      return Optional.of(String.format("FAIL: Usage: %s", getSyntax()));

    return Optional.empty();
  }

  /**
   * Determines whether a command is incorrect, taking into account any
   * optional-literal arguments (of the form "[OPT-LIT]") in the command syntax.
   * Optional-literal arguments are assumed to be at the end of the command.
   *
   * @return true if the user omits a non-optional argument, or if they supply
   * an optional-literal argument other than the one specified. Otherwise 
   * returns false.
   */
  private boolean incorrectOptionalLiterals(String[] expectedTokens) {
    Pattern optionalLiteralRegex = Pattern.compile("^\\[.+]$");
    int tokenCount = tokens.length;
    for (int i = expectedTokens.length - 1; i >= 0; i--) {
      Matcher match = optionalLiteralRegex.matcher(expectedTokens[i]);
      if (match.matches()) {
        if (tokenCount > i && match.group().equalsIgnoreCase(tokens[i]))
          return true;
      }

      else return tokenCount <= i;
    }
    return false;
  }

  protected boolean isLoggedIn() {
    return !customerID.getKey().isEmpty();
  }
}
