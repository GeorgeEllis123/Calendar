package model.CalendarExceptions;

public class InvalidTimeZoneFormat extends IllegalArgumentException {
  public InvalidTimeZoneFormat(String message) {
    super(message);
  }
}
