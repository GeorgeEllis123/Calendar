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
public class CalendarControllerImplTest extends ControllersTest {;
  private MockCalendarModel mockModel;

  @Before
  public void setUp() {
    super.setUp();
    mockModel = new MockCalendarModel();
  }

  protected CalendarController makeController(ByteArrayInputStream in ,
                                                  MockCalendarView mockView) {
    return new CalendarControllerImpl(mockModel, in, mockView);
  }

  @Test
  public void testEditCommand() {
    String input = "edit\nexit\n";
    ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
    MockCalendarView mockView = new MockCalendarView();

    CalendarController controller = makeController(in, mockView);
    controller.runController();
    assertEquals("Invalid edit command.", mockView.errors.get(0));
  }


  @Test
  public void testInvalidDateTimeFormat() {
    String input = "create event \"Meeting\" from June-10-2025T09:00 to 2025-06-10T10:00\nexit\n";
    ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
    MockCalendarView mockView = new MockCalendarView();

    CalendarController controller = makeController(in, mockView);
    controller.runController();

    assertEquals("Invalid date time format! Should be: yyyy-MM-ddTHH:mm",
        mockView.errors.get(0));
  }

  @Test
  public void testQuotationInNewProperty() {
    String input =
        "edit event subject \"Birthday Party\" from 2025-06-10T09:00 to 2025-06-10T10:00 " +
            "with \"Wait Still a Birthday Party\"\nexit\n";
    ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
    MockCalendarView mockView = new MockCalendarView();

    CalendarController controller = makeController(in, mockView);
    controller.runController();

    assertEquals("Single event edited.", mockView.messages.get(0));
  }

  @Test
  public void testMissingEndQuotation() {
    String input =
        "create  event \"Birthday Party from 2025-06-10T09:00 to 2025-06-10T10:00\nexit\n";
    ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
    MockCalendarView mockView = new MockCalendarView();

    CalendarController controller = makeController(in, mockView);
    controller.runController();
    assertEquals("Unclosed quotes in input.", mockView.errors.get(0));
  }

  @Test
  public void testQuotationInSubject() {
    String input =
        "create event \"Birthday Party\" from 2025-06-10T09:00 to 2025-06-10T10:00\nexit\n";
    ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
    MockCalendarView mockView = new MockCalendarView();

    CalendarController controller = makeController(in, mockView);
    controller.runController();
    assertEquals("Event created.", mockView.messages.get(0));
  }



}