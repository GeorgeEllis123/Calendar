package model;

import java.util.ArrayList;

public class TZSeries extends AbstractSeries implements IEvent {
    public TZSeries(ArrayList<IEvent> events, String subject) {
        super(events, subject);
    }
}
