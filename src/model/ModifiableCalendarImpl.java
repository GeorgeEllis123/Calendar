package model;

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
  public void add(IEvent event, TimeZone prevTimeZone) {
    //TODO: Look at JavaDocs. For updating the timezone i think we can just use the builder like
    // we did before.
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
