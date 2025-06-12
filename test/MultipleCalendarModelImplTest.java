import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.TimeZone;

import model.exceptions.InvalidCalendar;
import model.exceptions.InvalidEvent;
import model.exceptions.InvalidProperty;
import model.exceptions.NoCalendar;
import model.CalendarModel;
import model.IEvent;
import model.MultipleCalendarModel;
import model.MultipleCalendarModelImpl;
import model.SeriesEvent;
import model.SingleEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@code model.MultipleCalendarModelImpl} class.
 */
public class MultipleCalendarModelImplTest extends ACalendarTest {
  private MultipleCalendarModel multipleCalendarModel;

  @Override
  protected CalendarModel getCalendarModel() {
    multipleCalendarModel = new MultipleCalendarModelImpl();
    multipleCalendarModel.create("Default", "America/New_York");
    multipleCalendarModel.use("Default");
    return multipleCalendarModel;
  }

  @Test
  public void testCreateWithAllTimeZones() {
    int i = 0;
    for (String tz : TimeZone.getAvailableIDs()) {
      multipleCalendarModel.create("Calendar" + i, tz);
      multipleCalendarModel.use("Calendar" + i);
      assertNotNull(multipleCalendarModel.getCurrentCalendar());
      i++;
    }
  }

  @Test(expected = InvalidProperty.class)
  public void testCreateAleardyExists() {
    multipleCalendarModel.create("Default", "America/Tijuana");
  }

  @Test(expected = InvalidProperty.class)
  public void testInvalidTZFormat() {
    multipleCalendarModel.create("New Cal", "Hello!");
  }

  @Test
  public void testEditCalendarName() {
    multipleCalendarModel.edit("Default", "name", "New Name");
    assertEquals("New Name", multipleCalendarModel.getCurrentCalendar().getName());
  }

  @Test
  public void testEditCalendarTimeZone() {
    multipleCalendarModel.addSingleEvent("Meeting", start, end);
    multipleCalendarModel.edit("Default", "timezone", "America/Los_Angeles");
    assertEquals(TimeZone.getTimeZone("America/Los_Angeles"),
        multipleCalendarModel.getCurrentCalendar().getTimeZone());
    assertEquals(new SingleEvent("Meeting", start.minusHours(3), end.minusHours(3)),
        multipleCalendarModel.queryEvent(start.toLocalDate()).get(0));
  }

  @Test(expected = InvalidProperty.class)
  public void testEditCalendarNameCreatesDuplicate() {
    multipleCalendarModel.create("New Name", "America/Los_Angeles");
    multipleCalendarModel.edit("Default", "name", "New Name");
  }

  @Test(expected = InvalidProperty.class)
  public void testEditCalendarTimeZoneInvalidTZFormat() {
    multipleCalendarModel.edit("Default", "timezone", "blah blah");
  }

  @Test(expected = InvalidProperty.class)
  public void testEditInvalidProperty() {
    multipleCalendarModel.edit("Default", "hellooo", "New Name");
  }

  @Test(expected = InvalidCalendar.class)
  public void testEditCalendarDoesntExist() {
    multipleCalendarModel.edit("Nope", "name", "New Name");
  }

  @Test(expected = InvalidCalendar.class)
  public void testUseCalendarDoesntExist() {
    multipleCalendarModel.use("Nope");
  }

  @Test
  public void testUseSameCalendar() {
    assertEquals("Default", multipleCalendarModel.getCurrentCalendar().getName());
    multipleCalendarModel.use("Default");
    assertEquals("Default", multipleCalendarModel.getCurrentCalendar().getName());
  }

  @Test
  public void testCopyEvent() {
    multipleCalendarModel.addSingleEvent("Meeting", start, end);
    multipleCalendarModel.create("Copy To", "America/Los_Angeles");
    multipleCalendarModel.copyEvent("Meeting", start, "Copy To", start.plusDays(2).plusHours(1));
    IEvent expected = new SingleEvent("Meeting", start.plusDays(2).plusHours(1),
        end.plusDays(2).plusHours(1));
    multipleCalendarModel.use("Copy To");
    assertEquals(expected,
        multipleCalendarModel.queryEvent(start.plusDays(2).toLocalDate()).get(0));
  }

