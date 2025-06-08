package model;

import java.time.LocalDateTime;

public class TZSingle extends AbstractSingle implements TZEvent {
    TimeZone tz;

    /**
     * Constructs the event.
     *
     * @param subject       The subject of the event
     * @param startDateTime The start time of the event
     * @param endDateTime   The end time of the event (may span multiple days)
     * @throws IllegalArgumentException If the start time is after the end time
     */
    public TZSingle(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                    TimeZone tz) throws IllegalArgumentException {
        super(subject, startDateTime, endDateTime);
        this.tz = tz;
    }

  @Override
  public void changeTimeZone(TimeZone newTimeZone) {

  }
}
