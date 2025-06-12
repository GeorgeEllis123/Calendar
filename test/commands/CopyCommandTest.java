package commands;

import org.junit.Before;
import org.junit.Test;

import java.util.TimeZone;

import controller.commands.CopyCommand;
import controller.commands.CreateCommand;
import controller.commands.UseCommand;
import mocks.MockCalendarView;
import mocks.MockMultipleCalendarModel;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@code controller.commands.CopyCommand} class using mock CalendarModel and
 * CalendarView.
 */
public class CopyCommandTest {

  private MockMultipleCalendarModel mockModel;
  private MockCalendarView mockView;
  private CopyCommand command;
  private CreateCommand createCommand;
  private UseCommand useCommand;

  @Before
  public void setUp() {
    mockModel = new MockMultipleCalendarModel();
    mockView = new MockCalendarView();
    command = new CopyCommand(mockModel, mockView);
    createCommand = new CreateCommand(mockModel, mockView);
    useCommand = new UseCommand(mockModel, mockView);


    mockModel.multipleCalendarModels.add(new UseCommandTest.MockModifiableCalendar("Work",
        TimeZone.getTimeZone("Europe/Berlin")));
    mockModel.multipleCalendarModels.add(new UseCommandTest.MockModifiableCalendar("Meetings",
        TimeZone.getTimeZone("American/New_York")));
    useCommand.execute(new String[]{"use", "calendar", "--name", "Work"});
    createCommand.execute(new String[]{"create", "event", "Meeting", "from", "2025-06-05T09:00", "to",
        "2025-06-05T10:00"});

    mockModel.multipleCalendarModels.add(new UseCommandTest.MockModifiableCalendar("Social",
        TimeZone.getTimeZone("Europe/Berlin")));
    mockModel.multipleCalendarModels.add(new UseCommandTest.MockModifiableCalendar("Personal",
        TimeZone.getTimeZone("American/New_York")));
    useCommand.execute(new String[]{"use", "calendar", "--name", "Social"});
    createCommand.execute(new String[]{"create", "event", "Dinner", "from", "2025-06-05T09:00", "to",
        "2025-06-05T10:00"});
    createCommand.execute(new String[]{"create", "event", "Date", "from", "2025-06-07T09:00", "to",
        "2025-06-07T11:00"});
    createCommand.execute(new String[]{"create", "event", "Dance", "from", "2025-06-05T13:00", "to",
        "2025-06-05T15:00"});

    useCommand.execute(new String[]{"use", "calendar", "--name", "Work"});

  }

  @Test
  public void testWhenCommandLineIsTooShort() {
    command.execute(new String[]{"copy", "something"});
    assertEquals("Invalid copy command.", mockView.errors.get(0));
  }

  @Test
  public void testCopyEventNoOnKeyWord() {

    command.execute(new String[]{"copy", "event", "Meeting", "blah", "2025-06-05T09:00",
        "--target", "Meetings", "to", "2025-06-05T10:00"});
    assertEquals("Please state the date the event is on.", mockView.errors.get(0));
  }

  @Test
  public void testCopyEventInvalidStartDate() {

    command.execute(new String[]{"copy", "event", "Meeting", "on", "fail",
        "--target", "Meetings", "to", "2025-06-05T10:00"});
    assertEquals("Invalid date time format! Should be: yyyy-MM-ddTHH:mm",
        mockView.errors.get(0));
  }

  @Test
  public void testCopyEventNoTargetKeyWord() {

    command.execute(new String[]{"copy", "event", "Meeting", "on", "2025-06-05T09:00",
        "fail", "Meetings", "to", "2025-06-05T10:00"});
    assertEquals("Please specify a target calendar to copy the events of.",
        mockView.errors.get(0));
  }

  @Test
  public void testCopyEventNoToKeyWord() {
    command.execute(new String[]{"copy", "event", "Meeting", "on", "2025-06-05T09:00",
        "--target", "Meetings", "fail", "2025-06-05T10:00"});
    assertEquals("Please close the range of events that are to be copied.",
        mockView.errors.get(0));
  }

  @Test
  public void testCopyEventInvalidNewDate() {
    command.execute(new String[]{"copy", "event", "Meeting", "on", "2025-06-05T09:00",
        "--target", "Meetings", "to", "fail"});
    assertEquals("Invalid date time format! Should be: yyyy-MM-ddTHH:mm",
        mockView.errors.get(0));
  }

  @Test
  public void testCopyNoCurrentCalendar() {
    mockModel.currentCal = null;
    command.execute(new String[]{"copy", "event", "Meeting", "on", "2025-06-05T09:00",
        "--target", "Meetings", "to", "2025-06-05T10:00"});
    assertEquals("You must have an active calendar to copy",
        mockView.errors.get(0));
  }

  @Test
  public void testCopyCalendarDoesntExist() {
    command.execute(new String[]{"copy", "event", "Meeting", "on", "2025-06-05T09:00",
        "--target", "fail", "to", "2025-06-05T10:00"});
    assertEquals("Could not find fail",
        mockView.errors.get(0));
  }

