package controller.commands;

import model.CalendarModel;
import view.CalendarView;

/**
 * Represents the shared methods and logic between all CalendarControllerCommands.
 */
public abstract class ACommand extends CommandParsing {
  protected final CalendarModel model;

  /**
   * The constructor for any ACommand class.
   *
   * @param model the model that was passed into the controller.
   * @param view  the view that was passed into the controller.
   */
  public ACommand(CalendarModel model, CalendarView view) {
    super(view);
    this.model = model;

  }

}
