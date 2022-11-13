package views;

import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

/**
 * A test class that is used to test the methods implemented int the StockMenu class.
 */
public class StockMenuInflexibleTest extends AbstractStockMenuTest {

  protected Menu createObject(InputStream in, PrintStream out) {
    return new StockMenuInflexible(in, out);
  }

  protected String getMainMenuExpected() {
    return "\n1. Create portfolio.\n"
            + "2. See portfolio composition.\n"
            + "3. Check portfolio value.\n"
            + "Press any other key to go back.\n"
            + "\n"
            + "Enter your choice : ";
  }

  protected String getBuySellChoiceExpected() {
    return "\nNot allowed for inflexible portfolio\n";
  }

  protected String getCommissionPercentExpected() {
    return "\nNot allowed for inflexible portfolio\n";
  }

  protected void getAssertStatementForBuySell(char c) {
    assertEquals('q', c);
  }

  @Override
  protected void getAssertStatementForCommission(double c) {
    assertEquals(0.0, c, 0);
  }
}