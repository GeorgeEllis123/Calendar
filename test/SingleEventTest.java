import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import model.IEvent;
import model.SingleEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests the {@code model.SingleEvent} class.
 */
public class SingleEventTest {

  private IEvent event;
  private LocalDateTime start;
  private LocalDateTime end;

  @Before
  public void setUp() {
    start = LocalDateTime.of(2025, 6, 5, 9, 0);
    end = LocalDateTime.of(2025, 6, 5, 10, 0);
    event = new SingleEvent("Meeting", start, end);
  }

  //Tests contains to confirm that when initializing an event the range is only within that time

  @Test
  public void testContainsDateTimeInsideRange() {
    assertTrue(event.containsDateTime(
            LocalDateTime.of(2025, 6, 5, 9, 30)));
  }

  @Test
  public void testContainsDateTimeOnStart() {
    assertTrue(event.containsDateTime(start));
  }

  @Test
  public void testContainsDateTimeOnEnd() {
    assertTrue(event.containsDateTime(end));
  }

  @Test
  public void testContainsDateTimeBeforeStart() {
    LocalDateTime check = LocalDateTime.of(2025, 6, 5, 8, 59);
    assertFalse(event.containsDateTime(check));
  }

  @Test
  public void testContainsDateTimeAfterEnd() {
    LocalDateTime check = LocalDateTime.of(2025, 6, 5, 10, 1);
    assertFalse(event.containsDateTime(check));
  }

  @Test
  public void testGetIfEventIsOnDateSameDay() {
    assertEquals(event, event.getIfEventIsOnDate(LocalDate.of(2025, 6, 5)));
  }

  @Test
  public void testGetIfEventIsOnDateDifferentDay() {
    assertNull(event.getIfEventIsOnDate(LocalDate.of(2025, 6, 6)));
  }

  //Tests method getIfBetween

  @Test
  public void testGetIfBetweenInsideRange() {
    assertEquals(event, event.getIfBetween(start.minusMinutes(5), end.plusMinutes(5)));
  }

  @Test
  public void testGetIfBetweenExactMatch() {
    assertEquals(event, event.getIfBetween(start, end));
  }

  @Test
  public void testGetIfBetweenOutsideRange() {
    assertNull(event.getIfBetween(start.plusMinutes(1), end.minusMinutes(1)));
  }

  //Test overriding equals
  @Test
  public void testEqualsSameObject() {
    assertTrue(event.equals(event));
  }

  @Test
  public void testEqualsEquivalentObject() {
    SingleEvent copy = new SingleEvent("Meeting", start, end);
    assertTrue(event.equals(copy));
  }

  @Test
  public void testEqualsDifferentSubject() {
    SingleEvent diff = new SingleEvent("Call", start, end);
    assertFalse(event.equals(diff));
  }

  @Test
  public void testEqualsDifferentTime() {
    SingleEvent diff = new SingleEvent("Meeting", start, end.plusMinutes(30));
    assertFalse(event.equals(diff));
  }

  //tests overriding hashcode

  @Test
  public void testHashCodeConsistency() {
    int hash1 = event.hashCode();
    int hash2 = event.hashCode();
    assertEquals(hash1, hash2);
  }

  @Test
  public void testHashCodeEqualObjects() {
    SingleEvent duplicate = new SingleEvent("Meeting", start, end);
    assertEquals(event.hashCode(), duplicate.hashCode());
  }

  //tests the toString method

  @Test
  public void testToString() {
    String result = event.toString();
    assertTrue(result.contains("Meeting"));
    assertTrue(result.contains(start.toString()));
    assertTrue(result.contains(end.toString()));
  }

  // Tests the constructor

  //Tests that the start time cant be before the end time

  @Test
  public void testConstructor_InvalidDatesThrowsException() {
    try {
      LocalDateTime later = LocalDateTime.of(2025, 6, 5, 11, 0);
      LocalDateTime earlier = LocalDateTime.of(2025, 6, 5, 10, 0);
      new SingleEvent("Bad Event", later, earlier);
      fail("End Date cannot be before Start Date");
    } catch (IllegalArgumentException e) {
      //should be blank because the exception was thrown
    }
  }

  @Test
  public void testConstructor_ValidDatesThrowsException() {
    try {
      LocalDateTime later = LocalDateTime.of(2025, 6, 5, 11, 0);
      LocalDateTime earlier = LocalDateTime.of(2025, 6, 5, 10, 0);
      new SingleEvent("Good Event", earlier, later);
      //should be blank because the exception was NOT thrown
    } catch (IllegalArgumentException e) {
      fail("End Date cannot be before Start Date");
    }
  }

