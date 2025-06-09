package controller.commands;

import model.CalendarModel;
import model.MultipleCalendarModel;
import view.CalendarView;

public class CreateWithCalendarCommand extends CreateCommand {

    /**
     * The public constructor of the CreateCommand class.
     *
     * @param model the model that was passed into the controller.
     * @param view  the view that was passed into the controller.
     */
    public CreateWithCalendarCommand(MultipleCalendarModel model, CalendarView view) {
        super(model, view);
    }

    @Override
    public void execute(String[] inputTokens) {

        if (inputTokens.length < 8 ||
            (!inputTokens[1].equals("calendar")) && (!inputTokens[2].equals("--name"))) {
            view.displayError("Invalid create calendar command.");
            return;
        }

        String calendarName = inputTokens[3];

        MultipleCalendarModel multipleModel = (MultipleCalendarModel) this.model;

        if (inputTokens[4].equals("--timezone")) {
            if (multipleModel.create(calendarName, inputTokens[5])) {
                view.displayError("Calendar created.");
            } else {
                view.displayError("Invalid time zone.");
            }
        }
    }
}
