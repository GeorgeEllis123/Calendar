import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import view.CalendarView;
import view.CalendarViewImpl;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@code view.CalendarViewImpl} class.
 */
public class CalendarViewImplTest {

  private ByteArrayOutputStream outputStream;
  private CalendarView view;

  @Before
  public void setUp() {
    outputStream = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(outputStream);
    view = new CalendarViewImpl(printStream);
  }

  @Test
  public void testPromptUser() {
    view.promptUser();
    String output = outputStream.toString().trim();
    assertEquals("Enter command:", output);
  }

  @Test
  public void testDisplayError() {
    String errorMsg = "Invalid input";
    view.displayError(errorMsg);
    String output = outputStream.toString().trim();
    assertEquals("ERROR: " + errorMsg, output);
  }

  @Test
  public void testDisplayMessage() {
    String message = "Hello world";
    view.displayMessage(message);
    String output = outputStream.toString().trim();
    assertEquals(message, output);
  }
}
