package mocks;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.TimeZone;

import model.CalendarExceptions.InvalidCalendar;
import model.CalendarExceptions.InvalidEvent;
import model.CalendarExceptions.InvalidProperty;
import model.CalendarExceptions.InvalidTimeZoneFormat;
import model.CalendarExceptions.NoCalendar;
import model.CalendarModel;
import model.IEvent;
import model.ModifiableCalendar;
import model.ModifiableCalendarImpl;
import model.MultipleCalendarModel;

/**
 * Represents a mock model to test the calendar mode.
 */
public class MockMultipleCalendarModel implements MultipleCalendarModel {
    public boolean testBoolean = true;
    public ArrayList<IEvent> events = new ArrayList<>();
    public ModifiableCalendar currentCal;
    public ArrayList<String> log = new ArrayList<>();
    public String name;
    public ArrayList<MultipleCalendarModel> multipleCalendarModels = new ArrayList<>();

    @Override
    public boolean addSingleEvent(String subject, LocalDateTime start, LocalDateTime end) {
        log.add("addSingleEvent:" + subject);
        return testBoolean;
    }

    @Override
    public boolean addRepeatingEvent(String subject, LocalDateTime start,
                                     LocalDateTime end, String weekdays, int count) {
        log.add("addRepeatingEventWithCount:" + subject);
        return testBoolean;
    }

    @Override
    public boolean addRepeatingEvent(String subject, LocalDateTime start,
                                     LocalDateTime end, String weekdays, LocalDate until) {
        log.add("addRepeatingEventWithUntil:" + subject);
        return testBoolean;
    }

    @Override
    public ArrayList<IEvent> queryEvent(LocalDate date) {
        log.add("queryEvent:" + date);
        return events;
    }

    @Override
    public ArrayList<IEvent> queryEvent(LocalDateTime start, LocalDateTime end) {
        log.add("queryEvent:" + start + " to " + end);
        return events;
    }

    @Override
    public boolean getStatus(LocalDateTime dateTime) {
        log.add("getStatus:" + dateTime);
        return testBoolean;
    }

    @Override
    public boolean editSingleEvent(String subject, LocalDateTime start,
                                   LocalDateTime end, String property, String newValue) {
        log.add("editSingleEvent:" + subject);
        return testBoolean;
    }

    @Override
    public boolean editFutureSeriesEvents(String subject, LocalDateTime start,
                                          String property, String newValue) {
        log.add("editFutureSeries:" + subject);
        return testBoolean;
    }

    @Override
    public boolean editEntireSeries(String subject, LocalDateTime start,
                                    String property, String newValue) {
        log.add("editEntireSeries:" + subject);
        return testBoolean;
    }

    @Override
    public void create(String calendarName, String timezone) throws InvalidProperty,
        InvalidTimeZoneFormat {
        log.add("create:" + calendarName + ":" + timezone);

        if (calendarName.equals("fail")) {
            throw new InvalidProperty("Simulated failure for testing");
        }
    }

    @Override
    public void edit(String calendarName, String property, String newProperty)
        throws InvalidProperty, InvalidCalendar {
        log.add("edit:" + calendarName + ":" + property + ":" + newProperty);

        if (calendarName.equals("fail")) {
            throw new InvalidProperty("Simulated failure for testing");
        }

    }

    @Override
    public void use(String calendarName) throws InvalidCalendar {
        log.add("use:" + calendarName);

        this.currentCal = new ModifiableCalendarImpl(calendarName,
            TimeZone.getTimeZone("EST"));

        if (calendarName.equals("fail")) {
            throw new InvalidProperty("Simulated failure for testing");
        }
    }

    @Override
    public void copyEvent(String eventName, LocalDateTime start, String calendarName,
                          LocalDateTime newStart) throws InvalidCalendar, InvalidEvent, NoCalendar {
        log.add("copyEvent:" + eventName + ":" + start + ":" + calendarName + ":" + newStart);

        if (eventName.equals("fail")) {
            throw new InvalidProperty("Simulated failure for testing");
        }
    }

    @Override
    public boolean copyEvents(LocalDate date, String calendarName, LocalDate toDate)
        throws InvalidCalendar, NoCalendar {
        log.add("copyEvents:" + date + ":" + calendarName + ":" + toDate);

        if (calendarName.equals("fail")) {
            throw new InvalidProperty("Simulated failure for testing");
        }

        return testBoolean;
    }

    @Override
    public boolean copyEvents(LocalDate start, LocalDate end, String calendarName,
                              LocalDate newStart) throws InvalidCalendar, NoCalendar {
        log.add("copyEvents:" + start + ":" + end + ":" + calendarName + ":" + newStart);

        if (calendarName.equals("fail")) {
            throw new InvalidProperty("Simulated failure for testing");
        }

        return testBoolean;
    }

    @Override
    public ModifiableCalendar getCurrentCalendar() {
        log.add("getCurrentCalendar");
        return this.currentCal;
    }
}