  @Test
  public void testCopyEventFromSeries() {
    multipleCalendarModel.addRepeatingEvent("Meeting", start, end, "MWF", 3);
    multipleCalendarModel.create("Copy To", "America/Los_Angeles");
    multipleCalendarModel.copyEvent("Meeting", start.plusDays(1), "Copy To",
        start.plusDays(2).plusHours(1));
    IEvent expected = new SingleEvent("Meeting", start.plusDays(2).plusHours(1),
        end.plusDays(2).plusHours(1));
    multipleCalendarModel.use("Copy To");
    assertEquals(expected,
        multipleCalendarModel.queryEvent(start.plusDays(2).toLocalDate()).get(0));
  }

  @Test(expected = NoCalendar.class)
  public void testCopyEventNoCalendar() {
    MultipleCalendarModel empty = new MultipleCalendarModelImpl();
    empty.create("Default", "America/New_York");
    empty.create("Copy To", "America/Los_Angeles");
    empty.copyEvent("Meeting", start, "Copy To", start.plusDays(2).plusHours(1));
  }

  @Test(expected = InvalidCalendar.class)
  public void testCopyEventNoTargetCalendar() {
    multipleCalendarModel.addRepeatingEvent("Meeting", start, end, "MWF", 3);
    multipleCalendarModel.copyEvent("Meeting", start, "Copy To", start.plusDays(2).plusHours(1));
  }

  @Test(expected = InvalidEvent.class)
  public void testCopyEventCantFindEvent() {
    multipleCalendarModel.create("Copy To", "America/Los_Angeles");
    multipleCalendarModel.copyEvent("Meeting", start, "Copy To", start.plusDays(2).plusHours(1));
  }

  @Test
  public void testCopyEventAliasingCheck() {
    multipleCalendarModel.addSingleEvent("Meeting", start, end);
    multipleCalendarModel.create("Copy To", "America/Los_Angeles");
    multipleCalendarModel.copyEvent("Meeting", start, "Copy To", start);
    multipleCalendarModel.use("Copy To");
    IEvent expected = new SingleEvent("Meeting", start, end);
    IEvent expected2 = new SingleEvent("Not Meeting", start, end);
    assertEquals(expected, multipleCalendarModel.queryEvent(start.toLocalDate()).get(0));
    multipleCalendarModel.editSingleEvent("Meeting", start, end, "subject", "Not Meeting");
    assertEquals(expected2, multipleCalendarModel.queryEvent(start.toLocalDate()).get(0));
    multipleCalendarModel.use("Default");
    assertEquals(expected, multipleCalendarModel.queryEvent(start.toLocalDate()).get(0));
    assertNotEquals(expected2, multipleCalendarModel.queryEvent(start.toLocalDate()).get(0));
  }

  @Test
  public void testCopyEventsOnDate() {
    multipleCalendarModel.addSingleEvent("Meeting", start.minusHours(1), end.minusHours(1));
    multipleCalendarModel.addSingleEvent("Running", start.plusHours(2), end.plusHours(2));
    multipleCalendarModel.addRepeatingEvent("Class", start.minusDays(1), end.minusDays(1),
        "MTWRF", 5);
    multipleCalendarModel.create("Copy To", "America/Los_Angeles");
    assertTrue(multipleCalendarModel.copyEvents(start.toLocalDate(), "Copy To",
        start.plusDays(2).toLocalDate()));
    IEvent expected1 = new SingleEvent("Meeting", start.plusDays(2).minusHours(4),
        end.plusDays(2).minusHours(4));
    IEvent expected2 = new SingleEvent("Running", start.plusDays(2).minusHours(1),
        end.plusDays(2).minusHours(1));
    IEvent expected3 = new SingleEvent("Class", start.plusDays(2).minusHours(3),
        end.plusDays(2).minusHours(3));
    multipleCalendarModel.use("Copy To");
    ArrayList<IEvent> events = multipleCalendarModel.queryEvent(start.minusDays(10),
        end.plusDays(20));
    assertTrue(events.contains(expected1));
    assertTrue(events.contains(expected2));
    assertEquals(expected3, events.get(2).getExactMatch("Class", start.plusDays(2).minusHours(3)));
  }

