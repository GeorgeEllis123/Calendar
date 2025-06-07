import model.SeriesEvent;
import model.IEvent;
import model.SingleEvent;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@code model.SeriesEvent} class.
 */
public class SeriesEventTest {

  private IEvent weeklyMeeting;
  private IEvent weeklyMeeting2;
  private LocalDateTime start;
  private LocalDateTime end;

  @Before
  public void setUp() {
    start = LocalDateTime.of(2025, 6, 2, 9, 0);
    end = LocalDateTime.of(2025, 6, 2, 10, 0);
    weeklyMeeting = new SeriesEvent("Meeting", start, end, "MWF", LocalDate.of(2025, 6, 13));
    weeklyMeeting2 = new SeriesEvent("Meeting", start, end, "MWF", 6);
  }

  @Test
  public void testContainsDateTimeMatch() {
    LocalDate[] expectedDates = {
            LocalDate.of(2025, 6, 2),
            LocalDate.of(2025, 6, 4),
            LocalDate.of(2025, 6, 6),
            LocalDate.of(2025, 6, 9),
            LocalDate.of(2025, 6, 11),
            LocalDate.of(2025, 6, 13)
    };

    for (LocalDate date : expectedDates) {
      LocalDateTime testTime = date.atTime(9, 30);
      assertTrue(weeklyMeeting.containsDateTime(testTime));
      assertTrue(weeklyMeeting2.containsDateTime(testTime));
    }
  }

  @Test
  public void testDoesNotContainInvalidDateTime() {
    LocalDateTime invalidTime = LocalDateTime.of(2025, 6, 3, 9, 30);
    assertFalse(weeklyMeeting.containsDateTime(invalidTime));
    assertFalse(weeklyMeeting2.containsDateTime(invalidTime));
  }

  @Test
  public void testGetIfEventIsOnDateIncluded() {
    LocalDate check = LocalDate.of(2025, 6, 4);

    ArrayList<IEvent> r1 = weeklyMeeting.getIfEventIsOnDate(check);
    ArrayList<IEvent> r2 = weeklyMeeting2.getIfEventIsOnDate(check);

    assertEquals(1, r1.size());
    assertEquals(1, r2.size());
    assertEquals(r1.get(0), r2.get(0));
  }

  @Test
  public void testGetIfEventIsOnDateNotIncluded() {
    LocalDate check = LocalDate.of(2025, 6, 3);

    assertTrue(weeklyMeeting.getIfEventIsOnDate(check).isEmpty());
    assertTrue(weeklyMeeting2.getIfEventIsOnDate(check).isEmpty());
  }

  @Test
  public void testGetIfBetweenFullRange() {
    LocalDateTime rangeStart = LocalDateTime.of(2025, 6, 2, 8, 0);
    LocalDateTime rangeEnd = LocalDateTime.of(2025, 6, 13, 11, 0);

    ArrayList<IEvent> r1 = weeklyMeeting.getIfBetween(rangeStart, rangeEnd);
    ArrayList<IEvent> r2 = weeklyMeeting2.getIfBetween(rangeStart, rangeEnd);

    assertEquals(6, r1.size());
    assertEquals(6, r2.size());
  }

  @Test
  public void testGetIfBetweenPartialOverlap() {
    LocalDateTime rangeStart = LocalDateTime.of(2025, 6, 5, 8, 0);
    LocalDateTime rangeEnd = LocalDateTime.of(2025, 6, 9, 9, 15);

    ArrayList<IEvent> r1 = weeklyMeeting.getIfBetween(rangeStart, rangeEnd);
    ArrayList<IEvent> r2 = weeklyMeeting2.getIfBetween(rangeStart, rangeEnd);

    assertEquals(1, r1.size());
    assertEquals(1, r2.size());
  }

  @Test
  public void testGetIfBetweenNoMatch() {
    LocalDateTime rangeStart = LocalDateTime.of(2025, 7, 1, 8, 0);
    LocalDateTime rangeEnd = LocalDateTime.of(2025, 7, 2, 9, 0);

    assertTrue(weeklyMeeting.getIfBetween(rangeStart, rangeEnd).isEmpty());
    assertTrue(weeklyMeeting2.getIfBetween(rangeStart, rangeEnd).isEmpty());
  }

  @Test
  public void testEqualsAndHashCode() {
    SeriesEvent copy = new SeriesEvent("Meeting", start, end, "MWF", 6);
    assertEquals(weeklyMeeting, weeklyMeeting2);
    assertEquals(weeklyMeeting2, copy);
    assertEquals(weeklyMeeting2.hashCode(), copy.hashCode());
  }

