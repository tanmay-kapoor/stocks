package controllers;

import java.time.LocalDate;
import java.util.Map;

import models.api.ShareApi;
import models.portfolio.Portfolio;
import models.portfolio.StockPortfolio;
import views.Menu;

public class StockController extends AbstractController {
  public StockController(Menu menu, ShareApi api, String folder) {
    super(menu, api, folder);
  }

  protected Portfolio createPortfolio(String portfolioName, LocalDate dateCreated) {
    return new StockPortfolio(portfolioName, dateCreated, api);
  }

  protected Portfolio createPortfolio(String portfolioName, LocalDate dateCreated, Map<String, Map<String, Object>> stocks) {
    return new StockPortfolio(portfolioName, dateCreated, stocks, api);
  }
}




