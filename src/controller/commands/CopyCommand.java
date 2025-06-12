package controller.commands;

import java.time.LocalDate;
import java.time.LocalDateTime;

import model.exceptions.InvalidCalendar;
import model.exceptions.InvalidEvent;
import model.exceptions.NoCalendar;
import model.MultipleCalendarModel;
import view.CalendarView;

/**
 * Represents the copying of an event or events.
 */
public class CopyCommand extends CommandParsing {
  final private MultipleCalendarModel model;

  /**
   * The constructor for CopyCommand class.
   *
   * @param model the model that was passed into the controller.
   * @param view  the view that was passed into the controller.
   */
  public CopyCommand(MultipleCalendarModel model, CalendarView view) {
    super(view);
    this.model = model;
  }

  /**
   * Completes the copying of an event/events.
   *
   * @param inputTokens the users input line.
   */
  @Override
  public void execute(String[] inputTokens) {
    //checks if it was a valid copy command
    if (inputTokens.length < 7) {
      view.displayError("Invalid copy command.");
      return;
    }

    if (model.getCurrentCalendar() == null) {
      view.displayError("You must have an active calendar to copy");
    } else {
      //for a single event
      if (inputTokens[1].equals("event")) {
        String eventName = inputTokens[2];

        if (inputTokens[3].equals("on")) {
          LocalDateTime start = tryToGetLocalDateTime(inputTokens[4]);
          if (start == null) {
            return;
          }
          if (inputTokens[5].equals("--target")) {
            String calendarName = inputTokens[6];
            if (inputTokens[7].equals("to")) {
              LocalDateTime end = tryToGetLocalDateTime(inputTokens[8]);
              if (end == null) {
                return;
              }
              try {
                model.copyEvent(eventName, start, calendarName, end);
                view.displayMessage("Event copied.");
              } catch (InvalidCalendar e) {
                view.displayError(e.getMessage());
              } catch (InvalidEvent e) {
                view.displayError(e.getMessage());
              } catch (NoCalendar e) {
                view.displayError(e.getMessage());
              }
            } else {
              view.displayError("Please close the range of events that " +
                  "are to be copied.");
            }
          } else {
            view.displayError("Please specify a target calendar to " +
                "copy the events of.");
          }
        } else {
          view.displayError("Please state the date the event is on.");
        }
      } else if (inputTokens[1].equals("events")) {
        if (inputTokens[2].equals("on")) {
          LocalDate start = tryToGetLocalDate(inputTokens[3]);
          if (start == null) {
            return;
          }
          if (inputTokens[4].equals("--target")) {
            String calendarName = inputTokens[5];
            if (inputTokens[6].equals("to")) {
              LocalDate end = tryToGetLocalDate(inputTokens[7]);
              if (end == null) {
                return;
              }
              try {
                model.copyEvents(start, calendarName, end);
                view.displayMessage("Events copied.");
              } catch (InvalidCalendar e) {
                view.displayError(e.getMessage());
              } catch (NoCalendar e) {
                view.displayError(e.getMessage());
              }
            } else {
              view.displayError("Please say what date you would like to stop copying events");
            }
          } else {
            view.displayError("Please specify a target calendar.");
          }
        } else if (inputTokens[2].equals("between")) {
          LocalDate start = tryToGetLocalDate(inputTokens[3]);
          if (inputTokens[4].equals("and")) {
            LocalDate end = tryToGetLocalDate(inputTokens[5]);
            if (inputTokens[6].equals("--target")) {
              String calendarName = inputTokens[7];
              if (inputTokens[8].equals("to")) {
                LocalDate newStart = tryToGetLocalDate(inputTokens[9]);
                if (start == null || end == null) {
                  return;
                }
                try {
                  model.copyEvents(start, end, calendarName, newStart);
                  view.displayMessage("Events copied.");
                } catch (InvalidCalendar e) {
                  view.displayError(e.getMessage());
                } catch (NoCalendar e) {
                  view.displayError(e.getMessage());
                }
              } else {
                view.displayError("Please specify to when the range will be moved to.");
              }
            } else {
              view.displayError("Please specify a target calendar.");
            }
          } else {
            view.displayError("Please specify when the date range ends.");
          }
        } else {
          view.displayError("Please state the date the event is on.");
        }
      } else {
        view.displayError("Please specify if you are copying an event or events.");
      }
    }

  }
}
