//package models.portfolio;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.time.LocalDate;
//import java.time.format.DateTimeParseException;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Scanner;
//import java.util.Set;
//import java.util.TreeSet;
//
//import models.Details;
//import models.Log;
//import models.api.AlphaVantage;
//import models.api.ShareApi;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertNotEquals;
//import static org.junit.Assert.assertTrue;
//import static org.junit.Assert.fail;
//
///**
// * A test method that is used to test the methods implemented in the StocksPortfolio class.
// */
//abstract class AbstractStockPortfolioTest {
//
//  private Portfolio portfolio;
//  private String portfolioName;
//  private LocalDate now;
//  private String directory;
//  private ShareApi api;
//  protected abstract Portfolio createPortfolio(String portfolioName, String directory, ShareApi api);
//
//  @Before
//  public void setUp() {
//    String rootPath = System.getProperty("user.dir");
//    String[] temp = rootPath.split("/");
//
//    directory = !temp[temp.length - 1].equals("res") ? rootPath + "/test/" : "../src/test/models/";
//    portfolioName = "test";
//    now = LocalDate.now();
//    api = new AlphaVantage();
//
//    portfolio = createPortfolio(portfolioName, directory, api);
//  }
//
//  @Test
//  public void checkPortfolioEmptyAtStart() {
//    assertEquals(new HashMap<>(), portfolio.getComposition());
//  }
//
//  @Test
//  public void testPortfolioCreated() {
//    try {
//      createPortfolio("idk", this.directory, api);
//    } catch (Exception e) {
//      fail("should not throw exception but did.");
//    }
//  }
//
//  @Test
//  public void buy() {
//    Map<String, Log> expected = new HashMap<>();
//    assertEquals(expected, portfolio.getComposition());
//
//    String ticker;
//    double quantity;
//
//    ticker = "META";
//    quantity = 22.0;
//    check(ticker, quantity, expected);
//
//    ticker = "gOoG";
//    quantity = 34;
//    check(ticker, quantity, expected);
//
//    ticker = "MSFT";
//    quantity = 33;
//    double price = api.getShareDetails(ticker, now).get("close");
//    portfolio.buy(ticker, quantity, price);
//
//    Map<String, Log> shareDetails = new HashMap<>();
//    Set<Details> detailsSet = newTreeSet();
//    Details d = new Details(quantity, LocalDate.now());
//    detailsSet.add(d);
//    expected.put(ticker.toUpperCase(), newLog(detailsSet));
//    checkHashMapEquality(expected, shareDetails);
//
//    ticker = "nflx";
//    quantity = 299;
//    check(ticker, quantity, expected);
//
//    ticker = "AMZN";
//    quantity = 11.5;
//    check(ticker, quantity, expected);
//
//
//    ticker = "MSFT";
//    quantity = 10.5;
//    price = api.getShareDetails(ticker, now).get("close");
//    portfolio.buy(ticker, quantity, price);
//
//    shareDetails = new HashMap<>();
//    detailsSet = newTreeSet();
//    d = new Details(quantity, LocalDate.now());
//    detailsSet.add(d);
//    expected.put(ticker.toUpperCase(), newLog(detailsSet));
//    checkHashMapEquality(expected, shareDetails);
//
//
//    ticker = "MSFT";
//    double toAdd = 22;
//    quantity += toAdd;
//    portfolio.buy(ticker, toAdd, price);
//    d.setQuantity(quantity);
//    expected.put(ticker.toUpperCase(), newLog(detailsSet));
//    checkHashMapEquality(expected, shareDetails);
//  }
//
//  @Test
//  public void buyWithPurchaseDate() {
//    Map<String, Log> expected = new HashMap<>();
//    assertEquals(expected, portfolio.getComposition());
//
//    Map<String, Log> shareDetails;
//    Set<Details> detailsSet;
//
//    String ticker = "META";
//    double quantity = 22.0;
//    LocalDate date = LocalDate.parse("2020-12-12");
//    Details d = new Details(quantity, date);
//    double price = api.getShareDetails(ticker, date).get("close");
//
//    portfolio.buy(ticker, d, 55, price);
//    shareDetails = portfolio.getComposition();
//    detailsSet = newTreeSet();
//    detailsSet.add(new Details(quantity, LocalDate.now()));
//    expected.put(ticker.toUpperCase(), newLog(detailsSet));
//    checkHashMapEquality(expected, shareDetails);
//
//    // purchase is automatically converted to LocalDate.now()
//    // commission is automatically converted to 0
//  }
//
//  @Test
//  public void buyInvalid() {
//    assertEquals(new HashMap<>(), portfolio.getComposition());
//
//    try {
//      portfolio.buy("GoOg", -55, 10);
//    } catch (IllegalArgumentException e1) {
//      try {
//        portfolio.buy("amzn", 0, 50);
//      } catch (IllegalArgumentException e2) {
//        try {
//          portfolio.buy("NFLX", -5.3, 50);
//        } catch (IllegalArgumentException e3) {
//          //passes
//        }
//      }
//    }
//  }
//
//  @Test
//  public void getValue() {
//    portfolio.buy("GOOG", 200, 50);
//    portfolio.buy("META", 655, 50);
//    double val = portfolio.getValue();
//
//    double expectedVal = 200 * getShareValue("GOOG", LocalDate.now());
//    expectedVal += (655 * getShareValue("META", LocalDate.now()));
//
//    assertEquals(expectedVal, val, 0);
//  }
//
//  @Test
//  public void getValueDateProvided() {
//    portfolio.buy("AMZN", 27, 69420);
//    portfolio.buy("NFLX", 18, 42069.1);
//    double val = portfolio.getValue(LocalDate.parse("2022-10-10"));
//    assertEquals(2631583.8, val, 0);
//
//    try {
//      portfolio.getValue(LocalDate.parse("2022/10/10"));
//      fail("Should be invalid date");
//    } catch (DateTimeParseException e1) {
//      try {
//        portfolio.getValue(LocalDate.parse("2003-07-15"));
//        fail("Should not be able to get val for this date");
//      } catch (RuntimeException e2) {
//        // passes
//      }
//    }
//  }
//
////  @Test
////  public void getValueWithIncorrectDate() {
////    portfolio.buy("AMZN", new Details(27, LocalDate.parse("2020-10-10")), 44);
////    portfolio.buy("NFLX", new Details(18, LocalDate.parse("2018-11-10")), 33);
////    double val = portfolio.getValue(LocalDate.now());
////
////    double expectedVal = 27 * getShareValue("AMZN", LocalDate.now());
////    expectedVal += (18 * getShareValue("NFLX", LocalDate.now()));
////
////    assertEquals(expectedVal, val, 0);
////    // automatically changes date to curr date and commission to 0
////  }
////
////  @Test
////  public void getComposition() {
////    Map<String, Log> expected = new HashMap<>();
////    assertEquals(expected, portfolio.getComposition());
////
////    Map<String, Log> shareDetails;
////    Set<Details> detailsSet;
////
////    portfolio.buy("META", 22.0);
////    shareDetails = portfolio.getComposition();
////    detailsSet = newTreeSet();
////    detailsSet.add(new Details(22.0, LocalDate.now()));
////    expected.put("META", newLog(detailsSet));
////    checkHashMapEquality(expected, shareDetails);
////
////    portfolio.buy("gOoG", 34);
////    shareDetails = portfolio.getComposition();
////    detailsSet = newTreeSet();
////    detailsSet.add(new Details(34, LocalDate.now()));
////    expected.put("GOOG", newLog(detailsSet));
////    checkHashMapEquality(expected, shareDetails);
////
//////    portfolio.buy("xyz", 33);
//////    shareDetails = portfolio.getComposition();
//////    detailsSet = newTreeSet();
//////    detailsSet.add(new Details(33, LocalDate.now()));
//////    expected.put("XYZ", newLog(detailsSet));
//////    checkHashMapEquality(expected, shareDetails);
////
////    portfolio.buy("nflx", 299);
////    shareDetails = portfolio.getComposition();
////    detailsSet = newTreeSet();
////    detailsSet.add(new Details(299, LocalDate.now()));
////    expected.put("NFLX", newLog(detailsSet));
////    checkHashMapEquality(expected, shareDetails);
////
////    portfolio.buy("AMZN", 11.5);
////    detailsSet = newTreeSet();
////    detailsSet.add(new Details(11.5, LocalDate.now()));
////    expected.put("AMZN", newLog(detailsSet));
////    checkHashMapEquality(expected, shareDetails);
////
////    portfolio.buy("AMZN", 22);
////    detailsSet = newTreeSet();
////    detailsSet.add(new Details(33.5, LocalDate.now()));
////    expected.put("AMZN", newLog(detailsSet));
////    checkHashMapEquality(expected, shareDetails);
////  }
////
////  // get composition for current date even who date provided is not today.
////  @Test
////  public void getCompositionDateProvided() {
////    Map<String, Log> expected = new HashMap<>();
////    assertEquals(expected, portfolio.getComposition());
////
////    Map<String, Log> shareDetails;
////    Set<Details> detailsSet;
////
////    portfolio.buy("META", 22.0);
////    shareDetails = portfolio.getComposition(LocalDate.parse("2020-10-10"));
////    detailsSet = newTreeSet();
////    detailsSet.add(new Details(22.0, now));
////    expected.put("META", newLog(detailsSet));
////    checkHashMapEquality(expected, shareDetails);
////  }
////
////  @Test
////  public void sell() {
////    Map<String, Log> expected = new HashMap<>();
////    assertEquals(expected, portfolio.getComposition());
////    portfolio.buy("META", 100);
////    Map<String, Log> og = portfolio.getComposition();
////    portfolio.sell("META", new Details(100, LocalDate.now()), 0);
////    Map<String, Log> test = portfolio.getComposition();
////    assertEquals(og, test);
////  }
////
////  @Test
////  public void savePortfolio() {
////    Set<String> expected = new HashSet<>();
////    expected.add("share,quantity,purchaseDate");
////
////    portfolio.buy("META", 22.0);
////    expected.add(String.format("META,22.0,%s", now));
////
////    portfolio.buy("gOoG", 34);
////    expected.add(String.format("GOOG,34.0,%s", now));
////
//////    portfolio.buy("xyz", 33);
//////    expected.add(String.format("XYZ,33.0,%s", now));
////
////    portfolio.buy("nflx", 299);
////    expected.add(String.format("NFLX,299.0,%s", now));
////
////    portfolio.buy("AMZN", 22);
////    expected.add(String.format("AMZN,22.0,%s", now));
////
////    boolean saved = portfolio.savePortfolio();
////    assertTrue(saved);
////
////    File file = new File(this.directory + this.portfolioName + ".csv");
////    try {
////      Scanner csvReader = new Scanner(file);
////      String line = csvReader.nextLine();
////      while (csvReader.hasNext()) {
////        assertTrue(expected.contains(line));
////        line = csvReader.nextLine();
////      }
////
////      if (!file.delete()) {
////        fail("Could not delete csv but should be able to.");
////      }
////    } catch (FileNotFoundException e) {
////      fail("File not found when it should.");
////    }
////  }
////
////  @Test
////  public void savePortfolioInvalid() {
////    assertFalse(portfolio.savePortfolio());
////    portfolio.buy("nflx", 299);
////    portfolio.savePortfolio();
////
////    try {
////      Files.createDirectories(Paths.get(this.directory));
////      File file = new File(this.directory + this.portfolioName + ".csv");
////      Scanner csvReader = new Scanner(file);
////      String firstLine = csvReader.nextLine();
////      assertNotEquals("idk,something,random", firstLine);
////      if (!file.delete()) {
////        fail("Could not delete csv but should be able to.");
////      }
////    } catch (IOException e) {
////      throw new RuntimeException(e);
////    }
////  }
//
//  private void checkHashMapEquality(Map<String, Log> expected,
//                                    Map<String, Log> shareDetails) {
//    for (String ticker : shareDetails.keySet()) {
//      Log expectedLog = expected.get(ticker);
//      Log currLog = shareDetails.get(ticker);
//
//      assertEquals(expectedLog.getLastSoldDate(), currLog.getLastSoldDate());
//      assertEquals(expectedLog.getDetailsSet(), currLog.getDetailsSet());
//    }
//  }
//
//  private Set<Details> newTreeSet() {
//    return new TreeSet<>(Comparator.comparing(Details::getPurchaseDate));
//  }
//
//  private Log newLog(Set<Details> detailsSet) {
//    return new Log(detailsSet, null);
//  }
//
//  private double getShareValue(String ticker, LocalDate date) {
//    return api.getShareDetails(ticker, date).get("close");
//  }
//
//  private void check(String ticker, double quantity, Map<String, Log> expected) {
//    double price = api.getShareDetails(ticker, now).get("close");
//
//    portfolio.buy(ticker, quantity, price);
//    Map<String, Log> shareDetails = portfolio.getComposition();
//    Set<Details> detailsSet = newTreeSet();
//    detailsSet.add(new Details(quantity, now));
//
//    expected.put(ticker.toUpperCase(), newLog(detailsSet));
//    checkHashMapEquality(expected, shareDetails);
//  }
//}