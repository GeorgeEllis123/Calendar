package controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import controller.commands.CalendarControllerCommands;
import controller.commands.CreateCommand;
import controller.commands.EditCommand;
import controller.commands.PrintCommand;
import controller.commands.ShowCommand;
import model.CalendarModel;
import view.CalendarView;

/**
 * Represents the implementation of the Controller part of the Calendar.
 */
public class CalendarControllerImpl implements CalendarController {
  private final Scanner in;
  private final CalendarView view;
  private final Map<String, CalendarControllerCommands> knownCommands = new HashMap<>();

  /**
   * The public constructor for the CalendarControllerImpl class.
   *
   * @param model the model passed into the controller.
   * @param in    the input that was passed into the controller.
   * @param view  the view that was passed into the controller.
   */
  public CalendarControllerImpl(CalendarModel model, InputStream in, CalendarView view) {
    this.view = view;
    this.in = new Scanner(in);

    knownCommands.put("create", new CreateCommand(model, view));
    knownCommands.put("print", new PrintCommand(model, view));
    knownCommands.put("show", new ShowCommand(model, view));
    knownCommands.put("edit", new EditCommand(model, view));
  }


  /**
   * Tells the Controller how to delegate the command.
   */
  public void runController() {
    boolean exit = false;
    while (!exit) {
      view.promptUser();
      String input = in.nextLine().trim();
      if (input.equalsIgnoreCase("exit")) {
        exit = true;
        return;
      }

      String[] inputTokens = parseCommand(input);
      String commandKey = inputTokens[0].toLowerCase();
      CalendarControllerCommands command = knownCommands.get(commandKey);

      if (command != null) {
        command.execute(inputTokens);
      } else {
        view.displayError("Invalid command: " + commandKey);
      }
    }
  }


  /**
   * Parses through the inputted command line.  Assumes that all multi-worded subjects
   * will have quotations.
   *
   * @param input the line that the controller is parsing through.
   * @return the parsed tokens.
   */
  private String[] parseCommand(String input) {
    if (input.isEmpty()) {
      view.displayError("Empty command");
    }

    List<String> tokens = new ArrayList<>();
    boolean inQuotes = false;
    StringBuilder currentToken = new StringBuilder();

    for (String word : input.split(" ")) {
      if (inQuotes) {
        currentToken.append(" ").append(word);
        if (word.endsWith("\"")) {
          inQuotes = false;
          tokens.add(currentToken.toString().replaceAll("^\"|\"$", ""));
          currentToken.setLength(0);
        }
      } else {
        if (word.startsWith("\"")) {
          if (word.endsWith("\"") && word.length() > 1) {
            tokens.add(word.substring(1, word.length() - 1));
          } else {
            inQuotes = true;
            currentToken.append(word);
          }
        } else {
          tokens.add(word);
        }
      }
    }

    if (inQuotes) {
      view.displayError("Unclosed quotes in input.");
    }

    return tokens.toArray(new String[0]);

  }
}
