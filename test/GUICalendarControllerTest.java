import org.junit.Before;
import org.junit.Test;

import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;

import controller.GUICalendarController;
import controller.IGUICalendarController;
import mocks.MockCalendarGUIImpl;
import mocks.MockMultipleCalendarModel;
import model.SingleEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@code controller.GUICalendarControllerTest} class.
 */
public class GUICalendarControllerTest {
  private MockCalendarGUIImpl mockView;
  private MockMultipleCalendarModel mockModel;
  private IGUICalendarController controller;

  @Before
  public void setUp() {
    mockView = new MockCalendarGUIImpl();
    mockModel = new MockMultipleCalendarModel();
    controller = new GUICalendarController(mockModel, mockView);
  }

  @Test
  public void testSubmitCreateSuccessfully() {
    String[] eventInfo = {
        "Meeting",
        "2025-06-18T10:00",
        "2025-06-18T11:00"
    };

    mockView.setCreateInfo(eventInfo);

    ActionEvent e = new ActionEvent(this, 0, "submit create");
    controller.actionPerformed(e);

    assertTrue(mockModel.wasAddSingleEventCalled);
    assertEquals("Meeting", mockModel.lastSubject);
    assertEquals("2025-06-18T10:00", mockModel.lastStart.toString());
    assertEquals("2025-06-18T11:00", mockModel.lastEnd.toString());
    assertEquals("Successfully created event", mockView.lastMessage);
  }

  @Test
  public void testSubmitCreateFails() {
    String[] info = {"Meeting", "2025-06-18T10:00", "2025-06-18T11:00"};
    mockView.setCreateInfo(info);
    mockModel.failOnAdd = true;

    ActionEvent e = new ActionEvent(new JButton(), 0, "submit create");
    controller.actionPerformed(e);

    assertEquals("Error creating event", mockView.lastError);
  }


  @Test
  public void testSubmitEditSuccessfully() {
    mockView.setEditInfo(Map.of(
        "oldSubject", "Old Event",
        "oldStart", "2025-06-18T10:00",
        "oldEnd", "2025-06-18T11:00",
        "subject", "New Event",
        "start", "2025-06-18T10:30",
        "end", "2025-06-18T11:30"
    ));

    ActionEvent e = new ActionEvent(this, 0, "submit edit");
    controller.actionPerformed(e);

    assertTrue(mockModel.wasEditSingleEventCalled);
    assertEquals("Successfully edited event", mockView.lastMessage);
  }

  @Test
  public void testEditButtonInvalidEvent() {
    JButton fakeButton = new JButton("Edit");
    fakeButton.setActionCommand("edit");
    fakeButton.putClientProperty("event", "notAnEvent");

    ActionEvent e = new ActionEvent(fakeButton, 0, "edit");
    controller.actionPerformed(e);

    assertEquals("Invalid event data.", mockView.lastError);
  }

  @Test
  public void testCreatePopupOpens() {
    ActionEvent e = new ActionEvent(this, 0, "create");
    controller.actionPerformed(e);
    assertTrue(mockView.wasPopupCreateCalled);
  }

  @Test
  public void testSearchPopupOpens() {
    ActionEvent e = new ActionEvent(this, 0, "search");
    controller.actionPerformed(e);
    assertTrue(mockView.wasPopupSearchCalled);
  }

  @Test
  public void testEditWithNonButtonSource() {
    ActionEvent e = new ActionEvent(new JLabel("Not a button"), 0, "edit");
    controller.actionPerformed(e);
    assertEquals(null, mockView.getError());
  }

  @Test(expected = java.time.format.DateTimeParseException.class)
  public void testCreateWithInvalidDateTimeThrows() {
    mockView.setCreateInfo(new String[]{"BadEvent", "notADate", "stillNotADate"});
    ActionEvent e = new ActionEvent(this, 0, "submit create");
    controller.actionPerformed(e);
  }

  @Test(expected = NullPointerException.class)
  public void testSubmitEditWithMissingFields() {
    mockView.setEditInfo(Map.of(
        "oldSubject", "Event",
        "oldStart", "2025-06-18T10:00",
        "subject", "Event",
        "start", "2025-06-18T10:00",
        "end", "2025-06-18T11:00"
    ));
    ActionEvent e = new ActionEvent(this, 0, "submit edit");
    controller.actionPerformed(e);
  }

  @Test
  public void testSubmitSearchWithNullDate() {
    mockView.setCurrentDate(String.valueOf(LocalDate.of(2025, 6, 18)));
    ActionEvent e = new ActionEvent(this, 0, "submit search");
    controller.actionPerformed(e);
    assertTrue(mockView.wasLoadDayCalled);
  }


  @Test
  public void testJumpToDay() {
    mockView.setCurrentDate("2025-06-20");

    ActionEvent e = new ActionEvent(this, 0, "submit search");
    controller.actionPerformed(e);

    assertEquals("2025-06-20", mockModel.lastQueriedDate.toString());

    assertTrue(mockView.wasLoadDayCalled);
  }

  @Test
  public void testEditNoChanges() {
    mockView.setEditInfo(Map.of(
        "oldSubject", "Event",
        "oldStart", "2025-06-18T10:00",
        "oldEnd", "2025-06-18T11:00",
        "subject", "Event",
        "start", "2025-06-18T10:00",
        "end", "2025-06-18T11:00"
    ));

    ActionEvent e = new ActionEvent(this, 0, "submit edit");
    controller.actionPerformed(e);

    assertEquals("Successfully edited event", mockView.lastMessage);
    assertFalse(mockModel.wasEditSingleEventCalled);
  }

  @Test
  public void testEditThrowsException() {
    mockView.setEditInfo(Map.of(
        "oldSubject", "Bad",
        "oldStart", "2025-06-18T10:00",
        "oldEnd", "2025-06-18T11:00",
        "subject", "ErrorTrigger",
        "start", "2025-06-18T10:30",
        "end", "2025-06-18T11:30"
    ));
    mockModel.throwOnEdit = true;

    ActionEvent e = new ActionEvent(this, 0, "submit edit");
    controller.actionPerformed(e);

    assertEquals("Error editing event.", mockView.lastError);
  }

  @Test
  public void testInvalidCommand() {
    ActionEvent e = new ActionEvent(this, 0, "invalid");
    controller.actionPerformed(e);

    assertEquals("Unknown command: invalid", mockView.getError());
  }

  @Test
  public void testRunControllerLoadsToday() {
    controller.runController();
    assertEquals(LocalDate.now(), mockModel.lastQueriedDate);
  }

  @Test
  public void testOnlyFirst10EventsLoaded() {
    mockModel.events.clear();
    for (int i = 0; i < 15; i++) {
      int index = i;
      mockModel.events.add(new SingleEvent("Event " + i, LocalDateTime.now(), LocalDateTime.now()));
    }

    mockView.setCurrentDate(LocalDate.now().toString());

    ActionEvent e = new ActionEvent(this, 0, "submit search");
    controller.actionPerformed(e);

    assertTrue(mockView.wasLoadDayCalled);
    assertEquals(10, mockView.loadedEvents.size());
    assertTrue(mockView.loadedEvents.get(0).toString().contains("Event 0"));
    assertTrue(mockView.loadedEvents.get(9).toString().contains("Event 9"));
  }


}

