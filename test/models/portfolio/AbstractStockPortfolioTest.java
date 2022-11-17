package models.portfolio;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import models.Details;
import models.Log;
import models.api.AlphaVantage;
import models.api.ShareApi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * A test method that is used to test the methods implemented in the StocksPortfolio class.
 */
abstract class AbstractStockPortfolioTest {

  protected Portfolio portfolio;
  protected String portfolioName;
  protected LocalDate now;
  protected String directory;
  protected ShareApi api;

  protected abstract Portfolio createPortfolio(String portfolioName, String directory,
                                               ShareApi api);

  protected abstract void deleteLogAndCostBasisIfRequired();

  protected abstract void getValueSomeStocksDifferently(double val);

  protected abstract LocalDate getPurchaseDate();

  protected abstract void addValueToDetailsSet(Map<String, Log> expected);

  @Before
  public void setUp() {
    String rootPath = System.getProperty("user.dir");
    String[] temp = rootPath.split("/");

    directory = !temp[temp.length - 1].equals("res") ? rootPath
            + "/test/models/portfolio/" : "../src/test/models/portfolio/";
    portfolioName = "test";
    now = LocalDate.now();
    api = new AlphaVantage();

    portfolio = createPortfolio(portfolioName, directory, api);
  }

  @Test
  public void checkPortfolioEmptyAtStart() {
    assertEquals(new HashMap<>(), portfolio.getComposition());
  }

  @Test
  public void testPortfolioCreated() {
    try {
      createPortfolio("idk", this.directory, api);
    } catch (Exception e) {
      fail("should not throw exception but did.");
    }
  }

  @Test
  public void buy() {
    Map<String, Log> expected = new HashMap<>();
    assertEquals(expected, portfolio.getComposition());

    String ticker;
    double quantity;

    ticker = "META";
    quantity = 22.0;
    check(ticker, quantity, expected);

    ticker = "gOoG";
    quantity = 34;
    check(ticker, quantity, expected);

    ticker = "MSFT";
    quantity = 33;
    check(ticker, quantity, expected);
    portfolio.buy(ticker, quantity);

    Map<String, Log> shareDetails = portfolio.getComposition();
    Set<Details> detailsSet = newTreeSet();
    Details d = new Details(quantity, LocalDate.now());
    detailsSet.add(d);
    expected.put(ticker.toUpperCase(), newLog(detailsSet));
    checkHashMapEquality(expected, shareDetails);

    ticker = "nflx";
    quantity = 299;
    check(ticker, quantity, expected);

    ticker = "AMZN";
    quantity = 11.5;
    check(ticker, quantity, expected);

    ticker = "MSFT";
    quantity = 10.5;
    check(ticker, quantity, expected);
    portfolio.buy(ticker, quantity);

    shareDetails = portfolio.getComposition();
    detailsSet = newTreeSet();
    d = new Details(quantity, LocalDate.now());
    detailsSet.add(d);
    expected.put(ticker.toUpperCase(), newLog(detailsSet));
    checkHashMapEquality(expected, shareDetails);


    ticker = "MSFT";
    double toAdd = 22;
    quantity += toAdd;
    portfolio.buy(ticker, toAdd);

    shareDetails = portfolio.getComposition();
    d.setQuantity(quantity);
    expected.put(ticker.toUpperCase(), newLog(detailsSet));
    checkHashMapEquality(expected, shareDetails);
  }

  @Test
  public void buyWithPurchaseDate() {
    Map<String, Log> expected = new HashMap<>();
    assertEquals(expected, portfolio.getComposition());

    Map<String, Log> shareDetails;
    Set<Details> detailsSet;

    portfolio.buy("META", new Details(22.0, LocalDate.parse("2020-12-12")), 55);
    shareDetails = portfolio.getComposition();
    detailsSet = newTreeSet();
    detailsSet.add(new Details(22.0, getPurchaseDate()));
    expected.put("META", newLog(detailsSet));
    checkHashMapEquality(expected, shareDetails);

    // purchase is automatically converted to LocalDate.now()
    // commission is automatically converted to 0
  }

