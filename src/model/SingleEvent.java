package model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a single event used for a Calendar like Google Calendar.
 */
public class SingleEvent implements IEvent {

  // Required
  protected String subject;
  protected LocalDateTime startDateTime;

  // Optional
  protected LocalDateTime endDateTime;
  protected Location location;
  protected Status status;
  protected String description;

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
    if (subject == null || startDateTime == null) {
      throw new IllegalArgumentException("Subject and Start Date cannot be null");
    } else if ((endDateTime != null) && (endDateTime.isBefore(startDateTime))) {
      throw new IllegalArgumentException("End Date cannot be before Start Date");
    }
    this.subject = subject;

    // If the end date and time is null then the start time becomes 8am and the end time
    // becomes 5pm.
    if (endDateTime == null) {
      this.startDateTime = LocalDateTime.of(startDateTime.toLocalDate(), LocalTime.of(8, 0));
      this.endDateTime = LocalDateTime.of(startDateTime.toLocalDate(), LocalTime.of(17, 0));
    } else {
      this.startDateTime = startDateTime;
      this.endDateTime = endDateTime;
    }
  }

  // Constructor for the builder class
  private SingleEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime,
                      Location location, Status status, String description)
      throws IllegalArgumentException {
    if (subject == null || startDateTime == null) {
      throw new IllegalArgumentException("Subject and Start Date cannot be null");
    } else if ((endDateTime != null) && (endDateTime.isBefore(startDateTime))) {
      throw new IllegalArgumentException("End Date cannot be before Start Date");
    }
    this.subject = subject;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.location = location;
    this.status = status;
    this.description = description;
  }

  /**
   * The builder class for a Single Event.
   */
  public static class SingleEventBuilder {
    private String subject;
    protected LocalDateTime startDateTime;
    protected LocalDateTime endDateTime;
    private Location location;
    private Status status;
    private String description;

    /**
     * Public constructor for the SingleEventBuilder class.
     *
     * @param subject       the subject line of the Event.
     * @param startDateTime the date and time of the start of the event.
     * @param endDateTime   the date and time of the end of the event.
     * @throws IllegalArgumentException when the subject line or the start
     *                                  time and date of the event is null.
     */
    public SingleEventBuilder(String subject, LocalDateTime startDateTime,
                              LocalDateTime endDateTime)
        throws IllegalArgumentException {
      if (subject == null || startDateTime == null) {
        throw new IllegalArgumentException("Subject and Start Date cannot be null");
      } else if ((endDateTime != null) && (endDateTime.isBefore(startDateTime))) {
        throw new IllegalArgumentException("End Date cannot be before Start Date");
      }
      this.subject = subject;

      // If the end date and time is null then the start time becomes 8am and
      // the end time becomes 5pm.
      if (endDateTime == null) {
        this.startDateTime = LocalDateTime.of(startDateTime.toLocalDate(),
            LocalTime.of(8, 0));
        this.endDateTime = LocalDateTime.of(startDateTime.toLocalDate(),
            LocalTime.of(17, 0));
      } else {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
      }
    }

    /**
     * Public constructor for the SingleEventBuilder class.
     *
     * @param singleEvent A prexisting singleEvent to copy all of its properties.
     */
    public SingleEventBuilder(SingleEvent singleEvent) {
      this.subject = singleEvent.subject;
      this.startDateTime = singleEvent.startDateTime;
      this.endDateTime = singleEvent.endDateTime;
      this.location = singleEvent.location;
      this.status = singleEvent.status;
    }

    /**
     * Changes the subject of the event.
     *
     * @return the Event with the new subject.
     */
    public SingleEventBuilder changeSubject(String subject) {
      this.subject = subject;
      return this;
    }

    /**
     * Changes the start of the event.
     *
     * @return the Event with the new start.
     */
    public SingleEventBuilder changeStart(LocalDateTime startDateTime) {
      this.startDateTime = startDateTime;
      return this;
    }

    /**
     * Changes the end of the event.
     *
     * @return the Event with the new end.
     */
    public SingleEventBuilder changeEnd(LocalDateTime endDateTime) {
      this.endDateTime = endDateTime;
      return this;
    }

    /**
     * Makes the location of the event as in person.
     *
     * @return the Event with the location as Physical.
     */
    public SingleEventBuilder isInPerson() {
      this.location = Location.PHYSICAL;
      return this;
    }

    /**
     * Makes the location of the event online.
     *
     * @return the Event with the location as online.
     */
    public SingleEventBuilder isOnline() {
      this.location = Location.ONLINE;
      return this;
    }

    /**
     * Makes the status of the event private.
     *
     * @return the Event with the status as private.
     */
    public SingleEventBuilder isPrivate() {
      this.status = Status.PRIVATE;
      return this;
    }

    /**
     * Makes the status of the event public.
     *
     * @return the Event with the status as public.
     */
    public SingleEventBuilder isPublic() {
      this.status = Status.PUBLIC;
      return this;
    }

    /**
     * Sets a new description as the description.
     *
     * @return the Event with the input description.
     */
    public SingleEventBuilder setDescription(String description) {
      this.description = description;
      return this;
    }

    /**
     * Builds a new SingleEvent with the altered fields.
     *
     * @return a new SingleEvent.
     */
    public SingleEvent build() {
      return new SingleEvent(subject, startDateTime, endDateTime, location, status, description);
    }
  }

  /**
   * Presents the details of a SingleEvent.
   *
   * @return a String with the details of the given event.
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append("- ").append(subject)
        .append(" | ").append(startDateTime)
        .append(" to ").append(endDateTime);

    if (location != null) {
      sb.append(" | Location: ").append(location);
    }
    if (status != null) {
      sb.append(" | Status: ").append(status);
    }
    if (description != null && !description.isBlank()) {
      sb.append(" | Description: ").append(description);
    }
    return sb.toString();
  }

  /**
   * Checks whether the event overlaps the given DateTime.
   *
   * @param dateTime The time to check if it overlaps with
   * @return Whether the date overlaps with the DateTime
   */
  @Override
  public boolean containsDateTime(LocalDateTime dateTime) {
    return (dateTime.isEqual(startDateTime) || dateTime.isAfter(startDateTime)) &&
        (dateTime.isEqual(endDateTime) || dateTime.isBefore(endDateTime));
  }

  /**
   * Checks whether the event is on the given date.
   *
   * @param date The date to check
   * @return This event if it is on the given date otherwise null
   */
  @Override
  public IEvent getIfEventIsOnDate(LocalDate date) {
    LocalDate startDate = startDateTime.toLocalDate();
    LocalDate endDate = endDateTime.toLocalDate();

    if (date.isEqual(startDate) || date.isEqual(endDate) ||
        (date.isAfter(startDate) && date.isBefore(endDate))) {
      return this;
    }
    return null;
  }

  /**
   * Checks whether the event is between two DateTimes.
   *
   * @param startTime The lower date range to check
   * @param endTime   The upper date range to check
   * @return This event if it is between the two date times otherwise null
   */
  @Override
  public IEvent getIfBetween(LocalDateTime startTime, LocalDateTime endTime) {
    if ((startDateTime.isEqual(startTime) || startDateTime.isAfter(startTime)) &&
        (endDateTime.isEqual(endTime) || endDateTime.isBefore(endTime))) {
      return this;
    }
    return null;
  }

  /**
   * Checks whether the passed Event is a duplicate of this event or any other events it contains.
   * An event is a duplicate if it has the exact same subject, start, and end times.
   *
   * @param newEvent The event to check if it already exists.
   * @return Whether the event already exists.
   */
  @Override
  public boolean checkDuplicate(IEvent newEvent) {
    return this.equals(newEvent.getExactMatch(this.subject, this.startDateTime, this.endDateTime));
  }

  public String getSubject() {
    return subject;
  }

  @Override
  public IEvent getAllMatchingEventsAfter(String subject, LocalDateTime start) {
    return getExactMatch(subject, start, this.endDateTime);
  }

  @Override
  public IEvent getAllMatchingEvents(String subject, LocalDateTime start) {
    return getExactMatch(subject, start, this.endDateTime);
  }

  @Override
  public IEvent getExactMatch(String subject, LocalDateTime start, LocalDateTime end) {
    if (this.subject.equals(subject) && this.startDateTime.equals(start)
        && this.endDateTime.equals(end)) {
      return this;
    }
    return null;
  }

  @Override
  public IEvent getExactMatch(String subject, LocalDateTime start) {
    return getExactMatch(subject, start, this.endDateTime);
  }

  @Override
  public IEvent getEdittedCopy(String property, String newProperty)
      throws IllegalArgumentException {
    return applyEditToBuilder(property, newProperty).build();
  }

  @Override
  public void editEvent(String property, String newProperty) throws IllegalArgumentException {
    SingleEventBuilder builder = applyEditToBuilder(property, newProperty);
    SingleEvent updated = builder.build();

    this.subject = updated.subject;
    this.startDateTime = updated.startDateTime;
    this.endDateTime = updated.endDateTime;
    this.description = updated.description;
    this.location = updated.location;
    this.status = updated.status;
  }

  @Override
  public LocalDateTime getStart() {
    return this.startDateTime;
  }

  /**
   * Overrides the Java Equals to determine if two Objects are equal.
   *
   * @param other The Object that will be checked for equality against the passed in event.
   * @return Whether the two Objects are equal.
   */
  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }

    SingleEvent otherEvent = (SingleEvent) other;

    return this.subject.equals(otherEvent.subject)
        && this.startDateTime.equals(otherEvent.startDateTime)
        && this.endDateTime.equals(otherEvent.endDateTime);
  }

  /**
   * Determine equality between two Objects.
   *
   * @return an int that represents the equality of two objects.
   */
  @Override
  public int hashCode() {
    return Objects.hash(subject, startDateTime, endDateTime);
  }

  private SingleEventBuilder applyEditToBuilder(String property, String newProperty) {
    SingleEventBuilder builder;
    Duration betweenStartAndEnd = Duration.between(this.startDateTime, this.endDateTime);

    if (property.equals("subject")) {
      builder = new SingleEventBuilder(this.subject, this.startDateTime, this.endDateTime)
          .changeSubject(newProperty);
    } else {
      builder = new SingleEventBuilder(this);
      switch (property) {
        case "start":
          try {
            builder = builder.changeStart(LocalDateTime.parse(newProperty));
          } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date time format");
          }
          break;
        case "end":
          try {
            builder = builder.changeEnd(LocalDateTime.parse(newProperty));
          } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date time format");
          }
          break;
        case "description":
          builder = builder.setDescription(newProperty);
          break;
        case "location":
          switch (newProperty) {
            case "online":
              builder = builder.isOnline();
              break;
            case "physical":
              builder = builder.isInPerson();
              break;
            default:
              throw new IllegalArgumentException("Invalid location property must be one of" +
                  "'online', 'physical'");
          }
          break;
        case "status":
          switch (newProperty) {
            case "public":
              builder = builder.isPublic();
              break;
            case "private":
              builder = builder.isPrivate();
              break;
            default:
              throw new IllegalArgumentException("Invalid status property must be one of" +
                  "'public', 'private'");
          }
          break;
        case "endWithStart": {
          LocalDateTime newStart = LocalDateTime.parse(newProperty);
          builder = builder.changeStart(newStart).changeEnd(newStart.plus(betweenStartAndEnd));
          break;
        }
        case "tzAndDateChange": {
          String[] info = newProperty.split("/");
          LocalDate newDate = LocalDate.parse(info[0]);
          Duration diff = Duration.parse(info[1]);
          LocalDateTime newStart = newDate.atTime(this.startDateTime.toLocalTime());
          newStart = newStart.plus(diff);
          builder = builder.changeStart(newStart).changeEnd(newStart.plus(betweenStartAndEnd));
          break;
        }
        case "tzAndRelativeDateChange": {
          String[] info = newProperty.split("/");
          LocalDate relativeTo = LocalDate.parse(info[0]);
          LocalDate newDate = LocalDate.parse(info[1]);
          Duration diff = Duration.parse(info[2]);
          LocalTime newTime = this.startDateTime.toLocalTime().plus(diff);
          long daysBetween = ChronoUnit.DAYS.between(relativeTo, this.startDateTime.toLocalDate());
          LocalDateTime newStart = newDate.plusDays(daysBetween).atTime(newTime);
          builder = builder.changeStart(newStart).changeEnd(newStart.plus(betweenStartAndEnd));
          break;
          }
        default:
          throw new IllegalArgumentException("Unknown property: " + property);
      }
    }

    return builder;
  }
}
