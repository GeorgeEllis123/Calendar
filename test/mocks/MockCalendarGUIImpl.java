package mocks;

import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import model.IEvent;
import model.ModifiableCalendar;
import view.CalendarGUI;

public class MockCalendarGUIImpl implements CalendarGUI {
  public String lastError;
  public boolean wasPopupCreateCalled;
  public boolean wasPopupSearchCalled;
  private String[] createInfo;
  private Map<String, String> editInfo;
  public String lastMessage;
  private LocalDate fakeCurrentDate;
  public boolean wasLoadDayCalled = false;

  public void setCurrentDate(String date) {
    this.fakeCurrentDate = LocalDate.parse(date);
  }

  @Override
  public LocalDate getLoadDay() {
    return fakeCurrentDate;
  }

  @Override
  public void popupCreateWindow() {
    wasPopupCreateCalled = true;
  }

  @Override
  public void popupSearchWindow() {
    wasPopupSearchCalled = true;
  }

  @Override
  public void loadDay(List<IEvent> events) {
    wasLoadDayCalled = true;
  }

  public void setCreateInfo(String[] info) {
    this.createInfo = info;
  }

  @Override
  public String[] getCreate() {
    return this.createInfo;
  }

  @Override
  public Map<String, String> getEdit() {
    return this.editInfo;
  }

  @Override
  public String getCalendar() {
    return "";
  }

  @Override
  public String getNewCalendar() {
    return "";
  }

  public void setEditInfo(Map<String, String> info) {
    this.editInfo = info;
  }

  @Override
  public void displayMessage(String msg) {
    this.lastMessage = msg;
  }

  @Override
  public void displayError(String msg) {
    this.lastError = msg;
  }

  @Override
  public void popupEditWindow(IEvent event) {

  }

  @Override
  public void popupCalendarWindow(List<ModifiableCalendar> calendars) {

  }

  @Override
  public void setListener(ActionListener listener) {

  }

  @Override
  public void promptUser() {

  }

  public String getError() {
    return lastError;
  }
}
