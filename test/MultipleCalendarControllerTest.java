import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import controller.CalendarController;
import controller.CalendarControllerImpl;
import controller.MultipleCalendarController;
import mocks.MockCalendarModel;
import mocks.MockCalendarView;
import mocks.MockMultipleCalendarModel;
import model.MultipleCalendarModel;
import model.MultipleCalendarModelImpl;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class MultipleCalendarControllerTest extends ControllersTest {
    private MockMultipleCalendarModel mockModel;

    @Before
    public void setUp() {
        super.setUp();
        mockModel = new MockMultipleCalendarModel();
    }

    protected CalendarController makeController(ByteArrayInputStream in ,
                                                MockCalendarView view) {
        return new MultipleCalendarController(mockModel, in, view);
    }

    @Test
    public void testValidCommand() {
        String input = "create calendar --name School --timezone Europe/Berlin\nuse calendar --name School\n" +
            "create event Meeting from 2025-06-10T09:00 to 2025-06-10T10:00\nexit\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        MockCalendarView mockView = new MockCalendarView();

        CalendarController controller = makeController(in, mockView);
        controller.runController();
        assertEquals("Calendar created successfully.", mockView.messages.get(0));
        assertEquals("Successfully using School", mockView.messages.get(1));
        assertEquals("Event created.", mockView.messages.get(2));
    }

    @Test
    public void testQuotationInNewProperty() {
        String input =
            "create calendar --name School --timezone Europe/Berlin\nuse calendar --name School\nedit event " +
                "subject \"Birthday Party\" from 2025-06-10T09:00 to 2025-06-10T10:00 " +
                "with \"Wait Still a Birthday Party\"\nexit\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        MockCalendarView mockView = new MockCalendarView();

        CalendarController controller = makeController(in, mockView);
        controller.runController();
        assertEquals("Calendar created successfully.", mockView.messages.get(0));
        assertEquals("Successfully using School", mockView.messages.get(1));
        assertEquals("Single event edited.", mockView.messages.get(2));
    }

    @Test
    public void testMissingEndQuotation() {
        String input =
            "create calendar --name School --timezone Europe/Berlin\nuse calendar --name School\ncreate " +
                "event \"Birthday Party from 2025-06-10T09:00 to 2025-06-10T10:00\nexit\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        MockCalendarView mockView = new MockCalendarView();

        CalendarController controller = makeController(in, mockView);
        controller.runController();
        assertEquals("Unclosed quotes in input.", mockView.errors.get(0));
    }

    @Test
    public void testQuotationInSubject() {
        String input =
            "create calendar --name School --timezone Europe/Berlin\nuse calendar --name School\ncreate " +
                "event \"Birthday Party\" from 2025-06-10T09:00 to 2025-06-10T10:00\nexit\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        MockCalendarView mockView = new MockCalendarView();

        CalendarController controller = makeController(in, mockView);
        controller.runController();
        assertEquals("Calendar created successfully.", mockView.messages.get(0));
        assertEquals("Successfully using School", mockView.messages.get(1));
        assertEquals("Event created.", mockView.messages.get(2));
    }

    @Test
    public void testUseCommand() {
        String input = "use\nexit\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        MockCalendarView mockView = new MockCalendarView();

        CalendarController controller = makeController(in, mockView);
        controller.runController();
        assertEquals("Please ensure that you are using the correct syntax",  mockView.errors.get(0));
    }

    @Test
    public void testValidUseCommand() {
        String input = "create calendar --name School --timezone Europe/Berlin\nuse calendar" +
            "School\nexit\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        MockCalendarView mockView = new MockCalendarView();

        CalendarController controller = makeController(in, mockView);
        controller.runController();
        assertEquals("Please ensure that you are using the correct syntax",  mockView.errors.get(0));
    }

    @Test
    public void testCopyCommand() {
        String input = "copy\nexit\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        MockCalendarView mockView = new MockCalendarView();

        CalendarController controller = makeController(in, mockView);
        controller.runController();
        assertEquals("Invalid copy command.",  mockView.errors.get(0));
    }

    @Test
    public void testValidCopyCommand() {
        String input = "create calendar --name School --timezone Europe/Berlin\ncreate calendar " +
            "--name Social --timezone Europe/Paris\nuse calendar --name School\ncopy " +
            "events between 2026-06-05 and 2026-06-07 --target Social to 2025-06-08\nexit\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        MockCalendarView mockView = new MockCalendarView();

        CalendarController controller = makeController(in, mockView);
        controller.runController();
        assertTrue(mockView.messages.contains("Events copied."));
    }

    @Test
    public void testValidCopyEventsCommand() {
        String input = "create calendar --name School --timezone Europe/Berlin\ncreate calendar " +
            "--name Social --timezone Europe/Paris\nuse calendar --name School\ncopy " +
            "events on 2026-06-05 --target Social to 2025-06-08\nexit\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        MockCalendarView mockView = new MockCalendarView();

        CalendarController controller = makeController(in, mockView);
        controller.runController();
        assertTrue(mockView.messages.contains("Events copied."));
    }

    @Test
    public void testValidCopyEventCommand() {
        String input = "create calendar --name School --timezone Europe/Berlin\ncreate calendar " +
            "--name Social --timezone Europe/Paris\nuse calendar --name School\ncreate " +
            "event birthday from 2025-06-04T14:30 to 2025-06-04T16:30\ncopy " +
            "event birthday on 2025-06-04T14:30 --target Social to 2025-06-04T16:30\nexit\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        MockCalendarView mockView = new MockCalendarView();

        CalendarController controller = makeController(in, mockView);
        controller.runController();
        assertEquals("Event copied.", mockView.messages.get(4));
    }
}