package view;

import java.io.PrintStream;

/**
 * Represents the implementation of the Calendars view, which is what the user sees.
 */
public class CalendarViewImpl implements CalendarView {
  private final PrintStream out;

  /**
   * Public constructor of the CalendarViewImpl class.
   *
   * @param out What is being printed to the user.
   */
  public CalendarViewImpl(PrintStream out) {
    this.out = out;
  }

  /**
   * Prompts the user to enter a command.
   */
  @Override
  public void promptUser() {
    out.println("Enter command: ");
  }

  /**
   * Displays an error when the user inputs an invalid command.
   *
   * @param msg the error message the user will see.
   */
  @Override
  public void displayError(String msg) {
    out.println("ERROR: " + msg);
  }

  /**
   * Displays a message when a command was correctly executed.
   *
   * @param msg the message that tells the user that their command was successful.
   */
  @Override
  public void displayMessage(String msg) {
    out.println(msg);
  }
}
