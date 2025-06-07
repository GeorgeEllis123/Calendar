package controller.commands;

import java.time.LocalDateTime;

import model.CalendarModel;
import view.CalendarView;

/**
 * Represents the editing of an existing event.
 */
public class EditCommand extends ACommand {
  /**
   * The public constructor of the EditCommand class.
   * @param model the model that was passed into the controller.
   * @param view the view that was passed into the controller.
   */
  public EditCommand(CalendarModel model, CalendarView view) {
    super(model, view);
  }

  /**
   * Completes the edit command.
   * @param inputTokens the users input line.
   */
  @Override
  public void execute(String[] inputTokens) {

    if (inputTokens.length < 7) {
      view.displayError("Invalid edit command.");
      return;
    }

    String eventType = inputTokens[1].toLowerCase();
    String property = inputTokens[2].toLowerCase();
    String subject = inputTokens[3];

    switch (eventType) {
      case "event": {
        if (!inputTokens[4].equals("from") || !inputTokens[6].equals("to") ||
            (!inputTokens[8].equals("with"))) {
          view.displayMessage("Invalid format edit command.  Please give a subject," +
              "start date and time, and an end date and time.");
          return;
        }
        LocalDateTime start = tryToGetLocalDateTime(inputTokens[5]);
        LocalDateTime end = tryToGetLocalDateTime(inputTokens[7]);
        if (start == null || end == null) {
          return;
        }

        String newProperty = inputTokens[9];

        if (model.editSingleEvent(subject, start, end, property, newProperty)) {
          view.displayMessage("Single event edited.");
        } else {
          view.displayMessage("Event not found.");
        }

        break;
      }
      case "events": {
        if (!inputTokens[4].equals("from") || !inputTokens[6].equals("with")) {
          view.displayError("Invalid format edit command.  Please give a subject, " +
              "start date and time, and an end date and time.");
          return;
        }

        LocalDateTime start = tryToGetLocalDateTime(inputTokens[5]);
        if (start == null) {
          return;
        }
        String newProperty = inputTokens[7];

        if (model.editFutureSeriesEvents(subject, start, property, newProperty)) {
          view.displayMessage("Future series edited.");
        } else {
          view.displayMessage("Event not found or causes overlap.");
        }

        break;
      }
      case "series": {
        if (!inputTokens[4].equals("from") || !inputTokens[6].equals("with")) {
          view.displayError("Invalid format edit command.  Please add a property, " +
              "subject, start date and time, and the property you would like to edit. ");
        }
        LocalDateTime start = tryToGetLocalDateTime(inputTokens[5]);
        if (start == null) {
          return;
        }
        String newProperty = inputTokens[7];

        if (model.editEntireSeries(subject, start, property, newProperty)) {
          view.displayMessage("Entire series edited.");
        } else {
          view.displayMessage("Event not found.");
        }

        break;
      }
      default:
        view.displayError("Unknown edit command: " + eventType);
    }
  }


}


