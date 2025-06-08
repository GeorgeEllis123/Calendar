package model.CalendarExceptions;

public class NoCalendar extends RuntimeException {
  public NoCalendar(String message) {
    super(message);
  }
}
