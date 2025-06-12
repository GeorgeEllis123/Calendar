// AbstractCalendarModelTest.java

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

import model.CalendarModel;
import model.IEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Abstract test class containing common tests for CalendarModel implementations.
 */
public abstract class ACalendarTest {

  protected CalendarModel model;
  protected LocalDateTime start;
  protected LocalDateTime end;

  /**
   * Gets the model to use.
   */
  protected abstract CalendarModel getCalendarModel();

  @Before
  public void setUp() {
    model = getCalendarModel();
    start = LocalDateTime.of(2025, 6, 5, 9, 0);
    end = LocalDateTime.of(2025, 6, 5, 10, 0);
  }

  @Test
  public void testAddSingleEventSuccessfully() {
    assertTrue(model.addSingleEvent("Meeting", start, end));
    assertEquals(1, model.queryEvent(LocalDate.of(2025, 6, 5)).size());
  }

  @Test
  public void testAddSingleEventDuplicate() {
    assertTrue(model.addSingleEvent("Meeting", start, end));
    assertFalse(model.addSingleEvent("Meeting", start, end));
  }

  @Test
  public void testAddRepeatingEventByCountSuccessfully() {
    assertTrue(model.addRepeatingEvent("Class", start, end, "MWF", 3));
    HashSet<IEvent> events = model.queryEvent(LocalDate.of(2025, 6, 6));
    assertEquals(1, events.size());
  }

  @Test
  public void testAddRepeatingEventByCountDuplicate() {
    assertTrue(model.addRepeatingEvent("Class", start, end, "MWF", 3));
    assertFalse(model.addRepeatingEvent("Class", start, end, "MWF", 3));
  }

  @Test
  public void testAddRepeatingEventByUntilDateSuccessfully() {
    LocalDate until = LocalDate.of(2025, 6, 13);
    assertTrue(model.addRepeatingEvent("Training", start, end, "MWF", until));
    assertEquals(3, model.queryEvent(LocalDate.of(2025, 6, 9)).size() +
        model.queryEvent(LocalDate.of(2025, 6, 11)).size() +
        model.queryEvent(LocalDate.of(2025, 6, 13)).size());
  }

  @Test
  public void testAddRepeatingEventByUntilOnFakeDay() {
    LocalDate until = LocalDate.of(2025, 6, 13);
    assertFalse(model.addRepeatingEvent("Training", start, end, "PG", until));
  }

  @Test
  public void testAddRepeatingEventByCountOnFakeDay() {
    assertFalse(model.addRepeatingEvent("Class", start, end, "BZ", 3));
  }

  @Test
  public void testAddRepeatingEventByNegativeCount() {
    assertFalse(model.addRepeatingEvent("Class", start, end, "MW", -1));
  }

  @Test
  public void testAddRepeatingEventByZero() {
    assertFalse(model.addRepeatingEvent("Class", start, end, "MW", 0));
  }

  @Test
  public void testAddRepeatingEventByUntilDayBeforeStart() {
    LocalDate until = LocalDate.of(2022, 12, 13);
    assertFalse(model.addRepeatingEvent("Training", start, end, "MW", until));
  }

  @Test
  public void testAddRepeatingEventByUntilDateDuplicate() {
    LocalDate until = LocalDate.of(2025, 6, 13);
    assertTrue(model.addRepeatingEvent("Training", start, end, "MWF", until));
    assertFalse(model.addRepeatingEvent("Training", start, end, "MWF", until));
  }

  @Test
  public void testAddRepeatingEventByCountSomeDuplicate() {
    assertTrue(model.addRepeatingEvent("Class", start, end, "MWF", 3));
    assertFalse(model.addRepeatingEvent("Class", start, end, "MWU", 3));
  }

  @Test
  public void testAddRepeatingEventByUntilDateSomeDuplicate() {
    LocalDate until = LocalDate.of(2025, 6, 13);
    assertTrue(model.addRepeatingEvent("Training", start, end, "MWF", until));
    assertFalse(model.addRepeatingEvent("Training", start, end, "MWU", until));
  }

