package mocks;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

import model.CalendarModel;
import model.IEvent;

/**
 * Represents a mock model to test the calendar mode.
 */
public class MockCalendarModel implements CalendarModel {
  public boolean testBoolean = true;
  public HashSet<IEvent> events = new HashSet<>();

  public HashSet<String> log = new HashSet<>();

  @Override
  public boolean addSingleEvent(String subject, LocalDateTime start, LocalDateTime end) {
    log.add("addSingleEvent:" + subject);
    return testBoolean;
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
  public HashSet<IEvent> queryEvent(LocalDate date) {
    log.add("queryEvent:" + date);
    return events;
  }

  @Override
  public HashSet<IEvent> queryEvent(LocalDateTime start, LocalDateTime end) {
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
    log.add("editSingleEvent:" + subject);
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