  @Test
  public void buyInvalid() {
    assertEquals(new HashMap<>(), portfolio.getComposition());

    try {
      portfolio.buy("GoOg", -55);
    } catch (IllegalArgumentException e1) {
      try {
        portfolio.buy("amzn", 0);
      } catch (IllegalArgumentException e2) {
        try {
          portfolio.buy("NFLX", -5.3);
        } catch (IllegalArgumentException e3) {
          //passes
        }
      }
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void buyOnFutureDate() {
    LocalDate purchaseDate = LocalDate.parse("2027-10-07");
    portfolio.buy("META", new Details(22, purchaseDate), 10);
  }

  @Test
  public void getValue() {
    portfolio.buy("GOOG", 200);
    portfolio.buy("META", 655);
    double val = portfolio.getValue();

    double expectedVal = 200 * getShareValue("GOOG", LocalDate.now());
    expectedVal += (655 * getShareValue("META", LocalDate.now()));

    assertEquals(expectedVal, val, 0.1);
  }

  @Test
  public void getValueFutureDate() {
    portfolio.buy("AMZN", 27);
    portfolio.buy("NFLX", 18);
    try {
      double val = portfolio.getValue(LocalDate.now().plusDays(20));
      fail("Should fail for future date but did not");
    } catch (IllegalArgumentException e) {
      // passes
    }
  }

  @Test
  public void getValueDateValidForSomeStocks() {
    assertEquals(new HashMap<>(), portfolio.getComposition());
    portfolio.buy("AMZN", 27);
    portfolio.buy("NFLX", 18);
    double val = portfolio.getValue(LocalDate.parse("2001-07-15"));
    getValueSomeStocksDifferently(val);
  }

  @Test
  public void getValueWithIncorrectDate() {
    portfolio.buy("AMZN", new Details(27, LocalDate.parse("2020-10-10")), 44);
    portfolio.buy("NFLX", new Details(18, LocalDate.parse("2018-11-10")), 33);
    double val = portfolio.getValue(LocalDate.now());

    double expectedVal = 27 * getShareValue("AMZN", LocalDate.now());
    expectedVal += (18 * getShareValue("NFLX", LocalDate.now()));

    assertEquals(expectedVal, val, 0.1);
    // automatically changes date to curr date and commission to 0
  }


  @Test
  public void getComposition() {
    Map<String, Log> expected = new HashMap<>();
    assertEquals(expected, portfolio.getComposition());

    String ticker;
    double quantity;

    ticker = "META";
    quantity = 22.0;
    check(ticker, quantity, expected);

    ticker = "gOoG";
    quantity = 34;
    check(ticker, quantity, expected);

    ticker = "MSFT";
    quantity = 33;
    check(ticker, quantity, expected);
    portfolio.buy(ticker, quantity);

    Map<String, Log> shareDetails = portfolio.getComposition();
    Set<Details> detailsSet = newTreeSet();
    Details d = new Details(quantity, LocalDate.now());
    detailsSet.add(d);
    expected.put(ticker.toUpperCase(), newLog(detailsSet));
    checkHashMapEquality(expected, shareDetails);

    ticker = "nflx";
    quantity = 299;
    check(ticker, quantity, expected);

    ticker = "AMZN";
    quantity = 11.5;
    check(ticker, quantity, expected);

    ticker = "MSFT";
    quantity = 10.5;
    check(ticker, quantity, expected);
    portfolio.buy(ticker, quantity);

    shareDetails = portfolio.getComposition();
    detailsSet = newTreeSet();
    d = new Details(quantity, LocalDate.now());
    detailsSet.add(d);
    expected.put(ticker.toUpperCase(), newLog(detailsSet));
    checkHashMapEquality(expected, shareDetails);


    ticker = "MSFT";
    double toAdd = 22;
    quantity += toAdd;
    portfolio.buy(ticker, toAdd);

    shareDetails = portfolio.getComposition();
    d.setQuantity(quantity);
    expected.put(ticker.toUpperCase(), newLog(detailsSet));
    checkHashMapEquality(expected, shareDetails);
  }

  // does not sell anything for inflexible
  @Test
  public void sell() {
    Map<String, Log> expected = new HashMap<>();
    assertEquals(expected, portfolio.getComposition());

    portfolio.buy("META", 100);
    portfolio.sell("META", new Details(50, now), 0);

    addValueToDetailsSet(expected);
  }

  @Test
  public void savePortfolio() {
    Set<String> expected = new HashSet<>();
    expected.add("share,quantity,purchaseDate");

    portfolio.buy("META", 22.0);
    expected.add(String.format("META,22.0,%s", now));

    portfolio.buy("gOoG", 34);
    expected.add(String.format("GOOG,34.0,%s", now));

    portfolio.buy("nflx", 299);
    expected.add(String.format("NFLX,299.0,%s", now));

    portfolio.buy("AMZN", 22);
    expected.add(String.format("AMZN,22.0,%s", now));

    boolean saved = portfolio.savePortfolio();
    assertTrue(saved);

    File file = new File(this.directory + this.portfolioName + ".csv");
    try {
      Scanner csvReader = new Scanner(file);
      String line = csvReader.nextLine();
      while (csvReader.hasNext()) {
        assertTrue(expected.contains(line));
        line = csvReader.nextLine();
      }
      if (!file.delete()) {
        fail("Could not delete portfolio csv but should be able to.");
      }

      deleteLogAndCostBasisIfRequired();
    } catch (FileNotFoundException e) {
      fail("File not found when it should.");
    }
  }

  @Test
  public void savePortfolioInvalid() {
    assertFalse(portfolio.savePortfolio());
    portfolio.buy("nflx", 299);
    portfolio.savePortfolio();

    try {
      Files.createDirectories(Paths.get(this.directory));
      File file = new File(this.directory + this.portfolioName + ".csv");

      Scanner csvReader = new Scanner(file);
      String firstLine = csvReader.nextLine();
      assertNotEquals("idk,something,random", firstLine);
      if (!file.delete()) {
        fail("Could not delete csv but should be able to.");
      }

      deleteLogAndCostBasisIfRequired();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected void checkHashMapEquality(Map<String, Log> expected,
                                      Map<String, Log> shareDetails) {
    for (String ticker : shareDetails.keySet()) {
      Log expectedLog = expected.get(ticker);
      Log currLog = shareDetails.get(ticker);

      assertEquals(expectedLog.getLastSoldDate(), currLog.getLastSoldDate());
      assertEquals(expectedLog.getDetailsSet(), currLog.getDetailsSet());
    }

    for (String ticker : expected.keySet()) {
      Log expectedLog = expected.get(ticker);
      Log currLog = shareDetails.get(ticker);

      assertEquals(expectedLog.getLastSoldDate(), currLog.getLastSoldDate());
      assertEquals(expectedLog.getDetailsSet(), currLog.getDetailsSet());
    }
  }

  protected Set<Details> newTreeSet() {
    return new TreeSet<>(Comparator.comparing(Details::getPurchaseDate));
  }

  protected Log newLog(Set<Details> detailsSet) {
    return new Log(detailsSet, null);
  }

  private double getShareValue(String ticker, LocalDate date) {
    return api.getShareDetails(ticker, date).get("close");
  }

  private void check(String ticker, double quantity, Map<String, Log> expected) {
    portfolio.buy(ticker, quantity);
    Map<String, Log> shareDetails = portfolio.getComposition();

    Set<Details> detailsSet = newTreeSet();
    detailsSet.add(new Details(quantity, now));
    expected.put(ticker.toUpperCase(), newLog(detailsSet));

    checkHashMapEquality(expected, shareDetails);
  }
}