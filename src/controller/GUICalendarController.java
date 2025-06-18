package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalDateTime;

import model.CalendarModel;
import view.CalendarGUI;

/**
 * Represents a Calendar Controller that handles the responses of the GUI view.
 */
public class GUICalendarController implements CalendarController, ActionListener {
  private CalendarModel model;
  private CalendarGUI view;

  /**
   * Public constructor the of the GUICalendarController
   *
   * @param model the model that the Controller will be using to create and query events.
   * @param view  the view that the Controller will receive user actions from.
   */
  public GUICalendarController(CalendarModel model, CalendarGUI view) {
    this.model = model;
    this.view = view;
    view.setListener(this);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    switch (e.getActionCommand()) {
      case "create":
        view.popupCreateWindow();
        break;
      case "submit create":
        String[] info = view.getCreate();
        if (model.addSingleEvent(info[0], LocalDateTime.parse(info[1]), LocalDateTime.parse(info[2]))) {
          view.displayMessage("Successfully created event");
          loadCurrentDay();
        } else {
          view.displayError("Error creating event");
        }
        break;
      case "search":
        view.popupSearchWindow();
        break;
      case "submit search":
        loadCurrentDay();
        break;
      default:
        System.out.println("Unknown command: " + e.getActionCommand());
    }
  }

  private void loadCurrentDay() {
    LocalDate selected = view.getLoadDay();
    view.loadDay(model.queryEvent(selected));
  }

  /**
   * Tells the Controller how to delegate the command.
   */
  @Override
  public void runController() {
    view.loadDay(model.queryEvent(LocalDate.now()));
  }
}
