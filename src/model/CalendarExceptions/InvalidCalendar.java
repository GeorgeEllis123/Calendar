package model.CalendarExceptions;

public class InvalidCalendar extends IllegalArgumentException {
  public InvalidCalendar(String message) {
    super(message);
  }
}
