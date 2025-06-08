package model;

import model.CalendarExceptions.InvalidEvent;

public interface ModifiableCalendar extends CalendarModel {

  /**
   * Changes this calendar's name.
   *
   * @param newName the new name for the calendar
   */
  void editName(String newName);

  /**
   * Changes this calendar's timezone. Assumption: changing the timezone does not cause you to have
   * to change the time's of the events in the calendar.
   *
   * @param newTimeZone the new name for the calendar
   */
  void editTimeZone(TimeZone newTimeZone);

  /**
   * Tries to add the event to this class' events and applies the timezone difference to the event
   * updating its times.
   *
   * @param event the event to add and modifies it's timezone
   * @throws InvalidEvent if the event causes an overlap
   */
  void add(TZEvent event, TimeZone prevTimeZone) throws InvalidEvent;

  /**
   * Gets this calendar's name.
   *
   * @return the name of this calendar
   */
  String getName();
}
