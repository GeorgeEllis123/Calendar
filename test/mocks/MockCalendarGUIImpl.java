package mocks;

import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;

import model.IEvent;
import view.CalendarGUI;

public class MockCalendarGUIImpl implements CalendarGUI {

  @Override
  public LocalDate getLoadDay() {
    return null;
  }

  @Override
  public String[] getCreate() {
    return new String[0];
  }

  @Override
  public void loadDay(ArrayList<IEvent> events) {

  }

  @Override
  public void displayError(String msg) {

  }

  @Override
  public void popupCreateWindow() {

  }

  @Override
  public void popupSearchWindow() {

  }

  @Override
  public void setListener(ActionListener listener) {

  }

  @Override
  public void promptUser() {

  }

  @Override
  public void displayMessage(String msg) {

  }
}
