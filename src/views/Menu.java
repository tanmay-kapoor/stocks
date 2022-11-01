package views;

import java.io.IOException;

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
   * @throws IOException
   */
  char getMainMenuChoice() throws IOException;

  /**
   * Gets the name of teh portfolio from the client.
   *
   * @return the name of the portfolio in string format.
   * @throws IOException
   */
  String getPortfolioName() throws IOException;

  /**
   * Prints the message to be displayed to the client.
   *
   * @param msg message to be displayed.
   * @throws IOException
   */
  void printMessage(String msg) throws IOException;

  /**
   * Provides the choices that the user gets after choosing to create a portfolio.
   *
   * @return choice entered by the user.
   * @throws IOException
   */
  char getCreatePortfolioThroughWhichMethod() throws IOException;

  char getAddToPortfolioChoice() throws IOException;

  String getFilePath() throws IOException;

  /**
   * Asks the user for the ticker symbol.
   *
   * @return the ticker symbol chosen by the user.
   * @throws IOException
   */
  String getTickerSymbol() throws IOException;

  /**
   * Gives the quantity of the shares.
   *
   * @return double representing the quantity.
   * @throws IOException
   */
  double getQuantity() throws IOException;

  /**
   * Provides user the options for the date that they would like to choose. They may their
   * choose to opt for today's date or a custom date.
   *
   * @return the choice entered by the user.
   * @throws IOException
   */
  char getDateChoice() throws IOException;

  /**
   * Prints the menu for user that states the format in which they are supposed to enter the date.
   *
   * @return the date in string format.
   * @throws IOException
   */
  String getDateForValue() throws IOException;
}