  @Test
  public void testConstructor_NullSubjectThrowsException() {
    try {
      String subject = null;
      LocalDateTime later = LocalDateTime.of(2025, 6, 5, 11, 0);
      LocalDateTime earlier = LocalDateTime.of(2025, 6, 5, 10, 0);
      new SingleEvent(subject, earlier, later);
      fail("Subject and Start Date cannot be null");
    } catch (IllegalArgumentException e) {
      //should be blank because the exception was thrown
    }
  }

  @Test
  public void testConstructor_NonNullSubjectDoesNotThrowException() {
    try {
      LocalDateTime later = LocalDateTime.of(2025, 6, 5, 11, 0);
      LocalDateTime earlier = LocalDateTime.of(2025, 6, 5, 10, 0);
      new SingleEvent("Good Event", earlier, later);
      //should be blank because the exception was NOT thrown
    } catch (IllegalArgumentException e) {
      fail("Subject and Start Date cannot be null");
    }
  }

  @Test
  public void testConstructor_NullStartThrowsException() {
    try {
      LocalDateTime later = LocalDateTime.of(2025, 6, 5, 11, 0);
      new SingleEvent("subject", null, later);
      fail("Subject and Start Date cannot be null");
    } catch (IllegalArgumentException e) {
      //should be blank because the exception was thrown
    }
  }

  @Test
  public void testConstructor_NonNullStartDoesNotThrowException() {
    try {
      LocalDateTime later = LocalDateTime.of(2025, 6, 5, 11, 0);
      LocalDateTime earlier = LocalDateTime.of(2025, 6, 5, 10, 0);
      new SingleEvent("Good Event", earlier, later);
      //should be blank because the exception was NOT thrown
    } catch (IllegalArgumentException e) {
      fail("Subject and Start Date cannot be null");
    }
  }

  @Test
  public void testConstructor_NullEndDoesNotThrowException() {
    try {
      LocalDateTime later = LocalDateTime.of(2025, 6, 5, 11, 0);
      LocalDateTime earlier = LocalDateTime.of(2025, 6, 5, 10, 0);
      new SingleEvent("Good Event", earlier, null);
      //should be blank because the exception was NOT thrown
    } catch (IllegalArgumentException e) {
      fail("Subject and Start Date cannot be null");
    }
  }

  @Test
  public void testConstructor_NullEndCreatesAllDayEventEndAtFivePM() {
    LocalDateTime earlier = LocalDateTime.of(2025, 6, 5, 10, 0);
    SingleEvent se = new SingleEvent("Good Event", earlier, null);
    assertTrue(se.containsDateTime(LocalDateTime.of(2025, 6, 5, 17, 0)));
  }

  @Test
  public void testConstructor_NullEndCreatesAllDayEventEndAtFivePMNotLater() {
    LocalDateTime earlier = LocalDateTime.of(2025, 6, 5, 10, 0);
    SingleEvent se = new SingleEvent("Good Event", earlier, null);
    assertFalse(se.containsDateTime(LocalDateTime.of(2025, 6, 5, 17, 1)));
  }

  @Test
  public void testConstructor_NullEndCreatesAllDayEventStartAtEightAMNotEarlier() {
    LocalDateTime earlier = LocalDateTime.of(2025, 6, 5, 10, 0);
    SingleEvent se = new SingleEvent("Good Event", earlier, null);
    assertFalse(se.containsDateTime(LocalDateTime.of(2025, 6, 5, 7, 59)));
  }

  @Test
  public void testConstructor_NullEndCreatesAllDayEventSpansFromEightAMToFivePM() {
    LocalDateTime earlier = LocalDateTime.of(2025, 6, 5, 10, 0);
    SingleEvent se = new SingleEvent("Good Event", earlier, null);
    assertTrue(se.containsDateTime(LocalDateTime.of(2025, 6, 5, 9, 59)));
  }

  //Check Duplicate Method

  @Test
  public void testCheckDuplicateMatching() {
    IEvent duplicate = new SingleEvent("Meeting", start, end);
    assertTrue(event.checkDuplicate(duplicate));
  }

  @Test
  public void testCheckDuplicateNotMatching() {
    IEvent duplicate = new SingleEvent("Bad Event", start, end);
    assertFalse(event.checkDuplicate(duplicate));
  }

  @Test
  public void testGetAllMatchingEventsAfterMatch() {
    IEvent result = event.getAllMatchingEventsAfter("Meeting", start);
    assertNotNull(result);
    assertEquals(event.toString(), result.toString());
  }

  @Test
  public void testGetAllMatchingEventsAfterNoMatchWrongSubject() {
    IEvent result = event.getAllMatchingEventsAfter("Lunch", start);
    assertNull(result);
  }

