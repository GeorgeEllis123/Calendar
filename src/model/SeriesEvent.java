package model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a series of repeating events used for a Calendar like Google Calendar.
 */
public class SeriesEvent extends AbstractSeries implements IEvent {

  /**
   * Constructor used to create a series of events that repeats until a given date.
   *
   * @param subject       The subject each of these events should have.
   * @param startDateTime When to consider starting to add the events as well as the time they will
   *                      occur
   * @param endDateTime   The end time the events will have
   * @param weekdays      Which weekdays to repeat on in the form of a string containing
   *                      the characters MTWRFSU to represent the days
   * @param untilDate     The date it should stop trying to add days until
   * @throws IllegalArgumentException If the start date is after the end date
   * @throws IllegalArgumentException If any of the weekday chars are not valid
   */
  public SeriesEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                     String weekdays, LocalDate untilDate) {
    super(subject, startDateTime, endDateTime, weekdays, untilDate);
  }

  /**
   * Constructor used to create a series of events that repeats a given number of times.
   *
   * @param subject       The subject each of these events should have.
   * @param startDateTime When to consider starting to add the events as well as the time they will
   *                      occur
   * @param endDateTime   The end time the events will have
   * @param weekdays      Which weekdays to repeat on in the form of a string containing the
   *                      characters MTWRFSU to represent the days
   * @param repeatTimes   The number of times the event should repeat.
   * @throws IllegalArgumentException If the start date is after the end date
   * @throws IllegalArgumentException If any of the weekday chars are not valid
   */
  public SeriesEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                     String weekdays, int repeatTimes) throws IllegalArgumentException {
    super(subject, startDateTime, endDateTime, weekdays, repeatTimes);
  }

}
