package model.exceptions;

/**
 * RunTimeException to be thrown when a calendar could not be found.
 */
public class InvalidCalendar extends IllegalArgumentException {
  public InvalidCalendar(String message) {
    super(message);
  }
}
