package views;

import controllers.Features;
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
  void getMainMenuChoice();

  /**
   * Gets the name of teh portfolio from the client.
   *
   * @return the name of the portfolio in string format.
   */
  void getPortfolioName();

  /**
   * Prints the message to be displayed to the client.
   *
   * @param msg message to be displayed.
   */
  void printMessage(String msg);

  void successMessage(String ticker, Details details, Txn txnType);

  /**
   * Provides the choices that the user gets after choosing to create a portfolio.
   *
   * @return choice entered by the user.
   */
  void getCreatePortfolioThroughWhichMethod();

  /**
   * Take the choice that determines the kind of portfolio the user wants to work with.
   * @return choice of the user.
   */
  void getAddToPortfolioChoice();

  /**
   * Take filepath form the user.
   * @return file path of the csv file.
   */
  void getFilePath();

  /**
   * Asks the user for the ticker symbol.
   *
   * @return the ticker symbol chosen by the user.
   */
  void getTickerSymbol();

  /**
   * Gives the quantity of the shares.
   *
   * @return double representing the quantity.
   */
  void getQuantity();

  /**
   * Provides user the options for the date that they would like to choose. They may their
   * choose to opt for today's date or a custom date.
   *
   * @return the choice entered by the user.
   */
  void getDateChoice();

  /**
   * Prints the menu for user that states the format in which they are supposed to enter the date.
   *
   * @return the date in string format.
   */
  void getDateForValue();

  /**
   * Choose the type of composition from the user.
   * @return User's choice.
   */
  void getPortfolioCompositionOption();

  /**
   * Choose the type of transaction from the user.
   * @return user's choice.
   */
  void getBuySellChoice();

  /**
   * get commission fee from the broker.
   * @return brokers fee form the user.
   */
  void getCommissionFee();

  void getStrategyName();

  void getWeightage();
  void getStrategyAmount();

  void getInterval();

//  void getPortfolioInterval();    //use this instead of getInterval()

}
