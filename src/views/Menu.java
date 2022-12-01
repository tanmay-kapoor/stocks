package views;

import models.Details;
import models.portfolio.Txn;

/**
 * An interface that states the methods to be implemented by any class that extends the
 * <code>Menu</code> class. The methods listed below shows the functions that are going
 * to be used to display the menu to the user.
 */
public interface Menu {
  /**
   * Shows the started options that the user gets when they run the program.
   */
  void getMainMenuChoice();

  /**
   * Gets the name of teh portfolio from the client.
   */
  void getPortfolioName();

  /**
   * Print msg to user.
   *
   * @param msg message to display to user.
   */
  void printMessage(String msg);

  /**
   * Print msg to user in case of errors.
   *
   * @param msg message to display to user.
   */
  void errorMessage(String msg);

  /**
   * Print success message to user in case buy/sell was successful.
   *
   * @param ticker  ticker bought/sold.
   * @param details quantity and dat of transaction.
   * @param txnType cane be Buy/Sell.
   */
  void successMessage(String ticker, Details details, Txn txnType);

  /**
   * Provides the choices that the user gets after choosing to create a portfolio.
   */
  void getCreatePortfolioThroughWhichMethod();

  /**
   * Take the choice that determines the kind of portfolio the user wants to work with.
   */
  void getAddToPortfolioChoice();

  /**
   * Take filepath form the user.
   */
  void getFilePath();

  /**
   * Asks the user for the ticker symbol.
   */
  void getTickerSymbol();

  /**
   * Gives the quantity of the shares.
   */
  void getQuantity();

  /**
   * Provides user the options for the date that they would like to choose. They may their
   * choose to opt for today's date or a custom date.
   */
  void getDateChoice();

  /**
   * Prints the menu for user that states the format in which they are supposed to enter the date.
   */
  void getDateForValue();

  /**
   * Choose the type of composition from the user.
   */
  void getPortfolioCompositionOption();

  /**
   * Choose the type of transaction from the user.
   */
  void getBuySellChoice();

  /**
   * Get commission fee from the broker.
   */
  void getCommissionFee();

}
