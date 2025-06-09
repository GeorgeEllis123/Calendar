package controller.commands;

import model.CalendarModel;
import model.MultipleCalendarModel;
import view.CalendarView;

public class UseCommand implements CalendarControllerCommands {
  private final CalendarModel model;
  private final CalendarView view;
  /**
   * The constructor for UseCommand class.
   *
   * @param model the model that was passed into the controller.
   * @param view  the view that was passed into the controller.
   */
  public UseCommand(MultipleCalendarModel model, CalendarView view) {
    this.model = model;
    this.view = view;
  }

  @Override
  public void execute(String[] inputTokens) {

  }
}
