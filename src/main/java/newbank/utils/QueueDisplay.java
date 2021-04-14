package newbank.utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/** Stores and manages messages internally in a queue. */
public class QueueDisplay extends Display {

  // note arbitrary size, the queue is unlikely to exceed 1024 elements
  private BlockingQueue<String> queue = new ArrayBlockingQueue<>(1024);

  @Override
  public void writeLine(String message) {
    try {
      queue.put(message);
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }
  }
  
  @Override
  public void write(String message) {
	  writeLine(message);
  }

  @Override
  public String getLine() {
    String message = null;

    try {
      message = queue.take();
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }

    return message;
  }

  @Override
  public String discardLinesUntil(String arg) {
    String message = getLine();

    while (!message.contains(arg)) {
      message = getLine();
    }

    return message;
  }
}
