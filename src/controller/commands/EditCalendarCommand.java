package controller.commands;

import java.time.format.DateTimeFormatter;

import controller.CalendarController;
import model.CalendarModel;
import model.MultipleCalendarModel;
import view.CalendarView;

public class EditCalendarCommand implements CalendarControllerCommands {
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
  private final MultipleCalendarModel model;
  private final CalendarView view;
  private EditCommand editCommand;

  /**
   * The public constructor of the EditCalendarCommand class.
   *
   * @param model the model that was passed into the controller.
   * @param view  the view that was passed into the controller.
   */
  public EditCalendarCommand(MultipleCalendarModel model, CalendarView view) {
    this.model = model;
    this.view = view;
    this.editCommand = new EditCommand(model, view);
  }

  @Override
  public void execute(String[] inputTokens) {
    this.editCommand.execute(inputTokens);
    //TODO: handle the rest of the stuff
  }
}
