import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.TimeZone;

import model.CalendarExceptions.InvalidEvent;
import model.IEvent;
import model.ModifiableCalendarImpl;
import model.SingleEvent;

import static org.junit.Assert.*;
public class ModifiableCalendarImplTest {

  ModifiableCalendarImpl estCal;
  ModifiableCalendarImpl pstCal;
  private LocalDateTime start;
  private LocalDateTime end;

  @Before
  public void setUp() {
    estCal = new ModifiableCalendarImpl("School", TimeZone.getTimeZone("America/New_York"));
    pstCal = new ModifiableCalendarImpl("Work", TimeZone.getTimeZone("America/Los_Angeles"));
    start = LocalDateTime.of(2025, 6, 5, 9, 0);
    end = LocalDateTime.of(2025, 6, 5, 10, 0);
  }

  // CalendarModel Stuff

  @Test
  public void testAddSingleEventSuccessfully() {
    assertTrue(estCal.addSingleEvent("Meeting", start, end));
    assertEquals(1, estCal.queryEvent(LocalDate.of(2025, 6, 5)).size());
  }

  @Test
  public void testAddSingleEventDuplicate() {
    assertTrue(estCal.addSingleEvent("Meeting", start, end));
    assertFalse(estCal.addSingleEvent("Meeting", start, end));
  }

  @Test
  public void testAddRepeatingEventByCountSuccessfully() {
    assertTrue(estCal.addRepeatingEvent("Class", start, end, "MWF", 3));
    ArrayList<IEvent> events = estCal.queryEvent(LocalDate.of(2025, 6, 6));
    assertEquals(1, events.size());
  }

  @Test
  public void testAddRepeatingEventByCountDuplicate() {
    assertTrue(estCal.addRepeatingEvent("Class", start, end, "MWF", 3));
    assertFalse(estCal.addRepeatingEvent("Class", start, end, "MWF", 3));
  }

  @Test
  public void testAddRepeatingEventByUntilDateSuccessfully() {
    LocalDate until = LocalDate.of(2025, 6, 13);
    assertTrue(estCal.addRepeatingEvent("Training", start, end, "MWF", until));
    assertEquals(3, estCal.queryEvent(LocalDate.of(2025, 6, 9)).size() +
        estCal.queryEvent(LocalDate.of(2025, 6, 11)).size() +
        estCal.queryEvent(LocalDate.of(2025, 6, 13)).size());
  }

  @Test
  public void testAddRepeatingEventByUntilOnFakeDay() {
    LocalDate until = LocalDate.of(2025, 6, 13);
    assertFalse(estCal.addRepeatingEvent("Training", start, end, "PG", until));
  }

  @Test
  public void testAddRepeatingEventByCountOnFakeDay() {
    assertFalse(estCal.addRepeatingEvent("Class", start, end, "BZ", 3));
  }

  @Test
  public void testAddRepeatingEventByNegativeCount() {
    assertFalse(estCal.addRepeatingEvent("Class", start, end, "MW", -1));
  }

  @Test
  public void testAddRepeatingEventByZero() {
    assertFalse(estCal.addRepeatingEvent("Class", start, end, "MW", 0));
  }

  @Test
  public void testAddRepeatingEventByUntilDayBeforeStart() {
    LocalDate until = LocalDate.of(2022, 12, 13);
    assertFalse(estCal.addRepeatingEvent("Training", start, end, "MW", until));
  }

  @Test
  public void testAddRepeatingEventByUntilDateDuplicate() {
    LocalDate until = LocalDate.of(2025, 6, 13);
    assertTrue(estCal.addRepeatingEvent("Training", start, end, "MWF", until));
    assertFalse(estCal.addRepeatingEvent("Training", start, end, "MWF", until));
  }

  @Test
  public void testAddRepeatingEventByCountSomeDuplicate() {
    assertTrue(estCal.addRepeatingEvent("Class", start, end, "MWF", 3));
    assertFalse(estCal.addRepeatingEvent("Class", start, end, "MWU", 3));
  }

  @Test
  public void testAddRepeatingEventByUntilDateSomeDuplicate() {
    LocalDate until = LocalDate.of(2025, 6, 13);
    assertTrue(estCal.addRepeatingEvent("Training", start, end, "MWF", until));
    assertFalse(estCal.addRepeatingEvent("Training", start, end, "MWU", until));
  }

