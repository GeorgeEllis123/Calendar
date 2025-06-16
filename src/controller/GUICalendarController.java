package controller;

import java.time.LocalDate;
import java.time.LocalDateTime;

import model.CalendarModel;
import view.CalendarGUI;

/**
 * Represents a Calendar Controller that handles the responses of the GUI view.
 */
public class GUICalendarController implements CalendarController {
  private CalendarModel model;
  private CalendarGUI view;

  /**
   * Public constructor the of the GUICalendarController
   * @param model the model that the Controller will be using to create and query events.
   * @param view the view that the Controller will receive user actions from.
   */
  public GUICalendarController(CalendarModel model, CalendarGUI view) {
    this.model = model;
    this.view = view;
  }

  /**
   * Tells the Controller how to delegate the command.
   */
  @Override
  public void runController() {
    LocalDate date = view.getLoadDay();
    view.loadDay(model.queryEvent(date));

    view.setListener(event -> {

      String command = event.getActionCommand();

      switch (command) {
        case "create":
          canAddEvent(date);
          break;
        case "exit":
          System.exit(0);
          break;
        case "load":
          view.loadDay(model.queryEvent(date));
          break;
        default:
          view.displayError("Unknown command" + command);
      }
    });
  }

  //Will determine if an event can be added depending on how many events are already on that date
  //and if the user input a non-duplicate event.
  private void canAddEvent(LocalDate date) {
    if (model.queryEvent(date).size() >= 10) {
      view.displayError("Maximum 10 events per day");
      return;
    }
    try {
      String [] input = view.getCreate();
      model.addSingleEvent(input[0], LocalDateTime.parse((date + "T" + input[1])),
          LocalDateTime.parse(date + "T" + input[2]));
      view.loadDay(model.queryEvent(date));
    } catch (Exception e) {
      view.displayError(e.getMessage());
    }
  }
}
