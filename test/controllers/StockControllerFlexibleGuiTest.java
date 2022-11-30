package controllers;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import models.Details;
import models.Log;
import models.api.DateDetails;
import models.api.ShareApi;
import models.portfolio.Dca;
import models.portfolio.Portfolio;
import views.Menu;
import views.StockMenuFlexible;

public class StockControllerFlexibleGuiTest {
  private String path;
  private ShareApi api;
  private PrintStream out;
  private StringBuilder log;
  private Features controller;

  private class MockApi implements ShareApi {

    @Override
    public boolean isTickerPresent(String ticker) {
      return false;
    }

    @Override
    public boolean hasPrice(String ticker, LocalDate date) {
      return false;
    }

    @Override
    public DateDetails getShareDetails(String tickerSymbol, LocalDate dateAsked) {
      log.append("Inside getShareDetails ")
              .append(tickerSymbol).append(" ")
              .append(dateAsked).append("\n");
      Map<String, Double> values = new HashMap<>();
      values.put("open", 0.0);
      values.put("high", 0.0);
      values.put("low", 0.0);
      values.put("close", 0.0);
      values.put("volume", 0.0);
      return new DateDetails(values, false);
    }
  }

  private class MockStockPortfolio implements Portfolio {

    private MockStockPortfolio(StringBuilder str) {
      log = str;
      log.append("Inside constructor").append("\n");
    }

    @Override
    public void buy(String ticker, double quantity) {
      log.append("Inside buy. Symbol : ").append(ticker)
              .append(" Quantity : ").append(quantity).append("\n");
    }

    @Override
    public void buy(String ticker, Details details, double commissionFee) {
      log.append("Inside buy(ticker, details, commissionFee). Symbol : ").append(ticker)
              .append(" Quantity : ").append(details.getQuantity())
              .append(" Purchase Date : ").append(details.getPurchaseDate())
              .append(" Commission Fee : ").append(commissionFee).append("\n");
    }

    @Override
    public boolean sell(String ticker, Details details, double commissionFee) {
      log.append("Inside sell(ticker, details), commissionFee. Symbol : ").append(ticker)
              .append(" Quantity : ").append(details.getQuantity())
              .append(" Purchase Date : ").append(details.getPurchaseDate())
              .append(" Commission Fee : ").append(commissionFee).append("\n");
      return false;
    }

    @Override
    public double getValue() {
      log.append("Inside getValue\n");
      return 2;
    }

    @Override
    public double getValue(LocalDate date) throws RuntimeException {
      log.append("Inside getValue(date) Received : ").append(date).append("\n");
      return 3;
    }

    @Override
    public Map<String, Log> getComposition() {
      log.append("Inside getComposition()\n");
      return new HashMap<>();
    }

    @Override
    public Map<String, Log> getComposition(LocalDate date) {
      log.append("Inside getComposition(date) Received : ").append(date).append("\n");
      return new HashMap<>();
    }

    @Override
    public boolean savePortfolio() {
      log.append("Inside savePortfolio\n");
      return false;
    }

    @Override
    public Map<LocalDate, Double> getPortfolioPerformance(LocalDate from, LocalDate to) {
      log.append("Inside getPortfolioPerformance(from, to) From : ").append(from)
              .append(" To : ").append(to).append("\n");
      return new HashMap<>();
    }

    @Override
    public double getCostBasis() {
      log.append("Inside getCostBasis()\n");
      return 2.2;
    }

    @Override
    public double getCostBasis(LocalDate date) {
      log.append("Inside getCostBasis(date) Date : ").append(date).append("\n");
      return 1.1;
    }

    @Override
    public void doDca(String dcaName, Dca dca) {
      log.append("Inside doDca. ").append(dcaName).append("\n");
    }

    @Override
    public Map<String, Dca> getDcaStrategies() {
      log.append("Inside getDcaStrategies").append("\n");
      return new HashMap<>();
    }
  }

  private class MockStockControllerGui extends StockControllerFlexibleGui {
    private MockStockControllerGui(ShareApi api, String path) {
      super(api, path);
      log = new StringBuilder();
    }

    @Override
    protected Portfolio createPortfolioObject(String portfolioName) {
      return new MockStockPortfolio(log);
    }

    @Override
    protected Portfolio createPortfolioObject(String portfolioName, Map<String, Log> stocks,
                                              String path, ShareApi api,
                                              Map<LocalDate, Double> costBasisHistory,
                                              Map<String, Dca> dcaMap) {
      return new MockStockPortfolio(log);
    }
  }

  @Before
  public void setUp() {
    this.path = System.getProperty("user.dir") + "/src/files/stocks/flexible/";
    api = new MockApi();

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    out = new PrintStream(bytes);

    controller = new MockStockControllerGui(api, path);
    Menu menu = new StockMenuFlexible(this.out);
    controller.setView(menu);
  }

  @Test
  public void test1() {
    controller.createPortfolio("something");
    System.out.println(this.log);
  }

  @Test
  public void idk() {
    controller.buyStock("trial", "GOOG", "10", "2022-10-10", "45.2");
    System.out.println(log);
  }
}