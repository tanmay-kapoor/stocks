//package controllers;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.InputStream;
//import java.io.PrintStream;
//import java.time.LocalDate;
//import java.util.HashMap;
//import java.util.Map;
//
//import models.Details;
//import models.api.ShareApi;
//import models.api.StockApi;
//import models.portfolio.Portfolio;
//import views.Menu;
//import views.StockMenu;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNull;
//import static org.junit.Assert.fail;
//
///**
// * A test class to test the methods implemented in the StockControllerInflexible.
// */
//public class StockControllerTest {
//  private String path;
//  private ShareApi api;
//  private PrintStream out;
//  private StringBuilder log;
//
//  private class MockStockPortfolioInflexible implements Portfolio {
//
//    private MockStockPortfolioInflexible(StringBuilder str) {
//      log = str;
//    }
//
//    @Override
//    public void addShare(String tickerSymbol, double quantity) {
//      log.append("\nInside addShare. Symbol : ")
//              .append(tickerSymbol).append(" Quantity : ")
//              .append(quantity);
//    }
//
//    @Override
//    public double getValue() {
//      log.append("\nInside getValue");
//      return 2;
//    }
//
//    @Override
//    public double getValue(LocalDate date) throws RuntimeException {
//      log.append("\nInside getValue(date) Received : ").append(date.toString());
//      return 3;
//    }
//
//    @Override
//    public Map<String, Details> getComposition() {
//      log.append("\nInside getComposition");
//      return new HashMap<>();
//    }
//
//    @Override
//    public boolean savePortfolio() {
//      log.append("\nInside savePortfolio");
//      return false;
//    }
//  }
//
//  private class MockStockController extends AbstractController {
//    private MockStockController(Menu menu, ShareApi api, String path) {
//      super(menu, api, path);
//    }
//
//    @Override
//    protected Portfolio createPortfolio(String portfolioName, LocalDate purchaseDate) {
//      StringBuilder log = new StringBuilder("Portfolio name : " + portfolioName
//              + "\nDate created : " + purchaseDate);
//      return new MockStockPortfolioInflexible(log);
//    }
//  }
//
//  @Before
//  public void setUp() {
//    this.path = System.getProperty("user.dir") + "/src/files/stocks/";
//    api = new StockApi();
//
//    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//    out = new PrintStream(bytes);
//
//  }
//
//  @Test
//  public void testStart() {
//    generateStream("1\n1\npf1\n1\nAAPL\n38\nx\nx\n");
//    String expected = "Portfolio name : pf1\n"
//            + "Date created : " + LocalDate.now() + "\n"
//            + "Inside addShare. Symbol : AAPL Quantity : 38.0\n"
//            + "Inside savePortfolio";
//    assertEquals(expected, log.toString());
//  }
//
//  @Test
//  public void testStart2() {
//    try {
//      generateStream("1\n1\nrandom\nx\nx\n");
//      fail("Should throw exception if portfolio already exists");
//    } catch (Exception e) {
//      // passes
//    }
//  }
//
//  @Test
//  public void testStart3() {
//    generateStream("1\n1\nrandom\nxyz\n1\nAAPL\n22\nx\nx\n");
//    String expected = "Portfolio name : xyz\n"
//            + "Date created : " + LocalDate.now() + "\n"
//            + "Inside addShare. Symbol : AAPL Quantity : 22.0\n"
//            + "Inside savePortfolio";
//    assertEquals(expected, log.toString());
//  }
//
//  @Test
//  public void testStart4() {
//    generateStream("1\n1\npf1\n1\nAAPL\n-38\nx\nx\n");
//    String expected = "Portfolio name : pf1\n"
//            + "Date created : " + LocalDate.now() + "\n"
//            + "Inside addShare. Symbol : AAPL Quantity : -38.0\n"
//            + "Inside savePortfolio";
//    assertEquals(expected, log.toString());
//  }
//
//  @Test
//  public void testGetComposition() {
//    generateStream("2\nrandom\n1\nx\n");
//    String expected = "Portfolio name : random\n"
//            + "Date created : 2022-10-31\n"
//            + "Inside addShare. Symbol : MSFT Quantity : 10000.0\n"
//            + "Inside getComposition";
//    assertEquals(expected, log.toString());
//  }
//
//  @Test
//  public void testGetComposition2() {
//    generateStream("2\nsfgweew\nq\n");
//    assertNull(log);
//  }
//
//  @Test
//  public void testGetComposition3() {
//    generateStream("2\nrandom\n2\nx\n");
//    String expected = "Portfolio name : random\n"
//            + "Date created : 2022-10-31\n"
//            + "Inside addShare. Symbol : MSFT Quantity : 10000.0\n"
//            + "Inside getComposition";
//    assertEquals(expected, log.toString());
//  }
//
//  @Test
//  public void testGetValue() {
//    generateStream("3\nrandom\n2\n2022-10-10\nq\n");
//    String expected = "Portfolio name : random\n"
//            + "Date created : 2022-10-31\n"
//            + "Inside addShare. Symbol : MSFT Quantity : 10000.0\n"
//            + "Inside getValue(date) Received : 2022-10-10";
//    assertEquals(expected, log.toString());
//  }
//
//  @Test
//  public void testGetValue2() {
//    generateStream("3\nwfw\nx\n");
//    assertNull(log);
//  }
//
//  @Test
//  public void testGetValue3() {
//    generateStream("3\nwfw\n3\nrandom\n2\n2022-10-10\nx\n");
//    String expected = "Portfolio name : random\n"
//            + "Date created : 2022-10-31\n"
//            + "Inside addShare. Symbol : MSFT Quantity : 10000.0\n"
//            + "Inside getValue(date) Received : 2022-10-10";
//    assertEquals(expected, log.toString());
//  }
//
//  @Test
//  public void testGetValue4() {
//    generateStream("3\nrandom\n1\nx\n");
//    String expected = "Portfolio name : random\n" +
//            "Date created : 2022-10-31\n" +
//            "Inside addShare. Symbol : MSFT Quantity : 10000.0\n" +
//            "Inside getValue";
//    assertEquals(expected, log.toString());
//  }
//
//  private void generateStream(String input) {
//    InputStream in = new ByteArrayInputStream(input.getBytes());
//    Menu menu = new StockMenu(in, out);
//    Controller controller = new MockStockController(menu, api, path);
//    controller.start();
//  }
//}