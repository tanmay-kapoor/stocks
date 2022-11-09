package views;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class MainMenuImpl implements MainMenu {

  private final InputStream in;
  private final PrintStream out;
  private final Scanner sc;

  public MainMenuImpl(InputStream in, PrintStream out) {
    this.in = in;
    this.out = out;
    sc = new Scanner(this.in);
  }

  @Override
  public char getPortfolioType() {
    this.out.print("\nWork with:\n" +
            "1. Flexible Portfolio\n" +
            "2. Inflexible Portfolio\n" +
            "Press any other key to exit.\n" +
            "\nEnter your choice : ");
    return sc.next().charAt(0);
  }
}
