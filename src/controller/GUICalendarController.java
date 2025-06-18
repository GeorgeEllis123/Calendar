package controller;

import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.TimeZone;

import javax.swing.*;

import model.IEvent;
import model.MultipleCalendarModel;
import view.CalendarGUI;

/**
 * Represents a Calendar Controller that handles the responses of the GUI view.
 */
public class GUICalendarController implements IGUICalendarController {
  private MultipleCalendarModel model;
  private CalendarGUI view;

  /**
   * Public constructor the of the GUICalendarController
   *
   * @param model the model that the Controller will be using to create and query events.
   * @param view  the view that the Controller will receive user actions from.
   */
  public GUICalendarController(MultipleCalendarModel model, CalendarGUI view) {
    this.model = model;
    this.view = view;
    view.setListener(this);
  }

  /**
   * Determines the action that the user is trying to perform.
   * @param e the event to be processed.
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    switch (e.getActionCommand()) {
      case "create":
        view.popupCreateWindow();
        break;
      case "submit create":
        submitCreate();
        break;
      case "search":
        view.popupSearchWindow();
        break;
      case "submit search":
        loadCurrentDay();
        break;
      case "edit":
        edit(e);
        break;
      case "submit edit":
        submitEdit();
        break;
      case "choose calendar":
        chooseCalendar();
        break;
      case "submit calendar":
        submitCalendar();
        break;
      case "create calendar":
        createCalendar();
        break;
      default:
        System.out.println("Unknown command: " + e.getActionCommand());
    }
  }

  private void createCalendar() {
    try {
      model.create(view.getNewCalendar(), TimeZone.getDefault().getID());
      view.displayMessage("Calendar created");
    } catch (Exception ex) {
      view.displayError(ex.getMessage());
    }
  }

  private void chooseCalendar() {
    view.popupCalendarWindow(model.getCalendars());
  }

  private void submitCalendar() {
    model.use(view.getCalendar());
    loadCurrentDay();
  }

  private void loadCurrentDay() {
    LocalDate selected = view.getLoadDay();
    view.loadDay(model.queryEvent(selected));
  }

  private void submitCreate() {
    String[] info = view.getCreate();
    if (model.addSingleEvent(info[0], LocalDateTime.parse(info[1]), LocalDateTime.parse(info[2]))) {
      view.displayMessage("Successfully created event");
      loadCurrentDay();
    } else {
      view.displayError("Error creating event");
    }
  }

  private void edit(ActionEvent e) {
    Object src = e.getSource();
    if (src != null && src.getClass().getSimpleName().equals("JButton")) {
      JButton button = (JButton) src;
      Object obj = button.getClientProperty("event");
      try {
        IEvent event = (IEvent) obj;
        view.popupEditWindow(event);
      } catch (ClassCastException ex) {
        view.displayError("Invalid event data.");
      }
    }
  }

  private void submitEdit() {
    Map<String, String> editInfo = view.getEdit();

    String oldSubject = editInfo.get("oldSubject");
    LocalDateTime oldStart = LocalDateTime.parse(editInfo.get("oldStart"));
    LocalDateTime oldEnd = LocalDateTime.parse(editInfo.get("oldEnd"));

    String newSubject = editInfo.get("subject");
    LocalDateTime newStart = LocalDateTime.parse(editInfo.get("start"));
    LocalDateTime newEnd = LocalDateTime.parse(editInfo.get("end"));

    try {
      if (!oldSubject.equals(newSubject)) {
        model.editSingleEvent(oldSubject, oldStart, oldEnd, "subject", newSubject);
        oldSubject = newSubject;
      }
      if (!oldStart.equals(newStart)) {
        model.editSingleEvent(oldSubject, oldStart, oldEnd, "start", newStart.toString());
        oldStart = newStart;
      }
      if (!oldEnd.equals(newEnd)) {
        model.editSingleEvent(oldSubject, oldStart, oldEnd, "end", newEnd.toString());
      }
      view.displayMessage("Successfully edited event");
      loadCurrentDay();
    } catch (IllegalArgumentException ex) {
      view.displayError("Error editing event.");
    }
  }

  /**
   * Tells the Controller how to delegate the command.
   */
  @Override
  public void runController() {
    model.create("Default", TimeZone.getDefault().getID());
    model.use("Default");
    view.loadDay(model.queryEvent(LocalDate.now()));
  }
}
