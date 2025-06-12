import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.TimeZone;

import model.exceptions.InvalidEvent;
import model.CalendarModel;
import model.IEvent;
import model.ModifiableCalendarImpl;
import model.SeriesEvent;
import model.SingleEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@code model.ModifiableCalendarImpl} class.
 */
public class ModifiableCalendarImplTest extends ACalendarTest {

  ModifiableCalendarImpl estCal;
  ModifiableCalendarImpl pstCal;

  @Override
  protected CalendarModel getCalendarModel() {
    return new ModifiableCalendarImpl("School", TimeZone.getTimeZone("America/New_York"));
  }

  @Before
  public void setUp2() {
    estCal = new ModifiableCalendarImpl("School", TimeZone.getTimeZone("America/New_York"));
    pstCal = new ModifiableCalendarImpl("Work", TimeZone.getTimeZone("America/Los_Angeles"));
  }

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
  public void testQueryExactEventWithJustStartMultipleFound() {
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
    estCal.add(newEvent, start.minusDays(2).toLocalDate(),
        TimeZone.getTimeZone("America/Los_Angeles"));
    assertEquals(expected, estCal.queryExactEvent("New Meeting", start.minusDays(2).plusHours(3)));
  }

  @Test
  public void testAddEventTZAndDateChangeFromESTToPST() {
    assertNull(pstCal.queryExactEvent("New Meeting", start.minusDays(2).minusHours(3)));
    IEvent newEvent = new SingleEvent("New Meeting", start, end);
    IEvent expected = new SingleEvent("New Meeting", start.minusDays(2).minusHours(3),
        end.minusDays(2).minusHours(3));
    pstCal.add(newEvent, start.minusDays(2).toLocalDate(),
        TimeZone.getTimeZone("America/New_York"));
    assertEquals(expected, pstCal.queryExactEvent("New Meeting", start.minusDays(2).minusHours(3)));
  }

  @Test
  public void testAddEventTZAndDateChangeTZSame() {
    assertNull(estCal.queryExactEvent("New Meeting", start.minusDays(2)));
    IEvent newEvent = new SingleEvent("New Meeting", start, end);
    IEvent expected = new SingleEvent("New Meeting", start.minusDays(2), end.minusDays(2));
    estCal.add(newEvent, start.minusDays(2).toLocalDate(),
        TimeZone.getTimeZone("America/New_York"));
    assertEquals(expected, estCal.queryExactEvent("New Meeting", start.minusDays(2)));
  }

  @Test
  public void testAddEventTZAndDateChangeFromMITToESTChangesDay() {
    assertNull(estCal.queryExactEvent("New Meeting", start.minusHours(17).minusDays(2)));
    IEvent newEvent = new SingleEvent("New Meeting", start, end);
    IEvent expected = new SingleEvent("New Meeting", start.minusHours(17).minusDays(2),
        end.minusHours(17).minusDays(2));
    estCal.add(newEvent, start.minusDays(2).toLocalDate(), TimeZone.getTimeZone("Pacific/Apia"));
    assertEquals(expected, estCal.queryExactEvent("New Meeting",
        start.minusHours(17).minusDays(2)));
  }

