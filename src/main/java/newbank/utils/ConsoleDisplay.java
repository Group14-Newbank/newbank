package newbank.utils;

/** Output messages directly to the standard output stream. */
public class ConsoleDisplay extends Display {

  @Override
  public void writeLine(String message) {
    System.out.println(message);
  }

  @Override
  public String getLine() {
    throw new RuntimeException("Not supported for console output");
  }

  @Override
  public String discardLinesUntil(String arg) {
    throw new RuntimeException("Not supported for console output");
  }
}
