package views;

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

  /**
   * Provides the choices that the user gets after choosing to create a portfolio.
   *
   * @return choice entered by the user.
   */
  char getCreatePortfolioThroughWhichMethod();

  char getAddToPortfolioChoice();

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

  char getPortfolioCompositionOption();
}
