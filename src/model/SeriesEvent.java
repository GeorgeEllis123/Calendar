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


  protected SeriesEvent(ArrayList<IEvent> events, String subject) {
    super(events, subject);
  }
}
