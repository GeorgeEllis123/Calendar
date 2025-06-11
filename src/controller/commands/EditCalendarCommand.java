package controller.commands;

import java.time.format.DateTimeFormatter;

import controller.CalendarController;
import model.CalendarExceptions.InvalidCalendar;
import model.CalendarExceptions.InvalidProperty;
import model.CalendarModel;
import model.MultipleCalendarModel;
import view.CalendarView;

/**
 * Represents the editing of an existing calendar.
 */
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

  /**
   * Allows a user to edit either a calendars name or timezone.
   * @param inputTokens the users input line.
   */
  @Override
  public void execute(String[] inputTokens) {

    if (inputTokens.length < 7) {
      view.displayError("Wrong number of arguments!");
      return;
    }

    if (inputTokens[1].equals("event") || inputTokens[1].equals("events") ||
        inputTokens[1].equals("series")) {
      if (model.getCurrentCalendar() == null) {
        view.displayError("No calendar selected. Use the 'use' command first.");
        return;
      } else {
        this.editCommand.execute(inputTokens);
        return;
      }
    }

    if ((inputTokens.length < 7) || (!inputTokens[1].equals("calendar"))) {
      view.displayError("If you are editing a calendar, please start with 'edit calendar'");
    } else if (inputTokens[1].equals("calendar")) {
      if (inputTokens[2].equals("--name")) {
        String calendarName = inputTokens[3];
        if (inputTokens[4].equals("--property")) {
          String propertyName = inputTokens[5];
          String propertyValue = inputTokens[6];
          try {
            model.edit(calendarName, propertyName, propertyValue);
            view.displayMessage("Edited calendar successfully!");
          } catch (InvalidProperty e) {
            view.displayError(e.getMessage());
          } catch (InvalidCalendar e) {
            view.displayError(e.getMessage());
          }
        } else {
          view.displayError("Please add a property and a property name!");
        }
      } else {
        view.displayError("You must specify a calendar name.");
      }
    }

  }
}
