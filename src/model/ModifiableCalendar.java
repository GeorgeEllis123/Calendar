package model;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.TimeZone;

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
   * Tries to add the event to this class' events with a new start time.
   *
   * @param event    the event to add and modifies it's timezone
   * @param newStart the new start time of the event in this calendar's timezone
   * @throws InvalidEvent if the event causes an overlap
   */
  void add(IEvent event, LocalDateTime newStart) throws InvalidEvent;

  /**
   * Tries to add the event to this class' events and applies the timezone difference to the event
   * updating its times.
   *
   * @param event       the event to add and modifies it's timezone
   * @param newStart    the new start date of the event in this calendar's timezone
   * @param oldTimeZone the old time zone of event being added
   * @throws InvalidEvent if the event causes an overlap
   */
  void add(IEvent event, LocalDate newStart, TimeZone oldTimeZone) throws InvalidEvent;

  /**
   * Tries to add the event to this class' events and applies the timezone difference to the event
   * updating its times. It applies an offset on the date based on the original event's date
   * difference relative to the relativeTo date.
   *
   * @param event       The event to add and modify
   * @param newStart    The new start date to base the date off of
   * @param relativeTo  The date the original event should base the offset off of
   * @param oldTimeZone The old time zone of the event being added
   * @throws InvalidEvent if th event causes an overlap
   */
  void add(IEvent event, LocalDate newStart, LocalDate relativeTo, TimeZone oldTimeZone)
      throws InvalidEvent;

  /**
   * Tries to find the event in this calendar that has the given subject and start time.
   *
   * @param subject the subject of the event
   * @param start   the start time of the event
   * @return the found event or null if not found
   */
  IEvent queryExactEvent(String subject, LocalDateTime start);

  /**
   * Gets this calendar's name.
   *
   * @return the name of this calendar
   */
  String getName();

  /**
   * Gets this calendar's timezone.
   *
   * @return the timezone of this calendar
   */
  TimeZone getTimeZone();

}
