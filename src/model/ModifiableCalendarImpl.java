package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.TimeZone;

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
  public void add(IEvent event, LocalDateTime newStart) {
    //TODO: Look at JavaDocs. For updating the start time we can just use the builder
  }

  @Override
  public void add(IEvent event, LocalDate newStartDate, TimeZone oldTimeZone) {
    //TODO: Look at JavaDocs. For updating the timezone i think we can just use the builder like
    // we did before.
  }

  @Override
  public IEvent queryExactEvent(String subject, LocalDateTime start) {
    return null;
    //TODO: implement... maybe use getExactEvent but would need access to the end time...
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public TimeZone getTimeZone() {
    return tz;
  }
}
