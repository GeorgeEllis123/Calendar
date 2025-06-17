package view;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import model.IEvent;

public class DayView extends JPanel {
  private LocalDate date;
  private List<IEvent> events;

  public DayView(LocalDate date, List<IEvent> events) {
    this.date = date;
    this.events = events;

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBackground(Color.WHITE);

    renderEvents();
  }

  public void setEvents(List<IEvent> newEvents) {
    this.events = newEvents;
    removeAll();
    renderEvents();
    revalidate();
    repaint();
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  private void renderEvents() {
    JLabel header = new JLabel("Events for " + date);
    header.setAlignmentX(Component.LEFT_ALIGNMENT);
    add(header);
    add(Box.createRigidArea(new Dimension(0, 10)));

    if (events.isEmpty()) {
      JLabel noEvents = new JLabel("No events today.");
      noEvents.setAlignmentX(Component.LEFT_ALIGNMENT);
      add(noEvents);
    } else {
      for (IEvent event : events) {
        if (event.getStart().toLocalDate().equals(date)) {
          LocalTime start = event.getStart().toLocalTime();
          LocalTime end = event.getEnd().toLocalTime();
          JLabel label = new JLabel(start + " - " + end + ":" + event.getSubject());
          label.setAlignmentX(Component.LEFT_ALIGNMENT);
          add(label);
          add(Box.createRigidArea(new Dimension(0, 5)));
        }
      }
    }
  }
}
