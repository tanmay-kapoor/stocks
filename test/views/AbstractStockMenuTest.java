package views;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

abstract class AbstractStockMenuTest {
  private ByteArrayOutputStream bytes;
  private PrintStream out;
  private Menu menu;

  protected abstract String getMainMenuExpected();

  protected abstract String getBuySellChoiceExpected();

  protected abstract String getCommissionFeeExpected();

  protected abstract Menu createObject(InputStream in, PrintStream out);

  protected abstract void getAssertStatementForBuySell(char c);

  protected abstract void getAssertStatementForCommission(double c);

  @Before
  public void setUp() {
    bytes = new ByteArrayOutputStream();
    out = new PrintStream(bytes);
  }

  @Test
  public void testMainMenu() {
    generateInputStream("1\n");
    char c = menu.getMainMenuChoice();
    assertEquals('1', c);
    String expected = getMainMenuExpected();
    assertEquals(expected, bytes.toString());
  }

  @Test
  public void testGetPortfolioName() {
    generateInputStream("idk");
    String name = menu.getPortfolioName();
    assertEquals("idk", name);
    String expected = "\nEnter portfolio name : ";
    assertEquals(expected, bytes.toString());
  }

  @Test
  public void testPrintMessage() {
    String input = "some random msg";
    generateInputStream(input);
    menu.printMessage(input);
    assertEquals(input + "\n", bytes.toString());
  }

  @Test
  public void getAddToPortfolioChoice() {
    generateInputStream("f\n");
    char c = menu.getAddToPortfolioChoice();
    assertEquals('f', c);
    String expected = "\n1. Add a share to your portfolio.\n"
            + "Press any other key to go back.\n"
            + "\nEnter your choice : ";
    assertEquals(expected, bytes.toString());
  }

  @Test
  public void getFilePath() {
    generateInputStream("/Users/tanmaykapoor/Desktop/idk.csv\n");
    String path = menu.getFilePath();
    assertEquals("/Users/tanmaykapoor/Desktop/idk.csv", path);
    String expected = "\nEnter the path of csv file for portfolio : ";
    assertEquals(expected, bytes.toString());
  }

  @Test
  public void getTickerSymbol() {
    generateInputStream("META\n");
    String ticker = menu.getTickerSymbol();
    assertEquals("META", ticker);
    String expected = "\nEnter ticker symbol of the company you would like to add to this "
            + "portfolio : ";
    assertEquals(expected, bytes.toString());
  }

  @Test
  public void getQuantity() {
    generateInputStream("45.7\n");
    double quantity = menu.getQuantity();
    assertEquals(45.7, quantity, 0);
    String expected = "Enter the number of shares : ";
    assertEquals(expected, bytes.toString());
  }

  @Test
  public void getDateChoice() {
    generateInputStream("2\n");
    char dateChoice = menu.getDateChoice();
    assertEquals('2', dateChoice);
    String expected = "\nCheck value for\n"
            + "1. Today\n"
            + "2. Custom date\n"
            + "Press any other key to go back.\n\n"
            + "Enter choice : ";
    assertEquals(expected, bytes.toString());
  }

  @Test
  public void getDateForValue() {
    generateInputStream("2022-10-10\n");
    String date = menu.getDateForValue();
    assertEquals("2022-10-10", date);
    String expected = "Enter date in YYYY-MM-DD format : ";
    assertEquals(expected, bytes.toString());
  }

  @Test
  public void getPortfolioCompositionOption() {
    generateInputStream("1\n");
    char choice = menu.getPortfolioCompositionOption();
    assertEquals('1', choice);
    String expected = "\n1. Get contents of the portfolio\n"
            + "2. Get weightage of shares in the portfolio\n"
            + "Press any other key to go back.\n\n"
            + "Enter your choice : ";
    assertEquals(expected, bytes.toString());
  }

  @Test
  public void getBuySellChoice() {
    generateInputStream("2\n");
    char c = menu.getBuySellChoice();
    getAssertStatementForBuySell(c);
    String expected = getBuySellChoiceExpected();
    assertEquals(expected, bytes.toString());
  }

  @Test
  public void getCommissionFee() {
    generateInputStream("44.0\n");
    double c = menu.getCommissionFee();
    getAssertStatementForCommission(c);
    String expected = getCommissionFeeExpected();
    assertEquals(expected, bytes.toString());
  }

  private void generateInputStream(String input) {
    InputStream in = new ByteArrayInputStream(input.getBytes());
    menu = createObject(in, out);
  }
}
