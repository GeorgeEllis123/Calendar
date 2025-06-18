package mocks;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import model.exceptions.InvalidCalendar;
import model.exceptions.InvalidEvent;
import model.exceptions.InvalidProperty;
import model.exceptions.InvalidTimeZoneFormat;
import model.exceptions.NoCalendar;
import model.IEvent;
import model.ModifiableCalendar;
import model.ModifiableCalendarImpl;
import model.MultipleCalendarModel;

/**
 * Represents a mock model to test the calendar mode.
 */
public class MockMultipleCalendarModel implements MultipleCalendarModel {
  public boolean testBoolean = true;
  public ArrayList<IEvent> events = new ArrayList<>();
  public ModifiableCalendar currentCal;
  public ArrayList<String> log = new ArrayList<>();
  public String name;
  public ArrayList<ModifiableCalendar> multipleCalendarModels = new ArrayList<>();
  public boolean failOnAdd;
  public boolean wasAddSingleEventCalled;
  public String lastSubject;
  public LocalDateTime lastStart;
  public LocalDateTime lastEnd;
  public boolean wasEditSingleEventCalled;
  public LocalDate lastQueriedDate;
  public boolean throwOnEdit;

  @Override
  public boolean addSingleEvent(String subject, LocalDateTime start, LocalDateTime end) {
    wasAddSingleEventCalled = true;
    lastSubject = subject;
    lastStart = start;
    lastEnd = end;
    log.add("addSingleEvent:" + subject);
    return !failOnAdd;
  }

  @Override
  public boolean addRepeatingEvent(String subject, LocalDateTime start,
                                   LocalDateTime end, String weekdays, int count) {
    log.add("addRepeatingEventWithCount:" + subject);
    return testBoolean;
  }

  @Override
  public boolean addRepeatingEvent(String subject, LocalDateTime start,
                                   LocalDateTime end, String weekdays, LocalDate until) {
    log.add("addRepeatingEventWithUntil:" + subject);
    return testBoolean;
  }

  @Override
  public ArrayList<IEvent> queryEvent(LocalDate date) {
    lastQueriedDate = date;
    log.add("queryEvent:" + date);
    return events;
  }

  @Override
  public ArrayList<IEvent> queryEvent(LocalDateTime start, LocalDateTime end) {
    log.add("queryEvent:" + start + " to " + end);
    return events;
  }

  @Override
  public boolean getStatus(LocalDateTime dateTime) {
    log.add("getStatus:" + dateTime);
    return testBoolean;
  }

  @Override
  public boolean editSingleEvent(String subject, LocalDateTime start,
                                 LocalDateTime end, String property, String newValue) {
    if (throwOnEdit) {
      throw new IllegalArgumentException("Simulated edit failure");
    }
    log.add("editSingleEvent:" + subject);
    wasEditSingleEventCalled = true;
    return testBoolean;
  }

  @Override
  public boolean editFutureSeriesEvents(String subject, LocalDateTime start,
                                        String property, String newValue) {
    log.add("editFutureSeries:" + subject);
    return testBoolean;
  }

  @Override
  public boolean editEntireSeries(String subject, LocalDateTime start,
                                  String property, String newValue) {
    log.add("editEntireSeries:" + subject);
    return testBoolean;
  }

  @Override
  public void create(String calendarName, String timezone) throws InvalidProperty,
      InvalidTimeZoneFormat {
    log.add("create:" + calendarName + ":" + timezone);

    if (calendarName.equals("Work")) {
      throw new InvalidProperty("Calendar with name Work already exists");
    } else if (timezone.equals("fail")) {
      throw new InvalidProperty("Could not find timezone with id fail");
    }
  }

  @Override
  public void edit(String calendarName, String property, String newProperty)
      throws InvalidProperty, InvalidCalendar {
    log.add("edit:" + calendarName + ":" + property + ":" + newProperty);

    if (calendarName.equals("MyCal")) {
      throw new InvalidCalendar("Could not find MyCal");
    } else if (newProperty.equals("fail2")) {
      throw new InvalidProperty("Calendar with name fail already exists");
    } else if (property.equals("fail")) {
      throw new InvalidProperty("Invalid property. Should be one of 'name' or 'timezone'");
    } else if (newProperty.equals("fail") && property.equals("timezone")) {
      throw new InvalidProperty("Could not find timezone with id fail");
    }

  }

  @Override
  public void use(String calendarName) throws InvalidCalendar {
    log.add("use:" + calendarName);

    this.currentCal = new ModifiableCalendarImpl(calendarName,
        TimeZone.getTimeZone("EST"));

    if (calendarName.equals("MyCal")) {
      throw new InvalidCalendar("Could not find MyCal");
    }
  }

  @Override
  public void copyEvent(String eventName, LocalDateTime start, String calendarName,
                        LocalDateTime newStart) throws InvalidCalendar, InvalidEvent, NoCalendar {
    log.add("copyEvent:" + eventName + ":" + start + ":" + calendarName + ":" + newStart);

    if (calendarName.equals("fail")) {
      throw new InvalidCalendar("Could not find fail");
    } else if (eventName.equals("babysit")) {
      throw new InvalidEvent("Could not find babysit @ 2025-06-05");
    } else if (eventName.equals("party")) {
      throw new InvalidEvent("Adding this event would cause an overlap");
    }
  }

  @Override
  public boolean copyEvents(LocalDate date, String calendarName, LocalDate toDate)
      throws InvalidCalendar, NoCalendar {
    log.add("copyEvents:" + date + ":" + calendarName + ":" + toDate);

    if (calendarName.equals("fail")) {
      throw new InvalidCalendar("Could not find fail");
    }

    return testBoolean;
  }

  @Override
  public boolean copyEvents(LocalDate start, LocalDate end, String calendarName,
                            LocalDate newStart) throws InvalidCalendar, NoCalendar {
    log.add("copyEvents:" + start + ":" + end + ":" + calendarName + ":" + newStart);

    if (start.equals("fail")) {
      throw new InvalidProperty("Invalid date time format! Should be: yyyy-MM-ddTHH:mm");
    } else if (calendarName.equals("Orange")) {
      throw new NoCalendar("Could not find Orange");
    }

    return testBoolean;
  }

  @Override
  public ModifiableCalendar getCurrentCalendar() {
    log.add("getCurrentCalendar");
    return this.currentCal;
  }

  @Override
  public List<ModifiableCalendar> getCalendars() {
    return List.of();
  }
}