  @Test
  public void testAddEventTZAndDateChangeFromMSTToESTChangesDay() {
    LocalDateTime veryLateStart = start.plusHours(12);
    LocalDateTime veryLateEnd = end.plusHours(12);
    assertNull(estCal.queryExactEvent("New Meeting", veryLateStart.plusHours(2).minusDays(2)));
    IEvent newEvent = new SingleEvent("New Meeting", veryLateStart, veryLateEnd);
    IEvent expected = new SingleEvent("New Meeting", veryLateStart.plusHours(2).minusDays(2),
        veryLateEnd.plusHours(2).minusDays(2));
    estCal.add(newEvent, veryLateStart.minusDays(2).toLocalDate(),
        TimeZone.getTimeZone("America/Denver"));
    assertEquals(expected, estCal.queryExactEvent("New Meeting",
        veryLateStart.plusHours(2).minusDays(2)));
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

  @Test
  public void testAddEventTZAndDateChangeFromPSTToESTSeries() {
    assertTrue(estCal.queryEvent(start.minusDays(4), end.plusDays(10)).isEmpty());
    IEvent newEvent = new SeriesEvent("Class", start, end, "MWF", LocalDate.of(2025, 6, 11));
    IEvent expected = new SeriesEvent("Class", start.minusDays(2).plusHours(3),
        end.minusDays(2).plusHours(3), "SMW", LocalDate.of(2025, 6, 9));
    // only minus 1 cause the 5th is a thursday
    estCal.add(newEvent, start.minusDays(1).toLocalDate(),
        TimeZone.getTimeZone("America/Los_Angeles"));
    assertEquals(expected, estCal.queryEvent(start.minusDays(4), end.plusDays(10)).get(0));
  }

  @Test
  public void testAddEventTZAndDateChangeFromPSTToESTSeriesCausesOverlap() {
    // only minus 1 cause the 5th is a thursday
    estCal.addSingleEvent("Class", start.minusDays(1).plusHours(3), end.minusDays(1).plusHours(3));
    IEvent newEvent = new SeriesEvent("Class", start, end, "MWF", LocalDate.of(2025, 6, 11));
    assertThrows(InvalidEvent.class, () -> estCal.add(newEvent, start.minusDays(1).toLocalDate(),
        TimeZone.getTimeZone("America/Los_Angeles")));
  }

  @Test
  public void testAddEventTZAndDateChangeFromPSTToESTSeriesCausesOverlapWithSeries() {
    estCal.addRepeatingEvent("Class", start.plusHours(3), end.plusHours(3), "MTWRFSU", 7);
    IEvent newEvent = new SeriesEvent("Class", start, end, "MWF", LocalDate.of(2025, 6, 11));
    assertThrows(InvalidEvent.class, () -> estCal.add(newEvent, start.minusDays(1).toLocalDate(),
        TimeZone.getTimeZone("America/Los_Angeles")));
  }

  @Test
  public void testAddEventTZAndRelativeDateChangeFromPSTToEST() {
    assertNull(estCal.queryExactEvent("New Meeting", start.plusDays(3).plusHours(3)));
    IEvent newEvent = new SingleEvent("New Meeting", start, end);
    IEvent expected = new SingleEvent("New Meeting", start.plusDays(3).plusHours(3),
        end.plusDays(3).plusHours(3));
    estCal.add(newEvent, start.minusDays(2).toLocalDate(), start.minusDays(5).toLocalDate(),
        TimeZone.getTimeZone("America/Los_Angeles"));
    assertEquals(expected, estCal.queryExactEvent("New Meeting", start.plusDays(3).plusHours(3)));
  }

  @Test
  public void testAddEventTZAndRelativeDateChangeFromESTToPST() {
    assertNull(pstCal.queryExactEvent("New Meeting", start.plusDays(3).minusHours(3)));
    IEvent newEvent = new SingleEvent("New Meeting", start, end);
    IEvent expected = new SingleEvent("New Meeting", start.plusDays(3).minusHours(3),
        end.plusDays(3).minusHours(3));
    pstCal.add(newEvent, start.minusDays(2).toLocalDate(), start.minusDays(5).toLocalDate(),
        TimeZone.getTimeZone("America/New_York"));
    assertEquals(expected, pstCal.queryExactEvent("New Meeting", start.plusDays(3).minusHours(3)));
  }


  @Test
  public void testAddEventTZAndRelativeDateChangeTZSame() {
    assertNull(estCal.queryExactEvent("New Meeting", start.plusDays(3)));
    IEvent newEvent = new SingleEvent("New Meeting", start, end);
    IEvent expected = new SingleEvent("New Meeting", start.plusDays(3), end.plusDays(3));
    estCal.add(newEvent, start.minusDays(2).toLocalDate(), start.minusDays(5).toLocalDate(),
        TimeZone.getTimeZone("America/New_York"));
    assertEquals(expected, estCal.queryExactEvent("New Meeting", start.plusDays(3)));
  }

  @Test
  public void testAddEventTZAndRelativeDateChangeFromMITToESTChangesDay() {
    assertNull(estCal.queryExactEvent("New Meeting", start.minusHours(17).plusDays(4)));
    IEvent newEvent = new SingleEvent("New Meeting", start, end);
    IEvent expected = new SingleEvent("New Meeting", start.minusHours(17).plusDays(4),
        end.minusHours(17).plusDays(4));
    estCal.add(newEvent, start.minusDays(2).toLocalDate(), start.minusDays(5).toLocalDate(),
        TimeZone.getTimeZone("Pacific/Apia"));
    assertEquals(expected, estCal.queryExactEvent("New Meeting", start.minusHours(17).plusDays(4)));
  }

  @Test
  public void testAddEventTZAndRelativeDateChangeFromMSTToESTChangesDay() {
    LocalDateTime veryLateStart = start.plusHours(12);
    LocalDateTime veryLateEnd = end.plusHours(12);
    assertNull(estCal.queryExactEvent("New Meeting", veryLateStart.plusHours(2).plusDays(3)));
    IEvent newEvent = new SingleEvent("New Meeting", veryLateStart, veryLateEnd);
    IEvent expected = new SingleEvent("New Meeting", veryLateStart.plusHours(2).plusDays(3),
        veryLateEnd.plusHours(2).plusDays(3));
    estCal.add(newEvent, veryLateStart.minusDays(2).toLocalDate(), start.minusDays(5).toLocalDate(),
        TimeZone.getTimeZone("America/Denver"));
    assertEquals(expected, estCal.queryExactEvent("New Meeting",
        veryLateStart.plusHours(2).plusDays(3)));
  }

  @Test
  public void testAddEventTZAndRelativeDateChangeCausesOverlap() {
    estCal.addSingleEvent("New Meeting", start.minusDays(1).plusHours(3),
        end.minusDays(1).plusHours(3));
    IEvent newEvent = new SingleEvent("New Meeting", start, end);
    assertThrows(InvalidEvent.class, () -> estCal.add(newEvent, start.minusDays(2).toLocalDate(),
        start.minusDays(1).toLocalDate(), TimeZone.getTimeZone("America/Los_Angeles")));

    pstCal.addSingleEvent("New Meeting", start.minusDays(1).minusHours(3),
        end.minusDays(1).minusHours(3));
    assertThrows(InvalidEvent.class, () -> pstCal.add(newEvent, start.minusDays(2).toLocalDate(),
        start.minusDays(1).toLocalDate(), TimeZone.getTimeZone("America/New_York")));
  }

  @Test
  public void testAddEventTZAndRelativeDateChangeCausesOverlapWithSeries() {
    estCal.addRepeatingEvent("Class", start.plusHours(3), end.plusHours(3), "MTWRFSU", 7);
    IEvent newEvent = new SingleEvent("Class", start, end);
    assertThrows(InvalidEvent.class, () -> estCal.add(newEvent, start.plusDays(2).toLocalDate(),
        start.minusDays(1).toLocalDate(), TimeZone.getTimeZone("America/Los_Angeles")));

    pstCal.addRepeatingEvent("Class", start.minusHours(3), end.minusHours(3), "MTWRFSU", 7);
    assertThrows(InvalidEvent.class, () -> pstCal.add(newEvent, start.plusDays(2).toLocalDate(),
        start.minusDays(1).toLocalDate(), TimeZone.getTimeZone("America/New_York")));
  }


  @Test
  public void testAddEventTZAndRelativeDateChangeFromPSTToESTSeries() {
    assertTrue(estCal.queryEvent(start.minusDays(10), end.plusDays(20)).isEmpty());
    IEvent newEvent = new SeriesEvent("Class", start, end, "MWF", LocalDate.of(2025, 6, 13));
    IEvent expected = new SeriesEvent("Class", start.plusDays(4).plusHours(3),
        end.plusDays(4).plusHours(3), "MRS", LocalDate.of(2025, 6, 18));
    // only minus 1 cause the 5th is a thursday; start -4 is 5 day offset
    estCal.add(newEvent, start.minusDays(1).toLocalDate(), start.minusDays(4).toLocalDate(),
        TimeZone.getTimeZone("America/Los_Angeles"));
    assertEquals(expected, estCal.queryEvent(start.minusDays(10), end.plusDays(20)).get(0));
  }

  @Test
  public void testAddEventTZAndRelativeDateChangeFromPSTToESTSeriesCausesOverlap() {
    estCal.addSingleEvent("Class", start.plusDays(4).plusHours(3), end.plusDays(4).plusHours(3));
    IEvent newEvent = new SeriesEvent("Class", start, end, "MWF", LocalDate.of(2025, 6, 11));
    assertThrows(InvalidEvent.class, () -> estCal.add(newEvent, start.minusDays(1).toLocalDate(),
        start.minusDays(4).toLocalDate(), TimeZone.getTimeZone("America/Los_Angeles")));
  }

  @Test
  public void testAddEventTZAndRelativeDateChangeFromPSTToESTSeriesCausesOverlapWithSeries() {
    estCal.addRepeatingEvent("Class", start.plusHours(3), end.plusHours(3), "MTWRFSU", 7);
    IEvent newEvent = new SeriesEvent("Class", start, end, "MWF", LocalDate.of(2025, 6, 11));
    assertThrows(InvalidEvent.class, () -> estCal.add(newEvent, start.minusDays(1).toLocalDate(),
        start.minusDays(4).toLocalDate(), TimeZone.getTimeZone("America/Los_Angeles")));
  }

  @Test
  public void testChangeTimeZone() {
    estCal.addSingleEvent("Meeting", start.minusHours(1), end.minusHours(1));
    estCal.addSingleEvent("Running", start.plusHours(2), end.plusHours(2));
    estCal.addRepeatingEvent("Class", start.minusDays(1), end.minusDays(1), "MTWRF", 5);
    estCal.editTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
    IEvent expected1 = new SingleEvent("Meeting", start.minusHours(4), end.minusHours(4));
    IEvent expected2 = new SingleEvent("Running", start.minusHours(1), end.minusHours(1));
    SeriesEvent expected3 = new SeriesEvent("Class", start.minusDays(1).minusHours(3),
        end.minusDays(1).minusHours(3), "MTWRF", 5);
    ArrayList<IEvent> events = estCal.queryEvent(start.minusDays(10), end.plusDays(20));
    assertTrue(events.contains(expected1));
    assertTrue(events.contains(expected2));
    SeriesEvent outSeries = (SeriesEvent) events.get(2);
    for (int i = 0; i < outSeries.getEvents().size(); i++) {
      assertEquals(expected3.getEvents().get(i), outSeries.getEvents().get(i));
    }
  }

  @Test
  public void testChangeOverlapTemporarilyIgnored() {
    estCal.addSingleEvent("Meeting", start, end);
    estCal.addSingleEvent("Earlier Meeting", start.minusHours(3), end.minusHours(3));
    estCal.editTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
    IEvent expected1 = new SingleEvent("Meeting", start.minusHours(3), end.minusHours(3));
    IEvent expected2 = new SingleEvent("Earlier Meeting", start.minusHours(6), end.minusHours(6));
    ArrayList<IEvent> events = estCal.queryEvent(start.minusDays(10), end.plusDays(20));
    assertTrue(events.contains(expected1));
    assertTrue(events.contains(expected2));
  }

}