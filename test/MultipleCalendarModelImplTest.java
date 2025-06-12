import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    HashSet<IEvent> events = multipleCalendarModel.queryEvent(start.toLocalDate());
    IEvent expected = new SingleEvent("Meeting", start.minusHours(3), end.minusHours(3));
    assertTrue("Expected shifted 'Meeting' event not found", events.contains(expected));
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

    HashSet<IEvent> events = multipleCalendarModel.queryEvent(start.plusDays(2).toLocalDate());

    assertTrue("Expected copied event not found in 'Copy To' calendar", events.contains(expected));
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
    HashSet<IEvent> events = multipleCalendarModel.queryEvent(start.plusDays(2).toLocalDate());
    assertTrue("Expected copied event not found", events.contains(expected));
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
    HashSet<IEvent> events = multipleCalendarModel.queryEvent(start.toLocalDate());
    assertTrue("Expected event not found before edit", events.contains(expected));
    multipleCalendarModel.editSingleEvent("Meeting", start, end, "subject", "Not Meeting");
    events = multipleCalendarModel.queryEvent(start.toLocalDate());
    assertTrue("Edited event not found after edit", events.contains(expected2));
    assertFalse("Original event still present after edit", events.contains(expected));
    multipleCalendarModel.use("Default");
    events = multipleCalendarModel.queryEvent(start.toLocalDate());
    assertTrue("Original event missing from Default calendar", events.contains(expected));
    assertFalse("Edited event incorrectly present in Default calendar", events.contains(expected2));
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
    HashSet<IEvent> events = multipleCalendarModel.queryEvent(start.minusDays(10), end.plusDays(20));
    assertTrue(events.contains(expected1));
    assertTrue(events.contains(expected2));
    boolean foundClassEvent = false;
    String expected3Str = expected3.toString();
    for (IEvent event : events) {
      if (event.toString().equals(expected3Str)) {
        assertEquals(expected3, event);
        foundClassEvent = true;
        break;
      }
    }
    assertTrue("Expected 'Class' event not found in copied events", foundClassEvent);
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
    HashSet<IEvent> events = multipleCalendarModel.queryEvent(start.toLocalDate());
    assertTrue(events.contains(expected));
    multipleCalendarModel.editSingleEvent("Meeting", start.minusHours(3), end.minusHours(3),
        "subject", "Not Meeting");
    events = multipleCalendarModel.queryEvent(start.toLocalDate());
    assertTrue(events.contains(expected2));
    multipleCalendarModel.use("Default");
    HashSet<IEvent> originalEvents = multipleCalendarModel.queryEvent(start.toLocalDate());
    assertTrue(originalEvents.contains(expected3));
    assertFalse(originalEvents.contains(expected2));
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
    HashSet<IEvent> events = multipleCalendarModel.queryEvent(start.minusDays(10),
        end.plusDays(20));
    assertEquals(3, events.size());
    assertTrue(events.contains(expected1));
    assertTrue(events.contains(expected2));
    boolean foundExactMatch = false;
    for (IEvent event : events) {
      if (event.equals(expected3)) {
        foundExactMatch = true;
        break;
      }
    }
    assertTrue("Expected class event not found in events", foundExactMatch);
  }


  @Test
  public void testCopyEventsInRangeWithPartialSeries() {
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
    HashSet<IEvent> events = multipleCalendarModel.queryEvent(start.minusDays(10),
        end.plusDays(20));
    assertTrue(events.contains(expected1));
    assertTrue(events.contains(expected2));
    SeriesEvent outSeries = null;
    for (IEvent event : events) {
      if (event instanceof SeriesEvent) {
        outSeries = (SeriesEvent) event;
        break;
      }
    }
    assertNotNull("Expected SeriesEvent not found", outSeries);
    List<IEvent> expectedSubs = (List<IEvent>) expected3.getEvents();
    List<IEvent> actualSubs = (List<IEvent>) outSeries.getEvents();
    assertEquals(expectedSubs.size(), actualSubs.size());
    for (int i = 0; i < expectedSubs.size(); i++) {
      assertEquals(expectedSubs.get(i), actualSubs.get(i));
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
    HashSet<IEvent> events = multipleCalendarModel.queryEvent(start.minusDays(10), end.plusDays(20));
    SeriesEvent outSeries = null;
    for (IEvent event : events) {
      if (event instanceof SeriesEvent) {
        outSeries = (SeriesEvent) event;
        break;
      }
    }
    assertNotNull("SeriesEvent not found in copied events", outSeries);
    List<IEvent> expectedSubs = (List<IEvent>) expected.getEvents();
    List<IEvent> actualSubs = (List<IEvent>) outSeries.getEvents();
    assertEquals(expectedSubs.size(), actualSubs.size());
    for (int i = 0; i < expectedSubs.size(); i++) {
      assertEquals(expectedSubs.get(i), actualSubs.get(i));
    }
    SeriesEvent expected2 = new SeriesEvent("Still Class", start.plusDays(2).minusHours(3),
        end.plusDays(2).minusHours(3), "SUWRF", 5);
    multipleCalendarModel.editEntireSeries("Class", start.plusDays(2).minusHours(3), "subject", "Still Class");
    events = multipleCalendarModel.queryEvent(start.minusDays(10), end.plusDays(20));
    outSeries = null;
    for (IEvent event : events) {
      if (event instanceof SeriesEvent) {
        outSeries = (SeriesEvent) event;
        break;
      }
    }
    assertNotNull("SeriesEvent not found after editing", outSeries);
    expectedSubs = (List<IEvent>) expected2.getEvents();
    actualSubs = (List<IEvent>) outSeries.getEvents();
    assertEquals(expectedSubs.size(), actualSubs.size());
    for (int i = 0; i < expectedSubs.size(); i++) {
      assertEquals(expectedSubs.get(i), actualSubs.get(i));
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
    HashSet<IEvent> copiedEvents = multipleCalendarModel.queryEvent(start.minusDays(1), end.plusDays(1));
    assertTrue(copiedEvents.contains(expected));
    multipleCalendarModel.editSingleEvent("Meeting", start.minusHours(3), end.minusHours(3),
        "subject", "Not Meeting");
    copiedEvents = multipleCalendarModel.queryEvent(start.minusDays(1), end.plusDays(1));
    assertTrue(copiedEvents.contains(expected2));
    multipleCalendarModel.use("Default");
    HashSet<IEvent> originalEvents = multipleCalendarModel.queryEvent(start.minusDays(1), end.plusDays(1));
    assertTrue(originalEvents.contains(expected3));
    assertFalse(originalEvents.contains(expected2));
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
    HashSet<IEvent> events = multipleCalendarModel.queryEvent(start.minusDays(10),
        end.plusDays(20));
    assertEquals(3, events.size());
    assertTrue(events.contains(expected1));
    assertTrue(events.contains(expected2));
    assertTrue(events.contains(expected3));
  }
}