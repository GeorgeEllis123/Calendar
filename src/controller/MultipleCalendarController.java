package controller;

import java.io.InputStream;

import controller.commands.CopyCommand;
import controller.commands.CreateCalendarCommand;
import controller.commands.EditCalendarCommand;
import controller.commands.UseCommand;
import model.MultipleCalendarModel;
import view.CalendarView;

/**
 * Represents the commands that support Multiple Calendars in a controller.
 */
public class MultipleCalendarController extends CalendarControllerImpl {
  /**
   * The public constructor for the MultipleCalendarController class.
   *
   * @param model the model passed into the controller.
   * @param in    the input that was passed into the controller.
   * @param view  the view that was passed into the controller.
   */
  public MultipleCalendarController(MultipleCalendarModel model,
                                    InputStream in, CalendarView view) {
    super(model, in, view);

    knownCommands.put("create", new CreateCalendarCommand(model, view));
    knownCommands.put("edit", new EditCalendarCommand(model, view));
    knownCommands.put("use", new UseCommand(model, view));
    knownCommands.put("copy", new CopyCommand(model, view));
  }
}
