package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.ZoneId;
import java.util.TimeZone;

import controller.CalendarController;
import controller.GUICalendarController;
import controller.IGUICalendarController;
import controller.MultipleCalendarController;
import model.MultipleCalendarModel;
import model.MultipleCalendarModelImpl;
import view.CalendarGUI;
import view.CalendarGUIImpl;
import view.CalendarView;
import view.CalendarViewImpl;

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

    // GUI mode
    if (args.length < 1) {
      CalendarGUI view = new CalendarGUIImpl();
      IGUICalendarController controller = new GUICalendarController(model, view);
      controller.runController();
    } else {
      CalendarView view = new CalendarViewImpl(System.out);
      String mode = args[0].toLowerCase();

      // Non-GUI mode
      switch (mode) {
        case "--interactive":
          CalendarController interactiveController =
              new MultipleCalendarController(model, System.in, view);
          interactiveController.runController();
          break;

        case "--headless":
          if (args.length < 2) {
            view.displayError("Please enter a file name");
            return;
          }

          // makes sure the file contains "exit"
          File commandFile = new File(args[1]);

          if (!commandFile.exists()) {
            view.displayError("File not found: " + args[1]);
            return;
          }

          boolean hasExit = false;
          try (BufferedReader reader = new BufferedReader(new FileReader(commandFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
              if (line.equals("exit")) {
                hasExit = true;
                break;
              }
            }
          } catch (IOException e) {
            view.displayError("Could not read file: " + e.getMessage());
            return;
          }
          if (!hasExit) {
            view.displayError("File must include an 'exit' command.");
            return;
          }

          // actually runs the controller
          try {
            FileInputStream fileInput = new FileInputStream(args[1]);
            CalendarController headlessController =
                new MultipleCalendarController(model, fileInput, view);
            headlessController.runController();
          } catch (FileNotFoundException e) {
            view.displayError("File not found: " + args[1]);
          }
          break;

        default:
          view.displayError("Unknown mode: " + mode);
      }
    }
  }
}
