package view;

import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import model.IEvent;
import model.ModifiableCalendar;

/**
 * Represents what a user can see with the GUI.
 */
public interface CalendarGUI extends CalendarView {

  /**
   * Returns the requested day get information from.
   *
   * @return the requested day to load
   */
  public LocalDate getLoadDay();

  /**
   * Sends all the information the user inputted when trying to create an event.
   *
   * @return the information the user inputed [0] is subject [1] is start and [2] is end
   */
  public String[] getCreate();

  /**
   * Loads all events on the current day in the calendar.
   *
   * @param events the events to display in the calendar
   */
  public void loadDay(List<IEvent> events);

  /**
   * Returns the information inputted into the edit form including the old data of the event.
   *
   * @return the unchanged and new data of the event being inputted
   */
  public Map<String, String> getEdit();

  /**
   * Returns the current calendar in use.
   *
   * @return the name of the current calendar in use
   */
  public String getCalendar();

  /**
   * Returns the current calendar to be created.
   *
   * @return the name of the calendar to be created
   */
  public String getNewCalendar();

  /**
   * Displays an error message to the user.
   *
   * @param msg the msg to display
   */
  public void displayError(String msg);

  /**
   * Creates a popup window used to create events.
   */
  public void popupCreateWindow();

  /**
   * Creates a popup window used to search dates.
   */
  public void popupSearchWindow();

  /**
   * Creates a popup window used to edit a selected event.
   */
  public void popupEditWindow(IEvent event);

  /**
   * Creates a popup window used to choose/create a calendar.
   */
  public void popupCalendarWindow(List<ModifiableCalendar> calendars);

  /**
   * Sets the listener for buttons.
   *
   * @param listener the controller to handle button inputs
   */
  public void setListener(ActionListener listener);

}
