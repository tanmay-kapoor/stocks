package models.portfolio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import models.Details;

/**
 * An interface that states the methods that are expected from any kind of portfolio (e.g. stock
 * portfolio, crypto portfolio, mutual fund portfolio, etc.).
 */
public interface Portfolio {
  /**
   * Adds a particular share to the <code>Portfolio</code> object.
   *
   * @param tickerSymbol ticker of the share to be added to the portfolio. Example-AAPL for
   *                     Apple, NFLX for Netflix.
   * @param quantity     amount of the share to be added to the portfolio. Example - 2 if the
   *                     client want to add two share to their portfolio.
   */
  void addShare(String tickerSymbol, double quantity, LocalDate purchaseDate);

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
   * @return quantity of shares next to their ticker symbols.
   */
  Map<String, PriorityQueue<Details>> getComposition();

  /**
   * Saves the portfolio created by the client in the local directory in csv format.
   *
   * @return true if the portfolio is saved successfully, false if not saved.
   */
  boolean savePortfolio();

}
