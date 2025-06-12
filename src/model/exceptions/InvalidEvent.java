package model.exceptions;

/**
 * RunTimeException to be thrown when an event overlap is caused.
 */
public class InvalidEvent extends IllegalArgumentException {
  public InvalidEvent(String message) {
    super(message);
  }
}
