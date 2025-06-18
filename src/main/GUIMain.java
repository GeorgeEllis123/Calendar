package main;

import java.time.ZoneId;
import java.util.TimeZone;

import controller.GUICalendarController;
import controller.IGUICalendarController;
import model.MultipleCalendarModel;
import model.MultipleCalendarModelImpl;
import view.CalendarGUI;
import view.CalendarGUIImpl;

/**
 * Allows the user to use the program GUI.
 */
public class GUIMain {
  /**
   * Runs the program with a GUI.
   *
   * @param args the arguments that determine the users desired command.
   */
  public static void main(String[] args) {
    TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.systemDefault()));

    MultipleCalendarModel model = new MultipleCalendarModelImpl();
    CalendarGUI view = new CalendarGUIImpl();

    IGUICalendarController controller = new GUICalendarController(model, view);
    controller.runController();
  }
}
