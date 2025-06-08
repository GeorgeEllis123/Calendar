package model.CalendarExceptions;

public class InvalidProperty extends IllegalArgumentException  {
  public InvalidProperty(String message) {
    super(message);
  }
}
