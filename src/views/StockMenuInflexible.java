package views;

import java.io.InputStream;
import java.io.PrintStream;

/**
 * View that is meant to specifically deal with menu while interacting with user about
 * stock portfolio.
 */
public class StockMenuInflexible extends AbstractMenu {

  public StockMenuInflexible(InputStream in, PrintStream out) {
    super(in, out);
  }

  protected void displayManyMenuOptions() {
    print("\n1. Create portfolio.\n"
            + "2. See portfolio composition.\n"
            + "3. Check portfolio value.\n"
            + "Press any other key to go back.\n"
            + "\nEnter your choice : ");
  }

  protected char getBuySellChoiceIfApplicable() {
    print("\nNot allowed for inflexible portfolio\n");
    return 'q';
  }

  protected double getCommissionFeeIfApplicable() {
    print("\nNot allowed for inflexible portfolio\n");
    return 0.0;
  }
}
