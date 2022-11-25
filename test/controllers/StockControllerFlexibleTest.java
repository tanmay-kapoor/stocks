//package controllers;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.InputStream;
//import java.io.PrintStream;
//import java.time.LocalDate;
//import java.time.format.DateTimeParseException;
//import java.util.HashMap;
//import java.util.Map;
//
//import models.Details;
//import models.Log;
//import models.api.ShareApi;
//import models.portfolio.Composition;
//import models.portfolio.Dca;
//import models.portfolio.Portfolio;
//import views.Menu;
//import views.StockMenuFlexible;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.fail;
//
///**
// * A test class to test the methods implemented in the StockControllerInflexible.
// */
//public class StockControllerFlexibleTest {
//  private String path;
//  private ShareApi api;
//  private PrintStream out;
//  private StringBuilder log;
//
//  private class MockApi implements ShareApi {
//
//    @Override
//    public boolean isTickerPresent(String ticker) {
//      return false;
//    }
//
//    @Override
//    public boolean hasPrice(String ticker, LocalDate date) {
//      return false;
//    }
//
//    @Override
//    public Map<String, Double> getShareDetails(String tickerSymbol, LocalDate dateAsked) {
//      log.append("Inside get share details ")
//              .append(tickerSymbol).append(" ")
//              .append(dateAsked).append("\n");
//      return new HashMap<>();
//    }
//  }
//
//  private class MockStockPortfolio implements Portfolio {
//
//    private MockStockPortfolio(StringBuilder str) {
//      log = str;
//    }
//
//    @Override
//    public void buy(String ticker, double quantity) {
//      log.append("Inside buy. Symbol : ").append(ticker)
//              .append(" Quantity : ").append(quantity).append("\n");
//    }
//
//    @Override
//    public void buy(String ticker, Details details, double commissionFee) {
//      log.append("Inside buy(ticker, details, commissionFee). Symbol : ").append(ticker)
//              .append(" Quantity : ").append(details.getQuantity())
//              .append(" Purchase Date : ").append(details.getPurchaseDate())
//              .append(" Commission Fee : ").append(commissionFee).append("\n");
//    }
//
//    @Override
//    public boolean sell(String ticker, Details details, double commissionFee) {
//      log.append("Inside sell(ticker, details), commissionFee. Symbol : ").append(ticker)
//              .append(" Quantity : ").append(details.getQuantity())
//              .append(" Purchase Date : ").append(details.getPurchaseDate())
//              .append(" Commission Fee : ").append(commissionFee).append("\n");
//      return false;
//    }
//
//    @Override
//    public double getValue() {
//      log.append("Inside getValue\n");
//      return 2;
//    }
//
//    @Override
//    public double getValue(LocalDate date) throws RuntimeException {
//      log.append("Inside getValue(date) Received : ").append(date).append("\n");
//      return 3;
//    }
//
//    @Override
//    public Map<String, Log> getComposition() {
//      log.append("Inside getComposition()\n");
//      return new HashMap<>();
//    }
//
//    @Override
//    public Map<String, Log> getComposition(LocalDate date) {
//      log.append("Inside getComposition(date) Received : ").append(date).append("\n");
//      return new HashMap<>();
//    }
//
//    @Override
//    public boolean savePortfolio() {
//      log.append("Inside savePortfolio\n");
//      return false;
//    }
//
//    @Override
//    public Map<LocalDate, Double> getPortfolioPerformance(LocalDate from, LocalDate to) {
//      log.append("Inside getPortfolioPerformance(from, to) From : ").append(from)
//              .append(" To : ").append(to).append("\n");
//      return new HashMap<>();
//    }
//
//    @Override
//    public double getCostBasis() {
//      log.append("Inside getCostBasis()\n");
//      return 2.2;
//    }
//
//    @Override
//    public double getCostBasis(LocalDate date) {
//      log.append("Inside getCostBasis(date) Date : ").append(date).append("\n");
//      return 1.1;
//    }
//
//    @Override
//    public void doDca(String dcaName, Dca dca) {
//
//    }
//
//    @Override
//    public Map<String, Dca> getDcaStrategies() {
//      return null;
//    }
//  }
//
//  private class MockStockControllerFlexible extends AbstractController {
//
//    private MockStockControllerFlexible(Menu menu, ShareApi api, String path) {
//      super(menu, api, path);
//      log = new StringBuilder();
//    }
//
//    @Override
//    protected Portfolio createPortfolio(String portfolioName) {
//      log.append("Portfolio name : ").append(portfolioName).append("\n");
//      return new MockStockPortfolio(log);
//    }
//
//    @Override
//    protected Portfolio createPortfolio(String portfolioName, Map<String, Log> stocks,
//                                        Map<LocalDate, Double> costBasisHistory) {
//      log.append("Portfolio name mult : ").append(portfolioName).append("\n");
//      return new MockStockPortfolio(log);
//    }
//
//    @Override
//    protected Map<String, LocalDate> readLastSoldDateFromCsv(File logFile)
//            throws FileNotFoundException {
//      return new HashMap<>();
//    }
//
//    @Override
//    protected Map<LocalDate, Double> readStockBasisHistoryFromCsv(File costBasisFile)
//            throws FileNotFoundException {
//      return new HashMap<>();
//    }
//
//    @Override
//    protected LocalDate getPurchaseDate() {
//      return LocalDate.parse(menu.getDateForValue());
//    }
//
//    @Override
//    protected char getLastOption() {
//      return '6';
//    }
//
//    private void handleBuySellInPortfolio(Portfolio portfolio) {
//      log.append("Inside handleBuySellInPortfolio()\n");
//      Map<String, Log> portfolioComposition = portfolio.getComposition();
//
//      char ch;
//      boolean shouldSave = false;
//      do {
//        ch = menu.getBuySellChoice();
//        String ticker;
//
//        switch (ch) {
//          case '1':
//            log.append("Inside buy\n");
//            try {
//              ticker = menu.getTickerSymbol().toUpperCase();
//              api.getShareDetails(ticker, LocalDate.now());
//              Details details = getDetails();
//              api.getShareDetails(ticker, details.getPurchaseDate());
//              portfolio.buy(ticker, details, getCommissionFee());
//              portfolioComposition = portfolio.getComposition();
//              shouldSave = true;
//            } catch (IllegalArgumentException e) {
//              menu.printMessage("\n" + e.getMessage());
//            }
//            break;
//
//          case '2':
//            log.append("Inside sell\n");
//            ticker = menu.getTickerSymbol().toUpperCase();
//            if (!portfolioComposition.containsKey(ticker)) {
//              menu.printMessage("\nCannot sell ticker that is not in portfolio");
//            } else {
//              try {
//                portfolio.sell(ticker, getDetails(), getCommissionFee());
//                portfolioComposition = portfolio.getComposition();
//                shouldSave = true;
//              } catch (IllegalArgumentException e) {
//                menu.printMessage("\n" + e.getMessage());
//              }
//            }
//            break;
//
//          default:
//            log.append("Neither buy nor sell\n");
//            if (shouldSave) {
//              portfolio.savePortfolio();
//            }
//            break;
//        }
//      }
//      while (ch >= '1' && ch <= '2');
//    }
//
//    private void handleGetPortfolioPerformance(Portfolio portfolio) {
//      log.append("Inside handleGetPortfolioPerformance(portfolio)\n");
//      LocalDate from = getDate("from");
//      LocalDate to = getDate("to");
//      portfolio.getPortfolioPerformance(from, to);
//    }
//
//    @Override
//    protected double getCommissionFee() {
//      log.append("Inside getCommissionFee()\n");
//      return menu.getCommissionFee();
//    }
//
//    @Override
//    protected void filterBasedOnFunction(Function function) {
//      log.append("Inside filterBasedOnFunction(Function function) Received : ")
//              .append(function).append("\n");
//      commonStuff(function);
//    }
//
//    @Override
//    protected void handleMenuOptions(Portfolio portfolio, Function function) {
//      log.append("Inside handleMenuOptions(p, f) function = ")
//              .append(function).append("\n");
//      switch (function) {
//        case Composition:
//          handleGetPortfolioComposition(portfolio);
//          break;
//
//        case GetValue:
//          handleGetPortfolioValue(portfolio);
//          break;
//
//        case BuySell:
//          handleBuySellInPortfolio(portfolio);
//          break;
//
//        case SeePerformance:
//          handleGetPortfolioPerformance(portfolio);
//          break;
//
//        case CostBasis:
//          handleGetCostBasis(portfolio);
//          break;
//
//        default:
//          throw new IllegalArgumentException("Illegal value");
//      }
//    }
//
//    @Override
//    protected boolean giveDateOptionsIfApplicable(Portfolio portfolio, Composition option) {
//      log.append("Inside giveDateOptionsIfApplicable(p, o) option = ").append(option).append("\n");
//
//      char ch = menu.getDateChoice();
//
//      switch (ch) {
//        case '1':
//          getCompositionForToday(portfolio, option);
//          return true;
//
//        case '2':
//          LocalDate date;
//          switch (option) {
//            case Contents:
//              date = getPurchaseDate();
//              menu.printMessage(getPortfolioContents(portfolio, date));
//              break;
//
//            case Weightage:
//              date = getPurchaseDate();
//              menu.printMessage(getPortfolioWeightage(portfolio, date));
//              break;
//
//            default:
//              return false;
//          }
//          break;
//
//        default:
//          return false;
//      }
//
//      return true;
//    }
//
//    @Override
//    protected boolean handleCreatePortfolioOption(char choice, Portfolio portfolio, String portfolioName) {
//      return false;
//    }
//
//    private void handleGetCostBasis(Portfolio portfolio) {
//      log.append("Inside handleGetCostBasis()\n");
//      char ch = menu.getDateChoice();
//      switch (ch) {
//        case '1':
//          log.append("Today chosen\n");
//          portfolio.getCostBasis();
//          break;
//
//        case '2':
//          log.append("Custom date chosen\n");
//          LocalDate date = LocalDate.parse(menu.getDateForValue());
//          portfolio.getCostBasis(date);
//          break;
//
//        default:
//          log.append("Neither option chosen\n");
//          break;
//      }
//    }
//
//    private Details getDetails() {
//      boolean isValid;
//      double quantity;
//      do {
//        quantity = menu.getQuantity();
//        isValid = this.validateQuantity(quantity);
//      }
//      while (!isValid);
//
//      LocalDate purchaseDate = getDate("");
//      return new Details(quantity, purchaseDate);
//    }
//
//    private LocalDate getDate(String msg) {
//      LocalDate date;
//      boolean isValidDate;
//
//      do {
//        date = LocalDate.now();
//        isValidDate = true;
//        try {
//          menu.printMessage("\n" + msg);
//          date = LocalDate.parse(menu.getDateForValue());
//        } catch (DateTimeParseException e) {
//          isValidDate = false;
//          menu.printMessage("\nInvalid Date. Please enter again.");
//        }
//      }
//      while (!isValidDate);
//
//      return date;
//    }
//  }
//
//  @Before
//  public void setUp() {
//    this.path = System.getProperty("user.dir") + "/src/files/stocks/flexible/";
//    api = new MockApi();
//
//    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//    out = new PrintStream(bytes);
//  }
//
//  @Test
//  public void testStart() {
//    generateStream("1\n1\nfff\n1\nAAPL\n38\n2022-10-10\n34.5\nx\nx\n");
//    String expected = "Portfolio name : fff\n" +
//            "Inside get share details AAPL " + LocalDate.now() + "\n" +
//            "Inside get share details AAPL 2022-10-10\n" +
//            "Inside getCommissionFee()\n" +
//            "Inside buy(ticker, details, commissionFee). Symbol : AAPL Quantity : " +
//            "38.0 Purchase Date : 2022-10-10 Commission Fee : 34.5\n" +
//            "Inside savePortfolio\n";
//    assertEquals(expected, log.toString());
//  }
//
//  @Test
//  public void testStart2() {
//    try {
//      generateStream("1\n1\nidk\nx\nx\n");
//      fail("Should throw exception if portfolio already exists");
//    } catch (Exception e) {
//      // passes
//    }
//  }
//
//  @Test
//  public void testStart3() {
//    generateStream("1\n1\nidk\nxyz\n1\nAAPL\n22\n2022-10-10\n22.3\nx\nx\n");
//    String expected = "Portfolio name : xyz\n" +
//            "Inside get share details AAPL " + LocalDate.now() + "\n" +
//            "Inside get share details AAPL 2022-10-10\n" +
//            "Inside getCommissionFee()\n" +
//            "Inside buy(ticker, details, commissionFee). Symbol : AAPL Quantity : " +
//            "22.0 Purchase Date : 2022-10-10 Commission Fee : 22.3\n" +
//            "Inside savePortfolio\n";
//    assertEquals(expected, log.toString());
//  }
//
//  @Test
//  public void testStart4() {
//    generateStream("1\n1\nxx\n1\nAAPL\n-38\n2022-10-10\n20.3\nx\nx\n");
//    String expected = "Portfolio name : xx\n" +
//            "Inside get share details AAPL " + LocalDate.now() + "\n" +
//            "Inside get share details AAPL 2022-10-10\n" +
//            "Inside getCommissionFee()\n" +
//            "Inside buy(ticker, details, commissionFee). Symbol : AAPL " +
//            "Quantity : -38.0 Purchase Date : 2022-10-10 Commission Fee : 20.3\n" +
//            "Inside savePortfolio\n";
//    assertEquals(expected, log.toString());
//  }
//
//  @Test
//  public void testGetComposition() {
//    generateStream("2\nidk\n1\n1\nx\nx\n");
//    String expected = "Inside filterBasedOnFunction(Function function) Received : Composition\n" +
//            "Portfolio name mult : idk\n" +
//            "Inside handleMenuOptions(p, f) function = Composition\n" +
//            "Inside giveDateOptionsIfApplicable(p, o) option = Contents\n" +
//            "Inside getComposition(date) Received : " + LocalDate.now() + "\n";
//    assertEquals(expected, log.toString());
//  }
//
//  @Test
//  public void testGetComposition2() {
//    generateStream("2\nsfgweew\nq\n");
//    String expected = "Inside filterBasedOnFunction(Function function) Received : Composition\n";
//    assertEquals(expected, log.toString());
//  }
//
//  @Test
//  public void testGetComposition3() {
//    generateStream("2\nidk\n2\n1\nx\nx\n");
//    String expected = "Inside filterBasedOnFunction(Function function) Received : Composition\n" +
//            "Portfolio name mult : idk\n" +
//            "Inside handleMenuOptions(p, f) function = Composition\n" +
//            "Inside giveDateOptionsIfApplicable(p, o) option = Weightage\n" +
//            "Inside getComposition(date) Received : " + LocalDate.now() + "\n";
//    assertEquals(expected, log.toString());
//  }
//
//  @Test
//  public void testGetComposition4() {
//    generateStream("2\nidk\n1\n2\n2022-10-10\nx\nx\n");
//    String expected = "Inside filterBasedOnFunction(Function function) Received : Composition\n" +
//            "Portfolio name mult : idk\n" +
//            "Inside handleMenuOptions(p, f) function = Composition\n" +
//            "Inside giveDateOptionsIfApplicable(p, o) option = Contents\n" +
//            "Inside getComposition(date) Received : 2022-10-10\n";
//    assertEquals(expected, log.toString());
//  }
//
//  @Test
//  public void testGetValue() {
//    generateStream("3\nidk\n2\n2022-10-10\nq\n");
//    String expected = "Inside filterBasedOnFunction(Function function) Received : GetValue\n" +
//            "Portfolio name mult : idk\n" +
//            "Inside handleMenuOptions(p, f) function = GetValue\n" +
//            "Inside getValue(date) Received : 2022-10-10\n";
//    assertEquals(expected, log.toString());
//  }
//
//  @Test
//  public void testGetValue2() {
//    generateStream("3\nwfw\nx\n");
//    String expected = "Inside filterBasedOnFunction(Function function) Received : GetValue\n";
//    assertEquals(expected, log.toString());
//  }
//
//  @Test
//  public void testGetValue3() {
//    generateStream("3\nwfw\n3\nidk\n2\n2022-10-10\nx\n");
//    String expected = "Inside filterBasedOnFunction(Function function) Received : GetValue\n" +
//            "Inside filterBasedOnFunction(Function function) Received : GetValue\n" +
//            "Portfolio name mult : idk\n" +
//            "Inside handleMenuOptions(p, f) function = GetValue\n" +
//            "Inside getValue(date) Received : 2022-10-10\n";
//    assertEquals(expected, log.toString());
//  }
//
//  @Test
//  public void testGetValue4() {
//    generateStream("3\nidk\n1\nx\n");
//    String expected = "Inside filterBasedOnFunction(Function function) Received : GetValue\n" +
//            "Portfolio name mult : idk\n" +
//            "Inside handleMenuOptions(p, f) function = GetValue\n" +
//            "Inside getValue\n";
//    assertEquals(expected, log.toString());
//  }
//
//  @Test
//  public void testBuy() {
//    generateStream("4\nidk\n1\nAAPL\n20\n2020-10-10\n20.6\nx\nx\n");
//    String expected = "Inside filterBasedOnFunction(Function function) Received : BuySell\n" +
//            "Portfolio name mult : idk\n" +
//            "Inside handleMenuOptions(p, f) function = BuySell\n" +
//            "Inside handleBuySellInPortfolio()\n" +
//            "Inside getComposition()\n" +
//            "Inside buy\n" +
//            "Inside get share details AAPL " + LocalDate.now() + "\n" +
//            "Inside get share details AAPL 2020-10-10\n" +
//            "Inside getCommissionFee()\n" +
//            "Inside buy(ticker, details, commissionFee). Symbol : AAPL Quantity : " +
//            "20.0 Purchase Date : 2020-10-10 Commission Fee : 20.6\n" +
//            "Inside getComposition()\n" +
//            "Neither buy nor sell\n" +
//            "Inside savePortfolio\n";
//    assertEquals(expected, log.toString());
//  }
//
//  @Test
//  public void testGetPerformance() {
//    generateStream("5\nidk\n2022-01-01\n2022-05-05\nx\n");
//    String expected = "Inside filterBasedOnFunction(Function function) Received : SeePerformance\n"
//            + "Portfolio name mult : idk\n" +
//            "Inside handleMenuOptions(p, f) function = SeePerformance\n" +
//            "Inside handleGetPortfolioPerformance(portfolio)\n" +
//            "Inside getPortfolioPerformance(from, to) From : 2022-01-01 To : 2022-05-05\n";
//    assertEquals(expected, log.toString());
//  }
//
//  @Test
//  public void testGetCostBasis() {
//    generateStream("6\nidk\n1\nx\nx\n");
//    String expected = "Inside filterBasedOnFunction(Function function) Received : CostBasis\n" +
//            "Portfolio name mult : idk\n" +
//            "Inside handleMenuOptions(p, f) function = CostBasis\n" +
//            "Inside handleGetCostBasis()\n" +
//            "Today chosen\n" +
//            "Inside getCostBasis()\n";
//    assertEquals(expected, log.toString());
//
//    generateStream("6\nidk\n2\n2022-01-01\nx\n");
//    expected = "Inside filterBasedOnFunction(Function function) Received : CostBasis\n" +
//            "Portfolio name mult : idk\n" +
//            "Inside handleMenuOptions(p, f) function = CostBasis\n" +
//            "Inside handleGetCostBasis()\n" +
//            "Custom date chosen\n" +
//            "Inside getCostBasis(date) Date : 2022-01-01\n";
//    assertEquals(expected, log.toString());
//  }
//
//  private void generateStream(String input) {
//    InputStream in = new ByteArrayInputStream(input.getBytes());
//    Menu menu = new StockMenuFlexible(in, out);
//    SpecificController controller = new MockStockControllerFlexible(menu, api, path);
//    controller.start();
//  }
//}