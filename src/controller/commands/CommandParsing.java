package controller.commands;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.concurrent.Callable;

import model.CalendarModel;
import model.MultipleCalendarModel;
import view.CalendarView;

public abstract class CommandParsing implements CalendarControllerCommands {
  protected final CalendarView view;
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

  /**
   * The constructor for any ACommand class.
   *
   * @param view the view that was passed into the controller.
   */
  public CommandParsing(CalendarView view) {
    this.view = view;
  }

  // Tries to convert a String into a LocalDateTime if it fails it sends an error to the view and
  // returns null
  protected LocalDateTime tryToGetLocalDateTime(String input) {
    LocalDateTime dateTime;
    try {
      dateTime = LocalDateTime.parse(input, formatter);
    }
    catch (DateTimeParseException e) {
      view.displayError("Invalid date time format! Should be: yyyy-MM-ddTHH:mm");
      return null;
    }
    return dateTime;
  }

  // Tries to convert a String into a LocalDate if it fails it sends an error to the view and
  // returns null
  protected LocalDate tryToGetLocalDate(String input) {
    LocalDate date;
    try {
      date = LocalDate.parse(input);
    }
    catch (DateTimeParseException e) {
      view.displayError("Invalid date format! Should be: yyyy-MM-dd");
      return null;
    }
    return date;
  }
}
