package view;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

import java.awt.Font;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import model.IEvent;

/**
 * Contains all the events in a day and displays them as a JPanel.
 */
public class DayView extends JPanel {
  private LocalDate date;
  private List<IEvent> events;
  private ActionListener actionListener;

  /**
   * Creates a DayView.
   *
   * @param date   the date of the view
   * @param events the events the view contains
   */
  public DayView(LocalDate date, List<IEvent> events) {
    this.date = date;
    this.events = events;

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBackground(Color.WHITE);
    setBorder(new EmptyBorder(10, 10, 10, 10));

    renderEvents();
  }

  /**
   * Sets the listener so it can handle user interaction to the controller.
   *
   * @param listener the listener for button presses to communicate with
   */
  public void setListener(ActionListener listener) {
    this.actionListener = listener;
  }

  /**
   * Updates the list of events.
   *
   * @param newEvents the new events to load
   */
  public void setEvents(List<IEvent> newEvents) {
    this.events = newEvents;
    removeAll();
    renderEvents();
    revalidate();
    repaint();
  }

  /**
   * Updates the date.
   *
   * @param date updates the date
   */
  public void setDate(LocalDate date) {
    this.date = date;
  }

  // renders the loaded events onto the screen
  private void renderEvents() {
    JLabel header = new JLabel("Events for " + date);
    header.setFont(new Font("SansSerif", Font.BOLD, 16));
    header.setAlignmentX(Component.LEFT_ALIGNMENT);
    add(header);
    add(Box.createRigidArea(new Dimension(0, 10)));

    if (events.isEmpty()) {
      JLabel noEvents = new JLabel("No events today.");
      noEvents.setFont(new Font("SansSerif", Font.ITALIC, 14));
      noEvents.setAlignmentX(Component.LEFT_ALIGNMENT);
      add(noEvents);
    } else {
      for (IEvent event : events) {
        if (event.getStart().toLocalDate().equals(date)) {
          JPanel eventPanel = new JPanel(new BorderLayout(10, 0));
          eventPanel.setBorder(BorderFactory.createCompoundBorder(
              BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
              new EmptyBorder(8, 10, 8, 10)
          ));
          eventPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

          LocalTime start = event.getStart().toLocalTime();
          LocalTime end = event.getEnd().toLocalTime();
          String labelText = start + " - " + end + ": " + event.getSubject();

          JLabel label = new JLabel(labelText);
          label.setFont(new Font("SansSerif", Font.PLAIN, 14));

          JButton editButton = new JButton("Edit");
          editButton.setActionCommand("edit");
          editButton.putClientProperty("event", event);
          editButton.addActionListener(actionListener);

          eventPanel.add(label, BorderLayout.CENTER);
          eventPanel.add(editButton, BorderLayout.EAST);

          add(eventPanel);
          add(Box.createRigidArea(new Dimension(0, 8)));
        }
      }
    }
  }
}
