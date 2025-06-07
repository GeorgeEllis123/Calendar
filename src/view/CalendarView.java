package view;

/**
 * Represents what the user can see.
 */
public interface CalendarView {

  /**
   * Prompts the user to enter a command.
   */
  void promptUser();

  /**
   * Displays an error when the user inputs an invalid command.
   *
   * @param msg the error message the user will see.
   */
  void displayError(String msg);

  /**
   * Displays a message when a command was correctly executed.
   *
   * @param msg the message that tells the user that their command was successful.
   */
  void displayMessage(String msg);
}
