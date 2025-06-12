package model.exceptions;

/**
 * RunTimeException to be thrown when an invalid property or property value is given.
 */
public class InvalidProperty extends IllegalArgumentException  {
  public InvalidProperty(String message) {
    super(message);
  }
}
