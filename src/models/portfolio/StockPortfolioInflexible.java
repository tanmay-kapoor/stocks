package models.portfolio;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import models.Details;
import models.Log;
import models.api.ShareApi;

/**
 * A class that is an extension of <code>Portfolio</code> class. This class specifically
 * deals with the portfolio that store shares in the form of stocks listed in the NASDAQ.
 */
public class StockPortfolioInflexible extends AbstractPortfolio {
  /**
   * Constructor for the class that initializes the name of the portfolio, and the API that it is
   * supposed to use for the fetching relevant data.
   *
   * @param portfolioName name of the portfolio.
   * @param api           API is meant to be used.
   */
  public StockPortfolioInflexible(String portfolioName, String path, ShareApi api) {
    super(portfolioName, path, api);
  }

  /**
   * Constructor for the class that initializes the name of the portfolio, stocks in them,
   * the cost basis history and the API that it is supposed to use for the fetching relevant data.
   *
   * @param portfolioName    name of the portfolio
   * @param stocks           initial stock in the portfolio
   * @param path             path where its stored
   * @param api              api to be used
   * @param costBasisHistory cost basis for the portfolio.
   */
  public StockPortfolioInflexible(String portfolioName, Map<String, Log> stocks, String path,
                                  ShareApi api, Map<LocalDate, Double> costBasisHistory,
                                  Map<String, Dca> dcaMap) {
    super(portfolioName, stocks, path, api, costBasisHistory, dcaMap);
  }

  protected boolean portfolioBasedSell(String ticker, Details details, double commissionFee) {
    return false;
  }

  protected void storeCostBasis(String ticker, Details details, double commissionFee, Txn txn) {
    return;
  }

  protected void saveLastSoldLog() {
    return;
  }

  protected void changePurchaseDateIfApplicable(Details details) {
    details.setPurchaseDate(LocalDate.now());
  }

  protected double changeCommissionFeeIfApplicable(double commissionFee) {
    return 0.0;
  }

  protected Map<LocalDate, Double> getPortfolioPerformanceIfApplicable(LocalDate from,
                                                                       LocalDate to) {
    // throw new RuntimeException("not allowed for inflexible portfolio");
    return new TreeMap<>();
  }

  @Override
  protected Map<String, Dca> getDcaStrategiesIfApplicable() {
    return new HashMap<>();
  }

  @Override
  protected void doDcaIfApplicable(String dcaName, Dca dca) {
    return;
  }
}