  @Test(expected = NoCalendar.class)
  public void testCopyEventsOnDateNoCalendar() {
    MultipleCalendarModel empty = new MultipleCalendarModelImpl();
    empty.create("Default", "America/New_York");
    empty.create("Copy To", "America/Los_Angeles");
    empty.copyEvents(start.toLocalDate(), "Copy To", start.plusDays(2).toLocalDate());
  }

  @Test(expected = InvalidCalendar.class)
  public void testCopyEventsOnDateNoTargetCalendar() {
    multipleCalendarModel.addSingleEvent("Meeting", start, end);
    multipleCalendarModel.copyEvents(start.toLocalDate(), "Copy To",
        start.plusDays(2).toLocalDate());
  }

  @Test
  public void testCopyEventsOnDateCantFindEvents() {
    multipleCalendarModel.create("Copy To", "America/Los_Angeles");
    assertFalse(multipleCalendarModel.copyEvents(start.toLocalDate(), "Copy To",
        start.plusDays(2).toLocalDate()));
  }

  @Test
  public void testCopyEventsOnDateAliasingCheck() {
    multipleCalendarModel.addSingleEvent("Meeting", start, end);
    multipleCalendarModel.create("Copy To", "America/Los_Angeles");
    multipleCalendarModel.copyEvents(start.toLocalDate(), "Copy To", start.toLocalDate());
    multipleCalendarModel.use("Copy To");
    IEvent expected = new SingleEvent("Meeting", start.minusHours(3), end.minusHours(3));
    IEvent expected2 = new SingleEvent("Not Meeting", start.minusHours(3), end.minusHours(3));
    IEvent expected3 = new SingleEvent("Meeting", start, end);
    assertEquals(expected, multipleCalendarModel.queryEvent(start.toLocalDate()).get(0));
    multipleCalendarModel.editSingleEvent("Meeting", start.minusHours(3), end.minusHours(3),
        "subject", "Not Meeting");
    assertEquals(expected2, multipleCalendarModel.queryEvent(start.toLocalDate()).get(0));
    multipleCalendarModel.use("Default");
    assertEquals(expected3, multipleCalendarModel.queryEvent(start.toLocalDate()).get(0));
    assertNotEquals(expected2, multipleCalendarModel.queryEvent(start.toLocalDate()).get(0));
  }

  @Test
  public void testCopyEventsOnDateIgnoresDuplicateEvents() {
    multipleCalendarModel.addSingleEvent("Meeting", start.minusHours(1), end.minusHours(1));
    multipleCalendarModel.addSingleEvent("Running", start.plusHours(2), end.plusHours(2));
    multipleCalendarModel.addRepeatingEvent("Class", start.minusDays(1), end.minusDays(1),
        "MTWRF", 5);
    multipleCalendarModel.create("Copy To", "America/Los_Angeles");
    multipleCalendarModel.use("Copy To");
    multipleCalendarModel.addSingleEvent("Meeting", start.plusDays(2).minusHours(4),
        end.plusDays(2).minusHours(4));
    multipleCalendarModel.use("Default");
    assertTrue(multipleCalendarModel.copyEvents(start.toLocalDate(), "Copy To",
        start.plusDays(2).toLocalDate()));
    IEvent expected1 = new SingleEvent("Meeting", start.plusDays(2).minusHours(4),
        end.plusDays(2).minusHours(4));
    IEvent expected2 = new SingleEvent("Running", start.plusDays(2).minusHours(1),
        end.plusDays(2).minusHours(1));
    IEvent expected3 = new SingleEvent("Class", start.plusDays(2).minusHours(3),
        end.plusDays(2).minusHours(3));
    multipleCalendarModel.use("Copy To");
    ArrayList<IEvent> events = multipleCalendarModel.queryEvent(start.minusDays(10),
        end.plusDays(20));
    assertEquals(3, events.size());
    assertTrue(events.contains(expected1));
    assertTrue(events.contains(expected2));
    assertEquals(expected3, events.get(2).getExactMatch("Class", start.plusDays(2).minusHours(3)));
  }

