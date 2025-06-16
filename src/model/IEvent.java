package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents an Event that would be used for a Calendar. The user can create, edit, and
 * query these events.
 */
public interface IEvent {

  /**
   * Checks whether the event overlaps the given DateTime.
   *
   * @param dateTime The time to check if it overlaps with
   * @return Whether the date overlaps with the DateTime
   */
  boolean containsDateTime(LocalDateTime dateTime);

  /**
   * Checks whether the event is on the given date.
   *
   * @param date The date to check
   * @return Whether the event is on the given date
   */
  IEvent getIfEventIsOnDate(LocalDate date);

  /**
   * Checks whether the event is between two DateTimes.
   *
   * @param startTime The lower date range to check
   * @param endTime   The upper date range to check
   * @return Whether the event between the two DateTimes.
   */
  IEvent getIfBetween(LocalDateTime startTime, LocalDateTime endTime);

  /**
   * Checks whether the passed Event is a duplicate of this event or any other events it contains.
   * An event is a duplicate if it has the exact same subject, start, and end times.
   *
   * @param newEvent The event to check if it already exists.
   * @return Whether the event already exists.
   */
  boolean checkDuplicate(IEvent newEvent);

  /**
   * Overrides the Java Equals to determines if two Objects are Equal.
   *
   * @param o The Object that will be checked for equality against the passed in event.
   * @return Whether the two Objects are equal.
   */
  boolean equals(Object o);

  /**
   * Determine equality between two Objects.
   *
   * @return an int that represents the equality of two objects.
   */
  int hashCode();

  /**
   * If the event contains at least 1 event that on the matching start time and has the matching
   * subject it will get that event and any subsequent events after that.
   *
   * @param subject The subject to search for
   * @param start   The start datetime to search for
   * @return All the events after and including the found event (if any)
   */
  IEvent getAllMatchingEventsAfter(String subject, LocalDateTime start);

  /**
   * If the event contains at least 1 event that on the matching start time and has the matching
   * subject it will get all the events associated with it.
   *
   * @param subject The subject to search for
   * @param start   The start datetime to search for
   * @return All the events associated with the found event (if any)
   */
  IEvent getAllMatchingEvents(String subject, LocalDateTime start);

  /**
   * If the event exactly matches the given subject, start time, and end time it will return it.
   *
   * @param subject The subject to search for
   * @param start   The start datetime to search for
   * @param end     The end datetime to search for
   * @return The matching event or null if no match is found
   */
  IEvent getExactMatch(String subject, LocalDateTime start, LocalDateTime end);

  /**
   * If the event exactly matches the given subject and start time it will return it.
   *
   * @param subject The subject to search for
   * @param start   The start datetime to search for
   * @return The matching event or null if no match is found
   */
  IEvent getExactMatch(String subject, LocalDateTime start);

  /**
   * Creates a copy of this event but with the passed property changed to the passed value.
   *
   * @param property    The property to change one of: subject, start, end, description, location,
   *                    status
   * @param newProperty The new value the property should have
   * @return            The editted copy of this event
   * @throws IllegalArgumentException if the property or newProperty are not valid properties or
   *                                  property values respectively
   */
  IEvent getEdittedCopy(String property, String newProperty) throws IllegalArgumentException;

  /**
   * Edits the actual event changing its information to match the passed property and value.
   *
   * @param property    The property to change one of: subject, start, end, description, location,
   *                    status
   * @param newProperty The new value the property should have
   * @throws IllegalArgumentException if the property or newProperty are not valid properties or
   *                                  property values respectively
   */
  void editEvent(String property, String newProperty) throws IllegalArgumentException;

  /**
   * Gets the start date of this event if possible.
   *
   * @return the start date of this event
   */
  LocalDateTime getStart();

  LocalDateTime getEnd();

  String getSubject();
}
