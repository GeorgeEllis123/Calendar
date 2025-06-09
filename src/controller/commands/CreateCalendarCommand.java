package controller.commands;

import model.CalendarExceptions.InvalidProperty;
import model.CalendarExceptions.InvalidTimeZoneFormat;
import model.MultipleCalendarModel;
import view.CalendarView;

/**
 * Represents the creation of a new calendar.
 */
public class CreateCalendarCommand implements CalendarControllerCommands {
  protected final MultipleCalendarModel model;
  protected final CalendarView view;
  CreateCommand createCommand;


  /**
   * The public constructor of the CreateCommand class.
   *
   * @param model the model that was passed into the controller.
   * @param view  the view that was passed into the controller.
   */
  public CreateCalendarCommand(MultipleCalendarModel model, CalendarView view) {
    this.model = model;
    this.view = view;
    this.createCommand = new CreateCommand(model, view);
  }

  /**
   * Completes the creation of a calendar.
   * @param inputTokens the users input line.
   */
  @Override
  public void execute(String[] inputTokens) {
    if (inputTokens.length > 4 && (inputTokens[1].equals("event") ||
        inputTokens[1].equals("events"))) {
      if (model.getCurrentCalendar() == null) {
        view.displayError("No calendar selected. Use the 'use' command first.");
      } else {
        createCommand.execute(inputTokens);
        return;
      }
    }

    if (inputTokens.length < 6 ||
        !inputTokens[1].equals("calendar") || (!inputTokens[2].equals("--name"))) {
      view.displayError("Invalid create calendar command.");
      return;
    }

    if (inputTokens[1].equals("calendar")) {
      String calendarName = inputTokens[3];
      if (inputTokens[4].equals("--timezone")) {
        try {
          this.model.create(calendarName, inputTokens[5]);
          view.displayMessage("Calendar created successfully.");
        } catch (InvalidProperty e) {
          view.displayError(e.getMessage());
        } catch (InvalidTimeZoneFormat e) {
          view.displayError(e.getMessage());
        }
      } else {
        view.displayError("Invalid time zone.");
      }
    }
  }
}
