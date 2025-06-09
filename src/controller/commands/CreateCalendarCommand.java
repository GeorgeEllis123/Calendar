package controller.commands;

import java.time.format.DateTimeFormatter;

import model.MultipleCalendarModel;
import view.CalendarView;

public class CreateCalendarCommand implements CalendarControllerCommands {
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
  protected final MultipleCalendarModel model;
  protected final CalendarView view;
  CreateCommand createCommand;


  /**
   * The public constructor of the CreateCommand class.
   *
   * @param model the model that was passed into the controller.
   * @param view  the view that was passed into the controller.
   */
  public CreateCalendarCommand(MultipleCalendarModel model, CalendarView view) {
    this.model = model;
    this.view = view;
    this.createCommand = new CreateCommand(model, view);
  }

  @Override
  public void execute(String[] inputTokens) {
    if (inputTokens[1].equals("event") || inputTokens[1].equals("events")) {
      this.createCommand.execute(inputTokens);
    }

    if (inputTokens.length < 6 ||
        (!inputTokens[1].equals("calendar")) && (!inputTokens[2].equals("--name"))) {
      view.displayError("Invalid create calendar command.");
      return;
    }

    String calendarName = inputTokens[3];

    if (inputTokens[4].equals("--timezone")) {
      if (this.model.create(calendarName, inputTokens[5])) {
        view.displayMessage("Calendar created.");
      } else {
        view.displayError("Invalid time zone.");
      }
    }
  }
}