  @Test
  public void testGetAllMatchingEventsAfterNoMatchWrongStart() {
    IEvent result = event.getAllMatchingEventsAfter("Meeting",
        LocalDateTime.of(2025, 6, 5, 8, 0));
    assertNull(result);
  }

  @Test
  public void testGetAllMatchingEvents() {
    IEvent result = event.getAllMatchingEvents("Meeting", start);
    assertNotNull(result);
    assertEquals(event.toString(), result.toString());
  }

  @Test
  public void testGetExactMatch() {
    IEvent result = event.getExactMatch("Meeting", start, end);
    assertNotNull(result);
    assertEquals(event.toString(), result.toString());
  }

  @Test
  public void testGetExactMatchWrongSubject() {
    IEvent result = event.getExactMatch("Call", start, end);
    assertNull(result);
  }

  @Test
  public void testGetEdittedCopyChangesSubject() {
    IEvent edited = event.getEdittedCopy("subject", "Something Else");
    assertTrue(edited.toString().contains("Something Else"));
    assertFalse(edited.toString().contains("Meeting |"));
  }

  @Test
  public void testEditEventMutatesOriginal() {
    event.editEvent("subject", "Updated");
    assertTrue(event.toString().contains("Updated"));
    assertFalse(event.toString().contains("Meeting |"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetEdittedCopyInvalidProperty() {
    event.getEdittedCopy("nonsense", "value");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditEventInvalidProperty() {
    event.editEvent("invalid", "value");
  }

  @Test
  public void testEditEventStatus() {
    event.editEvent("status", "private");
    assertTrue(event.toString().contains("Status: PRIVATE"));
  }

  @Test
  public void testEditEventLocation() {
    event.editEvent("location", "online");
    assertTrue(event.toString().contains("Location: ONLINE"));
  }

  @Test
  public void testEditEventDescription() {
    event.editEvent("description", "Bring laptop");
    assertTrue(event.toString().contains("Description: Bring laptop"));
  }

  @Test
  public void testEditEventStartAndEnd() {
    event.editEvent("start", "2025-06-05T07:00");
    event.editEvent("end", "2025-06-05T12:00");
    assertTrue(event.toString().contains("2025-06-05T07:00 to 2025-06-05T12:00"));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testMakeStartDateAfterEnd() {
    event.editEvent("start", "2025-06-05T12:00");
    assertTrue(event.toString().contains("2025-06-05T07:00 to 2025-06-05T12:00"));
  }

  @Test (expected = IllegalArgumentException.class)
  public void testMakeStartDateBeforeEnd() {
    event.editEvent("end", "2025-06-05T01:00");
    assertTrue(event.toString().contains("2025-06-05T07:00 to 2025-06-05T12:00"));
  }

  @Test
  public void testGetExactMatchWithoutEndPass() {
    assertEquals(event, event.getExactMatch("Meeting", start));
  }

  @Test
  public void testGetExactMatchWithoutEndFail() {
    assertNull(event.getExactMatch("Meeting", end));
  }

  @Test
  public void testGetEdittedCopyNewStartDateAndEndDate() {
    IEvent expected = new SingleEvent("Meeting", start.minusDays(2), end.minusDays(2));
    assertEquals(expected, event.getEdittedCopy("endWithStart", start.minusDays(2).toString()));
  }

  @Test
  public void testGetEdittedCopyTZAndDateChange() {
    IEvent expected = new SingleEvent("Meeting", start.minusDays(2).plusHours(3),
        end.minusDays(2).plusHours(3));
    String newPropertyString = start.minusDays(2).toLocalDate().toString() + "/" +
        Duration.ofHours(3).toString();
    assertEquals(expected, event.getEdittedCopy("tzAndDateChange", newPropertyString));
  }

  @Test
  public void testGetEdittedCopyTZAndDateChangeOverDays() {
    IEvent expected = new SingleEvent("Meeting", start.minusDays(2).plusHours(17),
        end.minusDays(2).plusHours(17));
    String newPropertyString = start.minusDays(2).toLocalDate().toString() + "/" +
        Duration.ofHours(17).toString();
    assertEquals(expected, event.getEdittedCopy("tzAndDateChange", newPropertyString));
  }

  @Test
  public void testGetEdittedCopyTZAndRelativeDateChange() {
    LocalDate twoDaysAgo = start.minusDays(2).toLocalDate();
    IEvent expected = new SingleEvent("Meeting", start.plusDays(4).plusHours(3),
        end.plusDays(4).plusHours(3));
    String newPropertyString = twoDaysAgo.toString() + "/" +
        start.plusDays(2).toLocalDate().toString() + "/" +
        Duration.ofHours(3).toString();
    assertEquals(expected, event.getEdittedCopy("tzAndRelativeDateChange", newPropertyString));
  }
}
