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
import java.util.HashMap;
import java.util.Map;

import models.Details;
import models.Log;
import models.api.ShareApi;
import models.portfolio.Composition;
import models.portfolio.Dca;
import models.portfolio.Portfolio;
import views.Menu;
import views.StockMenuInflexible;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * A test class to test the methods implemented in the StockControllerInflexible.
 */
public class StockControllerInflexibleTest {
  private String path;
  private ShareApi api;
  private PrintStream out;
  private StringBuilder log;

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
    public Map<String, Double> getShareDetails(String tickerSymbol, LocalDate dateAsked) {
      log.append("Inside get share details ")
              .append(tickerSymbol).append(" ")
              .append(dateAsked).append("\n");
      return new HashMap<>();
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

    }

    @Override
    public Map<String, Dca> getDcaStrategies() {
      return null;
    }
  }

  private class MockStockControllerInflexible extends AbstractController {

    private MockStockControllerInflexible(Menu menu, ShareApi api, String path) {
      super(menu, api, path);
      log = new StringBuilder();
    }

    @Override
    protected Portfolio createPortfolio(String portfolioName) {
      log = new StringBuilder("Portfolio name : " + portfolioName + "\n");
      return new MockStockPortfolio(log);
    }

    @Override
    protected Portfolio createPortfolio(String portfolioName, Map<String, Log> stocks,
                                        Map<LocalDate, Double> costBasisHistory) {
      log = new StringBuilder("Portfolio name mult : " + portfolioName + "\n");
      return new MockStockPortfolio(log);
    }

    @Override
    protected Map<String, LocalDate> readLastSoldDateFromCsv(File logFile)
            throws FileNotFoundException {
      return new HashMap<>();
    }

    @Override
    protected Map<LocalDate, Double> readStockBasisHistoryFromCsv(File costBasisFile)
            throws FileNotFoundException {
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
    protected double getCommissionFee() {
      log.append("wont be calling model from getCommissionFee\n");
      return 0.0;
    }

    @Override
    protected void filterBasedOnFunction(Function function) {
      log.append("Inside filterBasedOnFunction(Function function) Received : ")
              .append(function).append("\n");
      switch (function) {
        case BuySell:
        case SeePerformance:
        case CostBasis:
          log.append("wont be calling commonStuff(function)\n");
          break;

        default:
          commonStuff(function);
      }
    }

    @Override
    protected void handleMenuOptions(Portfolio portfolio, Function function) {
      log.append("Inside handleMenuOptions(p, f) function = ").append(function).append("\n");
      switch (function) {
        case Composition:
          handleGetPortfolioComposition(portfolio);
          break;

        case GetValue:
          handleGetPortfolioValue(portfolio);
          break;

        default:
          throw new IllegalArgumentException("Illegal Value");
      }
    }

    @Override
    protected boolean giveDateOptionsIfApplicable(Portfolio portfolio, Composition option) {
      log.append("Inside giveDateOptionsIfApplicable(p, o) option = ").append(option).append("\n");
      getCompositionForToday(portfolio, option);
      return true;
    }

    @Override
    protected boolean handleCreatePortfolioOption(char choice, Portfolio portfolio, String portfolioName) {
      return false;
    }
  }

  @Before
  public void setUp() {
    this.path = System.getProperty("user.dir") + "/src/files/stocks/inflexible/";
    api = new MockApi();

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    out = new PrintStream(bytes);
  }

  @Test
  public void testStart() {
    generateStream("1\n1\nidk\n1\nAAPL\n38\nx\nx\n");
    String expected = "Portfolio name : idk\n" +
            "Inside get share details AAPL " + LocalDate.now() + "\n" +
            "Inside get share details AAPL " + LocalDate.now() + "\n" +
            "wont be calling model from getCommissionFee\n" +
            "Inside buy(ticker, details, commissionFee). Symbol : AAPL Quantity : " +
            "38.0 Purchase Date : " + LocalDate.now() + " Commission Fee : 0.0\n" +
            "Inside savePortfolio\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testStart2() {
    try {
      generateStream("1\n1\nrandom\nx\nx\n");
      fail("Should throw exception if portfolio already exists");
    } catch (Exception e) {
      // passes
    }
  }

  @Test
  public void testStart3() {
    generateStream("1\n1\nrandom\nxyz\n1\nAAPL\n22\nx\nx\n");
    String expected = "Portfolio name : xyz\n" +
            "Inside get share details AAPL " + LocalDate.now() + "\n" +
            "Inside get share details AAPL " + LocalDate.now() + "\n" +
            "wont be calling model from getCommissionFee\n" +
            "Inside buy(ticker, details, commissionFee). Symbol : AAPL Quantity : " +
            "22.0 Purchase Date : " + LocalDate.now() + " Commission Fee : 0.0\n" +
            "Inside savePortfolio\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testStart4() {
    generateStream("1\n1\nxx\n1\nAAPL\n-38\nx\nx\n");
    String expected = "Portfolio name : xx\n" +
            "Inside get share details AAPL " + LocalDate.now() + "\n" +
            "Inside get share details AAPL " + LocalDate.now() + "\n" +
            "wont be calling model from getCommissionFee\n" +
            "Inside buy(ticker, details, commissionFee). Symbol : AAPL Quantity : " +
            "-38.0 Purchase Date : " + LocalDate.now() + " Commission Fee : 0.0\n" +
            "Inside savePortfolio\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testGetComposition() {
    generateStream("2\nrandom\n1\nx\n");
    String expected = "Portfolio name mult : random\n" +
            "Inside handleMenuOptions(p, f) function = Composition\n" +
            "Inside giveDateOptionsIfApplicable(p, o) option = Contents\n" +
            "Inside getComposition(date) Received : " + LocalDate.now() + "\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testGetComposition2() {
    generateStream("2\nsfgweew\nq\n");
    String expected = "Inside filterBasedOnFunction(Function function) Received : Composition\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testGetComposition3() {
    generateStream("2\nrandom\n2\nx\n");
    String expected = "Portfolio name mult : random\n" +
            "Inside handleMenuOptions(p, f) function = Composition\n" +
            "Inside giveDateOptionsIfApplicable(p, o) option = Weightage\n" +
            "Inside getComposition(date) Received : " + LocalDate.now() + "\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testGetValue() {
    generateStream("3\nrandom\n2\n2022-10-10\nq\n");
    String expected = "Portfolio name mult : random\n"
            + "Inside handleMenuOptions(p, f) function = GetValue\n"
            + "Inside getValue(date) Received : 2022-10-10\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testGetValue2() {
    generateStream("3\nwfw\nx\n");
    String expected = "Inside filterBasedOnFunction(Function function) Received : GetValue\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testGetValue3() {
    generateStream("3\nwfw\n3\nrandom\n2\n2022-10-10\nx\n");
    String expected = "Portfolio name mult : random\n" +
            "Inside handleMenuOptions(p, f) function = GetValue\n" +
            "Inside getValue(date) Received : 2022-10-10\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testGetValue4() {
    generateStream("3\nrandom\n1\nx\n");
    String expected = "Portfolio name mult : random\n" +
            "Inside handleMenuOptions(p, f) function = GetValue\n" +
            "Inside getValue\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testBuySell() {
    generateStream("4\nx\n");
    String expected = "Inside filterBasedOnFunction(Function function) Received : BuySell\n" +
            "wont be calling commonStuff(function)\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testGetPerformance() {
    generateStream("5\nx\n");
    String expected = "Inside filterBasedOnFunction(Function function) Received : SeePerformance\n"
            + "wont be calling commonStuff(function)\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testGetCostBasis() {
    generateStream("6\n");
    String expected = "Inside filterBasedOnFunction(Function function) Received : CostBasis\n" +
            "wont be calling commonStuff(function)\n";
    assertEquals(expected, log.toString());
  }

  private void generateStream(String input) {
    InputStream in = new ByteArrayInputStream(input.getBytes());
    Menu menu = new StockMenuInflexible(in, out);
    SpecificController controller = new MockStockControllerInflexible(menu, api, path);
    controller.start();
  }
}