package models.portfolio;

import java.time.LocalDate;
import java.util.Map;

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

  protected LocalDate getSpecificDate(LocalDate date) {
    return LocalDate.now();
  }
}
