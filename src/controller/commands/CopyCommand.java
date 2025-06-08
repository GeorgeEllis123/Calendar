package controller.commands;

import model.CalendarModel;
import view.CalendarView;

public class CopyCommand extends ACommand {
  /**
   * The constructor for CopyCommand class.
   *
   * @param model the model that was passed into the controller.
   * @param view  the view that was passed into the controller.
   */
  public CopyCommand(CalendarModel model, CalendarView view) {
    super(model, view);
  }

  @Override
  public void execute(String[] inputTokens) {

  }
}
