package models.portfolio;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import models.Details;
import models.Log;
import models.api.ShareApi;

/**
 * A class that is an extension of <code>Portfolio</code> class. This class specifically
 * deals with the portfolio that store shares in the form of stocks listed in the NASDAQ.
 */
public class StockPortfolioInflexible extends AbstractPortfolio {
  /**
   * Constructor for the class that initializes the name of the portfolio,
   * date it was created and the API that it is supposed to use for the fetching relevant data.
   *
   * @param portfolioName name of the portfolio.
   * @param purchaseDate  creation date of the portfolio.
   * @param api           API is meant to be used.
   */
  public StockPortfolioInflexible(String portfolioName, LocalDate purchaseDate,
                                  String path, ShareApi api) {
    super(portfolioName, purchaseDate, path, api);
  }

  public StockPortfolioInflexible(String portfolioName, LocalDate purchaseDate, Map<String, Log> stocks,
                                  String path, ShareApi api) {
    super(portfolioName, purchaseDate, stocks, path, api);
  }

  public boolean PortfolioBasedSell(String ticker, Details details) {
    return false;
  }
}
