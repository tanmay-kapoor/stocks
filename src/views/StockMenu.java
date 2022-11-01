package views;

import java.io.InputStream;
import java.io.PrintStream;

/**
 * View that is meant to specifically deal with menu while interacting with user about
 * stock portfolio.
 */
public class StockMenu extends AbstractMenu {

  public StockMenu(InputStream in, PrintStream out) {
    super(in, out);
  }
}
