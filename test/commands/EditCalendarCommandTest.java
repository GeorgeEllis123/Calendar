package commands;

import org.junit.Before;
import org.junit.Test;

import java.util.TimeZone;

import controller.commands.EditCalendarCommand;
import controller.commands.UseCommand;
import mocks.MockCalendarView;
import mocks.MockMultipleCalendarModel;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@code controller.commands.EditCalendarCommand} class using mock CalendarModel and
 * CalendarView.
 */
public class EditCalendarCommandTest {
  private MockMultipleCalendarModel mockModel;
  private MockCalendarView mockView;
  private EditCalendarCommand command;

  @Before
  public void setUp() {
    mockModel = new MockMultipleCalendarModel();
    mockView = new MockCalendarView();
    command = new EditCalendarCommand(mockModel, mockView);
  }

  @Test
  public void testWhenCommandLineIsTooShort() {
    command.execute(new String[]{"edit", "calendar"});
    assertEquals("Wrong number of arguments!",
        mockView.errors.get(0));
  }

  @Test
  public void testWithNoCalendarKeyWord() {
    mockModel.multipleCalendarModels.add(new UseCommandTest.MockModifiableCalendar("Work",
        TimeZone.getTimeZone("Europe/Berlin")));

    command.execute(new String[]{"edit", "blah", "--name", "Work", "--property",
        "name", "Clases"});
    assertEquals("If you are editing a calendar, please start with 'edit calendar'",
        mockView.errors.get(0));
  }

  @Test
  public void testWithNoCalendarName() {
    mockModel.multipleCalendarModels.add(new UseCommandTest.MockModifiableCalendar("Work",
        TimeZone.getTimeZone("Europe/Berlin")));

    command.execute(new String[]{"edit", "calendar", "blah", "Work", "--property",
        "name", "Clases"});
    assertEquals("You must specify a calendar name.",
        mockView.errors.get(0));
  }

  @Test
  public void testWithNoProperty() {
    mockModel.multipleCalendarModels.add(new UseCommandTest.MockModifiableCalendar("Work",
        TimeZone.getTimeZone("Europe/Berlin")));

    command.execute(new String[]{"edit", "calendar", "--name", "Work", "fail",
        "name", "Clases"});
    assertEquals("Please add a property and a property name!",
        mockView.errors.get(0));
  }

  @Test
  public void testWithNonExistentCalendar() {
    mockModel.multipleCalendarModels.add(new UseCommandTest.MockModifiableCalendar("Work",
        TimeZone.getTimeZone("Europe/Berlin")));

    command.execute(new String[]{"edit", "calendar", "--name", "MyCal", "--property",
        "name", "Clases"});
    assertEquals("Could not find MyCal",
        mockView.errors.get(0));
  }

  @Test
  public void testWithDuplicateCalendar() {
    mockModel.multipleCalendarModels.add(new UseCommandTest.MockModifiableCalendar("Work",
        TimeZone.getTimeZone("Europe/Berlin")));
    mockModel.multipleCalendarModels.add(new UseCommandTest.MockModifiableCalendar("fail",
        TimeZone.getTimeZone("Europe/Berlin")));

    command.execute(new String[]{"edit", "calendar", "--name", "Work", "--property",
        "name", "fail2"});
    assertEquals("Calendar with name fail already exists",
        mockView.errors.get(0));
  }

  @Test
  public void testWithInvalidProperty() {
    mockModel.multipleCalendarModels.add(new UseCommandTest.MockModifiableCalendar("Work",
        TimeZone.getTimeZone("Europe/Berlin")));

    command.execute(new String[]{"edit", "calendar", "--name", "Work", "--property",
        "fail", "blah"});
    assertEquals("Invalid property. Should be one of 'name' or 'timezone'",
        mockView.errors.get(0));
  }

  @Test
  public void testWithInvalidTimezone() {
    mockModel.multipleCalendarModels.add(new UseCommandTest.MockModifiableCalendar("Work",
        TimeZone.getTimeZone("Europe/Berlin")));

    command.execute(new String[]{"edit", "calendar", "--name", "Work", "--property",
        "timezone", "fail"});
    assertEquals("Could not find timezone with id fail",
        mockView.errors.get(0));
  }

