package controller.commands;

import model.CalendarExceptions.InvalidCalendar;
import model.CalendarModel;
import model.MultipleCalendarModel;
import view.CalendarView;

/**
 * Represents a user's ability to select a calendar to create events in.
 */
public class UseCommand implements CalendarControllerCommands {
  private final MultipleCalendarModel model;
  private final CalendarView view;
  /**
   * The constructor for UseCommand class.
   *
   * @param model the model that was passed into the controller.
   * @param view  the view that was passed into the controller.
   */
  public UseCommand(MultipleCalendarModel model, CalendarView view) {
    this.model = model;
    this.view = view;
  }

  /**
   * Completes the use command.  Allows the user to use a certain calendar.
   * @param inputTokens the users input line.
   */
  @Override
  public void execute(String[] inputTokens) {
    if (inputTokens.length < 4) {
      view.displayError("Please ensure that you are using the correct syntax");
    }

    if (inputTokens[1].equals("calendar")) {
      String calendarName = inputTokens[2];
      try {
        model.use(calendarName);
        view.displayMessage("Successfully using " + calendarName);
      } catch (InvalidCalendar e) {
        view.displayError(e.getMessage());
      }
    } else {
      view.displayError("Please enter a valid calendar name");
    }
  }
}
