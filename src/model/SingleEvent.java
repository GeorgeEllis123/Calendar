package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a single event used for a Calendar like Google Calendar.
 */
public class SingleEvent extends AbstractSingle implements IEvent {
  /**
   * Constructs the event.
   *
   * @param subject       The subject of the event
   * @param startDateTime The start time of the event
   * @param endDateTime   The end time of the event (may span multiple days)
   * @throws IllegalArgumentException If the start time is after the end time
   */
  public SingleEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime)
      throws IllegalArgumentException {
    super(subject, startDateTime, endDateTime);
  }

}
