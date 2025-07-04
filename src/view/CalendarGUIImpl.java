package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;

import model.IEvent;

public class CalendarGUIImpl implements CalendarGUI {

  private JFrame frame;
  private JPanel calendarPanel;
  private JLabel monthLabel;
  private JComboBox<String> calendarDropdown;
  private Map<String, Color> calendars;
  private YearMonth currentMonth;
  private String selectedCalendar;


  private JButton submitDate;

  private ArrayList<IEvent> events;

  private LocalDate currentDate;

  public CalendarGUIImpl() {
    frame = new JFrame("Calendar App");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(500, 500);
    frame.setLayout(new BorderLayout());

    currentMonth = YearMonth.now();
    calendars = new HashMap<>();
    calendars.put("Work", Color.BLUE);
    calendars.put("Personal", Color.GREEN);
    calendars.put("Holidays", Color.RED);
    selectedCalendar = "Work";

    JPanel topPanel = new JPanel();
    monthLabel = new JLabel();
    calendarDropdown = new JComboBox<>(calendars.keySet().toArray(new String[0]));
    topPanel.add(monthLabel);
    topPanel.add(calendarDropdown);


    // For date search form
    submitDate = new JButton("Submit");
    submitDate.setActionCommand("Load Date");


    frame.add(topPanel, BorderLayout.NORTH);

    calendarPanel = new JPanel();
    frame.add(calendarPanel, BorderLayout.CENTER);

    updateCalendar();
    frame.setVisible(true);
  }

  private void updateCalendar() {
    calendarPanel.removeAll();
    calendarPanel.setLayout(new GridLayout(0, 7));
    monthLabel.setText(currentMonth.getMonth() + " " + currentMonth.getYear());
    calendarPanel.setBackground(calendars.get(selectedCalendar));

    for (int day = 1; day <= currentMonth.lengthOfMonth(); day++) {
      LocalDate date = currentMonth.atDay(day);
      JButton dayButton = new JButton(String.valueOf(day));
      calendarPanel.add(dayButton);
    }

    frame.revalidate();
    frame.repaint();
  }

  @Override
  public LocalDate getLoadDay() {
    return this.currentDate;
  }

  @Override
  public String[] getCreate() {
    return new String[0];
  }

  @Override
  public void loadDay(ArrayList<IEvent> events) {
    this.events = events;
  }

  @Override
  public void displayError(String msg) {

  }

  @Override
  public void setListener(ActionListener listener) {
    submitDate.addActionListener(listener);
  }

  public static void main(String[] args) {
    /**
     * Runs the GUI asynchronously without blocking.
     */
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        new CalendarGUIImpl();
      }
    });
  }
}
