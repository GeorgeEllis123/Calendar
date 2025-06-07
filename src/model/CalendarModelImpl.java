package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Represents the implementation of all the methods needed for a Google Calendar
 * like Calendar that can add single and repeating events, edit them, and query them.
 */
public class CalendarModelImpl implements CalendarModel {
  private final ArrayList<IEvent> events;

  /**
   * A public constructor for the CalendarModelImpl class.
   */
  public CalendarModelImpl() {
    this.events = new ArrayList<IEvent>();
  }

  /**
   * Attempts to add a single event to the calendar.
   *
   * @param subject The subject of the event
   * @param start   A LocalDateTime of when the event should start
   * @param end     A LocalDateTime of when the event should end
   * @return Whether the single event was actually added, an event with a matching subject,
   *         start date, and end date cannot be added
   */
  @Override
  public boolean addSingleEvent(String subject, LocalDateTime start, LocalDateTime end) {
    SingleEvent possibleEvent = new SingleEvent(subject, start, end);
    if (!eventAlreadyExists(possibleEvent)) {
      this.events.add(possibleEvent);
      return true;
    }
    return false;
  }

  /**
   * Edits a SingleEvent.
   *
   * @param subject     The subject of the event that the user intends to edit.
   * @param start       The start date time of the event that the user intends to edit.
   * @param end         The end date time of the event that the user intends to edit.
   * @param property    Which field the user intends to change.  Must be either "subject", "start",
   *                    "end", "location", "description", or "status" to be a valid property.
   * @param newProperty What the user would like to change the field's information to.
   * @return whether the event could be changed.
   */
  @Override
  public boolean editSingleEvent(String subject, LocalDateTime start, LocalDateTime end,
                                 String property, String newProperty) {
    ArrayList<IEvent> toEdit = new ArrayList<>();
    for (IEvent event : this.events) {
      toEdit.addAll(event.getExactMatch(subject, start, end));
    }
    return attemptToEdit(property, newProperty, toEdit);
  }

  /**
   * Edits a SeriesEvent starting with the passed in event, and removing those events from the
   * original series.
   *
   * @param subject     The subject of the event that the user intends to edit.
   * @param start       The start date time of the event that the user intends to edit.
   * @param property    Which field the user intends to change.  Must be either "subject", "start",
   *                    "end", "location", "description", or "status" to be a valid property.
   * @param newProperty What the user would like to change the field's information to.
   * @return whether the event could be changed.
   */
  @Override
  public boolean editFutureSeriesEvents(String subject, LocalDateTime start, String property,
                                        String newProperty) {
    return helpEditSeries(subject, start, property, newProperty, "after", this.events);
  }

  /**
   * Edits an entire SeriesEvent.
   *
   * @param subject     The subject of the event that the user intends to edit.
   * @param start       The start date time of the event that the user intends to edit.
   * @param property    Which field the user intends to change.  Must be either "subject", "start",
   *                    "end", "location", "description", or "status" to be a valid property.
   * @param newProperty What the user would like to change the field's information to.
   * @return whether the event could be changed.
   */
  @Override
  public boolean editEntireSeries(String subject, LocalDateTime start, String property,
                                  String newProperty) {
    return helpEditSeries(subject, start, property, newProperty, "all", this.events);
  }

  //the helper method that edits a series either from the date given or the start date
  private boolean helpEditSeries(String subject, LocalDateTime start, String property,
                                 String newProperty, String addOrAfter, ArrayList<IEvent> events) {

    ArrayList<IEvent> toEdit = new ArrayList<>();
    for (IEvent event : events) {
      if (addOrAfter.equals("all")) {
        toEdit.addAll(event.getAllMatchingEventsAfter(subject, start));
      } else {
        toEdit.addAll(event.getAllMatchingEventsAfter(subject, start));
      }

    }
    return attemptToEdit(property, newProperty, toEdit);

  }


  /**
   * Attempts to add a series of events that repeat on the given weekdays for a given number of
   * times.
   *
   * @param subject  The subject of the events
   * @param start    When to consider starting to add the events as well as the time they will
   *                 occur
   * @param end      The end time the events will have
   * @param weekdays Which weekdays to repeat on in the form of a string containing the characters
   *                 MTWRFSU to represent the days
   * @param count    The amount of times to repeat the given event
   * @return Whether the series event was actually added, if any of the events have a
   *         matching subject, start date, and end date, of a preexisting event the whole
   *         series will not be added
   */
  @Override
  public boolean addRepeatingEvent(String subject, LocalDateTime start, LocalDateTime end,
                                   String weekdays, int count) {
    if (count <= 0) {
      return false;
    }
    SeriesEvent possibleEvent;
    try {
      possibleEvent = new SeriesEvent(subject, start, end, weekdays, count);
    } catch (IllegalArgumentException e) {
      return false;
    }

    return addSeriesIfNotDuplicated(possibleEvent);
  }

