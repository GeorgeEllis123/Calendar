import model.SeriesEvent;
import model.IEvent;
import model.SingleEvent;

import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
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

    IEvent r1 = weeklyMeeting.getIfEventIsOnDate(check);
    IEvent r2 = weeklyMeeting2.getIfEventIsOnDate(check);

    assertEquals(r1, r2);
    assertEquals("- Meeting | 2025-06-04T09:00 to 2025-06-04T10:00\n", r1.toString());
  }

  @Test
  public void testGetIfEventIsOnDateNotIncluded() {
    LocalDate check = LocalDate.of(2025, 6, 3);

    assertNull(weeklyMeeting.getIfEventIsOnDate(check));
    assertNull(weeklyMeeting2.getIfEventIsOnDate(check));
  }

  @Test
  public void testGetIfBetweenFullRange() {
    LocalDateTime rangeStart = LocalDateTime.of(2025, 6, 2, 8, 0);
    LocalDateTime rangeEnd = LocalDateTime.of(2025, 6, 13, 11, 0);

    IEvent r1 = weeklyMeeting.getIfBetween(rangeStart, rangeEnd);
    IEvent r2 = weeklyMeeting2.getIfBetween(rangeStart, rangeEnd);

    assertEquals(r1, r2);
    assertEquals(
        "- Meeting | 2025-06-02T09:00 to 2025-06-02T10:00\n" +
        "- Meeting | 2025-06-04T09:00 to 2025-06-04T10:00\n" +
        "- Meeting | 2025-06-06T09:00 to 2025-06-06T10:00\n" +
        "- Meeting | 2025-06-09T09:00 to 2025-06-09T10:00\n" +
        "- Meeting | 2025-06-11T09:00 to 2025-06-11T10:00\n" +
        "- Meeting | 2025-06-13T09:00 to 2025-06-13T10:00\n", r1.toString());
  }

  @Test
  public void testGetIfBetweenPartialOverlap() {
    LocalDateTime rangeStart = LocalDateTime.of(2025, 6, 5, 8, 0);
    LocalDateTime rangeEnd = LocalDateTime.of(2025, 6, 10, 9, 15);

    IEvent r1 = weeklyMeeting.getIfBetween(rangeStart, rangeEnd);
    IEvent r2 = weeklyMeeting2.getIfBetween(rangeStart, rangeEnd);

    assertEquals(r1, r2);
    assertEquals("- Meeting | 2025-06-06T09:00 to 2025-06-06T10:00\n" +
        "- Meeting | 2025-06-09T09:00 to 2025-06-09T10:00\n", r1.toString());
  }

  @Test
  public void testGetIfBetweenNoMatch() {
    LocalDateTime rangeStart = start.minusDays(5);
    LocalDateTime rangeEnd = end.minusDays(5);

    assertNull(weeklyMeeting.getIfBetween(rangeStart, rangeEnd));
    assertNull(weeklyMeeting2.getIfBetween(rangeStart, rangeEnd));
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
    SeriesEvent result = (SeriesEvent) weeklyMeeting.getAllMatchingEventsAfter("Meeting",
        queryTime);

    for (IEvent e : result.getEvents()) {
      assertTrue(e.toString().contains("Meeting"));
      assertTrue(e.toString().contains("2025-06"));
    }
  }

  @Test
  public void testGetAllMatchingEventsFindAll() {
    SeriesEvent result = (SeriesEvent) weeklyMeeting.getAllMatchingEvents("Meeting", start);
    assertEquals(6, result.getEvents().size());

    for (IEvent e : result.getEvents()) {
      String output = e.toString();
      assertTrue(output.contains("Meeting"));
      assertTrue(output.contains("2025-06"));
      assertTrue(output.contains("09:00"));
    }
  }

  @Test
  public void testGetAllMatchingEventsFindAll_byOccurrences() {
    SeriesEvent result = (SeriesEvent) weeklyMeeting2.getAllMatchingEvents("Meeting", start);
    assertEquals(6, result.getEvents().size());

    for (IEvent e : result.getEvents()) {
      assertTrue(e.toString().contains("Meeting"));
    }
  }

  @Test
  public void testGetAllMatchingEventsAfterNoneFound() {
    IEvent result = weeklyMeeting.getAllMatchingEventsAfter("NotMeeting", start);
    assertNull(result);
  }

  @Test
  public void testGetAllMatchingEventsFindsAll() {
    SeriesEvent result = (SeriesEvent) weeklyMeeting.getAllMatchingEvents("Meeting", start);

    for (IEvent e : result.getEvents()) {
      assertTrue(e.toString().contains("Meeting"));
    }
  }

  @Test
  public void testGetAllMatchingEventsNoneFound() {
    IEvent result = weeklyMeeting.getAllMatchingEvents("NotMeeting", start);
    assertNull(result);
  }

  @Test
  public void testGetExactMatchFoundOne() {
    IEvent result = weeklyMeeting.getExactMatch("Meeting", start, end);

    assertTrue(result.toString().contains(start.toString()));
    assertTrue(result.toString().contains(end.toString()));
  }

  @Test
  public void testGetExactMatchFoundOneInMiddle() {
    IEvent result = weeklyMeeting.getExactMatch("Meeting",
        start.plusDays(2), end.plusDays(2));
    assertTrue(result.toString().contains(start.plusDays(2).toString()));
    assertTrue(result.toString().contains(end.plusDays(2).toString()));
  }

  @Test
  public void testGetExactMatchNoneFound() {
    IEvent result = weeklyMeeting.getExactMatch("Meeting", start, end.plusHours(1));
    assertNull(result);
  }

  @Test
  public void testGetExactMatchWithoutEndPass() {
    IEvent result = weeklyMeeting.getExactMatch("Meeting", start);

    assertTrue(result.toString().contains(start.toString()));
    assertTrue(result.toString().contains(end.toString()));
  }

  @Test
  public void testGetExactMatchWithoutEndInMiddle() {
    IEvent result = weeklyMeeting.getExactMatch("Meeting",
        start.plusDays(2));
    assertTrue(result.toString().contains(start.plusDays(2).toString()));
    assertTrue(result.toString().contains(end.plusDays(2).toString()));
  }

  @Test
  public void testGetExactMatchWithoutEndFail() {
    IEvent result = weeklyMeeting.getExactMatch("Meeting", end);
    assertNull(result);
  }

  @Test
  public void testGetEdittedCopyNewStartDateAndEndDate() {
    IEvent expected = new SeriesEvent("Meeting", start.minusDays(2), end.minusDays(2), "SMW", LocalDate.of(2025, 6, 11));
    assertEquals(expected,
        weeklyMeeting.getEdittedCopy("endWithStart", start.minusDays(2).toString()));
  }

  @Test
  public void testGetEdittedCopyTZAndDateChange() {
    IEvent expected = new SeriesEvent("Meeting", start.minusDays(2).plusHours(3),
        end.minusDays(2).plusHours(3), "SMW", LocalDate.of(2025, 6, 11));
    String newPropertyString = start.minusDays(2).toLocalDate().toString() + "/" +
        Duration.ofHours(3).toString();
    assertEquals(expected, weeklyMeeting.getEdittedCopy("tzAndDateChange", newPropertyString));
  }

  @Test
  public void testGetEdittedCopyTZAndRelativeDateChange() {
    LocalDate twoDaysAgo = start.minusDays(2).toLocalDate();
    IEvent expected = new SeriesEvent("Meeting", start.plusDays(4).plusHours(3),
        end.plusDays(4).plusHours(3), "FUT", LocalDate.of(2025, 6, 17));
    String newPropertyString = twoDaysAgo.toString() + "/" +
        start.plusDays(2).toLocalDate().toString() + "/" +
        Duration.ofHours(3).toString();
    assertEquals(expected, weeklyMeeting.getEdittedCopy("tzAndRelativeDateChange", newPropertyString));
  }
}
