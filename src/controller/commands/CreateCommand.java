package controller.commands;

import java.time.LocalDate;
import java.time.LocalDateTime;

import model.CalendarModel;
import view.CalendarView;

/**
 * Represents the creation of a new event.
 */
public class CreateCommand extends ACommand {

  /**
   * The public constructor of the CreateCommand class.
   * @param model the model that was passed into the controller.
   * @param view the view that was passed into the controller.
   */
  public CreateCommand(CalendarModel model, CalendarView view) {
    super(model, view);
  }

  /**
   * Completes the create command.
   * @param inputTokens the users input line.
   */
  public void execute(String[] inputTokens) {

    if (inputTokens.length < 4 || !inputTokens[1].equals("event")) {
      view.displayError("Invalid create command.");
      return;
    }

    String subject = inputTokens[2];
    if (inputTokens[3].equals("from")) {
      LocalDateTime start = tryToGetLocalDateTime(inputTokens[4]);
      LocalDateTime end = tryToGetLocalDateTime(inputTokens[6]);
      if (start == null || end == null) {
        return;
      }

      if (inputTokens.length == 7) {
        if (model.addSingleEvent(subject, start, end)) {
          view.displayMessage("Event created.");
        } else {
          view.displayError("Event already exists.");
        }
      } else if (inputTokens[7].equals("repeats")) {
        String weekdays = inputTokens[8];

        if (inputTokens[9].equals("for")) {
          int count = Integer.parseInt(inputTokens[10]);
          if (model.addRepeatingEvent(subject, start, end, weekdays, count)) {
            view.displayMessage("Repeating event created.");
          } else {
            view.displayError("Repeating event already exists.");
          }
        } else if (inputTokens[9].equals("until")) {
          LocalDate endDate = LocalDate.parse(inputTokens[10]);
          if (model.addRepeatingEvent(subject, start, end, weekdays, endDate)) {
            view.displayMessage("Repeating event created.");
          } else {
            view.displayError("Repeating event already exists.");
          }
        }
      }

    } else if (inputTokens[3].equals("on")) {
      LocalDate date = tryToGetLocalDate(inputTokens[4]);
      if (date == null) {
        return;
      }
      LocalDateTime start = date.atTime(8, 0);
      LocalDateTime end = date.atTime(17, 0);

      if (inputTokens.length == 5) {
        if (model.addSingleEvent(subject, start, end)) {
          view.displayMessage("All day event created.");
        } else {
          view.displayError("All day event already exists.");
        }
      } else if (inputTokens[5].equals("repeats")) {
        String weekdays = inputTokens[6];

        if (inputTokens[7].equals("for")) {
          int count;
          try {
            count = Integer.parseInt(inputTokens[8]);
          } catch (NumberFormatException e) {
            view.displayError("Cound not convert number of times to repeat into an integer.");
            return;
          }
          if (model.addRepeatingEvent(subject, start, end, weekdays, count)) {
            view.displayMessage("All day repeating event created.");
          } else {
            view.displayError("All day repeating event already exists.");
          }
        } else if (inputTokens[7].equals("until")) {
          LocalDate endDate = tryToGetLocalDate(inputTokens[8]);
          if (endDate == null) {
            return;
          }
          if (model.addRepeatingEvent(subject, start, end, weekdays, endDate)) {
            view.displayMessage("All day repeating event created.");
          } else {
            view.displayError("All day repeating event already exists.");
          }
        }
      }
    } else {
      view.displayError("Invalid create command.");
    }
  }
}
