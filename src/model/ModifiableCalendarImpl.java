package model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.TimeZone;

import model.exceptions.InvalidEvent;

/**
 * Represents the implementation of a Google Calendar-like calendar has a timezone, can add single
 * and repeating events, edit events, and query events, and copy events.
 */
public class ModifiableCalendarImpl extends CalendarModelImpl implements ModifiableCalendar {

  private TimeZone tz;
  private String name;

  public ModifiableCalendarImpl(String calendarName, TimeZone tz) {
    this.tz = tz;
    this.name = calendarName;
  }

  @Override
  public void editName(String newName) {
    this.name = newName;
  }

  @Override
  public void editTimeZone(TimeZone newTimeZone) {
    TimeZone oldTimeZone = tz;
    this.tz = newTimeZone;
    ArrayList<IEvent> updatedEvents = new ArrayList<>();
    for (IEvent event : this.events) {
      Duration tzDiff = getTZDifference(event.getStart().toLocalDate(), oldTimeZone);
      updatedEvents.add(event.getEdittedCopy("tzAndDateChange",
          event.getStart().toLocalDate().toString() + "/" + tzDiff.toString()));
    }
    this.events.clear();
    this.events.addAll(updatedEvents);
  }

  @Override
  public void add(IEvent event, LocalDateTime newStart) throws InvalidEvent {
    IEvent tempEvent = event.getEdittedCopy("endWithStart", newStart.toString());
    attemptToAddEvent(tempEvent);
  }

  @Override
  public void add(IEvent event, LocalDate newStartDate, TimeZone oldTimeZone)
      throws InvalidEvent {
    Duration tzDiff = getTZDifference(newStartDate, oldTimeZone);
    IEvent tempEvent = event.getEdittedCopy("tzAndDateChange",
        newStartDate.toString() + "/" + tzDiff.toString());
    attemptToAddEvent(tempEvent);
  }

  @Override
  public void add(IEvent event, LocalDate newStart, LocalDate relativeTo, TimeZone oldTimeZone)
      throws InvalidEvent {
    Duration tzDiff = getTZDifference(newStart, oldTimeZone);
    IEvent tempEvent = event.getEdittedCopy("tzAndRelativeDateChange", relativeTo.toString() + "/" +
        newStart.toString() + "/" + tzDiff.toString());
    attemptToAddEvent(tempEvent);
  }

  private Duration getTZDifference(LocalDate date, TimeZone oldTimeZone) {
    ZoneId oldTZ = oldTimeZone.toZoneId();
    ZoneId newTZ = this.tz.toZoneId();
    ZonedDateTime oldTime = ZonedDateTime.of(date.atStartOfDay(), oldTZ);
    ZonedDateTime newTime = ZonedDateTime.of(date.atStartOfDay(), newTZ);
    return Duration.between(newTime, oldTime);
  }

  @Override
  public IEvent queryExactEvent(String subject, LocalDateTime start) throws InvalidEvent {
    IEvent found;
    IEvent r = null;
    for (IEvent event : events) {
      found = event.getExactMatch(subject, start);
      if (found != null) {
        if (r == null) {
          r = found;
        }
        else {
          throw new InvalidEvent("There are more than event with that subject and start!");
        }
      }
    }
    return r;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public TimeZone getTimeZone() {
    return tz;
  }

  private void attemptToAddEvent(IEvent event) throws InvalidEvent {
    if (eventAlreadyExists(event)) {
      throw new InvalidEvent("Event already exists");
    }
    else {
      this.events.add(event);
    }
  }
}
