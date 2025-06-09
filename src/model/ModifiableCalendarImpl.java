package model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import model.CalendarExceptions.InvalidEvent;

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
    this.tz = newTimeZone;
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
    attemptToAddEvent(event);
  }

  private Duration getTZDifference(LocalDate date, TimeZone oldTimeZone) {
    ZoneId oldTZ = oldTimeZone.toZoneId();
    ZoneId newTZ = this.tz.toZoneId();
    ZonedDateTime oldTime = ZonedDateTime.of(date.atStartOfDay(), oldTZ);
    ZonedDateTime newTime = ZonedDateTime.of(date.atStartOfDay(), newTZ);
    return Duration.between(oldTime, newTime);
  }

  @Override
  public void add(IEvent event, LocalDate newStart, LocalDate relativeTo, TimeZone oldTimeZone)
      throws InvalidEvent {
    Duration tzDiff = getTZDifference(newStart, oldTimeZone);
    IEvent tempEvent = event.getEdittedCopy("tzAndRelativeDateChange", relativeTo.toString() + "/" +
        newStart.toString() + "/" + tzDiff.toString());
    attemptToAddEvent(event);
  }

  @Override
  public IEvent queryExactEvent(String subject, LocalDateTime start) {
    IEvent found;
    for (IEvent event : events) {
      found = event.getExactMatch(subject, start);
      if (found != null) {
        return found;
      }
    }
    return null;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public TimeZone getTimeZone() {
    return tz;
  }

  private void attemptToAddEvent(IEvent event) {
    if (eventAlreadyExists(event)) {
      throw new InvalidEvent("Event already exists");
    }
    else {
      this.events.add(event);
    }
  }
}
