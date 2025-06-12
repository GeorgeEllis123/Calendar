import model.CalendarModel;
import model.CalendarModelImpl;

/**
 * Tests the {@code model.CalendarModelImpl} class.
 */
public class CalendarModelImplTest extends ACalendarTest {

  @Override
  protected CalendarModel getCalendarModel() {
    return new CalendarModelImpl();
  }

}