  @Test
  public void testCopyEventsInRangeWithParitalSeries() {
    multipleCalendarModel.addSingleEvent("Meeting", start.minusHours(1), end.minusHours(1));
    multipleCalendarModel.addSingleEvent("Running", start.plusHours(2), end.plusHours(2));
    multipleCalendarModel.addRepeatingEvent("Class", start.minusDays(1), end.minusDays(1),
        "MTWRF", 5);
    multipleCalendarModel.create("Copy To", "America/Los_Angeles");
    assertTrue(multipleCalendarModel.copyEvents(start.toLocalDate(), end.plusDays(3).toLocalDate(),
        "Copy To", start.plusDays(2).toLocalDate()));
    IEvent expected1 = new SingleEvent("Meeting", start.plusDays(2).minusHours(4),
        end.plusDays(2).minusHours(4));
    IEvent expected2 = new SingleEvent("Running", start.plusDays(2).minusHours(1),
        end.plusDays(2).minusHours(1));
    SeriesEvent expected3 = new SeriesEvent("Class", start.plusDays(2).minusHours(3),
        end.plusDays(2).minusHours(3), "SUW", 3);
    multipleCalendarModel.use("Copy To");
    ArrayList<IEvent> events = multipleCalendarModel.queryEvent(start.minusDays(10),
        end.plusDays(20));
    assertTrue(events.contains(expected1));
    assertTrue(events.contains(expected2));
    SeriesEvent outSeries = (SeriesEvent) events.get(2);
    for (int i = 0; i < outSeries.getEvents().size(); i++) {
      assertEquals(expected3.getEvents().get(i), outSeries.getEvents().get(i));
    }
  }

  @Test
  public void testCopyEventsInRangeOnSeriesKeepsMetaData() {
    multipleCalendarModel.addRepeatingEvent("Class", start, end, "MTWRF", 5);
    multipleCalendarModel.create("Copy To", "America/Los_Angeles");
    assertTrue(multipleCalendarModel.copyEvents(start.toLocalDate(), end.plusDays(10).toLocalDate(),
        "Copy To", start.plusDays(2).toLocalDate()));
    SeriesEvent expected = new SeriesEvent("Class", start.plusDays(2).minusHours(3),
        end.plusDays(2).minusHours(3), "SUWRF", 5);
    multipleCalendarModel.use("Copy To");
    ArrayList<IEvent> events = multipleCalendarModel.queryEvent(start.minusDays(10),
        end.plusDays(20));
    SeriesEvent outSeries = (SeriesEvent) events.get(0);
    for (int i = 0; i < outSeries.getEvents().size(); i++) {
      assertEquals(expected.getEvents().get(i), outSeries.getEvents().get(i));
    }

    SeriesEvent expected2 = new SeriesEvent("Still Class", start.plusDays(2).minusHours(3),
        end.plusDays(2).minusHours(3), "SUWRF", 5);
    multipleCalendarModel.editEntireSeries("Class", start.plusDays(2).minusHours(3), "subject",
        "Still Class");
    events = multipleCalendarModel.queryEvent(start.minusDays(10), end.plusDays(20));
    outSeries = (SeriesEvent) events.get(0);
    for (int i = 0; i < outSeries.getEvents().size(); i++) {
      assertEquals(expected2.getEvents().get(i), outSeries.getEvents().get(i));
    }
  }

  @Test(expected = NoCalendar.class)
  public void testCopyEventsInRangeNoCalendar() {
    MultipleCalendarModel empty = new MultipleCalendarModelImpl();
    empty.create("Default", "America/New_York");
    empty.create("Copy To", "America/Los_Angeles");
    empty.copyEvents(start.toLocalDate(), end.plusDays(5).toLocalDate(), "Copy To",
        start.plusDays(2).toLocalDate());
  }

