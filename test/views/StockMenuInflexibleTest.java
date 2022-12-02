package views;

import java.io.PrintStream;

/**
 * A test class that is used to test the methods implemented int the StockMenu class.
 */
public class StockMenuInflexibleTest extends AbstractStockMenuTest {

  @Override
  protected Menu createObject(PrintStream out) {
    return new StockMenuInflexible(out);
  }

  @Override
  protected String getMainMenuExpected() {
    return "\n1. Create portfolio.\n"
            + "2. See portfolio composition.\n"
            + "3. Check portfolio value.\n"
            + "Press any other key to go back.\n"
            + "\n"
            + "Enter your choice : ";
  }

  @Override
  protected String getAddToPortfolioChoiceExpected() {
    return "\n1. Add a share to your portfolio.\n" +
            "Press any other key to go back.\n" +
            "\n" +
            "Enter your choice : ";
  }

  @Override
  protected String getBuySellChoiceExpected() {
    return "\nNot allowed for inflexible portfolio\n";
  }

  @Override
  protected String getCommissionFeeExpected() {
    return "\nNot allowed for inflexible portfolio\n";
  }
}