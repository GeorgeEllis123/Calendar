package controller.commands;

import model.CalendarExceptions.InvalidCalendar;
import model.CalendarModel;
import model.MultipleCalendarModel;
import view.CalendarView;

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

  @Override
  public void execute(String[] inputTokens) {
    if (inputTokens.length < 4) {
      view.displayError("Please enter in this format: edit calendar --name <name-of-calendar>");
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
