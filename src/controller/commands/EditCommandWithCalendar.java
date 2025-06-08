package controller.commands;

import model.CalendarModel;
import view.CalendarView;

public class EditCommandWithCalendar extends EditCommand {
  /**
   * The public constructor of the EditCommandWithCalendar class.
   *
   * @param model the model that was passed into the controller.
   * @param view  the view that was passed into the controller.
   */
  public EditCommandWithCalendar(CalendarModel model, CalendarView view) {
    super(model, view);
  }
}
