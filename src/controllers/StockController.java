package controllers;

import java.util.Map;

import models.api.ShareApi;
import models.portfolio.Portfolio;
import models.portfolio.StockPortfolio;
import views.Menu;

public class StockController extends AbstractController {
  public StockController(Menu menu, ShareApi api, String folder) {
    super(menu, api, folder);
  }

  protected Portfolio createPortfolio(String portfolioName) {
    return new StockPortfolio(portfolioName, api);
  }

  protected Portfolio createPortfolio(String portfolioName, Map<String, Double> stocks) {
    return new StockPortfolio(portfolioName, stocks, api);
  }
}




