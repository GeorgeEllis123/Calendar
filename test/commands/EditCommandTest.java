package commands;

import org.junit.Before;
import org.junit.Test;

import controller.commands.EditCommand;
import mocks.MockCalendarModel;
import mocks.MockCalendarView;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@code controller.commands.EditCommand} class using mock CalendarModel and
 * CalendarView.
 */
public class EditCommandTest {

  private MockCalendarModel mockModel;
  private MockCalendarView mockView;
  private EditCommand command;

  @Before
  public void setUp() {
    mockModel = new MockCalendarModel();
    mockView = new MockCalendarView();
    command = new EditCommand(mockModel, mockView);
  }

  @Test
  public void testEditSingleEventSuccess() {
    mockModel.testBoolean = true;
    String[] tokens = {"edit", "event", "location", "meeting", "from", "2025-06-05T10:00", "to",
        "2025-06-05T11:00", "with", "newRoom"};
    command.execute(tokens);
    assertEquals("Single event edited.", mockView.messages.get(0));
  }

  @Test
  public void testEditSingleEventFailure() {
    mockModel.testBoolean = false;
    String[] tokens = {"edit", "event", "location", "meeting", "from", "2025-06-05T10:00", "to",
        "2025-06-05T11:00", "with", "newRoom"};
    command.execute(tokens);
    assertEquals("Event not found.", mockView.messages.get(0));
  }

  @Test
  public void testEditFutureSeriesSuccess() {
    mockModel.testBoolean = true;
    String[] tokens = {"edit", "events", "subject", "meeting", "from", "2025-06-05T10:00", "with",
        "newMeeting"};
    command.execute(tokens);
    assertEquals("Future series edited.", mockView.messages.get(0));
  }

  @Test
  public void testEditEntireSeriesSuccess() {
    mockModel.testBoolean = true;
    String[] tokens = {"edit", "series", "subject", "meeting", "from", "2025-06-05T10:00",
        "with", "projectMeeting"};
    command.execute(tokens);
    assertEquals("Entire series edited.", mockView.messages.get(0));
  }

  @Test
  public void testInvalidFormatTooShort() {
    command.execute(new String[]{"edit", "event"});
    assertEquals("Invalid edit command.", mockView.errors.get(0));
  }

  @Test
  public void testInvalidCommandKeyword() {
    command.execute(new String[]{"edit", "lol", "subject", "meeting", "from", "2025-06-05T10:00",
        "to", "2025-06-05T11:00", "with", "something"});
    assertEquals("Unknown edit command: lol", mockView.errors.get(0));
  }

  @Test
  public void testEditWithParseException() {
    command.execute(new String[]{"edit", "event", "subject", "meeting", "from", "not-a-date", "to",
        "2025-06-05T11:00", "with", "newTitle"});
    assertEquals("Invalid date time format! Should be: yyyy-MM-ddTHH:mm", mockView.errors.get(0));
  }

  @Test
  public void testEditEventMissingFrom() {
    String[] tokens = {"edit", "event", "subject", "meeting", "badKeyword", "2025-06-05T10:00",
        "to", "2025-06-05T11:00", "with", "newTitle"};
    command.execute(tokens);
    assertEquals(
        "Invalid format edit command.  Please give a subject," +
            "start date and time, and an end date and time.",
        mockView.messages.get(0)
    );
  }

  @Test
  public void testEditEventMissingTo() {
    String[] tokens = {"edit", "event", "subject", "meeting", "from", "2025-06-05T10:00", "wrong",
        "2025-06-05T11:00", "with", "newTitle"};
    command.execute(tokens);
    assertEquals(
        "Invalid format edit command.  Please give a subject," +
            "start date and time, and an end date and time.",
        mockView.messages.get(0)
    );
  }

  @Test
  public void testEditEventMissingWith() {
    String[] tokens = {"edit", "event", "subject", "meeting", "from", "2025-06-05T10:00", "to",
        "2025-06-05T11:00", "nope", "newTitle"};
    command.execute(tokens);
    assertEquals(
        "Invalid format edit command.  Please give a subject," +
            "start date and time, and an end date and time.",
        mockView.messages.get(0)
    );
  }

  @Test
  public void testEditEventsMissingFrom() {
    String[] tokens = {"edit", "events", "location", "meeting", "oops", "2025-06-05T10:00", "with",
        "newLocation"};
    command.execute(tokens);
    assertEquals(
        "Invalid format edit command.  Please give a subject, " +
            "start date and time, and an end date and time.",
        mockView.errors.get(0)
    );
  }

  @Test
  public void testEditEventsMissingWith() {
    String[] tokens = {"edit", "events", "location", "meeting", "from", "2025-06-05T10:00",
        "missing", "newLocation"};
    command.execute(tokens);
    assertEquals(
        "Invalid format edit command.  Please give a subject, " +
            "start date and time, and an end date and time.",
        mockView.errors.get(0)
    );
  }

  @Test
  public void testEditSeriesMissingFrom() {
    String[] tokens = {"edit", "series", "location", "meeting",
        "oops", "2025-06-05T10:00", "with", "newLocation"};
    command.execute(tokens);
    assertEquals(
        "Invalid format edit command.  Please add a property, " +
            "subject, start date and time, and the property you would like to edit. ",
        mockView.errors.get(0)
    );
  }

  @Test
  public void testEditSeriesMissingWith() {
    String[] tokens = {"edit", "series", "location", "meeting", "from", "2025-06-05T10:00",
        "badKeyword", "newLocation"};
    command.execute(tokens);
    assertEquals(
        "Invalid format edit command.  Please add a property, " +
            "subject, start date and time, and the property you would like to edit. ",
        mockView.errors.get(0)
    );
  }
}
