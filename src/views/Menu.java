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
   *
   * @return option chosen by the user.
   */
  char getMainMenuChoice();

  /**
   * Gets the name of teh portfolio from the client.
   *
   * @return the name of the portfolio in string format.
   */
  String getPortfolioName();

  /**
   * Prints the message to be displayed to the client.
   *
   * @param msg message to be displayed.
   */
  void printMessage(String msg);

  void successMessage(String ticker, Details details, Txn txn_type);

  /**
   * Provides the choices that the user gets after choosing to create a portfolio.
   *
   * @return choice entered by the user.
   */
  char getCreatePortfolioThroughWhichMethod();

  /**
   * Take the choice that determines the kind of portfolio the user wants to work with.
   * @return choice of the user.
   */
  char getAddToPortfolioChoice();

  /**
   * Take filepath form the user.
   * @return file path of the csv file.
   */
  String getFilePath();

  /**
   * Asks the user for the ticker symbol.
   *
   * @return the ticker symbol chosen by the user.
   */
  String getTickerSymbol();

  /**
   * Gives the quantity of the shares.
   *
   * @return double representing the quantity.
   */
  double getQuantity();

  /**
   * Provides user the options for the date that they would like to choose. They may their
   * choose to opt for today's date or a custom date.
   *
   * @return the choice entered by the user.
   */
  char getDateChoice();

  /**
   * Prints the menu for user that states the format in which they are supposed to enter the date.
   *
   * @return the date in string format.
   */
  String getDateForValue();

  /**
   * Choose the type of composition from the user.
   * @return User's choice.
   */
  char getPortfolioCompositionOption();

  /**
   * Choose the type of transaction from the user.
   * @return user's choice.
   */
  char getBuySellChoice();

  /**
   * get commission fee from the broker.
   * @return brokers fee form the user.
   */
  double getCommissionFee();
}
