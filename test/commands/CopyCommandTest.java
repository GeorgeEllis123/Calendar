package commands;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.TimeZone;

import controller.commands.CopyCommand;
import controller.commands.CreateCommand;
import controller.commands.UseCommand;
import mocks.MockCalendarView;
import mocks.MockMultipleCalendarModel;
import model.CalendarExceptions.InvalidEvent;
import model.IEvent;
import model.ModifiableCalendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@code controller.commands.CopyCommand} class using mock CalendarModel and
 * CalendarView.
 */
public class CopyCommandTest {

    private MockMultipleCalendarModel mockModel;
    private MockCalendarView mockView;
    private CopyCommand command;
    private CreateCommand createCommand;

    @Before
    public void setUp() {
        mockModel = new MockMultipleCalendarModel();
        mockView = new MockCalendarView();
        command = new CopyCommand(mockModel, mockView);
        createCommand = new CreateCommand(mockModel, mockView);


        mockModel.multipleCalendarModels.add(new UseCommandTest.MockModifiableCalendar("Work",
            TimeZone.getTimeZone("Europe/Berlin")));
        mockModel.multipleCalendarModels.add(new UseCommandTest.MockModifiableCalendar("Meetings",
            TimeZone.getTimeZone("American/New_York")));
        UseCommand use = new UseCommand(mockModel, mockView);
        use.execute(new String[]{"use", "calendar", "Work"});
        command.execute(new String[]{"create", "event", "Meeting", "from", "2025-06-05T09:00", "to",
            "2025-06-05T10:00"});
    }

    @Test
    public void testWhenCommandLineIsTooShort() {
        command.execute(new String[]{"copy", "something"});
        assertEquals("Invalid copy command.", mockView.errors.get(0));
    }

    @Test
    public void testCopyEventNoOnKeyWord() {

        command.execute(new String[]{"copy", "event", "Meeting", "blah", "2025-06-05T09:00",
            "--target", "Meetings", "to", "2025-06-05T10:00" });
        assertEquals("Please state when the event is on.", mockView.errors.get(0));
    }

    @Test
    public void testCopyEventInvalidStartDate() {

        command.execute(new String[]{"copy", "event", "Meeting", "blah", "2025-06-05T09:00",
            "--target", "Meetings", "to", "2025-06-05T10:00" });
        assertEquals("Please state when the event is on.", mockView.errors.get(0));
    }
}