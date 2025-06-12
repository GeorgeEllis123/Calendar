package mocks;

import java.util.ArrayList;

import view.CalendarView;

/**
 * Represents a mock view to test the calendar view.
 */
public class MockCalendarView implements CalendarView {
  public ArrayList<String> messages = new ArrayList<>();
  public ArrayList<String> errors = new ArrayList<>();

  @Override
  public void displayMessage(String message) {
    messages.add(message);
  }

  @Override
  public void promptUser() {
    //not used for testing
  }

  @Override
  public void displayError(String errorMessage) {
    errors.add(errorMessage);
    messages.add("ERROR: " + errorMessage);
  }
}