  @Test(expected = InvalidCalendar.class)
  public void testCopyEventsInRangeNoTargetCalendar() {
    multipleCalendarModel.addSingleEvent("Meeting", start, end);
    multipleCalendarModel.copyEvents(start.toLocalDate(), end.plusDays(5).toLocalDate(), "Copy To",
        start.plusDays(2).toLocalDate());
  }

  @Test
  public void testCopyEventsInRangeCantFindEvents() {
    multipleCalendarModel.create("Copy To", "America/Los_Angeles");
    assertFalse(multipleCalendarModel.copyEvents(start.toLocalDate(), end.plusDays(5).toLocalDate(),
        "Copy To", start.plusDays(2).toLocalDate()));
  }

  @Test
  public void testCopyEventsInRangeAliasingCheck() {
    multipleCalendarModel.addSingleEvent("Meeting", start, end);
    multipleCalendarModel.create("Copy To", "America/Los_Angeles");
    multipleCalendarModel.copyEvents(start.toLocalDate(), end.plusDays(1).toLocalDate(), "Copy To",
        start.toLocalDate());
    multipleCalendarModel.use("Copy To");
    IEvent expected = new SingleEvent("Meeting", start.minusHours(3), end.minusHours(3));
    IEvent expected2 = new SingleEvent("Not Meeting", start.minusHours(3), end.minusHours(3));
    IEvent expected3 = new SingleEvent("Meeting", start, end);
    assertEquals(expected, multipleCalendarModel.queryEvent(start.toLocalDate()).get(0));
    multipleCalendarModel.editSingleEvent("Meeting", start.minusHours(3), end.minusHours(3),
        "subject", "Not Meeting");
    assertEquals(expected2, multipleCalendarModel.queryEvent(start.toLocalDate()).get(0));
    multipleCalendarModel.use("Default");
    assertEquals(expected3, multipleCalendarModel.queryEvent(start.toLocalDate()).get(0));
    assertNotEquals(expected2, multipleCalendarModel.queryEvent(start.toLocalDate()).get(0));
  }

  @Test
  public void testCopyEventsInRangeWithIgnoresDuplicates() {
    multipleCalendarModel.addSingleEvent("Meeting", start.minusHours(1), end.minusHours(1));
    multipleCalendarModel.addSingleEvent("Running", start.plusHours(2), end.plusHours(2));
    multipleCalendarModel.addRepeatingEvent("Class", start.minusDays(1), end.minusDays(1),
        "MTWRF", 5);
    multipleCalendarModel.create("Copy To", "America/Los_Angeles");
    multipleCalendarModel.use("Copy To");
    multipleCalendarModel.addSingleEvent("Meeting", start.plusDays(2).minusHours(4),
        end.plusDays(2).minusHours(4));
    multipleCalendarModel.addSingleEvent("Class", start.plusDays(2).minusHours(3),
        end.plusDays(2).minusHours(3));
    multipleCalendarModel.use("Default");
    assertTrue(multipleCalendarModel.copyEvents(start.toLocalDate(), end.plusDays(3).toLocalDate(),
        "Copy To", start.plusDays(2).toLocalDate()));
    IEvent expected1 = new SingleEvent("Meeting", start.plusDays(2).minusHours(4),
        end.plusDays(2).minusHours(4));
    IEvent expected2 = new SingleEvent("Class", start.plusDays(2).minusHours(3),
        end.plusDays(2).minusHours(3));
    IEvent expected3 = new SingleEvent("Running", start.plusDays(2).minusHours(1),
        end.plusDays(2).minusHours(1));
    multipleCalendarModel.use("Copy To");
    ArrayList<IEvent> events = multipleCalendarModel.queryEvent(start.minusDays(10),
        end.plusDays(20));
    assertEquals(3, events.size());
    assertTrue(events.contains(expected1));
    assertTrue(events.contains(expected2));
    assertTrue(events.contains(expected3));
  }

  @Test(expected = InvalidProperty.class)
  public void testCreateCalendarWithNullTimeZone() {
    multipleCalendarModel.create("New Calendar", null);
  }

