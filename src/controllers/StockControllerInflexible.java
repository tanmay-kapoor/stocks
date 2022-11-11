package controllers;

import java.time.LocalDate;
import java.util.Map;

import models.Log;
import models.api.ShareApi;
import models.portfolio.Portfolio;
import models.portfolio.StockPortfolioInflexible;
import views.Menu;

/**
 * The StockControllerInflexible implements methods that are meant to be executed while working
 * specifically for stock data.
 */
public class StockControllerInflexible extends AbstractController {
  public StockControllerInflexible(Menu menu, ShareApi api, String path) {
    super(menu, api, path);
  }

  protected Portfolio createPortfolio(String portfolioName, LocalDate purchaseDate) {
    return new StockPortfolioInflexible(portfolioName, purchaseDate, path, api);
  }

  @Override
  protected Portfolio createPortfolio(String portfolioName, LocalDate purchaseDate, Map<String, Log> stocks) {
    return new StockPortfolioInflexible(portfolioName, purchaseDate, stocks, path, api);
  }

  @Override
  protected LocalDate getPurchaseDate() {
    return LocalDate.now();
  }

  @Override
  protected char getLastOption() {
    return '3';
  }

  @Override
  protected void handleBuySellOption() {
    return;
  }

  @Override
  protected void handleBuySellInPortfolio(String name) {
    return;
  }
}
