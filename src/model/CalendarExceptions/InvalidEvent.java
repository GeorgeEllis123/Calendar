package model.CalendarExceptions;

public class InvalidEvent extends IllegalArgumentException {
  public InvalidEvent(String message) {
    super(message);
  }
}
