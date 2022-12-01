package views;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Class to get flexible/inflexible choice form user through gui.
 */
public class UiOptionImpl implements UiOption {
  private final PrintStream out;
  private final Scanner sc;

  /**
   * Initialize input and print stream.
   *
   * @param in  InputStream object.
   * @param out PrintStream object.
   */
  public UiOptionImpl(InputStream in, PrintStream out) {
    this.out = out;
    sc = new Scanner(in);
  }

  @Override
  public char getUiOption() {
    this.out.print("\nWork with:\n"
            + "1. Graphical User Interface\n"
            + "2. Text based Interface\n"
            + "Press any other key to exit.\n"
            + "Enter your choice : ");
    return sc.next().charAt(0);
  }
}
