package views;

import java.io.InputStream;
import java.io.PrintStream;

/**
 * View that is meant to specifically deal with menu while interacting with user about
 * flexible stock portfolio. This has the ability to ask user the type of transaction they
 * would like to perform on the stocks present in the portfolio.
 */
public class StockMenuFlexible extends AbstractMenu {
  public StockMenuFlexible(InputStream in, PrintStream out) {
    super(in, out);
  }

  @Override
  protected void displayManyMenuOptions() {
    print("\n1. Create portfolio.\n"
            + "2. See portfolio composition.\n"
            + "3. Check portfolio value.\n"
            + "4. Buy/Sell shares from a portfolio.\n"
            + "5. See portfolio performance.\n"
            + "6. Get Cost basis.\n"
            + "Press any other key to go back.\n"
            + "\nEnter your choice : ");
  }

  @Override
  protected char getBuySellChoiceIfApplicable() {
    print("\n1. Buy a stock\n"
            + "2. Sell a stock\n"
            + "Press any other key to go back.\n\n"
            + "Enter your choice : ");
    return getCharVal();
  }

  @Override
  protected double getCommissionFeeIfApplicable() {
    print("Enter the commission fee ($) you want to charge for this transaction : ");
    return getDoubleVal();
  }

  @Override
  protected String getStrategyNameIfApplicable() {
    print("\nEnter a name for new strategy : ");
    return getWordVal();
  }

  @Override
  protected double getWeightageIfApplicable() {
    print("Enter % of amount you would like to invest : ");
    return getDoubleVal();
  }

  @Override
  protected double getStrategyAmountIfApplicable() {
    print("\nEnter amount to invest : ");
    return getDoubleVal();
  }

  @Override
  protected int getIntervalIfApplicable() {
    print("\nEnter interval for investment in days : ");
    return getIntVal();
  }

  @Override
  protected void printAddToPortfolioChoicesDifferently() {
    this.print("\n1. Add a share to your portfolio.\n"
            + "2. Create Dollar Cost Average Strategy.\n"
            + "Press any other key to go back.\n"
            + "\nEnter your choice : ");
  }
}
