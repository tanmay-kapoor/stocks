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
import models.api.ShareApi;
import models.portfolio.Portfolio;
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

  private class MockApi implements ShareApi {

    @Override
    public Map<String, Double> getShareDetails(String tickerSymbol, LocalDate dateAsked) {
      log.append("Inside get share details ").append(tickerSymbol).append(" ").append(dateAsked).append("\n");
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
  }

  private class MockStockControllerFlexible extends AbstractController {

    private MockStockControllerFlexible(Menu menu, ShareApi api, String path) {
      super(menu, api, path);
      log = new StringBuilder();
    }

    @Override
    protected Portfolio createPortfolio(String portfolioName) {
      log.append("Portfolio name : ").append(portfolioName).append("\n");
      return new MockStockPortfolio(log);
    }

    @Override
    protected Portfolio createPortfolio(String portfolioName, Map<String, Log> stocks, Map<LocalDate, Double> costBasisHistory) {
      log.append("Portfolio name mult : ").append(portfolioName).append("\n");
      return new MockStockPortfolio(log);
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
    protected void handleBuySellOption() {
      log.append("Inside handleBuySellOption()\n");
      commonStuff(Function.BuySell);
    }

    @Override
    protected void handleGetPortfolioPerformanceOption() {
      log.append("Inside handleGetPortfolioPerformanceOption()\n");
      commonStuff(Function.SeePerformance);
    }

    @Override
    protected char getLastOption() {
      return '6';
    }

    @Override
    protected void handleBuySellInPortfolio(Portfolio portfolio) {
      log.append("Inside handleBuySellInPortfolio()\n");
      Map<String, Log> portfolioComposition = portfolio.getComposition();

      char ch;
      boolean shouldSave = false;
      do {
        ch = menu.getBuySellChoice();
        String ticker;

        switch (ch) {
          case '1':
            log.append("Inside buy\n");
            try {
              ticker = menu.getTickerSymbol().toUpperCase();
              api.getShareDetails(ticker, LocalDate.now());
              Details details = getDetails();
              api.getShareDetails(ticker, details.getPurchaseDate());
              portfolio.buy(ticker, details, getCommissionFee());
              portfolioComposition = portfolio.getComposition();
              shouldSave = true;
            } catch (IllegalArgumentException e) {
              menu.printMessage("\n" + e.getMessage());
            }
            break;

          case '2':
            log.append("Inside sell\n");
            ticker = menu.getTickerSymbol().toUpperCase();
            if (!portfolioComposition.containsKey(ticker)) {
              menu.printMessage("\nCannot sell ticker that is not in portfolio");
            } else {
              try {
                portfolio.sell(ticker, getDetails(), getCommissionFee());
                portfolioComposition = portfolio.getComposition();
                shouldSave = true;
              } catch (IllegalArgumentException e) {
                menu.printMessage("\n" + e.getMessage());
              }
            }
            break;

          default:
            log.append("Neither buy nor sell\n");
            if (shouldSave) {
              portfolio.savePortfolio();
            }
            break;
        }
      } while (ch >= '1' && ch <= '2');
    }

    @Override
    protected void handleGetPortfolioPerformance(Portfolio portfolio) {
      log.append("Inside handleGetPortfolioPerformance(portfolio)\n");
      LocalDate from = getDate("from");
      LocalDate to = getDate("to");
      Map<LocalDate, Double> performance = portfolio.getPortfolioPerformance(from, to);
    }

    @Override
    protected double getCommissionFee() {
      log.append("Inside getCommissionFee()\n");
      return menu.getCommissionFee();
    }

    @Override
    protected void handleGetCostBasisOption() {
      log.append("Inside handleGetCostBasisOption()\n");
      commonStuff(Function.CostBasis);
    }

    @Override
    protected void handleGetCostBasis(Portfolio portfolio) {
      log.append("Inside handleGetCostBasis()\n");
      char ch = menu.getDateChoice();
      switch (ch) {
        case '1':
          log.append("Today chosen\n");
          portfolio.getCostBasis();
          break;

        case '2':
          log.append("Custom date chosen\n");
          LocalDate date = LocalDate.parse(menu.getDateForValue());
          portfolio.getCostBasis(date);
          break;

        default:
          log.append("Neither option chosen\n");
          break;
      }
    }

    private Details getDetails() {
      boolean isValid;
      double quantity;
      do {
        quantity = menu.getQuantity();
        isValid = this.validateQuantity(quantity);
      } while (!isValid);

      LocalDate purchaseDate = getDate("");
      return new Details(quantity, purchaseDate);
    }

    private LocalDate getDate(String msg) {
      LocalDate date;
      boolean isValidDate;

      do {
        date = LocalDate.now();
        isValidDate = true;
        try {
          menu.printMessage("\n" + msg);
          date = LocalDate.parse(menu.getDateForValue());
        } catch (DateTimeParseException e) {
          isValidDate = false;
          menu.printMessage("\nInvalid Date. Please enter again.");
        }
      } while (!isValidDate);

      return date;
    }
  }

  @Before
  public void setUp() {
    this.path = System.getProperty("user.dir") + "/src/files/stocks/flexible/";
    api = new MockApi();

    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    out = new PrintStream(bytes);
  }

  @Test
  public void testStart() {
    generateStream("1\n1\nfff\n1\nAAPL\n38\nx\nx\n");
    String expected = "Portfolio name : fff\n" +
            "Inside get share details AAPL 2022-11-16\n" +
            "Inside get share details AAPL 2022-11-16\n" +
            "Inside getCommissionFee()\n" +
            "Inside savePortfolio\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testStart2() {
    try {
      generateStream("1\n1\nidk\nx\nx\n");
      fail("Should throw exception if portfolio already exists");
    } catch (Exception e) {
      // passes
    }
  }

  @Test
  public void testStart3() {
    generateStream("1\n1\nidk\nxyz\n1\nAAPL\n22\nx\nx\n");
    String expected = "Portfolio name : xyz\n" +
            "Inside get share details AAPL 2022-11-16\n" +
            "Inside get share details AAPL 2022-11-16\n" +
            "Inside getCommissionFee()\n" +
            "Inside savePortfolio\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testStart4() {
    generateStream("1\n1\nxx\n1\nAAPL\n-38\nx\nx\n");
    String expected = "Portfolio name : xx\n" +
            "Inside get share details AAPL " + LocalDate.now() + "\n" +
            "Inside get share details AAPL " + LocalDate.now() + "\n" +
            "Inside getCommissionFee()\n" +
            "Inside savePortfolio\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testGetComposition() {
    generateStream("2\nidk\n1\nx\nx\n");
    String expected = "Portfolio name mult : idk\n" +
            "Inside getComposition(date) Received : " + LocalDate.now() + "\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testGetComposition2() {
    generateStream("2\nsfgweew\nq\n");
    assertEquals("", log.toString());
  }

  @Test
  public void testGetComposition3() {
    generateStream("2\nidk\n2\nx\n");
    String expected = "Portfolio name mult : idk\n" +
            "Inside getComposition(date) Received : " + LocalDate.now() + "\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testGetValue() {
    generateStream("3\nidk\n2\n2022-10-10\nq\n");
    String expected = "Portfolio name mult : idk\n"
            + "Inside getValue(date) Received : 2022-10-10\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testGetValue2() {
    generateStream("3\nwfw\nx\n");
    assertEquals("", log.toString());
  }

  @Test
  public void testGetValue3() {
    generateStream("3\nwfw\n3\nidk\n2\n2022-10-10\nx\n");
    String expected = "Portfolio name mult : idk\n" +
            "Inside getValue(date) Received : 2022-10-10\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testGetValue4() {
    generateStream("3\nidk\n1\nx\n");
    String expected = "Portfolio name mult : idk\n" +
            "Inside getValue\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testBuy() {
    generateStream("4\nidk\n1\nAAPL\n20\n2020-10-10\n20.6\nx\nx\n");
    String expected = "Inside handleBuySellOption()\n" +
            "Portfolio name mult : idk\n" +
            "Inside handleBuySellInPortfolio()\n" +
            "Inside getComposition()\n" +
            "Inside buy\n" +
            "Inside get share details AAPL " + LocalDate.now() + "\n" +
            "Inside get share details AAPL 2020-10-10\n" +
            "Inside getCommissionFee()\n" +
            "Inside buy(ticker, details, commissionFee). Symbol : AAPL Quantity : 20.0 Purchase Date : 2020-10-10 Commission Fee : 20.6\n" +
            "Inside getComposition()\n" +
            "Neither buy nor sell\n" +
            "Inside savePortfolio\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testGetPerformance() {
    generateStream("5\nidk\n2022-01-01\n2022-05-05\nx\n");
    String expected = "Inside handleGetPortfolioPerformanceOption()\n" +
            "Portfolio name mult : idk\n" +
            "Inside handleGetPortfolioPerformance(portfolio)\n" +
            "Inside getPortfolioPerformance(from, to) From : 2022-01-01 To : 2022-05-05\n";
    assertEquals(expected, log.toString());
  }

  @Test
  public void testGetCostBasis() {
    generateStream("6\nidk\n1\nx\nx\n");
    String expected = "Inside handleGetCostBasisOption()\n" +
            "Portfolio name mult : idk\n" +
            "Inside handleGetCostBasis()\n" +
            "Today chosen\n" +
            "Inside getCostBasis()\n";
    assertEquals(expected, log.toString());

    generateStream("6\nidk\n2\n2022-01-01\nx\n");
    expected = "Inside handleGetCostBasisOption()\n" +
            "Portfolio name mult : idk\n" +
            "Inside handleGetCostBasis()\n" +
            "Custom date chosen\n" +
            "Inside getCostBasis(date) Date : 2022-01-01\n";
    assertEquals(expected, log.toString());
  }

  private void generateStream(String input) {
    InputStream in = new ByteArrayInputStream(input.getBytes());
    Menu menu = new StockMenuFlexible(in, out);
    SpecificController controller = new MockStockControllerFlexible(menu, api, path);
    controller.start();
  }
}