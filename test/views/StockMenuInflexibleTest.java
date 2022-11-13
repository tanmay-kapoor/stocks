package views;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

/**
 * A test class that is used to test the methods implemented int the StockMenu class.
 */
public class StockMenuInflexibleTest extends AbstractStockMenuTest {
  protected String getMainMenuExpected() {
    return "\n1. Create Portfolio.\n"
            + "2. See portfolio composition.\n"
            + "3. Check portfolio value.\n"
            + "Press any other key to exit.\n"
            + "\n"
            + "Enter your choice : ";
  }

  protected String getBuySellChoiceExpected() {
    return "\nNot allowed for inflexible portfolio\n";
  }

  protected String getCommissionPercentExpected() {
    return "\nNot allowed for inflexible portfolio\n";
  }
}