  @Test
  public void testCreateMultipleCalendarsWithSameTimeZone() {
    multipleCalendarModel.create("Calendar1", "America/New_York");
    multipleCalendarModel.create("Calendar2", "America/New_York");

    multipleCalendarModel.use("Calendar1");
    assertEquals(TimeZone.getTimeZone("America/New_York"),
        multipleCalendarModel.getCurrentCalendar().getTimeZone());

    multipleCalendarModel.use("Calendar2");
    assertEquals(TimeZone.getTimeZone("America/New_York"),
        multipleCalendarModel.getCurrentCalendar().getTimeZone());
  }

  @Test(expected = NoCalendar.class)
  public void testAddEventWhenNoCalendarSelected() {
    MultipleCalendarModel empty = new MultipleCalendarModelImpl();
    empty.create("Test", "America/New_York");
    empty.addSingleEvent("Meeting", start, end);
  }

  @Test(expected = NoCalendar.class)
  public void testQueryEventWhenNoCalendarSelected() {
    MultipleCalendarModel empty = new MultipleCalendarModelImpl();
    empty.create("Test", "America/New_York");
    empty.queryEvent(start.toLocalDate());
  }

  @Test(expected = NoCalendar.class)
  public void testAddRepeatingEventWithCountWhenNoCalendarSelected() {
    MultipleCalendarModel empty = new MultipleCalendarModelImpl();
    empty.create("Test", "America/New_York");
    empty.addRepeatingEvent("Meeting", start, end, "MWF", 5);
  }

  @Test(expected = NoCalendar.class)
  public void testAddRepeatingEventWithEndDateWhenNoCalendarSelected() {
    MultipleCalendarModel empty = new MultipleCalendarModelImpl();
    empty.create("Test", "America/New_York");
    empty.addRepeatingEvent("Meeting", start, end, "MWF", LocalDate.of(2024, 12, 31));
  }

  @Test(expected = NoCalendar.class)
  public void testEditSingleEventWhenNoCalendarSelected() {
    MultipleCalendarModel empty = new MultipleCalendarModelImpl();
    empty.create("Test", "America/New_York");
    empty.editSingleEvent("Meeting", start, end, "subject", "New Meeting");
  }

  @Test(expected = NoCalendar.class)
  public void testEditFutureSeriesEventsWhenNoCalendarSelected() {
    MultipleCalendarModel empty = new MultipleCalendarModelImpl();
    empty.create("Test", "America/New_York");
    empty.editFutureSeriesEvents("Meeting", start, "subject", "New Meeting");
  }

  @Test(expected = NoCalendar.class)
  public void testEditEntireSeriesWhenNoCalendarSelected() {
    MultipleCalendarModel empty = new MultipleCalendarModelImpl();
    empty.create("Test", "America/New_York");
    empty.editEntireSeries("Meeting", start, "subject", "New Meeting");
  }

  @Test(expected = NoCalendar.class)
  public void testQueryEventByDateWhenNoCalendarSelected() {
    MultipleCalendarModel empty = new MultipleCalendarModelImpl();
    empty.create("Test", "America/New_York");
    empty.queryEvent(LocalDate.of(2024, 1, 1));
  }

  @Test(expected = NoCalendar.class)
  public void testQueryEventByDateTimeRangeWhenNoCalendarSelected() {
    MultipleCalendarModel empty = new MultipleCalendarModelImpl();
    empty.create("Test", "America/New_York");
    empty.queryEvent(start, end);
  }

  @Test(expected = NoCalendar.class)
  public void testGetStatusWhenNoCalendarSelected() {
    MultipleCalendarModel empty = new MultipleCalendarModelImpl();
    empty.create("Test", "America/New_York");
    empty.getStatus(start);
  }

  @Test
  public void testCopyEventBetweenSameCalendar() {
    multipleCalendarModel.addSingleEvent("Meeting", start, end);
    multipleCalendarModel.copyEvent("Meeting", start, "Default", start.plusDays(1));

    IEvent expected = new SingleEvent("Meeting", start.plusDays(1), end.plusDays(1));
    assertEquals(expected, multipleCalendarModel.getCurrentCalendar().queryExactEvent("Meeting",
        start.plusDays(1)));

    IEvent original = new SingleEvent("Meeting", start, end);
    assertEquals(original, multipleCalendarModel.getCurrentCalendar().queryExactEvent("Meeting",
        start));
  }

