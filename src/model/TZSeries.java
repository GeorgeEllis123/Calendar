package model;

import java.util.ArrayList;

public class TZSeries extends AbstractSeries implements TZEvent {
    public TZSeries(ArrayList<IEvent> events, String subject) {
        super(events, subject);
    }

  @Override
  public void changeTimeZone(TimeZone newTimeZone) {

  }
}
