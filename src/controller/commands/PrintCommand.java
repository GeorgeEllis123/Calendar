package controller.commands;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import model.CalendarModel;
import model.IEvent;
import view.CalendarView;

/**
 * Represents the printing of existing events that fit the parameters given in the command.
 */
public class PrintCommand extends ACommand {
  /**
   * The public constructor of the PrintCommand class.
   * @param model the model that was passed into the controller.
   * @param view the view that was passed into the controller.
   */
  public PrintCommand(CalendarModel model, CalendarView view) {
    super(model, view);
  }

  /**
   * Completes the print command.
   * @param inputTokens the users input line.
   */
  @Override
  public void execute(String[] inputTokens) {
    if (inputTokens.length == 4 && inputTokens[1].equals("events")
        && inputTokens[2].equals("on")) {
      LocalDate date = tryToGetLocalDate(inputTokens[3]);
      if (date == null) {
        return;
      }
      List<IEvent> events = model.queryEvent(date);

      if (events.isEmpty()) {
        view.displayMessage("No events on " + date);
      } else {
        for (IEvent event : events) {
          view.displayMessage(event.toString());
        }
      }

    } else if (inputTokens.length == 6 && inputTokens[1].equals("events") &&
        inputTokens[2].equals("from") && inputTokens[4].equals("to")) {
      LocalDateTime start = tryToGetLocalDateTime(inputTokens[3]);
      LocalDateTime end = tryToGetLocalDateTime(inputTokens[5]);
      if (start == null || end == null) {
        return;
      }

      List<IEvent> events = model.queryEvent(start, end);

      if (events.isEmpty()) {
        view.displayMessage("No events between " + inputTokens[3] +
            " and " + inputTokens[5]);
      } else {
        for (IEvent event : events) {
          view.displayMessage(event.toString());
        }
      }

    } else {
      view.displayError("Invalid print command format.");
    }
  }
}
