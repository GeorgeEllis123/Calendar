package model.exceptions;

/**
 * RunTimeException when a given timezone does not exist.
 */
public class InvalidTimeZoneFormat extends IllegalArgumentException {
  public InvalidTimeZoneFormat(String message) {
    super(message);
  }
}
