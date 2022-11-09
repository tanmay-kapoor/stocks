package models.portfolio;

import java.time.LocalDate;

import models.api.ShareApi;

public class StockPortfolioFlexible extends AbstractPortfolio {
  /**
   * Constructor for the class that initializes the name of the portfolio,
   * date it was created and the API that it is supposed to use for the fetching relevant data.
   *
   * @param portfolioName name of the portfolio.
   * @param purchaseDate  creation date of the portfolio.
   * @param path
   * @param api           API is meant to be used.
   */
  public StockPortfolioFlexible(String portfolioName, LocalDate purchaseDate, String path, ShareApi api) {
    super(portfolioName, purchaseDate, path, api);
  }
}
