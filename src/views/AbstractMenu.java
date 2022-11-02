package views;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * An Abstract class that will be used for providing views that will be common to other
 * menu classes.
 */
abstract class AbstractMenu implements Menu {
  protected final InputStream in;
  protected final PrintStream out;
  protected final Scanner sc;

  protected AbstractMenu(InputStream in, PrintStream out) {
    this.in = in;
    this.out = out;
    sc = new Scanner(this.in);
  }

  @Override
  public char getMainMenuChoice() {
    this.print("\n1. Create Portfolio.\n"
            + "2. See portfolio composition.\n"
            + "3. Check portfolio value.\n"
            + "Press any other key to exit.\n"
            + "\nEnter your choice : ");
    return getCharVal();
  }

  @Override
  public String getPortfolioName() {
    this.print("\nEnter portfolio name : ");
    return sc.nextLine();
  }

  @Override
  public void printMessage(String msg) {
    this.print(msg + "\n");
  }

  @Override
  public char getCreatePortfolioThroughWhichMethod() {
    this.print("\nCreate portfolio through :\n1. Interface\n2. File upload"
            + "\nPress any other key to go back.\n\nEnter your choice : ");
    return getCharVal();
  }

  @Override
  public char getAddToPortfolioChoice() {
    this.print("\n1. Add a share to your portfolio.\n"
            + "Press any other key to go back.\n"
            + "\nEnter your choice : ");
    return getCharVal();
  }

  @Override
  public String getFilePath() {
    this.print("\nEnter the path of csv file for portfolio : ");
    return sc.nextLine();
  }

  @Override
  public String getTickerSymbol() {
    this.print("\nEnter ticker symbol of the company you would like to add to this portfolio : ");
    return getWordVal();
  }

  @Override
  public double getQuantity() {
    this.print("Enter the number of shares you would like to add : ");
    double quantity = sc.nextDouble();
    sc.nextLine();
    return quantity;
  }

  @Override
  public char getDateChoice() {
    this.print("\nCheck value for\n"
            + "1. Today\n"
            + "2. Custom date\n"
            + "Press any other key to go back.\n\n"
            + "Enter choice : ");
    return getCharVal();
  }

  @Override
  public String getDateForValue() {
    this.print("\nEnter date in YYYY-MM-DD format : ");
    return getWordVal();
  }

  private void print(String msg) {
    this.out.print(msg);
  }

  private char getCharVal() {
    char val = sc.next().charAt(0);
    sc.nextLine();
    return val;
  }

  private String getWordVal() {
    String val = sc.next();
    sc.nextLine();
    return val;
  }
}
