package controllers;

import java.time.LocalDate;

import models.api.ShareApi;
import models.portfolio.Portfolio;
import models.portfolio.StockPortfolioInflexible;
import views.Menu;

/**
 * The StockController implements methods that are meant to be executed while working
 * specifically for stock data.
 */
public class StockController extends AbstractController {
  public StockController(Menu menu, ShareApi api, String path) {
    super(menu, api, path);
  }

  protected Portfolio createPortfolio(String portfolioName, LocalDate purchaseDate) {
    return new StockPortfolioInflexible(portfolioName, purchaseDate, path, api);
  }
}
