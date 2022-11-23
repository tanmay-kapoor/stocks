package models.portfolio;

import java.time.LocalDate;
import java.util.Map;

import models.Details;
import models.Log;

/**
 * An interface that states the methods that are expected from any kind of portfolio (e.g. stock
 * portfolio, crypto portfolio, mutual fund portfolio, etc.).
 */
public interface Portfolio {
  /**
   * Buy a share of a stock and stores it in the portfolio.
   *
   * @param ticker   symbol of the stock to purchase.
   * @param quantity number of shares to buy.
   */
  void buy(String ticker, double quantity);


  void buy(String ticker, Details details, double commissionFee);

  /**
   * A method to sell a stock present in the portfolio.
   * Sell transactions fails if no shares are present for the stock user wants to sell.
   *
   * @param ticker        symbol of the company
   * @param details       `Details` object containing details about the stocks
   * @param commissionFee fee to be charged by the broker for the transaction.
   * @return true is sold successfully, or false sell fails.
   */
  boolean sell(String ticker, Details details, double commissionFee);

  /**
   * Get the total value of the portfolio. The valuation of a portfolio is determined by the
   * sum of the value of all share in the portfolio. The value of the share is determined by
   * its latest price multiplied by its quantity.
   *
   * @return the total valuation of a certain portfolio.
   */
  double getValue();

  /**
   * Get the total value of the portfolio. The valuation of a portfolio is determined by the
   * sum of the value of all share in the portfolio. The value of the share is determined by
   * its price on a certain date multiplied by its quantity.
   *
   * @param date date of the portfolio's valuation.
   * @return the total valuation of a certain portfolio.
   * @throws RuntimeException when the url used by the API is invalid or illegal date is passed.
   *                          A date is illegal if the date requested is before the
   *                          portfolio's creation date.
   */
  double getValue(LocalDate date) throws RuntimeException;

  /**
   * Shows the distribution of the shares and their quantity in a portfolio.
   *
   * @return hashmap containing stocks and Log object associated with the stock.
   */
  Map<String, Log> getComposition();

  /**
   * Shows the distribution of the shares and their quantity in a portfolio.
   *
   * @param date Date requested by the user.
   * @return hashmap containing stocks and Log object associated with the stock.
   */
  Map<String, Log> getComposition(LocalDate date);

  /**
   * Saves the portfolio created by the client in the local directory in csv format.
   *
   * @return true if the portfolio is saved successfully, false if not saved.
   */
  boolean savePortfolio();

  /**
   * Gives a map that states the valuation of the portfolio across a range of dates to
   * understand the variation of the value through time.
   *
   * @param from start date of performance.
   * @param to   end date of the performance.
   * @return map of dates and the corresponding valuations of the portfolio.
   */
  Map<LocalDate, Double> getPortfolioPerformance(LocalDate from, LocalDate to);

  /**
   * A method to get the cost basis till current date.
   *
   * @return cost basis of the portfolio.
   */
  double getCostBasis();

  /**
   * A method to get the cost basis till requested date.
   *
   * @return cost basis of the portfolio.
   */
  double getCostBasis(LocalDate date);

  void doDca(String dcaName, Dca dca);

  Map<String, Dca> getDcaStrategies();
}
