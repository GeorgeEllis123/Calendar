package mocks;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import model.CalendarModel;
import model.IEvent;

/**
 * Represents a mock model to test the calendar mode.
 */
public class MockCalendarModel implements CalendarModel {
  public boolean testBoolean = true;
  public ArrayList<IEvent> events = new ArrayList<>();

  public ArrayList<String> log = new ArrayList<>();
  public boolean wasAddSingleEventCalled = false;
  public boolean wasEditSingleEventCalled = false;
  public String lastSubject;
  public LocalDateTime lastStart;
  public LocalDateTime lastEnd;
  public LocalDate lastQueriedDate;
  public boolean failOnAdd;
  public boolean throwOnEdit;

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
}