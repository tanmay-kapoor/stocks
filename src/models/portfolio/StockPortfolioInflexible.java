package models.portfolio;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
   * @param api           API is meant to be used.
   */
  public StockPortfolioInflexible(String portfolioName, String path, ShareApi api) {
    super(portfolioName, path, api);
  }

  public StockPortfolioInflexible(String portfolioName, Map<String, Log> stocks, String path,
                                  ShareApi api, Map<LocalDate, Double> costBasisHistory) {
    super(portfolioName, stocks, path, api, costBasisHistory);
  }

  protected boolean portfolioBasedSell(String ticker, Details details) {
    return false;
  }

  protected void storeCostBasis(String ticker, Details details, double commissionFee, Txn txn) {
    return;
  }

  protected void saveLastSoldLog() {
    return;
  }

  protected Map<String, Log> getCompositionSpecificDate(LocalDate date) {
    date = LocalDate.now();
    return filterBasedOnDate(date);
  }
}
