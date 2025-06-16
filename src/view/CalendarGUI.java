package view;

import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;

import model.IEvent;

public interface CalendarGUI {

  /**
   * Returns the requested day get information from.
   *
   * @return the requested day to load
   */
  public LocalDate getLoadDay();

  /**
   * Sends all the information the user inputted when trying to create an event.
   *
   * @return the information the user inputed [0] is subject [1] is start [1] is end
   */
  public String[] getCreate();

  /**
   * Loads all events on the current day in the calendar.
   *
   * @param events the events to display in the calendar
   */
  public void loadDay(ArrayList<IEvent> events);

  /**
   * Displays an error message to the user.
   *
   * @param msg the msg to display
   */
  public void displayError(String msg);

  /**
   * Sets the listener for buttons.
   *
   * @param listener the controller to handle button inputs
   */
  public void setListener(ActionListener listener);

}
