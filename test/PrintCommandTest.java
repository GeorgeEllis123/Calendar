import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import controller.commands.PrintCommand;
import mocks.MockCalendarModel;
import mocks.MockCalendarView;
import model.IEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@code controller.commands.PrintCommand} class using mock CalendarModel, CalendarView,
 * and IEvent.
 */
public class PrintCommandTest {

  private MockCalendarModel mockModel;
  private MockCalendarView mockView;
  private PrintCommand command;

  @Before
  public void setUp() {
    mockModel = new MockCalendarModel();
    mockView = new MockCalendarView();
    command = new PrintCommand(mockModel, mockView);
  }

  @Test
  public void testPrintEventsOnDateWithResults() {
    mockModel.events.add(new MockEvent("Event A"));
    mockModel.events.add(new MockEvent("Event B"));

    command.execute(new String[]{"print", "events", "on", "2025-06-05"});

    assertTrue(mockView.messages.contains("Event A"));
    assertTrue(mockView.messages.contains("Event B"));
    assertEquals(2, mockView.messages.size());
  }

  @Test
  public void testPrintEventsOnDateNoResults() {
    command.execute(new String[]{"print", "events", "on", "2025-06-05"});

    assertEquals(1, mockView.messages.size());
    assertEquals("No events on 2025-06-05", mockView.messages.get(0));
  }

  @Test
  public void testPrintEventsFromToWithResults() {
    mockModel.events.add(new MockEvent("Meeting 1"));
    mockModel.events.add(new MockEvent("Meeting 2"));

    command.execute(new String[]{"print", "events", "from", "2025-06-05T09:00", "to",
        "2025-06-05T12:00"});

    assertTrue(mockView.messages.contains("Meeting 1"));
    assertTrue(mockView.messages.contains("Meeting 2"));
  }

  @Test
  public void testPrintEventsFromToNoResults() {
    command.execute(new String[]{"print", "events", "from", "2025-06-05T09:00", "to",
        "2025-06-05T12:00"});

    assertEquals(1, mockView.messages.size());
    assertEquals("No events between 2025-06-05T09:00 and 2025-06-05T12:00",
        mockView.messages.get(0));
  }

  @Test
  public void testInvalidCommandFormat() {
    command.execute(new String[]{"print", "this", "wrong"});
    assertEquals("Invalid print command format.", mockView.errors.get(0));
  }

  @Test
  public void testInvalidDateParsing() {
    command.execute(new String[]{"print", "events", "on", "blah blah blah"});
    assertEquals("Invalid date format! Should be: yyyy-MM-dd", mockView.errors.get(0));
  }

  /**
   * Mock implementation of IEvent used to simulate event output.
   */
  private static class MockEvent implements IEvent {
    private final String label;

    public MockEvent(String label) {
      this.label = label;
    }

    @Override
    public String toString() {
      return label;
    }

    @Override
    public boolean containsDateTime(LocalDateTime dateTime) {
      return false;
    }

    @Override
    public ArrayList<IEvent> getIfEventIsOnDate(LocalDate date) {
      return null;
    }

    @Override
    public ArrayList<IEvent> getIfBetween(LocalDateTime startTime, LocalDateTime endTime) {
      return null;
    }

    @Override
    public boolean checkDuplicate(IEvent newEvent) {
      return false;
    }

    @Override
    public ArrayList<IEvent> getAllMatchingEventsAfter(String subject, LocalDateTime start) {
      return null;
    }

    @Override
    public ArrayList<IEvent> getAllMatchingEvents(String subject, LocalDateTime start) {
      return null;
    }

    @Override
    public ArrayList<IEvent> getExactMatch(String subject, LocalDateTime start, LocalDateTime end) {
      return null;
    }

    @Override
    public IEvent getEdittedCopy(String property, String newProperty) {
      return null;
    }

    @Override
    public void editEvent(String property, String newProperty) {
      // not used for testing
    }
  }
}
