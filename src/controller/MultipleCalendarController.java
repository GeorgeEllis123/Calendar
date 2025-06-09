package controller;

import java.io.InputStream;

import controller.commands.CalendarControllerCommands;
import controller.commands.CopyCommand;
import controller.commands.CreateWithCalendarCommand;
import controller.commands.EditCommandWithCalendar;
import controller.commands.UseCommand;
import model.CalendarModel;
import model.MultipleCalendarModel;
import view.CalendarView;

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

    knownCommands.put("create-calendar", new CreateWithCalendarCommand(model, view));
    knownCommands.put("edit", new EditCommandWithCalendar(model, view));
    knownCommands.put("use", new UseCommand(model, view));
    knownCommands.put("copy", new CopyCommand(model, view));
  }

  //Need to override so that it can take in the command key as two inputs instead of one so that
  //instead of create being sent into the regular create command it is sent into the create
  //calendar
  public void runController() { };
}
