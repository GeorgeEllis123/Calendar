import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import controller.CalendarController;
import controller.CalendarControllerImpl;
import mocks.MockCalendarModel;
import mocks.MockCalendarView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@code controller.CalendarControllerImpl} class.
 */
public class CalendarControllerImplTest {

  private MockCalendarModel mockModel;
  private MockCalendarView mockView;
  private CalendarController controller;

  @Before
  public void setUp() {
    mockModel = new MockCalendarModel();
    mockView = new MockCalendarView();

  }

  @Test
  public void testExit() {
    String input = "exit\n";
    ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());

    controller = new CalendarControllerImpl(mockModel, in, mockView);
    controller.runController();
    assertEquals(0, mockView.errors.size());
    assertEquals(0, mockView.messages.size());
  }

  @Test
  public void testValidCommand() {
    String input = "create event Meeting from 2025-06-10T09:00 to 2025-06-10T10:00\nexit\n";
    ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());

    controller = new CalendarControllerImpl(mockModel, in, mockView);
    controller.runController();
    assertEquals("Event created.", mockView.messages.get(0));
  }

  @Test
  public void testCreateCommand() {
    String input = "create\nexit\n";
    ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());

    controller = new CalendarControllerImpl(mockModel, in, mockView);
    controller.runController();
    assertEquals("Invalid create command.", mockView.errors.get(0));
  }

  @Test
  public void testPrintCommand() {
    String input = "print\nexit\n";
    ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());

    controller = new CalendarControllerImpl(mockModel, in, mockView);
    controller.runController();
    assertEquals("Invalid print command format.", mockView.errors.get(0));
  }

  @Test
  public void testEditCommand() {
    String input = "edit\nexit\n";
    ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());

    controller = new CalendarControllerImpl(mockModel, in, mockView);
    controller.runController();
    assertEquals("Invalid edit command.", mockView.errors.get(0));
  }

  @Test
  public void testShowCommand() {
    String input = "show\nexit\n";
    ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());

    controller = new CalendarControllerImpl(mockModel, in, mockView);
    controller.runController();
    assertEquals("Invalid show command.", mockView.errors.get(0));
  }

  @Test
  public void testInvalidCommandType() {
    String input = "blah event \"Meeting\" from 2025-06-10T09:00 to 2025-06-10T10:00\nexit\n";
    ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());

    controller = new CalendarControllerImpl(mockModel, in, mockView);
    controller.runController();
    assertEquals("Invalid command: blah", mockView.errors.get(0));
  }

  @Test
  public void testEmptyCommand() {
    String input = "\nexit\n";
    ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());

    controller = new CalendarControllerImpl(mockModel, in, mockView);
    controller.runController();
    assertEquals("Empty command", mockView.errors.get(0));
  }

  @Test
  public void testQuotationInSubject() {
    String input =
            "create event \"Birthday Party\" from 2025-06-10T09:00 to 2025-06-10T10:00\nexit\n";
    ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());

    controller = new CalendarControllerImpl(mockModel, in, mockView);
    controller.runController();
    assertEquals("Event created.", mockView.messages.get(0));
  }

  @Test
  public void testQuotationInNewProperty() {
    String input =
            "edit event subject \"Birthday Party\" from 2025-06-10T09:00 to 2025-06-10T10:00 " +
                    "with \"Wait Still a Birthday Party\"\nexit\n";
    ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());

    controller = new CalendarControllerImpl(mockModel, in, mockView);
    controller.runController();
    assertEquals("Single event edited.", mockView.messages.get(0));
  }

  @Test
  public void testMissingEndQuotation() {
    String input =
            "create event \"Birthday Party from 2025-06-10T09:00 to 2025-06-10T10:00\nexit\n";
    ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());

    controller = new CalendarControllerImpl(mockModel, in, mockView);
    controller.runController();
    assertEquals("Unclosed quotes in input.", mockView.errors.get(0));
  }

  @Test
  public void testCapitalization() {
    String input =
            "Create event Meeting from 2025-06-10T09:00 to 2025-06-10T10:00\nexit\n";
    ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());

    controller = new CalendarControllerImpl(mockModel, in, mockView);
    controller.runController();
    assertEquals("Event created.", mockView.messages.get(0));
  }

  @Test
  public void testMultipleCommandsSequence() {
    String input =
            "create event \"Team Sync\" from 2025-06-10T09:00 to 2025-06-10T10:00\n" +
                    "show status on 2025-06-10T09:00\n" +
                    "edit event subject \"Team Sync\" from 2025-06-10T09:00 to 2025-06-10T10:00 " +
                    "with \"Updated Sync\"\nprint schedule on 2025-06-10\nexit\n";
    ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());

    controller = new CalendarControllerImpl(mockModel, in, mockView);
    controller.runController();

    assertTrue(mockView.messages.contains("Event created."));
    assertTrue(mockView.messages.contains("Busy"));
    assertTrue(mockView.messages.contains("Single event edited."));
    assertTrue(mockView.errors.contains("Invalid print command format."));
  }

  @Test
  public void testQuotedSubjectWithExtraSpaces() {
    String input =
            "create event \"   Strategy Meeting   \" from 2025-06-11T14:00 " +
                    "to 2025-06-11T15:00\nexit\n";
    ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());

    controller = new CalendarControllerImpl(mockModel, in, mockView);
    controller.runController();

    assertEquals("Event created.", mockView.messages.get(0));
  }

  @Test
  public void testEventWithEmptySubject() {
    String input = "create event \"\" from 2025-06-11T14:00 to 2025-06-11T15:00\nexit\n";
    ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());

    controller = new CalendarControllerImpl(mockModel, in, mockView);
    controller.runController();

    assertEquals("Event created.", mockView.messages.get(0));
  }

  @Test
  public void testInvalidDateTimeFormat() {
    String input = "create event \"Meeting\" from June-10-2025T09:00 to 2025-06-10T10:00\nexit\n";
    ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());

    controller = new CalendarControllerImpl(mockModel, in, mockView);
    controller.runController();

    assertEquals("Invalid date time format! Should be: yyyy-MM-ddTHH:mm", mockView.errors.get(0));
  }

  @Test
  public void testUnsupportedCommand() {
    String input = "delete event \"Meeting\" from 2025-06-10T09:00 to 2025-06-10T10:00\nexit\n";
    ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());

    controller = new CalendarControllerImpl(mockModel, in, mockView);
    controller.runController();

    assertEquals("Invalid command: delete", mockView.errors.get(0));
  }
}