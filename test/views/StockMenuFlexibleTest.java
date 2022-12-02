package views;

import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

/**
 * A test class to check the methods view shared by different portfolios of the program.
 */
public class StockMenuFlexibleTest extends AbstractStockMenuTest {

  @Override
  protected Menu createObject(PrintStream out) {
    return new StockMenuFlexible(out);
  }

  @Override
  protected String getMainMenuExpected() {
    return "\n1. Create portfolio.\n"
            + "2. See portfolio composition.\n"
            + "3. Check portfolio value.\n"
            + "4. Buy/Sell shares from a portfolio.\n"
            + "5. See portfolio performance.\n"
            + "6. Get Cost basis.\n"
            + "Press any other key to go back.\n"
            + "\nEnter your choice : ";
  }

  @Override
  protected String getAddToPortfolioChoiceExpected() {
    return "\n1. Add a share to your portfolio.\n" +
            "2. Create Dollar Cost Average Strategy.\n" +
            "Press any other key to go back.\n" +
            "\n" +
            "Enter your choice : ";
  }

  @Override
  protected String getBuySellChoiceExpected() {
    return "\n1. Buy a stock\n"
            + "2. Sell a stock\n"
            + "Press any other key to go back.\n\n"
            + "Enter your choice : ";
  }

  @Override
  protected String getCommissionFeeExpected() {
    return "Enter the commission fee ($) you want to charge for this transaction : ";
  }
}