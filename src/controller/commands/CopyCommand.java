package controller.commands;

import java.time.LocalDate;
import java.time.LocalDateTime;

import model.CalendarExceptions.InvalidCalendar;
import model.CalendarExceptions.InvalidEvent;
import model.CalendarExceptions.NoCalendar;
import model.MultipleCalendarModel;
import view.CalendarView;

public class CopyCommand extends CommandParsing {
    final private MultipleCalendarModel model;

    /**
     * The constructor for CopyCommand class.
     *
     * @param model the model that was passed into the controller.
     * @param view  the view that was passed into the controller.
     */
    public CopyCommand(MultipleCalendarModel model, CalendarView view) {
        super(view);
        this.model = model;
    }

    @Override
    public void execute(String[] inputTokens) {
        //checks if it was a valid copy command
        if (inputTokens.length < 8 || (!inputTokens[1].equals("copy"))) {
            view.displayError("Invalid copy command.");
            return;
        }

        //for a single event
        if (inputTokens[2].equals("event")) {
            String eventName = inputTokens[3];

            if (inputTokens[4].equals("on")) {
                LocalDateTime start = tryToGetLocalDateTime(inputTokens[5]);
                if (inputTokens[6].equals("--target")) {
                    String calendarName = inputTokens[7];
                    if (inputTokens[8].equals("to")) {
                        LocalDateTime end = tryToGetLocalDateTime(inputTokens[9]);
                        try {
                            model.copyEvent(eventName, start, calendarName, end);
                            view.displayMessage("Event copied.");
                        } catch (InvalidCalendar e) {
                            view.displayError(e.getMessage());
                        } catch (InvalidEvent e) {
                            view.displayError(e.getMessage());
                        } catch (NoCalendar e) {
                            view.displayError(e.getMessage());
                        }
                    } else {
                        view.displayError("Please close the range of events that " +
                            "are to be copied.");
                    }
                } else {
                    view.displayError("Please specify a target calendar to " +
                        "copy the events of.");
                }
            } else {
                view.displayError("Please state when the event is on.");
            }

        } else if (inputTokens[2].equals("events")) {
            if (inputTokens[3].equals("on")) {
                LocalDate start = tryToGetLocalDate(inputTokens[4]);
                if (inputTokens[5].equals("--target")) {
                    String calendarName = inputTokens[6];
                    if (inputTokens[7].equals("to")) {
                        LocalDate end = tryToGetLocalDate(inputTokens[8]);
                        try {
                            model.copyEvents(start, calendarName, end);
                            view.displayMessage("Events copied.");
                        } catch (InvalidCalendar e) {
                            view.displayError(e.getMessage());
                        } catch (NoCalendar e) {
                            view.displayError(e.getMessage());
                        }
                    } else {
                        view.displayError("Please say what date you would like to stop copying events");
                    }
                } else {
                    view.displayError("Please specify a target calendar.");
                }
            } else if (inputTokens[3].equals("between")) {
                LocalDate start = tryToGetLocalDate(inputTokens[4]);
                if (inputTokens[5].equals("and")) {
                    LocalDate end = tryToGetLocalDate(inputTokens[6]);
                    if (inputTokens[7].equals("--target")) {
                        String calendarName = inputTokens[8];
                        if (inputTokens[9].equals("to")) {
                            LocalDate newStart = tryToGetLocalDate(inputTokens[10]);
                            try {
                                model.copyEvents(start, end, calendarName, newStart);
                                view.displayMessage("Events copied.");
                            } catch (InvalidCalendar e) {
                                view.displayError(e.getMessage());
                            } catch (NoCalendar e) {
                                view.displayError(e.getMessage());
                            }
                        } else {
                            view.displayError("Please specify to when the range will be moved to.");
                        }
                    } else {
                        view.displayError("Please specify a target calendar.");
                    }
                } else {
                    view.displayError("Please specify when the date range ends.");
                }
            }
        } else {
            view.displayError("Please state the date the event is on.");
        }
    }
}