  @Test
  public void testNotEqualsDifferentSubject() {
    IEvent diff = new SeriesEvent("Different", start, end, "MWF", 6);
    assertNotEquals(weeklyMeeting2, diff);
  }

  @Test
  public void testNotEqualsDifferentPattern() {
    IEvent diff = new SeriesEvent("Meeting", start, end, "MTWRF", 6);
    assertNotEquals(weeklyMeeting2, diff);
  }

  @Test
  public void testEqualsSelf() {
    assertEquals(weeklyMeeting, weeklyMeeting);
    assertEquals(weeklyMeeting2, weeklyMeeting2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidUntilConstructor() {
    LocalDateTime start = LocalDateTime.of(2025, 6, 5, 12, 0);
    LocalDateTime end = LocalDateTime.of(2025, 6, 5, 10, 0);
    LocalDate untilDate = LocalDate.of(2025, 6, 20);

    new SeriesEvent("Bad Event", start, end, "MWF", untilDate);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidRepeatConstructor() {
    LocalDateTime start = LocalDateTime.of(2025, 6, 5, 12, 0);
    LocalDateTime end = LocalDateTime.of(2025, 6, 5, 10, 0);

    new SeriesEvent("Bad Event", start, end, "MWF", 6);
  }

  @Test
  public void testCheckDuplicateMatching() {
    IEvent event = new SingleEvent("Meeting", start, end);
    assertTrue(weeklyMeeting.checkDuplicate(event));
    assertTrue(weeklyMeeting2.checkDuplicate(event));
  }

  @Test
  public void testCheckDuplicateNotMatching() {
    IEvent event = new SingleEvent("Bad Event", start, end);
    assertFalse(weeklyMeeting.checkDuplicate(event));
    assertFalse(weeklyMeeting2.checkDuplicate(event));
  }

  @Test
  public void testGetAllMatchingEventsAfterFindSomeEvents() {
    LocalDateTime queryTime = LocalDateTime.of(2025, 6, 4, 9, 0);
    ArrayList<IEvent> result = weeklyMeeting.getAllMatchingEventsAfter("Meeting", queryTime);

    assertFalse(result.isEmpty());
    for (IEvent e : result) {
      assertTrue(e.toString().contains("Meeting"));
      assertTrue(e.toString().contains("2025-06"));
    }
  }

  @Test
  public void testGetAllMatchingEventsFindAll() {
    ArrayList<IEvent> result = weeklyMeeting.getAllMatchingEvents("Meeting", start);
    assertEquals(6, result.size());

    for (IEvent e : result) {
      String output = e.toString();
      assertTrue(output.contains("Meeting"));
      assertTrue(output.contains("2025-06"));
      assertTrue(output.contains("09:00"));
    }
  }

  @Test
  public void testGetAllMatchingEventsFindAll_byOccurrences() {
    ArrayList<IEvent> result = weeklyMeeting2.getAllMatchingEvents("Meeting", start);
    assertEquals(6, result.size());

    for (IEvent e : result) {
      assertTrue(e.toString().contains("Meeting"));
    }
  }

  @Test
  public void testGetAllMatchingEventsAfterNoneFound() {
    ArrayList<IEvent> result = weeklyMeeting.getAllMatchingEventsAfter("NotMeeting", start);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testGetAllMatchingEventsFindsAll() {
    ArrayList<IEvent> result = weeklyMeeting.getAllMatchingEvents("Meeting", start);
    assertFalse(result.isEmpty());
    for (IEvent e : result) {
      assertTrue(e.toString().contains("Meeting"));
    }
  }

  @Test
  public void testGetAllMatchingEventsNoneFound() {
    ArrayList<IEvent> result = weeklyMeeting.getAllMatchingEvents("NotMeeting", start);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testGetExactMatchFoundOne() {
    ArrayList<IEvent> result = weeklyMeeting.getExactMatch("Meeting", start, end);
    assertEquals(1, result.size());
    assertTrue(result.get(0).toString().contains("2025-06-02T09:00"));
  }

  @Test
  public void testGetExactMatchFoundOneInMiddle() {
    ArrayList<IEvent> result = weeklyMeeting.getExactMatch("Meeting", start.plusDays(2),
        end.plusDays(2));
    assertEquals(1, result.size());
    assertTrue(result.get(0).toString().contains("2025-06-04T09:00"));
  }

  @Test
  public void testGetExactMatchNoneFound() {
    ArrayList<IEvent> result = weeklyMeeting.getExactMatch("Meeting", start, end.plusHours(1));
    assertTrue(result.isEmpty());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testGetEdittedCopyThrowsException() {
    weeklyMeeting.getEdittedCopy("subject", "Updated");
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testEditEventThrowsException() {
    weeklyMeeting.editEvent("subject", "Updated");
  }
}
