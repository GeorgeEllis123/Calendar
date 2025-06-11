package commands;

import org.junit.Before;
import org.junit.Test;

import controller.commands.CreateCommand;
import mocks.MockCalendarModel;
import mocks.MockCalendarView;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@code controller.commands.CreateCommand} class using mock CalendarModel and
 * CalendarView.
 */
public class CreateCommandTest {

  private MockCalendarModel mockModel;
  private MockCalendarView mockView;
  private CreateCommand command;

  @Before
  public void setUp() {
    mockModel = new MockCalendarModel();
    mockView = new MockCalendarView();
    command = new CreateCommand(mockModel, mockView);
  }

  @Test
  public void testCreateSingleEvent() {
    mockModel.testBoolean = true;
    command.execute(new String[]{"create", "event", "Meeting", "from", "2025-06-05T09:00", "to",
        "2025-06-05T10:00"});
    assertEquals("Event created.", mockView.messages.get(0));
  }

  @Test
  public void testCreateSingleEventDuplicate() {
    mockModel.testBoolean = false;
    command.execute(new String[]{"create", "event", "Meeting", "from", "2025-06-05T09:00", "to",
        "2025-06-05T10:00"});
    assertEquals("Event already exists.", mockView.errors.get(0));
  }

  @Test
  public void testCreateRepeatingEventByCountSuccessfully() {
    mockModel.testBoolean = true;
    command.execute(new String[]{"create", "event", "Class", "from", "2025-06-05T09:00", "to",
        "2025-06-05T10:00", "repeats", "MWF", "for", "3"});
    assertEquals("Repeating event created.", mockView.messages.get(0));
  }

  @Test
  public void testCreateRepeatingEventByUntilDateFail() {
    mockModel.testBoolean = false;
    command.execute(new String[]{"create", "event", "Class", "from", "2025-06-05T09:00", "to",
        "2025-06-05T10:00", "repeats", "MWF", "until", "2025-06-15"});
    assertEquals("Repeating event already exists.", mockView.errors.get(0));
  }

  @Test
  public void testCreateAllDayEventSuccessfully() {
    mockModel.testBoolean = true;
    command.execute(new String[]{"create", "event", "Holiday", "on", "2025-07-04"});
    assertEquals("All day event created.", mockView.messages.get(0));
  }

  @Test
  public void testInvalidCreateCommandStructure() {
    command.execute(new String[]{"create", "something", "else"});
    assertEquals("Invalid create command.", mockView.errors.get(0));
  }

  @Test
  public void testCreateAllDayRepeatingEventByCountSuccessfully() {
    mockModel.testBoolean = true;
    command.execute(new String[]{"create", "event", "Practice", "on", "2025-06-05", "repeats",
        "TR", "for", "2"});
    assertEquals("All day repeating event created.", mockView.messages.get(0));
  }
}
