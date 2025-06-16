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
  private JLabel monthLabel;
  private JComboBox<String> calendarDropdown;

  private Map<String, Color> calendars;

  private JButton submitDate;

  private ArrayList<IEvent> events;
  private LocalDate currentDate;

  private DayView dayView;

  public CalendarGUIImpl() {
    frame = new JFrame("Day View");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(600, 800);
    frame.setLayout(new BorderLayout());

    currentDate = LocalDate.now();

    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    monthLabel = new JLabel(currentDate.toString());
    topPanel.add(monthLabel);

    submitDate = new JButton("Submit");
    submitDate.setActionCommand("load");
    topPanel.add(submitDate);

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
