package views;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import controllers.Features;
import models.Details;
import models.portfolio.Txn;

/**
 * An Abstract class that will be used for providing views that will be common to other
 * menu classes.
 */
abstract class AbstractMenu implements Menu {
  protected final InputStream in;
  protected final PrintStream out;

  protected AbstractMenu(InputStream in, PrintStream out) {
    this.in = in;
    this.out = out;
  }

  protected abstract void displayManyMenuOptions();

  protected abstract void getBuySellChoiceIfApplicable();

  protected abstract void getCommissionFeeIfApplicable();

  protected abstract void getStrategyNameIfApplicable();

  protected abstract void getWeightageIfApplicable();

  protected abstract void getStrategyAmountIfApplicable();
  protected abstract void getIntervalIfApplicable();

  protected abstract void printAddToPortfolioChoicesDifferently();

  @Override
  public void getMainMenuChoice() {
    displayManyMenuOptions();
  }

  @Override
  public void getPortfolioName() {
    this.print("\nEnter portfolio name : ");
  }

  @Override
  public void printMessage(String msg) {
    this.print(msg + "\n");
  }

  @Override
  public void clearTextIfDisplayed() {
    return;
  }

  @Override
  public void successMessage(String ticker, Details details, Txn txnType) {
    String txn = txnType == Txn.Buy ? "bought" : "sold";
    this.print("\nSuccessfully " + txn + " " + details.getQuantity()
            + " shares of " + ticker.toUpperCase() + " on " + details.getPurchaseDate() + "\n");
  }

  @Override
  public void getCreatePortfolioThroughWhichMethod() {
    this.print("\nCreate portfolio through :\n1. Interface\n2. File upload"
            + "\nPress any other key to go back.\n\nEnter your choice : ");
  }

  @Override
  public void getAddToPortfolioChoice() {
    printAddToPortfolioChoicesDifferently();
  }

  @Override
  public void getFilePath() {
    this.print("\nEnter the path of csv file for portfolio : ");
  }

  @Override
  public void getTickerSymbol() {
    this.print("\nEnter ticker symbol of the company you would like to add to this portfolio : ");
  }

  @Override
  public void getQuantity() {
    this.print("Enter the number of shares : ");
  }

  @Override
  public void getDateChoice() {
    this.print("\nCheck value for\n"
            + "1. Today\n"
            + "2. Custom date\n"
            + "Press any other key to go back.\n\n"
            + "Enter choice : ");
  }

  @Override
  public void getDateForValue() {
    this.print("Enter date in YYYY-MM-DD format : ");
  }

  @Override
  public void getPortfolioCompositionOption() {
    this.print("\n1. Get contents of the portfolio\n"
            + "2. Get weightage of shares in the portfolio\n"
            + "Press any other key to go back.\n\n"
            + "Enter your choice : ");
  }

  @Override
  public void getBuySellChoice() {
    getBuySellChoiceIfApplicable();
  }

  @Override
  public void getCommissionFee() {
    getCommissionFeeIfApplicable();
  }

  @Override
  public void getStrategyName() {
    getStrategyNameIfApplicable();
  }

  @Override
  public void getWeightage() {
    getWeightageIfApplicable();
  }

  @Override
  public void getStrategyAmount() {
    getStrategyAmountIfApplicable();
  }

  @Override
  public void getInterval() {
    getIntervalIfApplicable();
  }

  protected void print(String msg) {
    this.out.print(msg);
  }
}
