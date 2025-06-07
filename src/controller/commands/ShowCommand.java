package controller.commands;

import java.time.LocalDateTime;

import model.CalendarModel;
import view.CalendarView;

/**
 * Represents the showing of existing events that fit the parameters given in the command.
 */
public class ShowCommand extends ACommand {
  /**
   * The public constructor of the ShowCommand class.
   * @param model the model that was passed into the controller.
   * @param view the view that was passed into the controller.
   */
  public ShowCommand(CalendarModel model, CalendarView view) {
    super(model, view);
  }

  /**
   * Completes the show command.
   * @param inputTokens the users input line.
   */
  @Override
  public void execute(String[] inputTokens) {
    if (inputTokens.length != 4) {
      view.displayError("Invalid show command.");
      return;
    }
    else if (!(inputTokens[1].equals("status") && inputTokens[2].equals("on"))) {
      view.displayError("Invalid show command format.");
      return;
    }

    LocalDateTime dateTime = tryToGetLocalDateTime(inputTokens[3]);
    if (dateTime == null) {
      return;
    }

    if (model.getStatus(dateTime)) {
      view.displayMessage("Busy");
    } else {
      view.displayMessage("Free");
    }
  }
}
