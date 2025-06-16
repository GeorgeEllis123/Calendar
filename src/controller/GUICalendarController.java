package controller;

import model.CalendarModel;
import view.CalendarGUI;

public class GUICalendarController implements CalendarController {
  private CalendarModel model;
  private CalendarGUI view;

  GUICalendarController(CalendarModel model, CalendarGUI view) {
    this.model = model;
    this.view = view;
  }


  @Override
  public void runController() {

  }
}
