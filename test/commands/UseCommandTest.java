package commands;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.TimeZone;

import controller.commands.UseCommand;
import mocks.MockCalendarView;
import mocks.MockMultipleCalendarModel;
import model.exceptions.InvalidEvent;
import model.IEvent;
import model.ModifiableCalendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
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
    command.execute(new String[]{"use", "calendar", "--name", "MyCal"});
    assertEquals("Could not find MyCal", mockView.errors.get(0));
  }


  @Test
  public void testWhenThereIsACalendar() {
    mockModel.multipleCalendarModels.add(new MockModifiableCalendar("MyClasses",
        TimeZone.getTimeZone("Europe/Berlin")));

    command.execute(new String[]{"use", "calendar", "--name", "MyClasses"});

    assertTrue(mockView.messages.contains("Successfully using MyClasses"));
  }

  @Test
  public void testWhenThereIsACalendarButTheSelectedCalendarIsNotThere() {
    mockModel.multipleCalendarModels.add(new MockModifiableCalendar("MyClasses",
        TimeZone.getTimeZone("Europe/Berlin")));

    command.execute(new String[]{"use", "calendar", "--name", "MyCal"});

    assertEquals("Could not find MyCal", mockView.errors.get(0));
  }

  @Test
  public void testWhenThereAreMultipleCalendars() {
    mockModel.multipleCalendarModels.add(new MockModifiableCalendar("MyClasses",
        TimeZone.getTimeZone("Europe/Berlin")));
    mockModel.multipleCalendarModels.add(new MockModifiableCalendar("Work",
        TimeZone.getTimeZone("Europe/Berlin")));

    command.execute(new String[]{"use", "calendar", "--name", "Work"});

    assertTrue(mockView.messages.contains("Successfully using Work"));
  }

  @Test
  public void testWhenThereAreMultipleCalendarsButTheUsedCalendarDoesntExist() {
    mockModel.multipleCalendarModels.add(new MockModifiableCalendar("MyClasses",
        TimeZone.getTimeZone("Europe/Berlin")));
    mockModel.multipleCalendarModels.add(new MockModifiableCalendar("Work",
        TimeZone.getTimeZone("Europe/Berlin")));

    command.execute(new String[]{"use", "calendar", "--name", "MyCal"});

    assertEquals("Could not find MyCal", mockView.errors.get(0));
  }

  @Test
  public void testWhenThereNoIndicationOfItBeingACalendar() {
    mockModel.multipleCalendarModels.add(new MockModifiableCalendar("MyClasses",
        TimeZone.getTimeZone("Europe/Berlin")));
    mockModel.multipleCalendarModels.add(new MockModifiableCalendar("Work",
        TimeZone.getTimeZone("Europe/Berlin")));

    command.execute(new String[]{"use", "blah", "MyCal"});

    assertEquals("Please enter a valid calendar name", mockView.errors.get(0));
  }

  /**
   * Mock implementation of ModifiableCalendar used to simulate calendar output.
   */
  protected static class MockModifiableCalendar implements ModifiableCalendar {
    private final String name;
    private final TimeZone tz;

    public MockModifiableCalendar(String name, TimeZone tz) {
      this.name = name;
      this.tz = tz;
    }

    @Override
    public void editName(String newName) {
      //not necessary for testing
    }

    @Override
    public void editTimeZone(TimeZone newTimeZone) {
      //not necessary for testing
    }

    @Override
    public void add(IEvent event, LocalDateTime newStart) throws InvalidEvent {
      //not necessary for testing
    }

    @Override
    public void add(IEvent event, LocalDate newStart, TimeZone oldTimeZone) throws InvalidEvent {
      //not necessary for testing
    }

    @Override
    public void add(IEvent event, LocalDate newStart, LocalDate relativeTo, TimeZone oldTimeZone) throws InvalidEvent {
      //not necessary for testing
    }

    @Override
    public IEvent queryExactEvent(String subject, LocalDateTime start) {
      return null;
    }

    @Override
    public String getName() {
      return "";
    }

    @Override
    public TimeZone getTimeZone() {
      return null;
    }

    @Override
    public boolean addSingleEvent(String subject, LocalDateTime start, LocalDateTime end) {
      return false;
    }

    @Override
    public boolean addRepeatingEvent(String subject, LocalDateTime start, LocalDateTime end, String weekdays, int count) {
      return false;
    }

    @Override
    public boolean addRepeatingEvent(String subject, LocalDateTime start, LocalDateTime end, String weekdays, LocalDate endDate) {
      return false;
    }

    @Override
    public boolean editSingleEvent(String subject, LocalDateTime start, LocalDateTime end, String property, String newProperty) {
      return false;
    }

    @Override
    public boolean editFutureSeriesEvents(String subject, LocalDateTime start, String property, String newProperty) {
      return false;
    }

    @Override
    public boolean editEntireSeries(String subject, LocalDateTime start, String property, String newProperty) {
      return false;
    }

    @Override
    public ArrayList<IEvent> queryEvent(LocalDate date) {
      return null;
    }

    @Override
    public ArrayList<IEvent> queryEvent(LocalDateTime startTime, LocalDateTime endTime) {
      return null;
    }

    @Override
    public boolean getStatus(LocalDateTime dateTime) {
      return false;
    }


  }

}
