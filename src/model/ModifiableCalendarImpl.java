package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
  public void add(IEvent event, LocalDate newStartDate, TimeZone oldTimeZone) {
    //IEvent tempEvent = event.getEdittedCopy("startAndEndDate", );
    attemptToAddEvent(event);
  }

  @Override
  public IEvent queryExactEvent(String subject, LocalDateTime start) {
    ArrayList<IEvent> found = new ArrayList<IEvent>();
    for (IEvent event : events) {
      found.addAll(event.getExactMatch(subject, start));
    }
    //TODO: Figure out this exact logic
    if (found.isEmpty()) {
      return null;
    } else if (found.size() > 1) {
      return null;
    } else {
      return found.get(0);
    }
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
