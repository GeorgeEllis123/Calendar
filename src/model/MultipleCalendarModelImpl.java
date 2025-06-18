package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import model.exceptions.InvalidCalendar;
import model.exceptions.InvalidEvent;
import model.exceptions.InvalidProperty;
import model.exceptions.InvalidTimeZoneFormat;
import model.exceptions.NoCalendar;

/**
 * Represents the implementation of a Google Calendar-like calendar that can handle multiple
 * calendars each of which have a timezone, can add single and repeating events, edit events, and
 * query events, and copy events.
 */
public class MultipleCalendarModelImpl implements MultipleCalendarModel {
  private List<ModifiableCalendar> calendars;
  private ModifiableCalendar currentCalendar;

  public MultipleCalendarModelImpl() {
    this.calendars = new ArrayList<>();
    this.currentCalendar = null;
  }

  @Override
  public boolean addSingleEvent(String subject, LocalDateTime start, LocalDateTime end) {
    return currentCalendar.addSingleEvent(subject, start, end);
  }

  @Override
  public boolean addRepeatingEvent(String subject, LocalDateTime start, LocalDateTime end,
                                   String weekdays, int count) {
    return currentCalendar.addRepeatingEvent(subject, start, end, weekdays, count);
  }

  @Override
  public boolean addRepeatingEvent(String subject, LocalDateTime start, LocalDateTime end,
                                   String weekdays, LocalDate endDate) {
    return currentCalendar.addRepeatingEvent(subject, start, end, weekdays, endDate);
  }

  @Override
  public boolean editSingleEvent(String subject, LocalDateTime start, LocalDateTime end,
                                 String property, String newProperty) {
    return currentCalendar.editSingleEvent(subject, start, end, property, newProperty);
  }

  @Override
  public boolean editFutureSeriesEvents(String subject, LocalDateTime start,
                                        String property, String newProperty) {
    return currentCalendar.editFutureSeriesEvents(subject, start, property, newProperty);
  }

  @Override
  public boolean editEntireSeries(String subject, LocalDateTime start,
                                  String property, String newProperty) {
    return currentCalendar.editEntireSeries(subject, start, property, newProperty);
  }

  @Override
  public ArrayList<IEvent> queryEvent(LocalDate date) {
    return currentCalendar.queryEvent(date);
  }

  @Override
  public ArrayList<IEvent> queryEvent(LocalDateTime startTime, LocalDateTime endTime) {
    return currentCalendar.queryEvent(startTime, endTime);
  }

  @Override
  public boolean getStatus(LocalDateTime dateTime) {
    return currentCalendar.getStatus(dateTime);
  }

  @Override
  public void create(String calendarName, String timezone) throws InvalidProperty,
      InvalidTimeZoneFormat {
    TimeZone tz;
    try {
      tz = parseTimeZone(timezone);
    } catch (InvalidTimeZoneFormat e) {
      throw new InvalidProperty(e.getMessage());
    }
    if (findCalendar(calendarName) != null) {
      throw new InvalidProperty("Calendar with name " + calendarName + " already exists");
    } else {
      calendars.add(new ModifiableCalendarImpl(calendarName, tz));
    }
  }

  @Override
  public void edit(String calendarName, String property, String newProperty) throws InvalidProperty,
      InvalidCalendar {
    ModifiableCalendar calendarToEdit = findCalendar(calendarName);
    if (calendarToEdit == null) {
      throw new InvalidCalendar("Could not find" + calendarName);
    }

    switch (property) {
      case "name":
        if (findCalendar(newProperty) != null) {
          throw new InvalidProperty("Calendar with name " + newProperty + " already exists");
        } else {
          calendarToEdit.editName(newProperty);
        }
        break;
      case "timezone":
        TimeZone tz;
        try {
          tz = parseTimeZone(newProperty);
        } catch (InvalidTimeZoneFormat e) {
          throw new InvalidProperty(e.getMessage());
        }
        calendarToEdit.editTimeZone(tz);
        break;
      default:
        throw new InvalidProperty("Invalid property. Should be one of 'name' or 'timezone'");
    }
  }

  // Attempts to parse the given string into a timezone ID throws an exception if does not exist
  private TimeZone parseTimeZone(String timezone) throws InvalidTimeZoneFormat {
    for (String id : TimeZone.getAvailableIDs()) {
      if (id.equals(timezone)) {
        return TimeZone.getTimeZone(id);
      }
    }
    throw new InvalidTimeZoneFormat("Could not find timezone with id " + timezone);
  }

  // Attempts to find the calendar with the given name in its list returns null if can't find
  private ModifiableCalendar findCalendar(String calendarName) {
    for (ModifiableCalendar calendar : calendars) {
      if (calendar.getName().equals(calendarName)) {
        return calendar;
      }
    }
    return null;
  }

  @Override
  public void use(String calendarName) throws InvalidCalendar {
    ModifiableCalendar c = findCalendar(calendarName);
    if (c == null) {
      throw new InvalidCalendar("Could not find" + calendarName);
    }
    this.currentCalendar = c;
  }

  @Override
  public void copyEvent(String eventName, LocalDateTime start, String calendarName,
                        LocalDateTime newStart) throws InvalidCalendar, InvalidEvent, NoCalendar {
    if (currentCalendar == null) {
      throw new NoCalendar("You must have an active calendar to copy");
    }
    ModifiableCalendar targetCalendar = findCalendar(calendarName);
    if (targetCalendar == null) {
      throw new InvalidCalendar("Could not find " + calendarName);
    }
    IEvent event = currentCalendar.queryExactEvent(eventName, start);

    if (event == null) {
      throw new InvalidEvent("Could not find " + eventName + " @ " + start.toString());
    }

    targetCalendar.add(event, newStart);
  }

  @Override
  public boolean copyEvents(LocalDate date, String calendarName, LocalDate toDate)
      throws InvalidCalendar, NoCalendar {
    if (currentCalendar == null) {
      throw new NoCalendar("You must have an active calendar to copy");
    }
    ModifiableCalendar targetCalendar = findCalendar(calendarName);
    if (targetCalendar == null) {
      throw new InvalidCalendar("Could not find " + calendarName);
    }
    ArrayList<IEvent> events = currentCalendar.queryEvent(date);
    int numberAdded = events.size();
    for (IEvent event : events) {
      try {
        targetCalendar.add(event, toDate, currentCalendar.getTimeZone());
      } catch (InvalidEvent e) {
        numberAdded--;
      }
    }
    return numberAdded > 0;
  }

  @Override
  public boolean copyEvents(LocalDate start, LocalDate end, String calendarName,
                            LocalDate newStart) throws InvalidCalendar, NoCalendar {
    if (currentCalendar == null) {
      throw new NoCalendar("You must have an active calendar to copy");
    }
    ModifiableCalendar targetCalendar = findCalendar(calendarName);
    if (targetCalendar == null) {
      throw new InvalidCalendar("Could not find " + calendarName);
    }
    ArrayList<IEvent> events = currentCalendar.queryEvent(start.atTime(LocalTime.MIN),
        end.atTime(LocalTime.MAX));
    int numberAdded = events.size();
    for (IEvent event : events) {
      try {
        targetCalendar.add(event, newStart, start, currentCalendar.getTimeZone());
      } catch (InvalidEvent e) {
        numberAdded--;
      }
    }
    return numberAdded > 0;
  }

  @Override
  public ModifiableCalendar getCurrentCalendar() {
    if (currentCalendar != null) {
      return this.currentCalendar;
    }
    return null;
  }

  @Override
  public List<ModifiableCalendar> getCalendars() {
    return calendars;
  }
}
