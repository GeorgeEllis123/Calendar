package controller.commands;

import model.CalendarModel;
import view.CalendarView;

public class CreateCommandWithCalendar extends CreateCommand {
  /**
   * The public constructor of the CreateCommandWithCalendar class.
   *
   * @param model the model that was passed into the controller.
   * @param view  the view that was passed into the controller.
   */
  public CreateCommandWithCalendar(CalendarModel model, CalendarView view) {
    super(model, view);
  }
}
