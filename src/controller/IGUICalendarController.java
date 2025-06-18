package controller;

import java.awt.event.ActionEvent;

/**
 * Represents the Controller part of the Calendar.
 */
public interface IGUICalendarController extends CalendarController {

  /**
   * Determines the action that the user is trying to perform.
   * @param e the event to be processed.
   */
  void actionPerformed(ActionEvent e);
}
