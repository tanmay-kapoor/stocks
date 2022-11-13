package views;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class MainMenuImplTest {
  private ByteArrayOutputStream bytes;
  private PrintStream out;
  private MainMenu mainMenu;
  private String str;

  @Before
  public void setUp() {
    bytes = new ByteArrayOutputStream();
    out = new PrintStream(bytes);
    str = "\nWork with:\n" +
            "1. Flexible Portfolio\n" +
            "2. Inflexible Portfolio\n" +
            "Press any other key to exit.\n" +
            "\nEnter your choice : ";
  }

  @Test
  public void getPortfolioType() {
    char c;
    String expected;

    generateInputStream("1\n");
    c = mainMenu.getPortfolioType();
    assertEquals('1', c);
    expected = str;
    assertEquals(expected, bytes.toString());

    generateInputStream("2\n");
    c = mainMenu.getPortfolioType();
    assertEquals('2', c);
    expected += str;
    assertEquals(expected, bytes.toString());

    generateInputStream("q\n");
    c = mainMenu.getPortfolioType();
    assertEquals('q', c);
    expected += str;
    assertEquals(expected, bytes.toString());
  }

  private void generateInputStream(String input) {
    InputStream in = new ByteArrayInputStream(input.getBytes());
    mainMenu = new MainMenuImpl(in, out);
  }
}