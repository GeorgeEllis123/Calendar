package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;

import model.IEvent;
import model.SingleEvent;

public class CalendarGUIImpl implements CalendarGUI {

  private JFrame frame;
  private JLabel monthLabel;
  private JComboBox<String> calendarDropdown;

  private Map<String, Color> calendars;

  private JButton createButton;

  private ArrayList<IEvent> events;
  private LocalDate currentDate;

  private DayView dayView;

  public CalendarGUIImpl() {
    frame = new JFrame("Day View");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(600, 800);
    frame.setLayout(new BorderLayout());

    this.events = new ArrayList<>();
    currentDate = LocalDate.now();

    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    monthLabel = new JLabel(currentDate.toString());
    topPanel.add(monthLabel);

    createButton = new JButton("Create Event");
    topPanel.add(createButton);

    createButton.addActionListener(event -> {
      String name = JOptionPane.showInputDialog(frame, "Enter event name:");
      if (name != null && !name.isEmpty()) {
        String start = JOptionPane.showInputDialog(frame, "Enter the start time " +
            "(HH:mm format):");
        String end = JOptionPane.showInputDialog(frame, "Enter the end time (HH:mm):");

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        try {
          LocalTime startTime = LocalTime.parse(start.trim(), timeFormatter);
          LocalTime endTime = LocalTime.parse(end.trim(), timeFormatter);

          if (!endTime.isAfter(startTime)) {
            JOptionPane.showMessageDialog(frame, "End time must be after start time.",
                "Invalid Time", JOptionPane.ERROR_MESSAGE);
            return;
          }

          LocalDateTime startDateTime = currentDate.atTime(startTime);
          LocalDateTime endDateTime = currentDate.atTime(endTime);

          IEvent newEvent = new SingleEvent(name.trim(), startDateTime, endDateTime);
          events.add(newEvent);
          events.sort((a, b) -> a.getStart().compareTo(b.getStart())); // ✅ sort chronologically

          dayView.setEvents(events);
          dayView.repaint();

          dayView.setEvents(events);
          dayView.repaint();

          System.out.println("Event created: " + name + " from " + startTime + " to " + endTime);
        } catch (Exception e) {
          JOptionPane.showMessageDialog(frame, "Did not create event",
              "Error", JOptionPane.ERROR_MESSAGE);
        }
        System.out.println("Event created: " + name);
      }
    });

    frame.add(topPanel, BorderLayout.NORTH);

    dayView = new DayView(currentDate, new ArrayList<>());
    JScrollPane scrollPane = new JScrollPane(dayView);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    frame.add(scrollPane, BorderLayout.CENTER);

    frame.setVisible(true);
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
    this.dayView.setEvents(events);

  }

  @Override
  public void displayError(String msg) {

  }

  @Override
  public void setListener(ActionListener listener) {
    createButton.addActionListener(listener);

  }

  private void openCreateEventDialog() {

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
