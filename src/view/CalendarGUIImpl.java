package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Map;

import model.IEvent;

public class CalendarGUIImpl implements CalendarGUI {

  private JFrame frame;
  private JLabel monthLabel;
  private JButton searchButton;
  private JComboBox<String> calendarDropdown;
  private Map<String, Color> calendars;

  private JButton createButton;
  private ArrayList<IEvent> events;
  private LocalDate currentDate;
  private DayView dayView;
  private String[] createInfo;
  private ActionListener listener;

  public CalendarGUIImpl() {
    this.createInfo = new String[3];
    this.events = new ArrayList<>();
    this.currentDate = LocalDate.now();

    frame = new JFrame("Day View");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(600, 800);
    frame.setLayout(new BorderLayout());

    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    searchButton = new JButton("search");
    topPanel.add(searchButton);

    monthLabel = new JLabel(currentDate.toString());
    topPanel.add(monthLabel);

    createButton = new JButton("create");
    topPanel.add(createButton);

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
  public void popupCreateWindow() {
    JFrame popupFrame = new JFrame("Create Event");
    popupFrame.setSize(300, 200);
    popupFrame.setLayout(new GridLayout(4, 2));

    JTextField subjectField = new JTextField();
    JTextField startTimeField = new JTextField();
    JTextField endTimeField = new JTextField();

    popupFrame.add(new JLabel("Subject:"));
    popupFrame.add(subjectField);
    popupFrame.add(new JLabel("Start Time (HH:mm):"));
    popupFrame.add(startTimeField);
    popupFrame.add(new JLabel("End Time (HH:mm):"));
    popupFrame.add(endTimeField);

    JButton submitButton = new JButton("Submit");
    submitButton.setActionCommand("submit create");
    submitButton.addActionListener(e -> {
      createInfo[0] = subjectField.getText();
      createInfo[1] = getDateTime(startTimeField);
      createInfo[2] = getDateTime(endTimeField);
      if (listener != null) {
        listener.actionPerformed(new java.awt.event.ActionEvent(submitButton, 0, "submit create"));
      }
      popupFrame.dispose();
    });

    popupFrame.add(new JLabel());
    popupFrame.add(submitButton);

    popupFrame.setVisible(true);
  }

  @Override
  public void popupSearchWindow() {
    JFrame popupFrame = new JFrame("Search by Date");
    popupFrame.setSize(300, 150);
    popupFrame.setLayout(new GridLayout(4, 2));

    Integer[] days = new Integer[31];
    for (int i = 0; i < 31; i++) days[i] = i + 1;

    Integer[] years = new Integer[20];
    int baseYear = LocalDate.now().getYear();
    for (int i = 0; i < 20; i++) years[i] = baseYear - 10 + i;

    String[] months = new String[]{"JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE",
        "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"};

    JComboBox<String> monthDropdown = new JComboBox<>(months);
    JComboBox<Integer> dayDropdown = new JComboBox<>(days);
    JComboBox<Integer> yearDropdown = new JComboBox<>(years);

    popupFrame.add(new JLabel("Month:"));
    popupFrame.add(monthDropdown);
    popupFrame.add(new JLabel("Day:"));
    popupFrame.add(dayDropdown);
    popupFrame.add(new JLabel("Year:"));
    popupFrame.add(yearDropdown);

    JButton submit = new JButton("Search");
    submit.setActionCommand("submit search");
    submit.addActionListener(e -> {
      int day = (int) dayDropdown.getSelectedItem();
      int year = (int) yearDropdown.getSelectedItem();
      int month = monthDropdown.getSelectedIndex() + 1;
      try {
        this.currentDate = LocalDate.of(year, month, day);
        if (listener != null) {
          listener.actionPerformed(new java.awt.event.ActionEvent(submit, 0, "submit search"));
        }
        popupFrame.dispose();
      } catch (DateTimeParseException ex) {
        displayError("Invalid date.");
      }
    });

    popupFrame.add(new JLabel());
    popupFrame.add(submit);
    popupFrame.setVisible(true);
  }

  private String getDateTime(JTextField timeField) {
    try {
      LocalTime time = LocalTime.parse(timeField.getText().trim());
      return this.currentDate.atTime(time).toString();
    } catch (Exception e) {
      displayError("Invalid time format: " + timeField.getText() + ". Please use HH:mm format.");
    }
    return "";
  }

  @Override
  public String[] getCreate() {
    return this.createInfo;
  }

  @Override
  public void loadDay(ArrayList<IEvent> events) {
    this.events = events;
    this.dayView.setDate(currentDate);
    this.dayView.setEvents(events);
    this.monthLabel.setText(currentDate.toString());
  }

  @Override
  public void promptUser() {
    throw new UnsupportedOperationException("Does nothing in this implementation.");
  }

  @Override
  public void displayError(String msg) {
    JOptionPane.showMessageDialog(frame, msg, "Error", JOptionPane.ERROR_MESSAGE);
  }

  @Override
  public void displayMessage(String msg) {
    JOptionPane.showMessageDialog(frame, msg, "Message", JOptionPane.INFORMATION_MESSAGE);
  }

  @Override
  public void setListener(ActionListener listener) {
    this.listener = listener;
    createButton.addActionListener(listener);
    searchButton.addActionListener(listener);
  }

}

