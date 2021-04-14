package newbank.utils;

/**
 * This class abstracts the output thus allowing us to send the output to various classes
 * implementing the protocols.
 */
public abstract class Display {
  /**
   * Write a line terminated message onto the display
   *
   * @param message
   */
  public abstract void writeLine(final String message);
  
  /**
   * Write a message onto the display
   *
   * @param message
   */
  public abstract void write(final String message);

  /**
   * Retrieve a line terminated message from the display
   *
   * @return the line read
   */
  public abstract String getLine();

  /**
   * Discard lines until a line containing the supplied argument is found.
   *
   * @return the first line containing the arg
   */
  public abstract String discardLinesUntil(String arg);
}
