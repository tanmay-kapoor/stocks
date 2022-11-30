package views;

import java.io.PrintStream;

/**
 * View that is meant to specifically deal with menu while interacting with user about
 * stock portfolio.
 */
public class StockMenuInflexible extends AbstractMenu {

  public StockMenuInflexible(PrintStream out) {
    super(out);
  }

  @Override
  protected void displayManyMenuOptions() {
    print("\n1. Create portfolio.\n"
            + "2. See portfolio composition.\n"
            + "3. Check portfolio value.\n"
            + "Press any other key to go back.\n"
            + "\nEnter your choice : ");
  }

  @Override
  protected void getBuySellChoiceIfApplicable() {
    printNotAllowed();
  }

  @Override
  protected void getCommissionFeeIfApplicable() {
    printNotAllowed();
  }

  @Override
  protected void getStrategyNameIfApplicable() {
    printNotAllowed();
  }

  @Override
  protected void getWeightageIfApplicable() {
    printNotAllowed();
  }

  @Override
  protected void getStrategyAmountIfApplicable() {
    printNotAllowed();
  }

  @Override
  protected void getIntervalIfApplicable() {
    printNotAllowed();
  }

  @Override
  protected void printAddToPortfolioChoicesDifferently() {
    this.print("\n1. Add a share to your portfolio.\n"
            + "Press any other key to go back.\n"
            + "\nEnter your choice : ");
  }

  private void printNotAllowed() {
    print("\nNot allowed for inflexible portfolio\n");
  }

  @Override
  public void errorMessage(String msg) {
    return;
  }

  @Override
  public void getInterval() {

  }

  @Override
  public void getWeightage() {

  }

  @Override
  public void getStrategyAmount() {

  }

  @Override
  public void getStrategyName() {

  }
}
