import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import main.MultipleCalendarMain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests the {@code main.MultipleCalendarMain class}.
 */
public class MultipleCalendarMainTest {
  private final PrintStream originalOut = System.out;
  private final InputStream originalIn = System.in;
  private ByteArrayOutputStream outContent;

  @Before
  public void setUpStreams() {
    outContent = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outContent));
  }

  @After
  public void restoreStreams() {
    System.setOut(originalOut);
    System.setIn(originalIn);
  }

  @Test
  public void testNoArgumentsProvided() {
    MultipleCalendarMain.main(new String[]{});
    assertTrue(outContent.toString().contains("Please enter a mode"));
  }

  @Test
  public void testInvalidModeProvided() {
    MultipleCalendarMain.main(new String[]{"--blahblahblah"});
    assertTrue(outContent.toString().contains("Unknown mode"));
  }

  @Test
  public void testHeadlessModeNoFileProvided() {
    MultipleCalendarMain.main(new String[]{"--headless"});
    assertTrue(outContent.toString().contains("Please enter a file name"));
  }

  @Test
  public void testHeadlessModeFileNotFound() {
    MultipleCalendarMain.main(new String[]{"--headless", "res/hellothereimnotreal.txt"});
    assertTrue(outContent.toString().contains("File not found"));
  }

  @Test
  public void testHeadlessModeFileMissingExit() throws IOException {
    String path = "res/noExit.txt";
    MultipleCalendarMain.main(new String[]{"--headless", path});
    assertTrue(outContent.toString().contains("File must include an 'exit' command."));
  }

  @Test
  public void testHeadlessModeValidFile() {
    MultipleCalendarMain.main(new String[]{"--headless", "res/validCommandsMultiple.txt"});
    assertFalse(outContent.toString().contains("File must include an 'exit' command."));
    assertFalse(outContent.toString().contains("File not found"));
  }

  @Test
  public void testInteractiveMode() {
    String input = "exit\n";
    System.setIn(new ByteArrayInputStream(input.getBytes()));
    MultipleCalendarMain.main(new String[]{"--interactive"});
    assertFalse(outContent.toString().contains("Error"));
  }
}
