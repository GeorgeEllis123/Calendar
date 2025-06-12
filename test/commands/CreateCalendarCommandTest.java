package commands;

import org.junit.Before;
import org.junit.Test;

import java.util.TimeZone;

import controller.commands.CreateCalendarCommand;
import controller.commands.UseCommand;
import mocks.MockCalendarView;
import mocks.MockMultipleCalendarModel;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@code controller.commands.CreateCalendarCommand} class using mock CalendarModel and
 * CalendarView.
 */
public class CreateCalendarCommandTest {
    private MockMultipleCalendarModel mockModel;
    private MockCalendarView mockView;
    private CreateCalendarCommand command;

    @Before
    public void setUp() {
        mockModel = new MockMultipleCalendarModel();
        mockView = new MockCalendarView();
        command = new CreateCalendarCommand(mockModel, mockView);
    }

    @Test
    public void testWhenCommandLineIsTooShort() {
        command.execute(new String[]{"create", "calendar"});
        assertEquals("Invalid create command.",
            mockView.errors.get(0));
    }

    @Test
    public void testWhenCommandLineIsMissingNameKeyWord() {
        command.execute(new String[]{"create", "calendar", "blah", "MyCal",  "--timezone",
            "Europe/Berlin" });
        assertEquals("Invalid create command.",
            mockView.errors.get(0));
    }

    @Test
    public void testWhenCommandLineIsMissingTimezoneKeyWord() {
        command.execute(new String[]{"create", "calendar", "--name", "MyCal",  "blah",
            "Europe/Berlin" });
        assertEquals("Invalid time zone.", mockView.errors.get(0));
    }

    @Test
    public void testWhenCalendarIsMadeSuccessfully() {
        command.execute(new String[]{"create", "calendar", "--name", "MyCal",  "--timezone",
            "Europe/Berlin" });
        assertEquals("Calendar created successfully.", mockView.messages.get(0));
    }

    @Test
    public void testWhenManyCalendarsAreMadeSuccessfully() {
        command.execute(new String[]{"create", "calendar", "--name", "MyCal",  "--timezone",
            "Europe/Berlin" });
        assertEquals("Calendar created successfully.", mockView.messages.get(0));

        command.execute(new String[]{"create", "calendar", "--name", "Classes",  "--timezone",
            "Europe/Paris" });
        assertEquals("Calendar created successfully.", mockView.messages.get(0));

        command.execute(new String[]{"create", "calendar", "--name", "Family",  "--timezone",
            "America/New_York" });
        assertEquals("Calendar created successfully.", mockView.messages.get(0));
    }

    @Test
    public void testWhenManyCalendarsAreMadeSuccessfullySameTimezone() {
        command.execute(new String[]{"create", "calendar", "--name", "MyCal",  "--timezone",
            "Europe/Berlin" });
        assertEquals("Calendar created successfully.", mockView.messages.get(0));

        command.execute(new String[]{"create", "calendar", "--name", "Classes",  "--timezone",
            "Europe/Berlin" });
        assertEquals("Calendar created successfully.", mockView.messages.get(0));

        command.execute(new String[]{"create", "calendar", "--name", "Family",  "--timezone",
            "Europe/Berlin" });
        assertEquals("Calendar created successfully.", mockView.messages.get(0));
    }

    @Test
    public void testWhenTimezoneIsInvalid() {
        command.execute(new String[]{"create", "calendar", "--name", "MyCal",  "--timezone",
            "fail" });
        assertEquals("Could not find timezone with id fail", mockView.errors.get(0));
    }

    @Test
    public void testWhenCalendarIsDuplicate() {
        mockModel.multipleCalendarModels.add(new UseCommandTest.MockModifiableCalendar("Work",
            TimeZone.getTimeZone("Europe/Berlin")));
        command.execute(new String[]{"create", "calendar", "--name", "Work",  "--timezone",
            "Europe/Berlin" });
        assertEquals("Calendar with name Work already exists", mockView.errors.get(0));
    }

    @Test
    public void testCreateEventWithUse() {
        mockModel.multipleCalendarModels.add(new UseCommandTest.MockModifiableCalendar("Work",
            TimeZone.getTimeZone("Europe/Berlin")));
        UseCommand use = new UseCommand(mockModel, mockView);
        use.execute(new String[]{"use", "calendar", "--name", "Work"});
        assertEquals("Successfully using Work",
            mockView.messages.get(0));

        command.execute(new String[]{"create", "event", "Meeting", "from", "2025-06-05T09:00", "to",
            "2025-06-05T10:00"});
        assertEquals("Event created.",
            mockView.messages.get(1));
    }

    @Test
    public void testCreateEventNoUse() {
        mockModel.multipleCalendarModels.add(new UseCommandTest.MockModifiableCalendar("Work",
            TimeZone.getTimeZone("Europe/Berlin")));

        command.execute(new String[]{"create", "event", "Meeting", "from", "2025-06-05T09:00", "to",
            "2025-06-05T10:00"});
        assertEquals("No calendar selected. Use the 'use' command first.",
            mockView.errors.get(0));
    }

    @Test
    public void testRepeatingEventWithUse() {
        mockModel.multipleCalendarModels.add(new UseCommandTest.MockModifiableCalendar("Work",
            TimeZone.getTimeZone("Europe/Berlin")));
        UseCommand use = new UseCommand(mockModel, mockView);
        use.execute(new String[]{"use", "calendar", "--name",  "Work"});
        assertEquals("Successfully using Work",
            mockView.messages.get(0));

        command.execute(new String[]{"create", "event", "Class", "from", "2025-06-05T09:00", "to",
            "2025-06-05T10:00", "repeats", "MWF", "for", "3"});
        assertEquals("Repeating event created.",
            mockView.messages.get(1));
    }

    @Test
    public void testRepeatingEventWithNoUse() {
        mockModel.multipleCalendarModels.add(new UseCommandTest.MockModifiableCalendar("Work",
            TimeZone.getTimeZone("Europe/Berlin")));

        command.execute(new String[]{"create", "event", "Class", "from", "2025-06-05T09:00", "to",
            "2025-06-05T10:00", "repeats", "MWF", "for", "3"});
        assertEquals("No calendar selected. Use the 'use' command first.",
            mockView.errors.get(0));
    }

}