  @Test
  public void testCopyCalendarEventDoesntExist() {
    command.execute(new String[]{"copy", "event", "babysit", "on", "2025-06-05T09:00",
        "--target", "Meetings", "to", "2025-06-05T10:00"});
    assertEquals("Could not find babysit @ 2025-06-05",
        mockView.errors.get(0));
  }

  @Test
  public void testCopyOverlappingEvents() {
    createCommand.execute(new String[]{"create", "event", "party", "from", "2025-06-04T09:00",
        "to", "2025-06-05T10:00"});
    command.execute(new String[]{"copy", "event", "party", "on", "2025-06-04T09:00",
        "--target", "Meetings", "to", "2025-06-05T09:00"});
    assertEquals("Adding this event would cause an overlap",
        mockView.errors.get(0));
  }

  @Test
  public void testCopySuccessfulSingleEvent() {
    assertEquals("Successfully using Work", mockView.messages.get(6));
    command.execute(new String[]{"copy", "event", "Meeting", "on", "2025-06-05T09:00",
        "--target", "Meetings", "to", "2025-06-05T09:00"});
    assertEquals("Event copied.", mockView.messages.get(7));
  }

  @Test
  public void testCopyEventsSuccessful() {
    useCommand.execute(new String[]{"use", "calendar", "--name", "Social"});
    assertEquals("Successfully using Social", mockView.messages.get(7));

    command.execute(new String[]{"copy", "events", "on", "2025-06-05", "--target",
        "Personal", "to", "2025-06-07"});
    assertEquals("Events copied.", mockView.messages.get(8));
  }

  @Test
  public void testCopyEventsNoOnKeyWord() {
    useCommand.execute(new String[]{"use", "calendar", "--name", "Social"});
    assertEquals("Successfully using Social", mockView.messages.get(7));

    command.execute(new String[]{"copy", "events", "blah", "2025-06-05", "--target",
        "Personal", "to", "2025-06-07"});
    assertEquals("Please state the date the event is on.", mockView.errors.get(0));
  }

  @Test
  public void testCopyEventsIncorrectDate() {
    useCommand.execute(new String[]{"use", "calendar", "--name", "Social"});
    assertEquals("Successfully using Social", mockView.messages.get(7));

    command.execute(new String[]{"copy", "events", "on", "2025-06-05T09:10", "--target",
        "Personal", "to", "2025-06-07"});
    assertEquals("Invalid date format! Should be: yyyy-MM-dd",
        mockView.errors.get(0));
  }

  @Test
  public void testCopyEventsNoTargetKeyWord() {
    useCommand.execute(new String[]{"use", "calendar", "--name", "Social"});
    assertEquals("Successfully using Social", mockView.messages.get(7));

    command.execute(new String[]{"copy", "events", "on", "2025-06-05", "blah",
        "Personal", "to", "2025-06-07"});
    assertEquals("Please specify a target calendar.",
        mockView.errors.get(0));
  }

  @Test
  public void testCopyEventsNoToKeyWord() {
    useCommand.execute(new String[]{"use", "calendar", "--name", "Social"});
    assertEquals("Successfully using Social", mockView.messages.get(7));

    command.execute(new String[]{"copy", "events", "on", "2025-06-05", "--target",
        "Personal", "blah", "2025-06-07"});
    assertEquals("Please say what date you would like to stop copying events",
        mockView.errors.get(0));
  }

  @Test
  public void testCopyEventsInvalidEndTime() {
    useCommand.execute(new String[]{"use", "calendar", "--name", "Social"});
    assertEquals("Successfully using Social", mockView.messages.get(7));

    command.execute(new String[]{"copy", "events", "on", "2025-06-05", "--target",
        "Personal", "to", "2025/06/07"});
    assertEquals("Invalid date format! Should be: yyyy-MM-dd",
        mockView.errors.get(0));
  }

  @Test
  public void testCopyEventsNoCalendarInUse() {
    mockModel.currentCal = null;

    command.execute(new String[]{"copy", "events", "on", "2025-06-05", "--target",
        "Personal", "to", "2025-06-07"});
    assertEquals("You must have an active calendar to copy",
        mockView.errors.get(0));
  }

  @Test
  public void testCopyEventsCalendarDoesntExist() {
    command.execute(new String[]{"copy", "events", "on", "2025-06-05", "--target",
        "fail", "to", "2025-06-07"});
    assertEquals("Could not find fail",
        mockView.errors.get(0));
  }

  @Test
  public void testCopyRangeOfEventsSuccessful() {
    useCommand.execute(new String[]{"use", "calendar", "--name", "Social"});
    assertEquals("Successfully using Social", mockView.messages.get(7));

    command.execute(new String[]{"copy", "events", "between", "2025-06-05", "and",
        "2025-06-06", "--target", "Social", "to", "2025-06-08"});
    assertEquals("Events copied.", mockView.messages.get(8));
  }

