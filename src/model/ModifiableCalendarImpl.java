package model;

public class ModifiableCalendarImpl extends CalendarModelImpl implements ModifiableCalendar {

  private TimeZone tz;
  private String name;

  @Override
  public void editName(String newName) {
    this.name = newName;
  }

  @Override
  public void editTimeZone(TimeZone newTimeZone) {
    this.tz = newTimeZone;
  }

  @Override
  public void add(TZEvent event, TimeZone prevTimeZone) {
    //TODO: Look at JavaDocs
  }

  @Override
  public String getName() {
    return name;
  }
}
