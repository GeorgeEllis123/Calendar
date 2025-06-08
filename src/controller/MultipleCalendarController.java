package controller;

import java.io.InputStream;

import controller.commands.CopyCommand;
import controller.commands.CreateCommandWithCalendar;
import controller.commands.EditCommandWithCalendar;
import controller.commands.UseCommand;
import model.CalendarModel;
import view.CalendarView;

public class MultipleCalendarController extends CalendarControllerImpl {
  /**
   * The public constructor for the MultipleCalendarController class.
   *
   * @param model the model passed into the controller.
   * @param in    the input that was passed into the controller.
   * @param view  the view that was passed into the controller.
   */
  public MultipleCalendarController(CalendarModel model, InputStream in, CalendarView view) {
    super(model, in, view);

    knownCommands.put("create", new CreateCommandWithCalendar(model, view));
    knownCommands.put("edit", new EditCommandWithCalendar(model, view));
    knownCommands.put("use", new UseCommand(model, view));
    knownCommands.put("copy", new CopyCommand(model, view));
  }
}
