package controller.commands;

/**
 * Represents the interface that holds all the commands that the user can utilize.
 */
public interface CalendarControllerCommands {

  /**
   * Completes the command that the user input.
   * @param inputTokens the users input line.
   */
  void execute(String[] inputTokens);
}
