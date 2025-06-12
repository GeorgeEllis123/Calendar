package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Represents all the methods needed for a Google Calendar-like calendar that can add single and
 * repeating events, edit them, and query them.
 */
public interface CalendarModel {

  /**
   * Attempts to add a single event to the calendar.
   *
   * @param subject The subject of the event
   * @param start   A LocalDateTime of when the event should start
   * @param end     A LocalDateTime of when the event should end
   * @return Whether the single event was actually added. An event with a matching subject,
   *     start date, and end date cannot be added
   */
  boolean addSingleEvent(String subject, LocalDateTime start, LocalDateTime end);

  /**
   * Attempts to add a series of events that repeat on the given weekdays for a given number of
   * times.
   *
   * @param subject   The subject of the events
   * @param start     When to consider starting to add the events as well as the time they will
   *     occur
   * @param end       The end time the events will have
   * @param weekdays  Which weekdays to repeat on in the form of a string containing the characters
   *     MTWRFSU to represent the days
   * @param count     The number of times to repeat the given event
   * @return Whether the series event was actually added. If any of the events have a
   *     matching subject, start date, and end date of a preexisting event, the whole
   *     series will not be added
   */
  boolean addRepeatingEvent(String subject, LocalDateTime start, LocalDateTime end,
                            String weekdays, int count);

  /**
   * Attempts to add a series of events that repeat on the given weekdays until a given date.
   *
   * @param subject   The subject of the events
   * @param start     When to consider starting to add the events as well as the time they will
   *     occur
   * @param end       The end time the events will have
   * @param weekdays  Which weekdays to repeat on in the form of a string containing the characters
   *     MTWRFSU to represent the days
   * @param endDate   The date it should stop trying to add events at or before
   * @return Whether the series event was actually added. If any of the events have a
   *     matching subject, start date, and end date of a preexisting event, the whole
   *     series will not be added
   */
  boolean addRepeatingEvent(String subject, LocalDateTime start, LocalDateTime end,
                            String weekdays, LocalDate endDate);

  /**
   * Edits a SingleEvent.
   *
   * @param subject     The subject of the event that the user intends to edit
   * @param start       The start date and time of the event that the user intends to edit
   * @param end         The end date and time of the event that the user intends to edit
   * @param property    The field the user intends to change. Must be either "subject", "start",
   *     "end", "location", "description", or "status" to be valid
   * @param newProperty The new value the user would like to set for the field
   * @return Whether the event could be changed
   */
  boolean editSingleEvent(String subject, LocalDateTime start, LocalDateTime end, String property,
                          String newProperty);

  /**
   * Edits a SeriesEvent starting from the specified event and separates it from the original
   * series.
   *
   * @param subject     The subject of the event that the user intends to edit
   * @param start       The start date and time of the event that the user intends to edit
   * @param property    The field the user intends to change. Must be either "subject", "start",
   *     "end", "location", "description", or "status" to be valid
   * @param newProperty The new value the user would like to set for the field
   * @return Whether the event could be changed
   */
  boolean editFutureSeriesEvents(String subject, LocalDateTime start, String property,
                                 String newProperty);

  /**
   * Edits an entire SeriesEvent.
   *
   * @param subject     The subject of the event that the user intends to edit
   * @param start       The start date and time of the event that the user intends to edit
   * @param property    The field the user intends to change. Must be either "subject", "start",
   *     "end", "location", "description", or "status" to be valid
   * @param newProperty The new value the user would like to set for the field
   * @return Whether the event could be changed
   */
  boolean editEntireSeries(String subject, LocalDateTime start, String property,
                           String newProperty);

  /**
   * Finds all events on the given date.
   *
   * @param date The date to check
   * @return All the events found on that date (if any)
   */
  HashSet<IEvent> queryEvent(LocalDate date);

  /**
   * Finds all events between a range of days/times.
   *
   * @param startTime The DateTime lower bound
   * @param endTime   The DateTime upper bound
   * @return All the events found in the given range (if any)
   */
  HashSet<IEvent> queryEvent(LocalDateTime startTime, LocalDateTime endTime);

  /**
   * Checks whether an event overlaps with the given DateTime.
   *
   * @param dateTime The DateTime to check for overlap
   * @return True if an event overlaps, false otherwise
   */
  boolean getStatus(LocalDateTime dateTime);
}
