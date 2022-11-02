package controllers;

import java.time.LocalDate;
import java.util.Map;

import models.Details;
import models.api.ShareApi;
import models.portfolio.Portfolio;
import models.portfolio.StockPortfolio;
import views.Menu;

/**
 * The stockController implements methods that are meant to be executed while working
 * specifically for stock data.
 */
public class StockController extends AbstractController {
  public StockController(Menu menu, ShareApi api, String path) {
    super(menu, api, path);
  }

  protected Portfolio createPortfolio(String portfolioName, LocalDate dateCreated) {
    return new StockPortfolio(portfolioName, dateCreated, path, api);
  }
}