  /**
   * Attempts to add a series of events that repeat on the given weekdays until a given date.
   *
   * @param subject  The subject of the events
   * @param start    When to consider starting to add the events as well as the time they will
   *                 occur
   * @param end      The end time the events will have
   * @param weekdays Which weekdays to repeat on in the form of a string containing the characters
   *                 MTWRFSU to represent the days
   * @param endDate  The date it should stop trying to add days until
   * @return Whether the series event was actually added, if any of the events have a
   *         matching subject, start date, and end date, of a preexisting event the whole
   *         series will not be added
   */
  @Override
  public boolean addRepeatingEvent(String subject, LocalDateTime start, LocalDateTime end,
                                   String weekdays, LocalDate endDate) {
    if (start.toLocalDate().isAfter(endDate)) {
      return false;
    }
    SeriesEvent possibleEvent;
    try {
      possibleEvent = new SeriesEvent(subject, start, end, weekdays, endDate);
    } catch (IllegalArgumentException e) {
      return false;
    }
    return addSeriesIfNotDuplicated(possibleEvent);
  }

  /**
   * Finds all events on the given date.
   *
   * @param date The date to check
   * @return All the events found on that date (if any)
   */
  @Override
  public ArrayList<IEvent> queryEvent(LocalDate date) {
    ArrayList<IEvent> r = new ArrayList<IEvent>();
    for (IEvent event : this.events) {
      r.addAll(event.getIfEventIsOnDate(date));
    }
    return r;
  }

  /**
   * Finds all events between a range of days/times.
   *
   * @param startTime The DateTime lower bound
   * @param endTime   The DateTime upper bound
   * @return All the events found on that date (if any)
   */
  @Override
  public ArrayList<IEvent> queryEvent(LocalDateTime startTime, LocalDateTime endTime) {
    ArrayList<IEvent> r = new ArrayList<IEvent>();
    for (IEvent event : this.events) {
      r.addAll(event.getIfBetween(startTime, endTime));
    }
    return r;
  }

  /**
   * Checks whether an event overlaps with the given DateTime.
   *
   * @param dateTime The DateTime to check for overlap with
   * @return All the events found on that date (if any)
   */
  @Override
  public boolean getStatus(LocalDateTime dateTime) {
    for (IEvent event : this.events) {
      if (event.containsDateTime(dateTime)) {
        return true;
      }
    }
    return false;
  }

  // Checks if the given event already exists
  private boolean eventAlreadyExists(IEvent newEvent) {
    for (IEvent event : this.events) {
      if (event.checkDuplicate(newEvent)) {
        return true;
      }
    }
    return false;
  }

  // Adds a series event if none of its events will overlap with pre-existing events
  private boolean addSeriesIfNotDuplicated(SeriesEvent possibleEvent) {
    for (IEvent event : possibleEvent.getEvents()) {
      if (eventAlreadyExists(event)) {
        return false;
      }
    }
    events.add(possibleEvent);
    return true;
  }

  // Attempts to edit a list of events
  private boolean attemptToEdit(String property, String newProperty, ArrayList<IEvent> toEdit) {
    if (toEdit.isEmpty()) {
      return false;
    }

    if (property.equals("subject") || property.equals("start") || property.equals("end")) {
      // checks to make sure there won't be any duplicates made bc of this
      ArrayList<IEvent> potentialEvents = new ArrayList<>();
      for (IEvent event : toEdit) {
        IEvent newEvent;
        try {
          newEvent = event.getEdittedCopy(property, newProperty);
        } catch (IllegalArgumentException e) {
          return false;
        }

        if (eventAlreadyExists(newEvent)) {
          return false;
        }
        potentialEvents.add(newEvent);
      }
    }

    // actually applies the edits
    for (IEvent event : toEdit) {
      try {
        event.editEvent(property, newProperty);
      } catch (IllegalArgumentException e) {
        return false;
      }
    }
    return true;
  }
}
