package controllers;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import models.Details;
import models.Log;
import models.api.DateDetails;
import models.api.ShareApi;
import models.portfolio.Dca;
import models.portfolio.Portfolio;
import models.portfolio.Report;
import views.Menu;
import views.StockMenuFlexible;

import static org.junit.Assert.assertEquals;

/**
 * test file for gui flexible controller.
 */
public class StockControllerFlexibleGuiTest {
  private String portfolioName;
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
      return getComposition(LocalDate.now());
    }

    @Override
    public Map<String, Log> getComposition(LocalDate date) {
      log.append("Inside getComposition(date) Received : ").append(date).append("\n");
      Map<String, Log> composition = new HashMap<>();
      Set<Details> detailsList = new TreeSet<>(Comparator.comparing(Details::getPurchaseDate));
      detailsList.add(new Details(10, date));
      composition.put("GOOG", new Log(detailsList, null));
      return composition;
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
      Map<LocalDate, Double> performance = new HashMap<>();
      performance.put(LocalDate.parse("2020-10-10"), 10.2);
      performance.put(LocalDate.parse("2020-11-10"), 10.4);
      return performance;
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

  private class MockStockControllerFlexibleGui extends StockControllerFlexibleGui {
    private MockStockControllerFlexibleGui(ShareApi api, String path) {
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
    String path = System.getProperty("user.dir") + "/src/files/stocks/flexible/";
    ShareApi api = new MockApi();

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(bytes);

    controller = new MockStockControllerFlexibleGui(api, path);
    Menu menu = new StockMenuFlexible(out);
    controller.setView(menu);

    portfolioName = "new";
  }

  @Test
  public void testCreatePortfolio() {
    controller.createPortfolio("something");
    String expected = "Inside constructor\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testBuyStock() {
    controller.buyStock(portfolioName, "GOOG", "10", "2022-10-10", "45.2");
    String expected = "Inside constructor\n" +
            "Inside getShareDetails GOOG 2022-11-30\n" +
            "Inside buy(ticker, details, commissionFee). Symbol : GOOG Quantity : 10.0 " +
            "Purchase Date : 2022-10-10 Commission Fee : 45.2\n" +
            "Inside savePortfolio\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testSellStock() {
    controller.sellStock(portfolioName, "GOOG", "10", "2022-10-10", "45.2");
    String expected = "Inside constructor\n" +
            "Inside getComposition()\n" +
            "Inside getComposition(date) Received : 2022-11-30\n" +
            "Inside sell(ticker, details), commissionFee. Symbol : GOOG Quantity : 10.0 " +
            "Purchase Date : 2022-10-10 Commission Fee : 45.2\n" +
            "Inside savePortfolio\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void savePortfolio() {
    controller.savePortfolio(portfolioName);
    String expected = "Inside constructor\n" +
            "Inside savePortfolio\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void getPortfolioContents() {
    Map<String, Double> contents = controller.getPortfolioContents(portfolioName,
            LocalDate.now().toString());
    Map<String, Double> ex = new HashMap<>();
    ex.put("GOOG", 10.0);
    String expected = "Inside constructor\n" +
            "Inside getComposition(date) Received : 2022-11-30\n";
    assertEquals(expected, log.toString());
    assertEquals(ex, contents);
  }

  @Test
  public void getPortfolioWeightage() {
    Map<String, Double> contents = controller.getPortfolioWeightage(portfolioName,
            LocalDate.now().toString());
    Map<String, Double> ex = new HashMap<>();
    ex.put("GOOG", 100.0);
    String expected = "Inside constructor\n" +
            "Inside getComposition(date) Received : 2022-11-30\n";
    assertEquals(expected, log.toString());
    assertEquals(ex, contents);
  }

  @Test
  public void getPortfolioValue() {
    double val = controller.getPortfolioValue(portfolioName, LocalDate.parse("2022-10-10")
            .toString());
    String expected = "Inside constructor\n" +
            "Inside getValue(date) Received : 2022-10-10\n";
    assertEquals(expected, log.toString());
    assertEquals(3.0, val, 0);
  }

  @Test
  public void getPortfolioPerformance() {
    Report report = controller.getPortfolioPerformance(portfolioName, "2020-10-10",
            "2022-10-10");
    String expected = "Inside constructor\n" +
            "Inside getPortfolioPerformance(from, to) From : 2020-10-10 To : 2022-10-10\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void getCostBasis() {
    double c = controller.getCostBasis(portfolioName, "2022-10-10");
    String expected = "Inside constructor\n" +
            "Inside getCostBasis(date) Date : 2022-10-10\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void addTickerToStrategy() {
    controller.addTickerToStrategy("GOOG", "45.5");
    String expected = "Inside getShareDetails GOOG 2022-11-30\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void saveDca() {
    Map<String, Double> stockWeightage = new HashMap<>();
    stockWeightage.put("GOOG", 100.0);
    controller.saveDca(portfolioName, "idk", "1000", "2021-10-10",
            "2022-10-10", "60", "33", stockWeightage);
    String expected = "Inside constructor\n" +
            "Inside doDca. idk\n";
    assertEquals(expected, log.toString());
  }
}