import org.junit.Before;
import org.junit.Test;

import controller.commands.ShowCommand;
import controller.commands.UseCommand;
import mocks.MockCalendarModel;
import mocks.MockCalendarView;
import mocks.MockMultipleCalendarModel;

import static org.junit.Assert.assertEquals;

/**
 * A
 * Tests the {@code controller.commands.UseCommand} class using mock CalendarModel and
 * CalendarView.
 */
public class UseCommandTest {

    private MockMultipleCalendarModel mockModel;
    private MockCalendarView mockView;
    private UseCommand command;

    @Before
    public void setUp() {
        mockModel = new MockMultipleCalendarModel();
        mockView = new MockCalendarView();
        command = new UseCommand(mockModel, mockView);
    }

    @Test
    public void testWhenCommandLineIsTooShort() {
        command.execute(new String[]{"use", "something"});
        assertEquals("Please ensure that you are using the correct syntax",
            mockView.errors.get(0));
    }

    @Test
    public void testWhenThereIsNoCalendar() {
        command.execute(new String[]{"use", "calendar", "MyCal"});
        assertEquals("Could not find MyCal", mockView.errors.get(0));
    }

    @Test
    public void testWhenThereIsACalendar() {

    }

    @Test
    public void testWhenThereIsACalendarButTheSelectedCalendarIsNotThere(){

    }

    @Test
    public void testWhenThereAreMultipleCalendars() {

    }

    @Test
    public void testWhenThereAreMultipleCalendarsButTheUsedCalendarDoesntExist() {

    }

}
