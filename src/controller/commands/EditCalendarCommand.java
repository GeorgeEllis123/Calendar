package controller.commands;

import java.time.format.DateTimeFormatter;

import controller.CalendarController;
import model.CalendarExceptions.InvalidCalendar;
import model.CalendarExceptions.InvalidProperty;
import model.CalendarModel;
import model.MultipleCalendarModel;
import view.CalendarView;

public class EditCalendarCommand implements CalendarControllerCommands {
  private final MultipleCalendarModel model;
  private final CalendarView view;
  private EditCommand editCommand;

  /**
   * The public constructor of the EditCalendarCommand class.
   *
   * @param model the model that was passed into the controller.
   * @param view  the view that was passed into the controller.
   */
  public EditCalendarCommand(MultipleCalendarModel model, CalendarView view) {
    this.model = model;
    this.view = view;
    this.editCommand = new EditCommand(model, view);
  }

  @Override
  public void execute(String[] inputTokens) {

    if (inputTokens.length < 7) {
      view.displayMessage("Error: Wrong number of arguments!");
      return;
    }

    if (model.getCurrentCalendar() == null) {
      view.displayError("No calendar selected. Use the 'use' command first.");
    } else if (inputTokens[1].equals("event") || inputTokens[1].equals("events") ||
        inputTokens[1].equals("series")) {
      this.editCommand.execute(inputTokens);
      return;
    } else if ((inputTokens.length < 7) || (inputTokens[1].equals("calendar"))) {
      view.displayError("If you are editing a calendar, please start with 'edit calendar'");
    } else if (inputTokens[1].equals("calendar")) {
      String eventName = inputTokens[2];
      if (inputTokens[3].equals("--name")) {
        String calendarName = inputTokens[4];
        if (inputTokens[5].equals("--property")) {
          String propertyName = inputTokens[6];
          String propertyValue = inputTokens[7];
          try {
            model.edit(calendarName, propertyName, propertyValue);
          } catch (InvalidProperty e) {
            view.displayError(e.getMessage());
          } catch (InvalidCalendar e) {
            view.displayError(e.getMessage());
          }
        } else {
          view.displayMessage("Please add a property and a property name!");
        }
      } else {
        view.displayError("You must specify a calendar name.");
      }
    }

  }
}
