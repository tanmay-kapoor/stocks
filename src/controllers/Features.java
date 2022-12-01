package controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import models.portfolio.Report;
import views.Menu;

/**
 * An interface that states the features of the controller. The GUI view calls these functions
 * from its action listeners to proceed further.
 */
public interface Features {
  /**
   * Set the view of the controller. used to switch between GUI and Text based view.
   *
   * @param view The particular view that is to be used (GUI?Text based).
   */
  void setView(Menu view);

  /**
   * Feature to handle the case when user choose to work with flexible portfolio.
   */
  void handleFlexibleSelected();

  /**
   * Feature to handle the case when user chooses to work with inflexible portfolio.
   */
  void handleInflexibleSelected();

  /**
   * Feature to stop the program whenever the user chooses to do so.
   */
  void exitProgram();

  /**
   * Feature to handle the case when the user chooses to upload csv file to add an
   * existing portfolio to the project.
   *
   * @param filePath File path of the portfolio the user wants to add to the project.
   */
  void handleCreatePortfolioThroughUpload(String filePath);

  /**
   * Feature to create portfolio using the portfolioName obtained from the view and calls the
   * corresponding method from the model to create a new portfolio.
   *
   * @param portfolioName Name of the portfolio as specified by the user.
   */
  void createPortfolio(String portfolioName);

  /**
   * Feature to buy shares of a stock and add it to a portfolio. Gets the required details
   * from the view, processes the details and passes it to the corresponding model method.
   *
   * @param portfolioName Name of the portfolio to buy stocks for.
   * @param ticker        Ticker symbol of the stock to add to the portfolio.
   * @param quant         Number of shares to buy for the mentioned ticker.
   * @param purchaseDate  Date on which to purchase the stock.
   * @param commission    Commission fee for this transaction.
   */
  void buyStock(String portfolioName, String ticker, String quant, String purchaseDate,
                String commission);


  /**
   * Feature to sell shares of a stock and update a portfolio. Gets the required details
   * from the view, processes the details and passes it to the corresponding model method.
   *
   * @param portfolioName Name of the portfolio to sell stocks of.
   * @param ticker        Ticker symbol of the stock to sell from the portfolio.
   * @param quant         Number of shares to sell for the mentioned ticker.
   * @param d             Date on which to sell the stock.
   * @param commission    Commission fee for this transaction.
   */
  void sellStock(String portfolioName, String ticker, String quant, String d, String commission);

  /**
   * Feature to save the portfolio after the required transactions have happened. This is used to
   * call the model method tp persist the portfolio.
   *
   * @param portfolioName Name of the portfolio to save.
   */
  void savePortfolio(String portfolioName);

  /**
   * Feature to get all the portfolio names created.
   *
   * @return list of strings where each string is a portfolio name.
   */
  List<String> getAllPortfolios();

  /**
   * Feature to get call model method for getting composition of the portfolio on a specified date.
   *
   * @param portfolioName Name of portfolio to get contents of.
   * @param date          Date on which to get the contents.
   * @return map where each key is a ticker in portfolio and each value is quantity on that date.
   */
  Map<String, Double> getPortfolioContents(String portfolioName, String date);

  /**
   * Feature to get call model method for getting weightages of the portfolio on a specified date.
   *
   * @param portfolioName Name of portfolio to get weightage of.
   * @param date          Date on which to get the weightage.
   * @return map where each key is a ticker in portfolio and each value is percentage on that date.
   */
  Map<String, Double> getPortfolioWeightage(String portfolioName, String date);

  /**
   * Feature to call the model for getting the value of a portfolio on a specific date.
   *
   * @param portfolioName Name of portfolio to get value of.
   * @param date          Date on which to get the value.
   * @return double value which is the valuation of portfolio on that date.
   */
  double getPortfolioValue(String portfolioName, String date);

  /**
   * Feature to get portfolio performance for a specific time frame.
   *
   * @param portfolioName Name of portfolio to get performance of.
   * @param f             Start date for the performance time frame.
   * @param t             End date for the performance time frame.
   * @return report of the portfolio performance for the time frame mentioned.
   */
  Report getPortfolioPerformance(String portfolioName, String f, String t);

  /**
   * Feature to call model method for getting cost basis of portfolio on mentioned date.
   *
   * @param portfolioName Name of portfolio to get the cost basis of.
   * @param date          Date on which to get the cost basis on.
   * @return double value which is the cost basis for the portfolio on that date.
   */
  double getCostBasis(String portfolioName, String date);

  /**
   * Feature to reset the total weightage to 100% before adding ticker to dca and also
   * after dca has been created.
   */
  void resetTotalWeightage();

  /**
   * Feature to add ticker and weightage to a particular dca strategy.
   *
   * @param ticker    Ticker symbol to add to the dca strategy.
   * @param weightage Percentage of the amount to add.
   */
  void addTickerToStrategy(String ticker, String weightage);

  /**
   * Feature to save dca to the portfolio if all the requirements are satisfied.
   *
   * @param portfolioName  Name of portfolio to add the dca to.
   * @param strategyName   Name of strategy to be added to the portfolio.
   * @param amt            Total amount being invested in the dca strategy.
   * @param f              Start date of the strategy.
   * @param t              End date of the strategy.
   * @param interval       Time interval in days for each investment in the strategy.
   * @param commission     Commission fee for the transaction.
   * @param stockWeightage Map representing the weightage of each ticker in the strategy.
   *                       All weightages should add up to 100%..
   */
  void saveDca(String portfolioName, String strategyName, String amt, String f, String t,
               String interval, String commission, Map<String, Double> stockWeightage);

  /**
   * Feature to get the percentage of weightage left for the dca being created currently.
   *
   * @return double value representing the percentage left for the rest of the tickers for the dca.
   */
  double getWeightageLeft();

  void createEmptyDcaLog(String pName) throws IOException;
}
