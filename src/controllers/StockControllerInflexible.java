package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.HashMap;
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

  protected Portfolio createPortfolio(String portfolioName) {
    return new StockPortfolioInflexible(portfolioName, path, api);
  }

  @Override
  protected Portfolio createPortfolio(String portfolioName, Map<String, Log> stocks,
                                      Map<LocalDate, Double> costBasisHistory) {
    return new StockPortfolioInflexible(portfolioName, stocks, path, api, costBasisHistory);
  }

  @Override
  protected Map<String, LocalDate> readLastSoldDateFromCsv(File logFile) throws FileNotFoundException {
    return new HashMap<>();
  }

  @Override
  protected Map<LocalDate, Double> readStockBasisHistoryFromCsv(File costBasisFile) throws FileNotFoundException {
    return new HashMap<>();
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

  protected double getCommissionFee() {
    return 0.0;
  }
}
