package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import model.exceptions.InvalidEvent;
import model.exceptions.InvalidProperty;
import model.exceptions.InvalidCalendar;
import model.exceptions.InvalidTimeZoneFormat;
import model.exceptions.NoCalendar;

/**
 * Represents all the methods needed for a Google Calendar-like calendar has a timezone, can add
 * single and repeating events, edit events, and query events, and copy events.
 */
public interface MultipleCalendarModel extends CalendarModel {

  /**
   * Tries to create a calendar with the given name and in the given timezone.
   *
   * @param calendarName the name of the calendar
   * @param timezone     the timezone code of the calendar
   * @throws InvalidProperty       if a calendar already exists with the given name
   * @throws InvalidTimeZoneFormat if the format of the date time is invalid
   */
  public void create(String calendarName, String timezone) throws InvalidProperty,
      InvalidTimeZoneFormat;

  /**
   * Edits passed property of the matching calendar with the new property.
   *
   * @param calendarName the name of the calendar to edit
   * @param property     the property to be editted
   * @param newProperty  the new property value
   * @throws InvalidProperty if the property or property value is not valid
   * @throws InvalidCalendar if the calendar does not exist
   */
  public void edit(String calendarName, String property, String newProperty)
      throws InvalidProperty, InvalidCalendar;

  /**
   * Makes this calendar start using the given calendar if possible.
   *
   * @param calendarName the name of the calendar to use
   * @throws InvalidCalendar the requested calendar does not exist
   */
  public void use(String calendarName) throws InvalidCalendar;

  /**
   * Attempts to copy an event from the current calendar to another calendar.
   *
   * @param eventName    the name of the event to copy
   * @param start        the time the event should start
   * @param calendarName the name of the calendar to copy too
   * @param newStart     the time the event should start on (in the target's timezone)
   * @throws InvalidCalendar if the calendar does not exist
   * @throws InvalidEvent    if an event could not be found with the matching name and time or
   *                         more than one event was found
   * @throws NoCalendar      if there is no calendar currently in use
   */
  public void copyEvent(String eventName, LocalDateTime start, String calendarName,
                        LocalDateTime newStart) throws InvalidCalendar, InvalidEvent, NoCalendar;


  /**
   * Attempts to copy all events on a specific day from the current calendar to another calendar.
   * Assumption: if one or more of these events being copied causes an overlap it will still allow
   * the other events to be copied over just not the conflicting event(s).
   *
   * @param date         the date to query for events
   * @param calendarName the name of the calendar to copy to
   * @param toDate       the date to copy the events to on the target calendar converting the dates
   *                     in the process
   * @return true if at least one event was found and copied
   * @throws InvalidCalendar if the calendar does not exist
   * @throws NoCalendar      if there is no calendar currently in use
   */
  public boolean copyEvents(LocalDate date, String calendarName, LocalDate toDate)
      throws InvalidCalendar, NoCalendar;

  /**
   * Attempts to copy all events on between a range of days from the current calendar to another
   * calendar. Assumption: if one or more of these events being copied causes an overlap it will
   * still allow the other events to be copied over just not the conflicting event(s).
   *
   * @param start        the start of the date timezone to check
   * @param end          the end of the date timezone to check
   * @param calendarName the name of the calendar to copy to
   * @param newStart     the new relative start day for the events keeping their original offset
   *                     from the start day.
   * @return true if at least one event was found and copied
   * @throws InvalidCalendar if the calendar does not exist
   * @throws NoCalendar      if there is no calendar currently in use
   */
  public boolean copyEvents(LocalDate start, LocalDate end, String calendarName, LocalDate newStart)
      throws InvalidCalendar, NoCalendar;

  /**
   * Gets the current calendar.
   *
   * @return the current calendar
   */
  public ModifiableCalendar getCurrentCalendar();

  /**
   * Returns all the calendars this class holds.
   *
   * @return all the calendars this class holds
   */
  public List<ModifiableCalendar> getCalendars();
}
