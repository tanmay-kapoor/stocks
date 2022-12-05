package views;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

abstract class AbstractStockMenuTest {
  private ByteArrayOutputStream bytes;
  private Menu menu;

  protected abstract String getMainMenuExpected();

  protected abstract String getAddToPortfolioChoiceExpected();

  protected abstract String getBuySellChoiceExpected();

  protected abstract String getCommissionFeeExpected();

  protected abstract Menu createObject(PrintStream out);

  @Before
  public void setUp() {
    bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);
    menu = createObject(out);
  }

  @Test
  public void testMainMenu() {
    menu.getMainMenuChoice();
    String expected = getMainMenuExpected();
    assertEquals(expected, bytes.toString());
  }

  @Test
  public void testGetPortfolioName() {
    menu.getPortfolioName();
    String expected = "\nEnter portfolio name : ";
    assertEquals(expected, bytes.toString());
  }

  @Test
  public void testPrintMessage() {
    String input = "some random msg";
    menu.printMessage(input);
    assertEquals(input + "\n", bytes.toString());
  }

  @Test
  public void getAddToPortfolioChoice() {
    menu.getAddToPortfolioChoice();
    String expected = getAddToPortfolioChoiceExpected();
    assertEquals(expected, bytes.toString());
  }

  @Test
  public void getFilePath() {
    menu.getFilePath();
    String expected = "\nEnter the path of csv file for portfolio : ";
    assertEquals(expected, bytes.toString());
  }

  @Test
  public void getTickerSymbol() {
    menu.getTickerSymbol();
    String expected = "\nEnter ticker symbol of the company you would like to add to this "
            + "portfolio : ";
    assertEquals(expected, bytes.toString());
  }

  @Test
  public void getQuantity() {
    menu.getQuantity();
    String expected = "Enter the number of shares : ";
    assertEquals(expected, bytes.toString());
  }

  @Test
  public void getDateChoice() {
    menu.getDateChoice();
    String expected = "\nCheck value for\n"
            + "1. Today\n"
            + "2. Custom date\n"
            + "Press any other key to go back.\n\n"
            + "Enter choice : ";
    assertEquals(expected, bytes.toString());
  }

  @Test
  public void getDateForValue() {
    menu.getDateForValue();
    String expected = "Enter date in YYYY-MM-DD format : ";
    assertEquals(expected, bytes.toString());
  }

  @Test
  public void getPortfolioCompositionOption() {
    menu.getPortfolioCompositionOption();
    String expected = "\n1. Get contents of the portfolio\n"
            + "2. Get weightage of shares in the portfolio\n"
            + "Press any other key to go back.\n\n"
            + "Enter your choice : ";
    assertEquals(expected, bytes.toString());
  }

  @Test
  public void getBuySellChoice() {
    menu.getBuySellChoice();
    String expected = getBuySellChoiceExpected();
    assertEquals(expected, bytes.toString());
  }

  @Test
  public void getCommissionFee() {
    menu.getCommissionFee();
    String expected = getCommissionFeeExpected();
    assertEquals(expected, bytes.toString());
  }
}
