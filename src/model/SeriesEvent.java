package model;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a series of repeating events used for a Calendar like Google Calendar.
 */
public class SeriesEvent implements IEvent {
  public ArrayList<IEvent> events;
  String subject;

  protected SeriesEvent(ArrayList<IEvent> events, String subject) {
    this.events = events;
    this.subject = subject;

  }

  /**
   * Checks whether the event overlaps the given DateTime.
   *
   * @param dateTime The time to check if it overlaps with
   * @return Whether the date overlaps with the DateTime
   */
  @Override
  public boolean containsDateTime(LocalDateTime dateTime) {
    for (IEvent event : events) {
      if (event.containsDateTime(dateTime)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks whether the event is on the given date.
   *
   * @param date The date to check
   * @return Whether the event is on the given date
   */
  @Override
  public IEvent getIfEventIsOnDate(LocalDate date) {
    ArrayList<IEvent> r = new ArrayList<>();

    for (IEvent event : events) {
      IEvent toAdd = event.getIfEventIsOnDate(date);
      if (toAdd != null) {
        r.add(toAdd);
      }
    }
    if (r.isEmpty()) {
      return null;
    }
    return new SeriesEvent(r, subject);
  }

  /**
   * Checks whether the event is between two DateTimes.
   *
   * @param startTime The lower date range to check
   * @param endTime   The upper date range to check
   * @return Whether the event between the two DateTimes.
   */
  @Override
  public IEvent getIfBetween(LocalDateTime startTime, LocalDateTime endTime) {
    ArrayList<IEvent> r = new ArrayList<>();

    for (IEvent event : events) {
      IEvent toAdd = event.getIfBetween(startTime, endTime);
      if (toAdd != null) {
        r.add(toAdd);
      }
    }
    if (r.isEmpty()) {
      return null;
    }
    return new SeriesEvent(r, subject);
  }

  /**
   * Checks whether the passed Event is a duplicate of this event or any other events it contains.
   * An event is a duplicate if it has the exact same subject, start, and end times.
   *
   * @param newEvent The event to check if it already exists.
   * @return Whether the event already exists.
   */
  @Override
  public boolean checkDuplicate(IEvent newEvent) {
    for (IEvent event : events) {
      if (event.checkDuplicate(newEvent)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Gets all the events this event contains.
   *
   * @return All the events this event contains
   */
  public ArrayList<IEvent> getEvents() {
    return events;
  }

  /**
   * Constructor used to create a series of events that repeats until a given date.
   *
   * @param subject       The subject each of these events should have.
   * @param startDateTime When to consider starting to add the events as well as the time they will
   *                      occur
   * @param endDateTime   The end time the events will have
   * @param weekdays      Which weekdays to repeat on in the form of a string containing
   *                      the characters MTWRFSU to represent the days
   * @param untilDate     The date it should stop trying to add days until
   * @throws IllegalArgumentException If the start date is after the end date
   * @throws IllegalArgumentException If any of the weekday chars are not valid
   */
  public SeriesEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                     String weekdays, LocalDate untilDate) {
    if (startDateTime.isAfter(endDateTime)) {
      throw new IllegalArgumentException("Start date cannot be after end date");
    }
    checkValidWeekdays(weekdays);

    this.events = new ArrayList<>();
    this.subject = subject;
    LocalDateTime current = startDateTime;

    while (!current.toLocalDate().isAfter(untilDate)) {
      char dayChar = getDayChar(current.getDayOfWeek());

      if (weekdays.indexOf(dayChar) != -1) {
        LocalDateTime thisEnd = current.withHour(endDateTime.getHour())
            .withMinute(endDateTime.getMinute());
        events.add(new SingleEvent(subject, current, thisEnd));
      }

      current = current.plusDays(1);
    }
  }

  /**
   * Constructor used to create a series of events that repeats a given number of times.
   *
   * @param subject       The subject each of these events should have.
   * @param startDateTime When to consider starting to add the events as well as the time they will
   *                      occur
   * @param endDateTime   The end time the events will have
   * @param weekdays      Which weekdays to repeat on in the form of a string containing the
   *                      characters MTWRFSU to represent the days
   * @param repeatTimes   The number of times the event should repeat.
   * @throws IllegalArgumentException If the start date is after the end date
   * @throws IllegalArgumentException If any of the weekday chars are not valid
   */
  public SeriesEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                     String weekdays, int repeatTimes) throws IllegalArgumentException {
    if (startDateTime.isAfter(endDateTime)) {
      throw new IllegalArgumentException("Start date cannot be after end date");
    }
    checkValidWeekdays(weekdays);
    this.events = new ArrayList<>();
    this.subject = subject;

    LocalDateTime current = startDateTime;
    int count = 0;

    while (count < repeatTimes) {
      char dayChar = getDayChar(current.getDayOfWeek());

      if (weekdays.indexOf(dayChar) != -1) {
        LocalDateTime thisEnd = current.withHour(endDateTime.getHour())
            .withMinute(endDateTime.getMinute());
        events.add(new SingleEvent(subject, current, thisEnd));
        count++;
      }

      current = current.plusDays(1);
    }
  }

  @Override
  public IEvent getAllMatchingEventsAfter(String subject, LocalDateTime start) {
    boolean searching = true;
    ArrayList<IEvent> newEvents = new ArrayList<>();
    for (IEvent event : events) {
      if (searching) {
        if (event.getAllMatchingEventsAfter(subject, start) != null) {
          newEvents.add(event);
        };
      }
      else {
        newEvents.add(event);
      }
      if (!newEvents.isEmpty()) {
        searching = false;
      }
    }
    if (newEvents.isEmpty()) {
      return null;
    }
    return new SeriesEvent(newEvents, subject);
  }

  @Override
  public IEvent getAllMatchingEvents(String subject, LocalDateTime start) {
    for (IEvent event : events) {
      if (event.getAllMatchingEvents(subject, start) != null) {
        return this;
      }
    }
    return null;
  }

  @Override
  public IEvent getExactMatch(String subject, LocalDateTime start, LocalDateTime end) {
    for (IEvent event : events) {
      IEvent foundEvent = event.getExactMatch(subject, start, end);
      if (foundEvent != null) {
        return foundEvent;
      }
    }
    return null;
  }

  @Override
  public IEvent getExactMatch(String subject, LocalDateTime start) {
    for (IEvent event : events) {
      IEvent foundEvent = event.getExactMatch(subject, start);
      if (foundEvent != null) {
        return foundEvent;
      }
    }
    return null;
  }

  @Override
  public IEvent getEdittedCopy(String property, String newProperty) {
    ArrayList<IEvent> newEvents = new ArrayList<>();
    LocalDate prev = events.get(0).getStart().toLocalDate();
    for (IEvent event : events) {
      newProperty = accountForOffsetIfNeeded(event, property, newProperty, prev);
      prev = event.getStart().toLocalDate();
      newEvents.add(event.getEdittedCopy(property, newProperty));
    }
    return new SeriesEvent(newEvents, this.subject);
  }

  private String accountForOffsetIfNeeded(IEvent e, String property, String newProperty, LocalDate prevStart) {
    switch (property) {
      case "endWithStart": {
        LocalDateTime newStart = LocalDateTime.parse(newProperty);
        long daysBetween = ChronoUnit.DAYS.between(prevStart, e.getStart().toLocalDate());
        return newStart.plusDays(daysBetween).toString();
      }
      case "tzAndDateChange": {
        String[] info = newProperty.split("/");
        LocalDate newStart = LocalDate.parse(info[0]);
        long daysBetween = ChronoUnit.DAYS.between(prevStart, e.getStart().toLocalDate());
        return newStart.plusDays(daysBetween).toString() + "/" + info[1];
      }
      default:
        return newProperty;
    }
  }

  @Override
  public void editEvent(String property, String newProperty) {
    for (IEvent event : events) {
      event.editEvent(property, newProperty);
    }
  }

  @Override
  public LocalDateTime getStart() {
    return events.get(0).getStart();
  }

  // Converts a day into its respective character representation
  private char getDayChar(DayOfWeek day) {
    switch (day) {
      case MONDAY:
        return 'M';
      case TUESDAY:
        return 'T';
      case WEDNESDAY:
        return 'W';
      case THURSDAY:
        return 'R';
      case FRIDAY:
        return 'F';
      case SATURDAY:
        return 'S';
      case SUNDAY:
        return 'U';
      default:
        throw new IllegalArgumentException("Invalid day: " + day);
    }
  }

  /**
   * Overrides the Java Equals to determines if two Objects are Equal.
   * @param other The Object that will be checked for equality against the passed in event.
   * @return Whether the two Objects are equal.
   */
  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }

    SeriesEvent otherEvent = (SeriesEvent) other;

    return Objects.equals(events, otherEvent.events);
  }

  /**
   * Determine equality between two Objects.
   * @return an int that represents the equality of two objects.
   */
  @Override
  public int hashCode() {
    return Objects.hash(events);
  }

  // throws an exception if any of the characters in the string are not valid
  private void checkValidWeekdays(String weekdays) {
    for (char c : weekdays.toCharArray()) {
      if ("MTWRFSU".indexOf(c) == -1) {
        throw new IllegalArgumentException("Invalid weekday character: " + c);
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (IEvent event : events) {
      sb.append(event.toString());
      sb.append("\n");
    }
    return sb.toString();
  }

}
