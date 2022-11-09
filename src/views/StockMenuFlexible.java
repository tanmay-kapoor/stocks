package views;

import java.io.InputStream;
import java.io.PrintStream;

public class StockMenuFlexible extends AbstractMenu {
  public StockMenuFlexible(InputStream in, PrintStream out) {
    super(in, out);
  }

  protected void displayManyMenuOptions() {
    print("\n1. Create portfolio.\n"
            + "2. See portfolio composition.\n"
            + "3. Check portfolio value.\n"
            + "4. Buy/Sell shares from a portfolio.\n"
            + "Press any other key to go back.\n"
            + "\nEnter your choice : ");
  }
}
