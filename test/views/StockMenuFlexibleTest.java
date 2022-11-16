package views;

import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

/**
 * A test class to check the methods view shared by different portfolios of the program.
 */
public class StockMenuFlexibleTest extends AbstractStockMenuTest {

  protected Menu createObject(InputStream in, PrintStream out) {
    return new StockMenuFlexible(in, out);
  }

  protected String getMainMenuExpected() {
    return "\n1. Create portfolio.\n"
            + "2. See portfolio composition.\n"
            + "3. Check portfolio value.\n"
            + "4. Buy/Sell shares from a portfolio.\n"
            +"5. See portfolio performance.\n"
            + "6. Get Cost basis.\n"
            + "Press any other key to go back.\n"
            + "\nEnter your choice : ";
  }

  protected String getBuySellChoiceExpected() {
    return "\n1. Buy a stock\n"
            + "2. Sell a stock\n"
            + "Press any other key to go back.\n\n"
            + "Enter your choice : ";
  }

  protected String getCommissionFeeExpected() {
    return "Enter the commission fee ($) you want to charge for this transaction : ";
  }

  @Override
  protected void getAssertStatementForBuySell(char c) {
    assertEquals('2', c);
  }

  @Override
  protected void getAssertStatementForCommission(double c) {
    assertEquals(44.0, c, 0);
  }
}