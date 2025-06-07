package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import controller.CalendarController;
import controller.CalendarControllerImpl;
import model.CalendarModel;
import model.CalendarModelImpl;
import view.CalendarView;
import view.CalendarViewImpl;

/**
 * Determines how the user interacts with the program : headless or interactive.
 */
public class Main {

  /**
   * Runs the program in either headless or interactive mode.
   *
   * @param args the argument line that determines how the program will be run.
   */
  public static void main(String[] args) {
    CalendarModel model = new CalendarModelImpl();
    CalendarView view = new CalendarViewImpl(System.out);
    if (args.length < 1) {
      view.displayError("Please enter a mode: --interactive or --headless [filename]");
      return;
    }
    String mode = args[0].toLowerCase();

    switch (mode) {
      case "--interactive":
        CalendarController interactiveController =
                new CalendarControllerImpl(model, System.in, view);
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
                  new CalendarControllerImpl(model, fileInput, view);
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
