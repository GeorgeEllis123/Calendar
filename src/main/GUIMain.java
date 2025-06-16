package main;

import java.time.ZoneId;
import java.util.Calendar;
import java.util.TimeZone;

import controller.GUICalendarController;
import model.CalendarModel;
import model.CalendarModelImpl;
import view.CalendarGUI;
import view.CalendarGUIImpl;

public class GUIMain {
  public static void main(String[] args) {
    TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.systemDefault()));

    CalendarModel model = new CalendarModelImpl();
    CalendarGUI view = new CalendarGUIImpl();

    GUICalendarController controller = new GUICalendarController(model, view);
    controller.runController();


  }
}