  @Test
  public void testCopyRangeOfEventsNoBetweenKeyWord() {
    useCommand.execute(new String[]{"use", "calendar", "--name", "Social"});
    assertEquals("Successfully using Social", mockView.messages.get(7));

    command.execute(new String[]{"copy", "events", "blah", "2025-06-05", "and",
        "2025-06-06", "--target", "Social", "to", "2025-06-08"});
    assertEquals("Please state the date the event is on.", mockView.errors.get(0));
  }

  @Test
  public void testCopyRangeOfEventsInvalidStartTime() {
    useCommand.execute(new String[]{"use", "calendar", "--name", "Social"});
    assertEquals("Successfully using Social", mockView.messages.get(7));

    command.execute(new String[]{"copy", "events", "between", "2025", "and",
        "2025-06-06", "--target", "Social", "to", "2025-06-08"});
    assertEquals("Invalid date format! Should be: yyyy-MM-dd", mockView.errors.get(0));
  }

  @Test
  public void testCopyRangeOfEventsNoAndKeyWord() {
    useCommand.execute(new String[]{"use", "calendar", "--name", "Social"});
    assertEquals("Successfully using Social", mockView.messages.get(7));

    command.execute(new String[]{"copy", "events", "between", "2025-06-05", "blah",
        "2025-06-06", "--target", "Social", "to", "2025-06-08"});
    assertEquals("Please specify when the date range ends.", mockView.errors.get(0));
  }

  @Test
  public void testCopyRangeOfEventsSecondDateInvalid() {
    useCommand.execute(new String[]{"use", "calendar", "--name", "Social"});
    assertEquals("Successfully using Social", mockView.messages.get(7));

    command.execute(new String[]{"copy", "events", "between", "2025-06-05", "and",
        "2025-06-06T09:10", "--target", "Social", "to", "2025-06-08"});
    assertEquals("Invalid date format! Should be: yyyy-MM-dd", mockView.errors.get(0));
  }

  @Test
  public void testCopyRangeOfEventsNoTargetKeyWord() {
    useCommand.execute(new String[]{"use", "calendar", "--name", "Social"});
    assertEquals("Successfully using Social", mockView.messages.get(7));

    command.execute(new String[]{"copy", "events", "between", "2025-06-05", "and",
        "2025-06-06", "blah", "Social", "to", "2025-06-08"});
    assertEquals("Please specify a target calendar.", mockView.errors.get(0));
  }

  @Test
  public void testCopyRangeOfEventsNoToKeyWord() {
    useCommand.execute(new String[]{"use", "calendar", "--name", "Social"});
    assertEquals("Successfully using Social", mockView.messages.get(7));

    command.execute(new String[]{"copy", "events", "between", "2025-06-05", "and",
        "2025-06-06", "--target", "Social", "blah", "2025-06-08"});
    assertEquals("Please specify to when the range will be moved to.", mockView.errors.get(0));
  }

  @Test
  public void testCopyRangeOfEventsThirdInvalidDate() {
    useCommand.execute(new String[]{"use", "calendar", "--name", "Social"});
    assertEquals("Successfully using Social", mockView.messages.get(7));

    command.execute(new String[]{"copy", "events", "between", "2025-06-05", "and",
        "2025-06-06", "--target", "Social", "to", "blah"});
    assertEquals("Invalid date format! Should be: yyyy-MM-dd", mockView.errors.get(0));
  }

  @Test
  public void testCopyRangeOfEventsCalendarDoesNotExist() {
    useCommand.execute(new String[]{"use", "calendar", "--name", "Social"});
    assertEquals("Successfully using Social", mockView.messages.get(7));

    command.execute(new String[]{"copy", "events", "between", "2025-06-05", "and",
        "2025-06-06", "--target", "Orange", "to", "2025-06-08"});
    assertEquals("Could not find Orange", mockView.errors.get(0));
  }

  @Test
  public void testCopyRangeOfEventsNoActiveCalendar() {
    mockModel.currentCal = null;

    command.execute(new String[]{"copy", "events", "between", "2026-06-05", "and",
        "2026-06-06", "--target", "Social", "to", "2025-06-08"});
    assertEquals("You must have an active calendar to copy", mockView.errors.get(0));
  }

  @Test
  public void testCopyRangeOfEventsNoEventsDuringRange() {
    useCommand.execute(new String[]{"use", "calendar", "--name", "Social"});
    assertEquals("Successfully using Social", mockView.messages.get(7));


    command.execute(new String[]{"copy", "events", "between", "2026-06-05", "and",
        "2026-06-06", "--target", "Social", "to", "2025-06-08"});
    assertEquals("Events copied.", mockView.messages.get(8));
  }

  @Test
  public void testCopyInvalidTypeOfEvent() {
    useCommand.execute(new String[]{"use", "calendar", "--name", "Social"});
    assertEquals("Successfully using Social", mockView.messages.get(7));


    command.execute(new String[]{"copy", "allEvents", "between", "2026-06-05", "and",
        "2026-06-06", "--target", "Social", "to", "2025-06-08"});
    assertEquals("Please specify if you are copying an event or events.",
        mockView.errors.get(0));
  }

}