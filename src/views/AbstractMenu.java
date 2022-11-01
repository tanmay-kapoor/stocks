package views;

import java.io.IOException;
import java.util.Scanner;

/**
 * An Abstract class that will be used for providing views that will be common to other
 * menu classes.
 */
abstract class AbstractMenu implements Menu {
  protected final Readable in;
  protected final Appendable out;
  protected final Scanner sc;

  protected AbstractMenu(Readable in, Appendable out) {
    this.in = in;
    this.out = out;
    sc = new Scanner(this.in);
  }

  @Override
  public char getMainMenuChoice() throws IOException {
    this.print("\n1. Create Portfolio.\n" +
            "2. See portfolio composition.\n" +
            "3. Check portfolio value.\n" +
            "Press any other key to exit.\n" +
            "\nEnter your choice : ");
    return getCharVal();
  }

  @Override
  public String getPortfolioName() throws IOException {
    this.print("\nEnter portfolio name : ");
    return sc.nextLine();
  }

  @Override
  public void printMessage(String msg) throws IOException {
    this.print(msg + "\n");
  }

  @Override
  public char getCreatePortfolioChoice() throws IOException {
    this.print("\n1. Add a share to your portfolio.\n" +
            "Press any other key to exit.\n" +
            "\nEnter your choice : ");
    return getCharVal();
  }

  @Override
  public String getTickerSymbol() throws IOException {
    this.print("\nEnter ticker symbol of the company you would like to add to this portfolio : ");
    return getWordVal();
  }

  @Override
  public double getQuantity() throws IOException {
    this.print("Enter the number of shares you would like to add : ");
    double quantity = sc.nextDouble();
    sc.nextLine();
    return quantity;
  }

  @Override
  public char getDateChoice() throws IOException {
    this.print("\nCheck value for\n1. Today\n2. Custom date\nEnter choice : ");
    return getCharVal();
  }

  @Override
  public String getDateForValue() throws IOException {
    this.print("\nEnter date in YYYY-MM-DD format : ");
    return getWordVal();
  }

  private void print(String msg) throws IOException {
    this.out.append(msg);
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
