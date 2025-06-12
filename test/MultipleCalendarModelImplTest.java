import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.TimeZone;

import model.CalendarExceptions.InvalidCalendar;
import model.CalendarExceptions.InvalidEvent;
import model.CalendarExceptions.InvalidProperty;
import model.CalendarExceptions.NoCalendar;
import model.IEvent;
import model.MultipleCalendarModel;
import model.MultipleCalendarModelImpl;
import model.SeriesEvent;
import model.SingleEvent;

import static org.junit.Assert.*;

public class MultipleCalendarModelImplTest {
  private MultipleCalendarModel model;
  private LocalDateTime start;
  private LocalDateTime end;

  @Before
  public void setUp() {
    model = new MultipleCalendarModelImpl();
    model.create("Default", "America/New_York");
    model.use("Default");
    start = LocalDateTime.of(2025, 6, 5, 9, 0);
    end = LocalDateTime.of(2025, 6, 5, 10, 0);
  }

  // Old tests

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
    ArrayList<IEvent> events = model.queryEvent(LocalDate.of(2025, 6, 6));
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
    ArrayList<IEvent> result = model.queryEvent(LocalDate.of(2025, 6, 7));
    assertTrue(result.isEmpty());
  }

  @Test
  public void testQueryEventByTimeRangeReturnsCorrect() {
    model.addSingleEvent("Meeting", start, end);
    LocalDateTime rangeStart = start.minusHours(1);
    LocalDateTime rangeEnd = end.plusHours(1);
    ArrayList<IEvent> result = model.queryEvent(rangeStart, rangeEnd);
    assertEquals(1, result.size());
  }

  @Test
  public void testQueryEventByTimeRangeReturnsEmpty() {
    LocalDateTime rangeStart = start.minusHours(1);
    LocalDateTime rangeEnd = start.minusMinutes(1);
    ArrayList<IEvent> result = model.queryEvent(rangeStart, rangeEnd);
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

    ArrayList<IEvent> result = model.queryEvent(LocalDate.of(2025, 6, 5));
    assertEquals(3, result.size());
  }

  @Test
  public void testAddMultipleEventsAndQueryByRange() {
    model.addSingleEvent("Meeting", start, end);
    model.addSingleEvent("Workout", start.plusHours(2), end.plusHours(2));
    model.addSingleEvent("Lunch", start.plusHours(4), end.plusHours(4));

    ArrayList<IEvent> result = model.queryEvent(start, end.plusHours(5));
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

    ArrayList<IEvent> result = model.queryEvent(workoutStart.toLocalDate());
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

    ArrayList<IEvent> mondayEvents = model.queryEvent(LocalDate.of(2025, 6, 9));
    assertEquals(1, mondayEvents.stream().filter(e -> e.toString().contains("Class A")).count());

    ArrayList<IEvent> tuesdayEvents = model.queryEvent(LocalDate.of(2025, 6, 10));
    assertEquals(1, tuesdayEvents.stream().filter(e -> e.toString().contains("Class B")).count());
  }

  @Test
  public void testEditFutureSeriesAmongMultipleSeries() {
    model.addRepeatingEvent("Class A", start, end, "MWF", 5);
    model.addRepeatingEvent("Class B", start.plusHours(2), end.plusHours(2), "TR", 5);

    boolean editClassAFuture = model.editFutureSeriesEvents("Class A", start.plusDays(4),
        "subject", "Updated A");
    assertTrue(editClassAFuture);

    ArrayList<IEvent> futureEvents = model.queryEvent(LocalDate.of(2025, 6, 11));
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

  // Multiple Calendar Specific Tests

  @Test
  public void testCreateWithAllTimeZones() {
    int i = 0;
    for (String tz : TimeZone.getAvailableIDs()) {
      model.create("Calendar" + i, tz);
      model.use("Calendar" + i);
      i++;
    }
  }

  @Test (expected = InvalidProperty.class)
  public void testCreateAleardyExists() {
    model.create("Default", "America/Tijuana");
  }

  @Test (expected = InvalidProperty.class)
  public void testInvalidTZFormat() {
    model.create("New Cal", "Hello!");
  }

  @Test
  public void testEditCalendarName() {
    model.edit("Default", "name", "New Name");
    assertEquals("New Name", model.getCurrentCalendar().getName());
  }

  @Test
  public void testEditCalendarTimeZone() {
    model.addSingleEvent("Meeting", start, end);
    model.edit("Default", "timezone", "America/Los_Angeles");
    assertEquals(TimeZone.getTimeZone("America/Los_Angeles"),
        model.getCurrentCalendar().getTimeZone());
    assertEquals(new SingleEvent("Meeting", start.minusHours(3), end.minusHours(3)),
        model.queryEvent(start.toLocalDate()).get(0));
  }

  @Test (expected = InvalidProperty.class)
  public void testEditCalendarNameCreatesDuplicate() {
    model.create("New Name", "America/Los_Angeles");
    model.edit("Default", "name", "New Name");
  }

  @Test (expected = InvalidProperty.class)
  public void testEditCalendarTimeZoneInvalidTZFormat() {
    model.edit("Default", "timezone", "blah blah");
  }

  @Test (expected = InvalidProperty.class)
  public void testEditInvalidProperty() {
    model.edit("Default", "hellooo", "New Name");
  }

  @Test (expected = InvalidCalendar.class)
  public void testEditCalendarDoesntExist() {
    model.edit("Nope", "name", "New Name");
  }

  @Test (expected = InvalidCalendar.class)
  public void testUseCalendarDoesntExist() {
    model.use("Nope");
  }

  @Test
  public void testUseSameCalendar() {
    assertEquals("Default", model.getCurrentCalendar().getName());
    model.use("Default");
    assertEquals("Default", model.getCurrentCalendar().getName());
  }

  @Test
  public void testCopyEvent() {
    model.addSingleEvent("Meeting", start, end);
    model.create("Copy To", "America/Los_Angeles");
    model.copyEvent("Meeting", start, "Copy To", start.plusDays(2).plusHours(1));
    IEvent expected = new SingleEvent("Meeting", start.plusDays(2).plusHours(1),
        end.plusDays(2).plusHours(1));
    model.use("Copy To");
    assertEquals(expected, model.queryEvent(start.plusDays(2).toLocalDate()).get(0));
  }

  @Test
  public void testCopyEventFromSeries() {
    model.addRepeatingEvent("Meeting", start, end, "MWF", 3);
    model.create("Copy To", "America/Los_Angeles");
    model.copyEvent("Meeting", start.plusDays(1), "Copy To", start.plusDays(2).plusHours(1));
    IEvent expected = new SingleEvent("Meeting", start.plusDays(2).plusHours(1),
        end.plusDays(2).plusHours(1));
    model.use("Copy To");
    assertEquals(expected, model.queryEvent(start.plusDays(2).toLocalDate()).get(0));
  }

  @Test (expected = NoCalendar.class)
  public void testCopyEventNoCalendar() {
    MultipleCalendarModel empty = new MultipleCalendarModelImpl();
    empty.create("Default", "America/New_York");
    empty.create("Copy To", "America/Los_Angeles");
    empty.copyEvent("Meeting", start, "Copy To", start.plusDays(2).plusHours(1));
  }

  @Test (expected = InvalidCalendar.class)
  public void testCopyEventNoTargetCalendar() {
    model.addRepeatingEvent("Meeting", start, end, "MWF", 3);
    model.copyEvent("Meeting", start, "Copy To", start.plusDays(2).plusHours(1));
  }

  @Test (expected = InvalidEvent.class)
  public void testCopyEventCantFindEvent() {
    model.create("Copy To", "America/Los_Angeles");
    model.copyEvent("Meeting", start, "Copy To", start.plusDays(2).plusHours(1));
  }

  @Test
  public void testCopyEventAliasingCheck() {
    model.addSingleEvent("Meeting", start, end);
    model.create("Copy To", "America/Los_Angeles");
    model.copyEvent("Meeting", start, "Copy To", start);
    model.use("Copy To");
    IEvent expected = new SingleEvent("Meeting", start, end);
    IEvent expected2 = new SingleEvent("Not Meeting", start, end);
    assertEquals(expected, model.queryEvent(start.toLocalDate()).get(0));
    model.editSingleEvent("Meeting", start, end, "subject", "Not Meeting");
    assertEquals(expected2, model.queryEvent(start.toLocalDate()).get(0));
    model.use("Default");
    assertEquals(expected, model.queryEvent(start.toLocalDate()).get(0));
    assertNotEquals(expected2, model.queryEvent(start.toLocalDate()).get(0));
  }

  @Test
  public void testCopyEventsOnDate() {
    model.addSingleEvent("Meeting", start.minusHours(1), end.minusHours(1));
    model.addSingleEvent("Running", start.plusHours(2), end.plusHours(2));
    model.addRepeatingEvent("Class", start.minusDays(1), end.minusDays(1), "MTWRF", 5);
    model.create("Copy To", "America/Los_Angeles");
    assertTrue(model.copyEvents(start.toLocalDate(), "Copy To", start.plusDays(2).toLocalDate()));
    IEvent expected1 = new SingleEvent("Meeting", start.plusDays(2).minusHours(4),
        end.plusDays(2).minusHours(4));
    IEvent expected2 = new SingleEvent("Running", start.plusDays(2).minusHours(1),
        end.plusDays(2).minusHours(1));
    IEvent expected3 = new SingleEvent("Class", start.plusDays(2).minusHours(3),
        end.plusDays(2).minusHours(3));
    model.use("Copy To");
    ArrayList<IEvent> events = model.queryEvent(start.minusDays(10), end.plusDays(20));
    assertTrue(events.contains(expected1));
    assertTrue(events.contains(expected2));
    assertEquals(expected3, events.get(2).getExactMatch("Class", start.plusDays(2).minusHours(3)));
  }

  @Test (expected = NoCalendar.class)
  public void testCopyEventsOnDateNoCalendar() {
    MultipleCalendarModel empty = new MultipleCalendarModelImpl();
    empty.create("Default", "America/New_York");
    empty.create("Copy To", "America/Los_Angeles");
    empty.copyEvents(start.toLocalDate(), "Copy To", start.plusDays(2).toLocalDate());
  }

  @Test (expected = InvalidCalendar.class)
  public void testCopyEventsOnDateNoTargetCalendar() {
    model.addSingleEvent("Meeting", start, end);
    model.copyEvents(start.toLocalDate(), "Copy To", start.plusDays(2).toLocalDate());
  }

  @Test
  public void testCopyEventsOnDateCantFindEvents() {
    model.create("Copy To", "America/Los_Angeles");
    assertFalse(model.copyEvents(start.toLocalDate(), "Copy To", start.plusDays(2).toLocalDate()));
  }

  @Test
  public void testCopyEventsOnDateAliasingCheck() {
    model.addSingleEvent("Meeting", start, end);
    model.create("Copy To", "America/Los_Angeles");
    model.copyEvents(start.toLocalDate(), "Copy To", start.toLocalDate());
    model.use("Copy To");
    IEvent expected = new SingleEvent("Meeting", start.minusHours(3), end.minusHours(3));
    IEvent expected2 = new SingleEvent("Not Meeting", start.minusHours(3), end.minusHours(3));
    IEvent expected3 = new SingleEvent("Meeting", start, end);
    assertEquals(expected, model.queryEvent(start.toLocalDate()).get(0));
    model.editSingleEvent("Meeting", start.minusHours(3), end.minusHours(3), "subject",
        "Not Meeting");
    assertEquals(expected2, model.queryEvent(start.toLocalDate()).get(0));
    model.use("Default");
    assertEquals(expected3, model.queryEvent(start.toLocalDate()).get(0));
    assertNotEquals(expected2, model.queryEvent(start.toLocalDate()).get(0));
  }

  @Test
  public void testCopyEventsInRangeWithParitalSeries() {
    model.addSingleEvent("Meeting", start.minusHours(1), end.minusHours(1));
    model.addSingleEvent("Running", start.plusHours(2), end.plusHours(2));
    model.addRepeatingEvent("Class", start.minusDays(1), end.minusDays(1), "MTWRF", 5);
    model.create("Copy To", "America/Los_Angeles");
    assertTrue(model.copyEvents(start.toLocalDate(), end.plusDays(3).toLocalDate(),"Copy To", start.plusDays(2).toLocalDate()));
    IEvent expected1 = new SingleEvent("Meeting", start.plusDays(2).minusHours(4),
        end.plusDays(2).minusHours(4));
    IEvent expected2 = new SingleEvent("Running", start.plusDays(2).minusHours(1),
        end.plusDays(2).minusHours(1));
    SeriesEvent expected3 = new SeriesEvent("Class", start.plusDays(2).minusHours(3),
        end.plusDays(2).minusHours(3), "SUW", 3);
    model.use("Copy To");
    ArrayList<IEvent> events = model.queryEvent(start.minusDays(10), end.plusDays(20));
    assertTrue(events.contains(expected1));
    assertTrue(events.contains(expected2));
    SeriesEvent outSeries = (SeriesEvent) events.get(2);
    for (int i = 0; i < outSeries.getEvents().size(); i++) {
      assertEquals(expected3.getEvents().get(i), outSeries.getEvents().get(i));
    }
  }

  @Test
  public void testCopyEventsInRangeOnSeriesKeepsMetaData() {
    model.addRepeatingEvent("Class", start, end, "MTWRF", 5);
    model.create("Copy To", "America/Los_Angeles");
    assertTrue(model.copyEvents(start.toLocalDate(), end.plusDays(10).toLocalDate(),"Copy To", start.plusDays(2).toLocalDate()));
    SeriesEvent expected = new SeriesEvent("Class", start.plusDays(2).minusHours(3),
        end.plusDays(2).minusHours(3), "SUWRF", 5);
    model.use("Copy To");
    ArrayList<IEvent> events = model.queryEvent(start.minusDays(10), end.plusDays(20));
    SeriesEvent outSeries = (SeriesEvent) events.get(0);
    for (int i = 0; i < outSeries.getEvents().size(); i++) {
      assertEquals(expected.getEvents().get(i), outSeries.getEvents().get(i));
    }

    SeriesEvent expected2 = new SeriesEvent("Still Class", start.plusDays(2).minusHours(3),
        end.plusDays(2).minusHours(3), "SUWRF", 5);
    model.editEntireSeries("Class", start.plusDays(2).minusHours(3), "subject", "Still Class");
    events = model.queryEvent(start.minusDays(10), end.plusDays(20));
    outSeries = (SeriesEvent) events.get(0);
    for (int i = 0; i < outSeries.getEvents().size(); i++) {
      assertEquals(expected2.getEvents().get(i), outSeries.getEvents().get(i));
    }
  }

  @Test (expected = NoCalendar.class)
  public void testCopyEventsInRangeNoCalendar() {
    MultipleCalendarModel empty = new MultipleCalendarModelImpl();
    empty.create("Default", "America/New_York");
    empty.create("Copy To", "America/Los_Angeles");
    empty.copyEvents(start.toLocalDate(), end.plusDays(5).toLocalDate(),"Copy To", start.plusDays(2).toLocalDate());
  }

  @Test (expected = InvalidCalendar.class)
  public void testCopyEventsInRangeNoTargetCalendar() {
    model.addSingleEvent("Meeting", start, end);
    model.copyEvents(start.toLocalDate(), end.plusDays(5).toLocalDate(), "Copy To", start.plusDays(2).toLocalDate());
  }

  @Test
  public void testCopyEventsInRangeCantFindEvents() {
    model.create("Copy To", "America/Los_Angeles");
    assertFalse(model.copyEvents(start.toLocalDate(), end.plusDays(5).toLocalDate(), "Copy To", start.plusDays(2).toLocalDate()));
  }

  @Test
  public void testCopyEventsInRangeAliasingCheck() {
    model.addSingleEvent("Meeting", start, end);
    model.create("Copy To", "America/Los_Angeles");
    model.copyEvents(start.toLocalDate(), end.plusDays(1).toLocalDate(), "Copy To", start.toLocalDate());
    model.use("Copy To");
    IEvent expected = new SingleEvent("Meeting", start.minusHours(3), end.minusHours(3));
    IEvent expected2 = new SingleEvent("Not Meeting", start.minusHours(3), end.minusHours(3));
    IEvent expected3 = new SingleEvent("Meeting", start, end);
    assertEquals(expected, model.queryEvent(start.toLocalDate()).get(0));
    model.editSingleEvent("Meeting", start.minusHours(3), end.minusHours(3), "subject",
        "Not Meeting");
    assertEquals(expected2, model.queryEvent(start.toLocalDate()).get(0));
    model.use("Default");
    assertEquals(expected3, model.queryEvent(start.toLocalDate()).get(0));
    assertNotEquals(expected2, model.queryEvent(start.toLocalDate()).get(0));
  }

  //TODO: Copy events overlap for in range and on date!!!
}