package controllers;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import models.Details;
import models.Log;
import models.api.DateDetails;
import models.api.ShareApi;
import models.portfolio.Composition;
import models.portfolio.Dca;
import models.portfolio.Portfolio;
import models.portfolio.StockPortfolioFlexible;
import views.Menu;
import views.StockMenuFlexible;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * A test class to test the methods implemented in the StockControllerInflexible.
 */
public class StockControllerFlexibleTest {
  private String path;
  private ShareApi api;
  private PrintStream out;
  private StringBuilder log;
  private String portfolioName;

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
      log.append("Inside get share details ")
              .append(tickerSymbol).append(" ")
              .append(dateAsked).append("\n");
      return new DateDetails(new HashMap<>(), false);
    }
  }

  private class MockStockPortfolio implements Portfolio {

    private MockStockPortfolio(StringBuilder str) {
      log = str;
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
      Map<LocalDate, Double> map = new HashMap<>();
      map.put(from, 24.5);
      return map;
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
      // later
    }

    @Override
    public Map<String, Dca> getDcaStrategies() {
      return null;
    }
  }

  private class MockStockControllerFlexible extends StockControllerFlexible {

    protected MockStockControllerFlexible(InputStream in, Menu menu, ShareApi api, String path) {
      super(in, menu, api, path);
      log = new StringBuilder();
    }

    @Override
    protected Portfolio createPortfolio(String portfolioName) {
      log.append("Inside createPortfolio(portfolioName)\n");
      return new MockStockPortfolio(log);
    }

    @Override
    protected Portfolio createPortfolio(String portfolioName, Map<String, Log> stocks,
                                        Map<LocalDate, Double> costBasisHistory,
                                        Map<String, Dca> dcaMap) {
      log.append("Inside createPortfolio(lots of values)\n");
      return new MockStockPortfolio(log);
    }
  }

  @Before
  public void setUp() {
    this.path = System.getProperty("user.dir") + "/src/files/stocks/flexible/";
    api = new MockApi();
    portfolioName = "bussin";

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    out = new PrintStream(bytes);
  }

  @Test
  public void testStart() {
    generateStream("1\n1\nfff\n1\nAAPL\n38\n2022-10-10\n34.5\nx\nx\n");
    String expected = "Inside createPortfolio(portfolioName)\n" +
            "Inside get share details AAPL 2022-12-02\n" +
            "Inside get share details AAPL 2022-10-10\n" +
            "Inside buy(ticker, details, commissionFee). Symbol : AAPL Quantity : 38.0 Purchase Date : 2022-10-10 Commission Fee : 34.5\n" +
            "Inside savePortfolio\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testStart2() {
    try {
      generateStream("1\n1\n" + portfolioName + "\nx\nx\n");
      fail("Should throw exception if portfolio already exists");
    } catch (Exception e) {
      // passes
    }
  }

  @Test
  public void testStart3() {
    generateStream("1\n1\n" + portfolioName + "\nxyz\n1\nAAPL\n22\n2022-10-10\n22.3\nx\nx\n");
    String expected = "Inside createPortfolio(portfolioName)\n" +
            "Inside get share details AAPL 2022-12-02\n" +
            "Inside get share details AAPL 2022-10-10\n" +
            "Inside buy(ticker, details, commissionFee). Symbol : AAPL Quantity : 22.0 Purchase Date : 2022-10-10 Commission Fee : 22.3\n" +
            "Inside savePortfolio\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testStart4() {
    generateStream("1\n1\nxx\n1\nAAPL\n-38\n2022-10-10\n20.3\nx\nx\n");
    String expected = "Inside createPortfolio(portfolioName)\n" +
            "Inside get share details AAPL 2022-12-02\n" +
            "Inside get share details AAPL 2022-10-10\n" +
            "Inside buy(ticker, details, commissionFee). Symbol : AAPL Quantity : -38.0 Purchase Date : 2022-10-10 Commission Fee : 20.3\n" +
            "Inside savePortfolio\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testGetComposition() {
    generateStream("2\n" + portfolioName + "\n1\n1\nx\nx\n");
    String expected = "Inside createPortfolio(lots of values)\n" +
            "Inside getComposition(date) Received : 2022-12-02\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testGetComposition2() {
    generateStream("2\nsfgweew\nq\n");
    String expected = "";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testGetComposition3() {
    generateStream("2\n" + portfolioName + "\n2\n1\nx\nx\n");
    String expected = "Inside createPortfolio(lots of values)\n" +
            "Inside getComposition(date) Received : 2022-12-02\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testGetComposition4() {
    generateStream("2\n" + portfolioName + "\n1\n2\n2022-10-10\nx\nx\n");
    String expected = "Inside createPortfolio(lots of values)\n" +
            "Inside getComposition(date) Received : 2022-10-10\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testGetValue() {
    generateStream("3\n" + portfolioName + "\n2\n2022-10-10\nq\n");
    String expected = "Inside createPortfolio(lots of values)\n" +
            "Inside getValue(date) Received : 2022-10-10\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testGetValue2() {
    generateStream("3\nwfw\nx\n");
    String expected = "";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testGetValue3() {
    generateStream("3\nwfw\n3\n" + portfolioName + "\n2\n2022-10-10\nx\n");
    String expected = "Inside createPortfolio(lots of values)\n" +
            "Inside getValue(date) Received : 2022-10-10\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testGetValue4() {
    generateStream("3\n" + portfolioName + "\n1\nx\n");
    String expected = "Inside createPortfolio(lots of values)\n" +
            "Inside getValue\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testBuy() {
    generateStream("4\n" + portfolioName + "\n1\nAAPL\n20\n2020-10-10\n20.6\nx\nx\nx\n");
    String expected = "Inside createPortfolio(lots of values)\n" +
            "Inside getComposition()\n" +
            "Inside get share details AAPL 2022-12-02\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testGetPerformance() {
    generateStream("5\n" + portfolioName + "\n2022-01-01\n2022-05-05\nx\n");
    String expected = "Inside createPortfolio(lots of values)\n" +
            "Inside getPortfolioPerformance(from, to) From : 2022-01-01 To : 2022-05-05\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testGetCostBasis() {
    generateStream("6\n" + portfolioName + "\n1\nx\nx\n");
    String expected = "Inside createPortfolio(lots of values)\n" +
            "Inside getCostBasis()\n";
    assertEquals(expected, log.toString());

    generateStream("6\n" + portfolioName + "\n2\n2022-01-01\nx\n");
    expected = "Inside createPortfolio(lots of values)\n" +
            "Inside getCostBasis(date) Date : 2022-01-01\n";
    assertEquals(expected, log.toString());
  }

  private void generateStream(String input) {
    InputStream in = new ByteArrayInputStream(input.getBytes());
    Menu menu = new StockMenuFlexible(out);
    SpecificController controller = new MockStockControllerFlexible(in, menu, api, path);
    controller.start();
  }
}