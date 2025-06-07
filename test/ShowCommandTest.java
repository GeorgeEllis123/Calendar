import org.junit.Before;
import org.junit.Test;

import controller.commands.ShowCommand;
import mocks.MockCalendarModel;
import mocks.MockCalendarView;

import static org.junit.Assert.assertEquals;

/**
 * A
 * Tests the {@code controller.commands.CreateCommand} class using mock CalendarModel and
 * CalendarView.
 */
public class ShowCommandTest {

  private MockCalendarModel mockModel;
  private MockCalendarView mockView;
  private ShowCommand command;

  @Before
  public void setUp() {
    mockModel = new MockCalendarModel();
    mockView = new MockCalendarView();
    command = new ShowCommand(mockModel, mockView);
  }

  @Test
  public void testShowCommandWhenBusy() {
    mockModel.testBoolean = true;
    command.execute(new String[]{"show", "status", "on", "2025-06-05T10:00"});
    assertEquals("Busy", mockView.messages.get(0));
  }

  @Test
  public void testShowCommandWhenFree() {
    mockModel.testBoolean = false;
    command.execute(new String[]{"show", "status", "on", "2025-06-05T10:00"});
    assertEquals("Free", mockView.messages.get(0));
  }

  @Test
  public void testInvalidFormatTooShort() {
    command.execute(new String[]{"show", "status", "on"});
    assertEquals("Invalid show command.", mockView.errors.get(0));
  }

  @Test
  public void testInvalidKeyword() {
    command.execute(new String[]{"show", "info", "on", "2025-06-05T10:00"});
    assertEquals("Invalid show command format.", mockView.errors.get(0));
  }

  @Test
  public void testInvalidDateParsing() {
    command.execute(new String[]{"show", "status", "on", "blah blah blah"});
    assertEquals("Invalid date time format! Should be: yyyy-MM-ddTHH:mm", mockView.errors.get(0));
  }
}