  @Test
  public void testQueryEventByDateNoMatch() {
    ArrayList<IEvent> result = estCal.queryEvent(LocalDate.of(2025, 6, 7));
    assertTrue(result.isEmpty());
  }

  @Test
  public void testQueryEventByTimeRangeReturnsCorrect() {
    estCal.addSingleEvent("Meeting", start, end);
    LocalDateTime rangeStart = start.minusHours(1);
    LocalDateTime rangeEnd = end.plusHours(1);
    ArrayList<IEvent> result = estCal.queryEvent(rangeStart, rangeEnd);
    assertEquals(1, result.size());
  }

  @Test
  public void testQueryEventByTimeRangeReturnsEmpty() {
    LocalDateTime rangeStart = start.minusHours(1);
    LocalDateTime rangeEnd = start.minusMinutes(1);
    ArrayList<IEvent> result = estCal.queryEvent(rangeStart, rangeEnd);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testGetStatusWhenBusy() {
    estCal.addSingleEvent("Call", start, end);
    assertTrue(estCal.getStatus(start.plusMinutes(30)));
  }

  @Test
  public void testGetStatusWhenFree() {
    estCal.addSingleEvent("Call", start, end);
    assertFalse(estCal.getStatus(start.minusHours(1)));
  }

  @Test
  public void testGetStatusOnBoundaryStart() {
    estCal.addSingleEvent("Call", start, end);
    assertTrue(estCal.getStatus(start));
  }

  @Test
  public void testGetStatusOnBoundaryEnd() {
    estCal.addSingleEvent("Call", start, end);
    assertTrue(estCal.getStatus(end));
  }

  @Test
  public void testMultipleEventsQueryAndStatus() {
    estCal.addSingleEvent("Meeting", start, end);
    estCal.addRepeatingEvent("Lecture", start, end, "MWF", 3);
    assertEquals(1, estCal.queryEvent(LocalDate.from(start)).size());
    assertTrue(estCal.getStatus(start.plusMinutes(15)));
  }

  @Test
  public void testEmptyCalendarStatusAlwaysFalse() {
    assertFalse(estCal.getStatus(start));
  }

  @Test
  public void testEmptyCalendarQueryAlwaysEmpty() {
    assertTrue(estCal.queryEvent(start.toLocalDate()).isEmpty());
    assertTrue(estCal.queryEvent(start, end).isEmpty());
  }

  @Test
  public void testAddSingleEventOverlappingWithSeriesEvent() {
    boolean addedSeries = estCal.addRepeatingEvent("Weekly Class", start, end, "MWF", 3);
    assertTrue(addedSeries);

    // One day after cause monday is the 6th
    boolean addedSingle = estCal.addSingleEvent("Weekly Class",
        LocalDateTime.of(2025, 6, 6, 9, 0),
        LocalDateTime.of(2025, 6, 6, 10, 0));
    assertFalse(addedSingle);
  }

  @Test
  public void testAddSeriesOverlappingWithSingleEvent() {
    boolean addedSingle = estCal.addSingleEvent("Weekly Class",
        LocalDateTime.of(2025, 6, 6, 9, 0),
        LocalDateTime.of(2025, 6, 6, 10, 0));
    assertTrue(addedSingle);

    boolean addedSeries = estCal.addRepeatingEvent("Weekly Class", start, end, "MWF", 3);
    assertFalse(addedSeries);
  }

  @Test
  public void editSingleEventThatExistsEditSubject() {
    estCal.addSingleEvent("Class", start, end);
    boolean editSingleEvent = estCal.editSingleEvent("Class", start, end, "subject",
        "Test");
    assertTrue(editSingleEvent);
  }

  @Test
  public void editSingleEventThatExistsEditStartDateTime() {
    estCal.addSingleEvent("Class", start, end);
    boolean editSingleEvent = estCal.editSingleEvent("Class", start, end, "start",
        (start.plusMinutes(3)).toString());
    assertTrue(editSingleEvent);
  }

  @Test
  public void editSingleEventThatExistsEditEndDateTime() {
    estCal.addSingleEvent("Class", start, end);
    boolean editSingleEvent = estCal.editSingleEvent("Class", start, end, "end",
        (end.plusHours(3)).toString());
    assertTrue(editSingleEvent);
  }

  @Test
  public void editSingleEventThatExistsEditDescription() {
    estCal.addSingleEvent("Class", start, end);
    boolean editSingleEvent = estCal.editSingleEvent("Class", start, end, "description",
        ("boring"));
    assertTrue(editSingleEvent);
  }

  @Test
  public void editSingleEventThatExistsEditLocationPhysical() {
    estCal.addSingleEvent("Class", start, end);
    boolean editSingleEvent = estCal.editSingleEvent("Class", start, end, "location",
        ("physical"));
    assertTrue(editSingleEvent);
  }

  @Test
  public void editSingleEventThatExistsEditLocationOnline() {
    estCal.addSingleEvent("Class", start, end);
    boolean editSingleEvent = estCal.editSingleEvent("Class", start, end, "location",
        ("online"));
    assertTrue(editSingleEvent);
  }

  @Test
  public void editSingleEventThatExistsEditLocationNonLocation() {
    estCal.addSingleEvent("Class", start, end);
    boolean editSingleEvent = estCal.editSingleEvent("Class", start, end, "location",
        ("home"));
    assertFalse(editSingleEvent);
  }

  @Test
  public void editSingleEventThatExistsEditStatusPrivate() {
    estCal.addSingleEvent("Class", start, end);
    boolean editSingleEvent = estCal.editSingleEvent("Class", start, end, "status",
        ("private"));
    assertTrue(editSingleEvent);
  }

  @Test
  public void editSingleEventThatExistsEditStatusPublic() {
    estCal.addSingleEvent("Class", start, end);
    boolean editSingleEvent = estCal.editSingleEvent("Class", start, end, "status",
        ("public"));
    assertTrue(editSingleEvent);
  }

  @Test
  public void editSingleEventThatExistsEditStatusNonStatus() {
    estCal.addSingleEvent("Class", start, end);
    boolean editSingleEvent = estCal.editSingleEvent("Class", start, end, "location",
        ("busy"));
    assertFalse(editSingleEvent);
  }

  @Test
  public void editSingleEventThatExistsEditNonProperty() {
    estCal.addSingleEvent("Class", start, end);
    boolean editSingleEvent = estCal.editSingleEvent("Class", start, end, "goodbye",
        ("public"));
    assertFalse(editSingleEvent);
  }

  @Test
  public void editSingleEventThatDoesntExist() {
    boolean editSingleEvent = estCal.editSingleEvent("Class", start, end, "goodbye",
        ("public"));
    assertFalse(editSingleEvent);
  }

  @Test
  public void editFutureSeriesEvents() {
    estCal.addRepeatingEvent("Meeting", start, end, "MWF", 10);
    boolean editFutureSeriesEvent = estCal.editFutureSeriesEvents("Meeting",
        LocalDateTime.of(2025, 6, 9, 9, 0), "subject",
        "Homework");
    assertTrue(editFutureSeriesEvent);
  }

  @Test
  public void editFutureSeriesEventsOnSingleEvent() {
    estCal.addSingleEvent("Meeting", start, end);
    boolean editFutureSeriesEvent = estCal.editFutureSeriesEvents("Meeting",
        start, "subject",
        "Homework");
    assertTrue(editFutureSeriesEvent);
  }

  @Test
  public void editEntireSeriesEventsOnSingleEvent() {
    estCal.addSingleEvent("Meeting", start, end);
    boolean editFutureSeriesEvent = estCal.editFutureSeriesEvents("Meeting",
        start, "subject",
        "Homework");
    assertTrue(editFutureSeriesEvent);
  }

  @Test
  public void editEntireSeriesEventsOnSeriesEvent() {
    estCal.addRepeatingEvent("Meeting", start, end, "MWF", 10);
    boolean editFutureSeriesEvent = estCal.editFutureSeriesEvents("Meeting",
        start.plusDays(1), "subject",
        "Homework");
    assertTrue(editFutureSeriesEvent);
  }

  @Test
  public void testAddMultipleEventsAndQueryByDate() {
    estCal.addSingleEvent("Meeting", start, end);
    estCal.addSingleEvent("Workout", start.plusHours(2), end.plusHours(2));
    estCal.addSingleEvent("Lunch", start.plusHours(4), end.plusHours(4));

    ArrayList<IEvent> result = estCal.queryEvent(LocalDate.of(2025, 6, 5));
    assertEquals(3, result.size());
  }

  @Test
  public void testAddMultipleEventsAndQueryByRange() {
    estCal.addSingleEvent("Meeting", start, end);
    estCal.addSingleEvent("Workout", start.plusHours(2), end.plusHours(2));
    estCal.addSingleEvent("Lunch", start.plusHours(4), end.plusHours(4));

    ArrayList<IEvent> result = estCal.queryEvent(start, end.plusHours(5));
    assertEquals(3, result.size());
  }

  @Test
  public void testEditCorrectSingleEventAmongMany() {
    estCal.addSingleEvent("Meeting", start, end);
    LocalDateTime workoutStart = start.plusHours(2);
    LocalDateTime workoutEnd = end.plusHours(2);
    estCal.addSingleEvent("Workout", workoutStart, workoutEnd);
    estCal.addSingleEvent("Lunch", start.plusHours(4), end.plusHours(4));

    boolean edited = estCal.editSingleEvent("Workout", workoutStart, workoutEnd, "subject", "Gym");
    assertTrue(edited);

    ArrayList<IEvent> result = estCal.queryEvent(workoutStart.toLocalDate());
    boolean foundUpdated = result.stream().anyMatch(e -> e.toString().contains("Gym"));
    assertTrue(foundUpdated);
  }

  @Test
  public void testEditWrongSingleEventAmongMany() {
    estCal.addSingleEvent("Meeting", start, end);
    estCal.addSingleEvent("Workout", start.plusHours(2), end.plusHours(2));
    estCal.addSingleEvent("Lunch", start.plusHours(4), end.plusHours(4));

    boolean edited = estCal.editSingleEvent("Nap", start.plusHours(2), end.plusHours(2), "subject",
        "Rest");
    assertFalse(edited);
  }

  @Test
  public void testAddMultipleSeriesEventsAndQuery() {
    estCal.addRepeatingEvent("Class A", start, end, "MWF", 3);
    estCal.addRepeatingEvent("Class B", start.plusHours(2), end.plusHours(2), "TR", 2);

    ArrayList<IEvent> mondayEvents = estCal.queryEvent(LocalDate.of(2025, 6, 9));
    assertEquals(1, mondayEvents.stream().filter(e -> e.toString().contains("Class A")).count());

    ArrayList<IEvent> tuesdayEvents = estCal.queryEvent(LocalDate.of(2025, 6, 10));
    assertEquals(1, tuesdayEvents.stream().filter(e -> e.toString().contains("Class B")).count());
  }

  @Test
  public void testEditFutureSeriesAmongMultipleSeries() {
    estCal.addRepeatingEvent("Class A", start, end, "MWF", 5);
    estCal.addRepeatingEvent("Class B", start.plusHours(2), end.plusHours(2), "TR", 5);

    boolean editClassAFuture = estCal.editFutureSeriesEvents("Class A", start.plusDays(4),
        "subject", "Updated A");
    assertTrue(editClassAFuture);

    ArrayList<IEvent> futureEvents = estCal.queryEvent(LocalDate.of(2025, 6, 11));
    boolean updatedFound = futureEvents.stream().anyMatch(e -> e.toString().contains("Updated A"));
    assertTrue(updatedFound);
  }

  @Test
  public void editSingleEventWithSameSubjectIsInvalid() {
    estCal.addSingleEvent("Meeting", start, end);
    boolean editResult = estCal.editSingleEvent("Meeting", start, end, "subject", "Meeting");
    assertFalse(editResult);
  }

  @Test
  public void editSingleEventWithSameStartIsInvalid() {
    estCal.addSingleEvent("Meeting", start, end);
    boolean editResult = estCal.editSingleEvent("Meeting", start, end, "start", start.toString());
    assertFalse(editResult);
  }

  @Test
  public void editSingleEventWithSameEndIsInvalid() {
    estCal.addSingleEvent("Meeting", start, end);
    boolean editResult = estCal.editSingleEvent("Meeting", start, end, "end", end.toString());
    assertFalse(editResult);
  }

  // ModifiableCalendarModel Specific Stuff

  @Test
  public void testName() {
    assertEquals("School", estCal.getName());
    assertEquals("Work", pstCal.getName());
    estCal.editName("Still School");
    assertEquals("Still School", estCal.getName());
    pstCal.editName("Still Work");
    assertEquals("Still Work", pstCal.getName());
  }

  @Test
  public void testQueryExactEventWithJustStart() {
    IEvent expected = new SingleEvent("Meeting", start, end);
    estCal.addSingleEvent("Meeting", start, end);
    estCal.addSingleEvent("Something Else", start.plusDays(1), end.plusDays(1));
    estCal.addSingleEvent("Meeting", start.minusMinutes(1), end);
    assertEquals(expected, estCal.queryExactEvent("Meeting", start));

    pstCal.addSingleEvent("Meeting", start, end);
    pstCal.addSingleEvent("Something Else", start.plusDays(1), end.plusDays(1));
    pstCal.addSingleEvent("Meeting", start.minusMinutes(1), end);
    assertEquals(expected, pstCal.queryExactEvent("Meeting", start));
  }

  @Test
  public void testQueryExactEventWithJustStartNoneFound() {
    estCal.addSingleEvent("Meeting", start, end);
    estCal.addSingleEvent("Something Else", start.plusDays(1), end.plusDays(1));
    estCal.addSingleEvent("Meeting", start.minusMinutes(1), end);
    assertNull(estCal.queryExactEvent("Meeting", end));

    pstCal.addSingleEvent("Meeting", start, end);
    pstCal.addSingleEvent("Something Else", start.plusDays(1), end.plusDays(1));
    pstCal.addSingleEvent("Meeting", start.minusMinutes(1), end);
    assertNull(pstCal.queryExactEvent("Meeting", end));
  }

  @Test
  public void testQueryExactEventWithJustStartOverlap() {
    estCal.addSingleEvent("Meeting", start, end);
    estCal.addSingleEvent("Something Else", start.plusDays(1), end.plusDays(1));
    estCal.addSingleEvent("Meeting", start, end.plusMinutes(1));
    assertThrows(InvalidEvent.class, () -> estCal.queryExactEvent("Meeting", start));

    pstCal.addSingleEvent("Meeting", start, end);
    pstCal.addSingleEvent("Something Else", start.plusDays(1), end.plusDays(1));
    pstCal.addSingleEvent("Meeting", start, end.plusMinutes(1));
    assertThrows(InvalidEvent.class, () -> pstCal.queryExactEvent("Meeting", start));
  }

  @Test
  public void testGetTimeZone() {
    assertEquals(TimeZone.getTimeZone("America/New_York"), estCal.getTimeZone());
    assertEquals(TimeZone.getTimeZone("America/Los_Angeles"), pstCal.getTimeZone());
  }

  @Test
  public void testAddEventEndWithStart() {
    assertNull(estCal.queryExactEvent("New Meeting", start.minusDays(2)));
    IEvent newEvent = new SingleEvent("New Meeting", start, end);
    IEvent expected = new SingleEvent("New Meeting", start.minusDays(2), end.minusDays(2));
    estCal.add(newEvent, start.minusDays(2));
    assertEquals(expected, estCal.queryExactEvent("New Meeting", start.minusDays(2)));

    assertNull(pstCal.queryExactEvent("New Meeting", start.minusDays(2)));
    pstCal.add(newEvent, start.minusDays(2));
    assertEquals(expected, pstCal.queryExactEvent("New Meeting", start.minusDays(2)));
  }

  @Test
  public void testAddEventEndWithStartCausesOverlap() {
    estCal.addSingleEvent("New Meeting", start.minusDays(2), end.minusDays(2));
    IEvent newEvent = new SingleEvent("New Meeting", start, end);
    assertThrows(InvalidEvent.class, () -> estCal.add(newEvent, start.minusDays(2)));

    pstCal.addSingleEvent("New Meeting", start.minusDays(2), end.minusDays(2));
    assertThrows(InvalidEvent.class, () -> pstCal.add(newEvent, start.minusDays(2)));
  }

  @Test
  public void testAddEventEndWithStartCausesOverlapWithSeries() {
    estCal.addRepeatingEvent("Class", start, end, "MTWRFSU", 7);
    IEvent newEvent = new SingleEvent("Class", start, end);
    assertThrows(InvalidEvent.class, () -> estCal.add(newEvent, start.plusDays(2)));

    pstCal.addRepeatingEvent("Class", start, end, "MTWRFSU", 7);
    assertThrows(InvalidEvent.class, () -> pstCal.add(newEvent, start.plusDays(2)));
  }

  @Test
  public void testAddEventTZAndDateChangeFromPSTToEST() {
    assertNull(estCal.queryExactEvent("New Meeting", start.minusDays(2).plusHours(3)));
    IEvent newEvent = new SingleEvent("New Meeting", start, end);
    IEvent expected = new SingleEvent("New Meeting", start.minusDays(2).plusHours(3),
        end.minusDays(2).plusHours(3));
    estCal.add(newEvent, start.minusDays(2).toLocalDate(), TimeZone.getTimeZone("America/Los_Angeles"));
    assertEquals(expected, estCal.queryExactEvent("New Meeting", start.minusDays(2).plusHours(3)));
  }

  @Test
  public void testAddEventTZAndDateChangeFromESTToPST() {
    assertNull(pstCal.queryExactEvent("New Meeting", start.minusDays(2).minusHours(3)));
    IEvent newEvent = new SingleEvent("New Meeting", start, end);
    IEvent expected = new SingleEvent("New Meeting", start.minusDays(2).minusHours(3),
        end.minusDays(2).minusHours(3));
    pstCal.add(newEvent, start.minusDays(2).toLocalDate(), TimeZone.getTimeZone("America/New_York"));
    assertEquals(expected, pstCal.queryExactEvent("New Meeting", start.minusDays(2).minusHours(3)));
  }

  @Test
  public void testAddEventTZAndDateChangeTZSame() {
    assertNull(estCal.queryExactEvent("New Meeting", start.minusDays(2)));
    IEvent newEvent = new SingleEvent("New Meeting", start, end);
    IEvent expected = new SingleEvent("New Meeting", start.minusDays(2), end.minusDays(2));
    estCal.add(newEvent, start.minusDays(2).toLocalDate(), TimeZone.getTimeZone("America/New_York"));
    assertEquals(expected, estCal.queryExactEvent("New Meeting", start.minusDays(2)));
  }

  @Test
  public void testAddEventTZAndDateChangeFromMITToESTChangesDay() {
    assertNull(estCal.queryExactEvent("New Meeting", start.minusHours(17).minusDays(2)));
    IEvent newEvent = new SingleEvent("New Meeting", start, end);
    IEvent expected = new SingleEvent("New Meeting", start.minusHours(17).minusDays(2),
        end.minusHours(17).minusDays(2));
    estCal.add(newEvent, start.minusDays(2).toLocalDate(), TimeZone.getTimeZone("Pacific/Apia"));
    assertEquals(expected, estCal.queryExactEvent("New Meeting", start.minusHours(17).minusDays(2)));
  }

  @Test
  public void testAddEventTZAndDateChangeFromMSTToESTChangesDay() {
    LocalDateTime veryLateStart = start.plusHours(12);
    LocalDateTime veryLateEnd = end.plusHours(12);
    assertNull(estCal.queryExactEvent("New Meeting", veryLateStart.plusHours(2).minusDays(2)));
    IEvent newEvent = new SingleEvent("New Meeting", veryLateStart, veryLateEnd);
    IEvent expected = new SingleEvent("New Meeting", veryLateStart.plusHours(2).minusDays(2),
        veryLateEnd.plusHours(2).minusDays(2));
    estCal.add(newEvent, veryLateStart.minusDays(2).toLocalDate(), TimeZone.getTimeZone("America/Denver"));
    assertEquals(expected, estCal.queryExactEvent("New Meeting", veryLateStart.plusHours(2).minusDays(2)));
  }

  @Test
  public void testAddEventTZAndDateChangeCausesOverlap() {
    estCal.addSingleEvent("New Meeting", start.minusDays(2).plusHours(3),
        end.minusDays(2).plusHours(3));
    IEvent newEvent = new SingleEvent("New Meeting", start, end);
    assertThrows(InvalidEvent.class, () -> estCal.add(newEvent, start.minusDays(2).toLocalDate(),
        TimeZone.getTimeZone("America/Los_Angeles")));

    pstCal.addSingleEvent("New Meeting", start.minusDays(2).minusHours(3),
        end.minusDays(2).minusHours(3));
    assertThrows(InvalidEvent.class, () -> pstCal.add(newEvent, start.minusDays(2).toLocalDate(),
        TimeZone.getTimeZone("America/New_York")));
  }

  @Test
  public void testAddEventTZAndDateChangeCausesOverlapWithSeries() {
    estCal.addRepeatingEvent("Class", start.plusHours(3), end.plusHours(3), "MTWRFSU", 7);
    IEvent newEvent = new SingleEvent("Class", start, end);
    assertThrows(InvalidEvent.class, () -> estCal.add(newEvent, start.plusDays(2).toLocalDate(),
        TimeZone.getTimeZone("America/Los_Angeles")));

    pstCal.addRepeatingEvent("Class", start.minusHours(3), end.minusHours(3), "MTWRFSU", 7);
    assertThrows(InvalidEvent.class, () -> pstCal.add(newEvent, start.plusDays(2).toLocalDate(),
        TimeZone.getTimeZone("America/New_York")));
  }

}