  @Test
  public void testQueryEventByDateNoMatch() {
    HashSet<IEvent> result = model.queryEvent(LocalDate.of(2025, 6, 7));
    assertTrue(result.isEmpty());
  }

  @Test
  public void testQueryEventByTimeRangeReturnsCorrect() {
    model.addSingleEvent("Meeting", start, end);
    LocalDateTime rangeStart = start.minusHours(1);
    LocalDateTime rangeEnd = end.plusHours(1);
    HashSet<IEvent> result = model.queryEvent(rangeStart, rangeEnd);
    assertEquals(1, result.size());
  }

  @Test
  public void testQueryEventByTimeRangeReturnsEmpty() {
    LocalDateTime rangeStart = start.minusHours(1);
    LocalDateTime rangeEnd = start.minusMinutes(1);
    HashSet<IEvent> result = model.queryEvent(rangeStart, rangeEnd);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testGetStatusWhenBusy() {
    model.addSingleEvent("Call", start, end);
    assertTrue(model.getStatus(start.plusMinutes(30)));
  }

  @Test
  public void testGetStatusWhenFree() {
    model.addSingleEvent("Call", start, end);
    assertFalse(model.getStatus(start.minusHours(1)));
  }

  @Test
  public void testGetStatusOnBoundaryStart() {
    model.addSingleEvent("Call", start, end);
    assertTrue(model.getStatus(start));
  }

  @Test
  public void testGetStatusOnBoundaryEnd() {
    model.addSingleEvent("Call", start, end);
    assertTrue(model.getStatus(end));
  }

  @Test
  public void testMultipleEventsQueryAndStatus() {
    model.addSingleEvent("Meeting", start, end);
    model.addRepeatingEvent("Lecture", start, end, "MWF", 3);
    assertEquals(1, model.queryEvent(LocalDate.from(start)).size());
    assertTrue(model.getStatus(start.plusMinutes(15)));
  }

  @Test
  public void testEmptyCalendarStatusAlwaysFalse() {
    assertFalse(model.getStatus(start));
  }

  @Test
  public void testEmptyCalendarQueryAlwaysEmpty() {
    assertTrue(model.queryEvent(start.toLocalDate()).isEmpty());
    assertTrue(model.queryEvent(start, end).isEmpty());
  }

  @Test
  public void testAddSingleEventOverlappingWithSeriesEvent() {
    boolean addedSeries = model.addRepeatingEvent("Weekly Class", start, end, "MWF", 3);
    assertTrue(addedSeries);

    // One day after cause monday is the 6th
    boolean addedSingle = model.addSingleEvent("Weekly Class",
        LocalDateTime.of(2025, 6, 6, 9, 0),
        LocalDateTime.of(2025, 6, 6, 10, 0));
    assertFalse(addedSingle);
  }

  @Test
  public void testAddSeriesOverlappingWithSingleEvent() {
    boolean addedSingle = model.addSingleEvent("Weekly Class",
        LocalDateTime.of(2025, 6, 6, 9, 0),
        LocalDateTime.of(2025, 6, 6, 10, 0));
    assertTrue(addedSingle);

    boolean addedSeries = model.addRepeatingEvent("Weekly Class", start, end, "MWF", 3);
    assertFalse(addedSeries);
  }

  @Test
  public void editSingleEventThatExistsEditSubject() {
    model.addSingleEvent("Class", start, end);
    boolean editSingleEvent = model.editSingleEvent("Class", start, end, "subject",
        "Test");
    assertTrue(editSingleEvent);
  }

  @Test
  public void editSingleEventThatExistsEditStartDateTime() {
    model.addSingleEvent("Class", start, end);
    boolean editSingleEvent = model.editSingleEvent("Class", start, end, "start",
        (start.plusMinutes(3)).toString());
    assertTrue(editSingleEvent);
  }

  @Test
  public void editSingleEventThatExistsEditEndDateTime() {
    model.addSingleEvent("Class", start, end);
    boolean editSingleEvent = model.editSingleEvent("Class", start, end, "end",
        (end.plusHours(3)).toString());
    assertTrue(editSingleEvent);
  }

  @Test
  public void editSingleEventThatExistsEditDescription() {
    model.addSingleEvent("Class", start, end);
    boolean editSingleEvent = model.editSingleEvent("Class", start, end, "description",
        ("boring"));
    assertTrue(editSingleEvent);
  }

  @Test
  public void editSingleEventThatExistsEditLocationPhysical() {
    model.addSingleEvent("Class", start, end);
    boolean editSingleEvent = model.editSingleEvent("Class", start, end, "location",
        ("physical"));
    assertTrue(editSingleEvent);
  }

  @Test
  public void editSingleEventThatExistsEditLocationOnline() {
    model.addSingleEvent("Class", start, end);
    boolean editSingleEvent = model.editSingleEvent("Class", start, end, "location",
        ("online"));
    assertTrue(editSingleEvent);
  }

  @Test
  public void editSingleEventThatExistsEditLocationNonLocation() {
    model.addSingleEvent("Class", start, end);
    boolean editSingleEvent = model.editSingleEvent("Class", start, end, "location",
        ("home"));
    assertFalse(editSingleEvent);
  }

  @Test
  public void editSingleEventThatExistsEditStatusPrivate() {
    model.addSingleEvent("Class", start, end);
    boolean editSingleEvent = model.editSingleEvent("Class", start, end, "status",
        ("private"));
    assertTrue(editSingleEvent);
  }

  @Test
  public void editSingleEventThatExistsEditStatusPublic() {
    model.addSingleEvent("Class", start, end);
    boolean editSingleEvent = model.editSingleEvent("Class", start, end, "status",
        ("public"));
    assertTrue(editSingleEvent);
  }

  @Test
  public void editSingleEventThatExistsEditStatusNonStatus() {
    model.addSingleEvent("Class", start, end);
    boolean editSingleEvent = model.editSingleEvent("Class", start, end, "location",
        ("busy"));
    assertFalse(editSingleEvent);
  }

  @Test
  public void editSingleEventThatExistsEditNonProperty() {
    model.addSingleEvent("Class", start, end);
    boolean editSingleEvent = model.editSingleEvent("Class", start, end, "goodbye",
        ("public"));
    assertFalse(editSingleEvent);
  }

  @Test
  public void editSingleEventThatDoesntExist() {
    boolean editSingleEvent = model.editSingleEvent("Class", start, end, "goodbye",
        ("public"));
    assertFalse(editSingleEvent);
  }

  @Test
  public void editFutureSeriesEvents() {
    model.addRepeatingEvent("Meeting", start, end, "MWF", 10);
    boolean editFutureSeriesEvent = model.editFutureSeriesEvents("Meeting",
        LocalDateTime.of(2025, 6, 9, 9, 0), "subject",
        "Homework");
    assertTrue(editFutureSeriesEvent);
  }

  @Test
  public void editFutureSeriesEventsOnSingleEvent() {
    model.addSingleEvent("Meeting", start, end);
    boolean editFutureSeriesEvent = model.editFutureSeriesEvents("Meeting",
        start, "subject",
        "Homework");
    assertTrue(editFutureSeriesEvent);
  }

  @Test
  public void editEntireSeriesEventsOnSingleEvent() {
    model.addSingleEvent("Meeting", start, end);
    boolean editFutureSeriesEvent = model.editFutureSeriesEvents("Meeting",
        start, "subject",
        "Homework");
    assertTrue(editFutureSeriesEvent);
  }

  @Test
  public void editEntireSeriesEventsOnSeriesEvent() {
    model.addRepeatingEvent("Meeting", start, end, "MWF", 10);
    boolean editFutureSeriesEvent = model.editFutureSeriesEvents("Meeting",
        start.plusDays(1), "subject",
        "Homework");
    assertTrue(editFutureSeriesEvent);
  }

  @Test
  public void testAddMultipleEventsAndQueryByDate() {
    model.addSingleEvent("Meeting", start, end);
    model.addSingleEvent("Workout", start.plusHours(2), end.plusHours(2));
    model.addSingleEvent("Lunch", start.plusHours(4), end.plusHours(4));

    HashSet<IEvent> result = model.queryEvent(LocalDate.of(2025, 6, 5));
    assertEquals(3, result.size());
  }

  @Test
  public void testAddMultipleEventsAndQueryByRange() {
    model.addSingleEvent("Meeting", start, end);
    model.addSingleEvent("Workout", start.plusHours(2), end.plusHours(2));
    model.addSingleEvent("Lunch", start.plusHours(4), end.plusHours(4));

    HashSet<IEvent> result = model.queryEvent(start, end.plusHours(5));
    assertEquals(3, result.size());
  }

  @Test
  public void testEditCorrectSingleEventAmongMany() {
    model.addSingleEvent("Meeting", start, end);
    LocalDateTime workoutStart = start.plusHours(2);
    LocalDateTime workoutEnd = end.plusHours(2);
    model.addSingleEvent("Workout", workoutStart, workoutEnd);
    model.addSingleEvent("Lunch", start.plusHours(4), end.plusHours(4));

    boolean edited = model.editSingleEvent("Workout", workoutStart, workoutEnd, "subject", "Gym");
    assertTrue(edited);

    HashSet<IEvent> result = model.queryEvent(workoutStart.toLocalDate());
    boolean foundUpdated = result.stream().anyMatch(e -> e.toString().contains("Gym"));
    assertTrue(foundUpdated);
  }

  @Test
  public void testEditWrongSingleEventAmongMany() {
    model.addSingleEvent("Meeting", start, end);
    model.addSingleEvent("Workout", start.plusHours(2), end.plusHours(2));
    model.addSingleEvent("Lunch", start.plusHours(4), end.plusHours(4));

    boolean edited = model.editSingleEvent("Nap", start.plusHours(2), end.plusHours(2), "subject",
        "Rest");
    assertFalse(edited);
  }

  @Test
  public void testAddMultipleSeriesEventsAndQuery() {
    model.addRepeatingEvent("Class A", start, end, "MWF", 3);
    model.addRepeatingEvent("Class B", start.plusHours(2), end.plusHours(2), "TR", 2);

    HashSet<IEvent> mondayEvents = model.queryEvent(LocalDate.of(2025, 6, 9));
    assertEquals(1, mondayEvents.stream().filter(e -> e.toString().contains("Class A")).count());

    HashSet<IEvent> tuesdayEvents = model.queryEvent(LocalDate.of(2025, 6, 10));
    assertEquals(1, tuesdayEvents.stream().filter(e -> e.toString().contains("Class B")).count());
  }

  @Test
  public void testEditFutureSeriesAmongMultipleSeries() {
    model.addRepeatingEvent("Class A", start, end, "MWF", 5);
    model.addRepeatingEvent("Class B", start.plusHours(2), end.plusHours(2), "TR", 5);

    boolean editClassAFuture = model.editFutureSeriesEvents("Class A", start.plusDays(4),
        "subject", "Updated A");
    assertTrue(editClassAFuture);

    HashSet<IEvent> futureEvents = model.queryEvent(LocalDate.of(2025, 6, 11));
    boolean updatedFound = futureEvents.stream().anyMatch(e -> e.toString().contains("Updated A"));
    assertTrue(updatedFound);
  }

  @Test
  public void editSingleEventWithSameSubjectIsInvalid() {
    model.addSingleEvent("Meeting", start, end);
    boolean editResult = model.editSingleEvent("Meeting", start, end, "subject", "Meeting");
    assertFalse(editResult);
  }

  @Test
  public void editSingleEventWithSameStartIsInvalid() {
    model.addSingleEvent("Meeting", start, end);
    boolean editResult = model.editSingleEvent("Meeting", start, end, "start", start.toString());
    assertFalse(editResult);
  }

  @Test
  public void editSingleEventWithSameEndIsInvalid() {
    model.addSingleEvent("Meeting", start, end);
    boolean editResult = model.editSingleEvent("Meeting", start, end, "end", end.toString());
    assertFalse(editResult);
  }
}