  @Test
  public void testSuccessfulName() {
    mockModel.multipleCalendarModels.add(new UseCommandTest.MockModifiableCalendar("Work",
        TimeZone.getTimeZone("Europe/Berlin")));

    command.execute(new String[]{"edit", "calendar", "--name", "Work", "--property",
        "name", "school"});
    assertEquals("Edited calendar successfully!",
        mockView.messages.get(0));
  }

  @Test
  public void testSuccessfulTimezone() {
    mockModel.multipleCalendarModels.add(new UseCommandTest.MockModifiableCalendar("Work",
        TimeZone.getTimeZone("Europe/Berlin")));

    command.execute(new String[]{"edit", "calendar", "--name", "Work", "--property",
        "timezone", "Europe/Paris"});
    assertEquals("Edited calendar successfully!",
        mockView.messages.get(0));
  }

  @Test
  public void testEditEventWithUse() {
    mockModel.multipleCalendarModels.add(new UseCommandTest.MockModifiableCalendar("Work",
        TimeZone.getTimeZone("Europe/Berlin")));
    UseCommand use = new UseCommand(mockModel, mockView);
    use.execute(new String[]{"use", "calendar", "--name", "Work"});
    assertEquals("Successfully using Work",
        mockView.messages.get(0));

    command.execute(new String[]{"edit", "event", "location", "meeting", "from", "2025-06-05T10:00",
        "to", "2025-06-05T11:00", "with", "newRoom"});
    assertEquals("Single event edited.",
        mockView.messages.get(1));
  }

  @Test
  public void testEditEventNoUse() {
    mockModel.multipleCalendarModels.add(new UseCommandTest.MockModifiableCalendar("Work",
        TimeZone.getTimeZone("Europe/Berlin")));

    command.execute(new String[]{"edit", "event", "location", "meeting", "from", "2025-06-05T10:00",
        "to", "2025-06-05T11:00", "with", "newRoom"});
    assertEquals("No calendar selected. Use the 'use' command first.",
        mockView.errors.get(0));
  }

  @Test
  public void testEditSeriesWithUse() {
    mockModel.multipleCalendarModels.add(new UseCommandTest.MockModifiableCalendar("Work",
        TimeZone.getTimeZone("Europe/Berlin")));
    UseCommand use = new UseCommand(mockModel, mockView);
    use.execute(new String[]{"use", "calendar", "--name", "Work"});
    assertEquals("Successfully using Work",
        mockView.messages.get(0));

    command.execute(new String[]{"edit", "series", "subject", "meeting", "from", "2025-06-05T10:00",
        "with", "projectMeeting"});
    assertEquals("Entire series edited.",
        mockView.messages.get(1));
  }

  @Test
  public void testEditSeriesWithNoUse() {
    mockModel.multipleCalendarModels.add(new UseCommandTest.MockModifiableCalendar("Work",
        TimeZone.getTimeZone("Europe/Berlin")));

    command.execute(new String[]{"edit", "series", "subject", "meeting", "from", "2025-06-05T10:00",
        "with", "projectMeeting"});
    assertEquals("No calendar selected. Use the 'use' command first.",
        mockView.errors.get(0));
  }

  @Test
  public void testFutureSeriesWithUse() {
    mockModel.multipleCalendarModels.add(new UseCommandTest.MockModifiableCalendar("Work",
        TimeZone.getTimeZone("Europe/Berlin")));
    UseCommand use = new UseCommand(mockModel, mockView);
    use.execute(new String[]{"use", "calendar", "--name", "Work"});
    assertEquals("Successfully using Work",
        mockView.messages.get(0));

    command.execute(new String[]{"edit", "events", "subject", "meeting", "from", "2025-06-05T10:00",
        "with", "newMeeting"});
    assertEquals("Future series edited.",
        mockView.messages.get(1));
  }

  @Test
  public void testFutureSeriesWithNoUse() {
    mockModel.multipleCalendarModels.add(new UseCommandTest.MockModifiableCalendar("Work",
        TimeZone.getTimeZone("Europe/Berlin")));

    command.execute(new String[]{"edit", "events", "subject", "meeting", "from", "2025-06-05T10:00",
        "with", "newMeeting"});
    assertEquals("No calendar selected. Use the 'use' command first.",
        mockView.errors.get(0));
  }
}