  @Test
  public void testCopyEventsCausesOverlap() {
    multipleCalendarModel.addSingleEvent("Meeting", start, end);
    multipleCalendarModel.create("Copy To", "America/Los_Angeles");
    multipleCalendarModel.use("Copy To");
    multipleCalendarModel.addSingleEvent("Existing", start.minusHours(3), end.minusHours(3));
    multipleCalendarModel.use("Default");

    assertTrue(multipleCalendarModel.copyEvents(start.toLocalDate(), "Copy To",
        start.toLocalDate()));
  }

  @Test
  public void testCopyEventsInRangeWithStartAfterEnd() {
    multipleCalendarModel.create("Copy To", "America/Los_Angeles");
    assertFalse(multipleCalendarModel.copyEvents(end.toLocalDate(), start.toLocalDate(),
        "Copy To", start.toLocalDate()));
  }

  @Test
  public void testEditCalendarNameCaseInsensitive() {
    multipleCalendarModel.create("TestCal", "America/Los_Angeles");
    multipleCalendarModel.edit("Default", "name", "testcal");
    assertEquals("testcal", multipleCalendarModel.getCurrentCalendar().getName());
  }

  @Test
  public void testCreateCalendarNameCaseInsensitive() {
    multipleCalendarModel.create("default", "America/Los_Angeles");
    multipleCalendarModel.use("default");
    assertEquals("default", multipleCalendarModel.getCurrentCalendar().getName());
  }

  @Test
  public void testCopyComplexSeriesEventPartially() {
    multipleCalendarModel.addRepeatingEvent("Complex Class", start, end, "MWF", 10);
    multipleCalendarModel.create("Copy To", "America/Los_Angeles");

    LocalDate copyStart = start.plusDays(5).toLocalDate();
    LocalDate copyEnd = start.plusDays(15).toLocalDate();

    assertTrue(multipleCalendarModel.copyEvents(copyStart, copyEnd, "Copy To",
        start.plusDays(20).toLocalDate()));

    multipleCalendarModel.use("Copy To");
    ArrayList<IEvent> copiedEvents = multipleCalendarModel.queryEvent(
        start.plusDays(15), start.plusDays(30));

    assertFalse(copiedEvents.isEmpty());
    assertTrue(copiedEvents.get(0) instanceof SeriesEvent);
  }

  @Test
  public void testSwitchBetweenMultipleCalendarsPreservesState() {
    multipleCalendarModel.addSingleEvent("Default Event", start, end);

    multipleCalendarModel.create("Calendar2", "America/Los_Angeles");
    multipleCalendarModel.use("Calendar2");
    multipleCalendarModel.addSingleEvent("Cal2 Event", start.plusHours(1), end.plusHours(1));

    multipleCalendarModel.create("Calendar3", "Europe/London");
    multipleCalendarModel.use("Calendar3");
    multipleCalendarModel.addSingleEvent("Cal3 Event", start.plusHours(2), end.plusHours(2));

    multipleCalendarModel.use("Default");
    assertTrue(
        multipleCalendarModel.queryEvent(start.toLocalDate()).get(0).toString().contains("Default Event"));
    assertEquals(1, multipleCalendarModel.queryEvent(start.toLocalDate()).size());

    multipleCalendarModel.use("Calendar2");
    assertTrue(
        multipleCalendarModel.queryEvent(start.toLocalDate()).get(0).toString().contains("Cal2 Event"));
    assertEquals(1, multipleCalendarModel.queryEvent(start.toLocalDate()).size());

    multipleCalendarModel.use("Calendar3");
    assertTrue(
        multipleCalendarModel.queryEvent(start.toLocalDate()).get(0).toString().contains("Cal3 Event"));
    assertEquals(1, multipleCalendarModel.queryEvent(start.toLocalDate()).size());
  }
}