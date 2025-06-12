package model.exceptions;

/**
 * RunTimeException when trying to modify a calendar but no calendar is in use.
 */
public class NoCalendar extends RuntimeException {
  public NoCalendar(String message) {
    super(message);
  }
}
