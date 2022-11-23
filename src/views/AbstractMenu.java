package views;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import models.Details;
import models.portfolio.Txn;

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

  protected abstract void displayManyMenuOptions();

  protected abstract char getBuySellChoiceIfApplicable();

  protected abstract double getCommissionFeeIfApplicable();

  protected abstract String getStrategyNameIfApplicable();

  protected abstract double getWeightageIfApplicable();

  protected abstract double getStrategyAmountIfApplicable();
  protected abstract int getIntervalIfApplicable();

  protected abstract void printAddToPortfolioChoicesDifferently();

  @Override
  public char getMainMenuChoice() {
    displayManyMenuOptions();
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
  public void successMessage(String ticker, Details details, Txn txnType) {
    String txn = txnType == Txn.Buy ? "bought" : "sold";
    this.print("\nSuccessfully " + txn + " " + details.getQuantity()
            + " shares of " + ticker.toUpperCase() + " on " + details.getPurchaseDate() + "\n");
  }

  @Override
  public char getCreatePortfolioThroughWhichMethod() {
    this.print("\nCreate portfolio through :\n1. Interface\n2. File upload"
            + "\nPress any other key to go back.\n\nEnter your choice : ");
    return getCharVal();
  }

  @Override
  public char getAddToPortfolioChoice() {
    printAddToPortfolioChoicesDifferently();
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
    this.print("Enter the number of shares : ");
    return getDoubleVal();
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
    this.print("Enter date in YYYY-MM-DD format : ");
    return getWordVal();
  }

  @Override
  public char getPortfolioCompositionOption() {
    this.print("\n1. Get contents of the portfolio\n"
            + "2. Get weightage of shares in the portfolio\n"
            + "Press any other key to go back.\n\n"
            + "Enter your choice : ");
    return getCharVal();
  }

  @Override
  public char getBuySellChoice() {
    return getBuySellChoiceIfApplicable();
  }

  @Override
  public double getCommissionFee() {
    return getCommissionFeeIfApplicable();
  }

  @Override
  public String getStrategyName() {
    return getStrategyNameIfApplicable();
  }

  @Override
  public double getWeightage() {
    return getWeightageIfApplicable();
  }

  @Override
  public double getStrategyAmount() {
    return getStrategyAmountIfApplicable();
  }

  @Override
  public int getInterval() {
    return getIntervalIfApplicable();
  }

  protected void print(String msg) {
    this.out.print(msg);
  }

  protected char getCharVal() {
    char val = sc.next().charAt(0);
    sc.nextLine();
    return val;
  }

  protected String getWordVal() {
    String val = sc.next();
    sc.nextLine();
    return val;
  }

  protected double getDoubleVal() {
    double quantity = sc.nextDouble();
    sc.nextLine();
    return quantity;
  }

  protected int getIntVal() {
    int val = sc.nextInt();
    sc.nextLine();
    return val;
  }
}
