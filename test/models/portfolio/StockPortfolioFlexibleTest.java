package models.portfolio;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import models.Details;
import models.Log;
import models.api.ShareApi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * A test class that check the methods implemented foe flexible portfolio.
 */
public class StockPortfolioFlexibleTest extends AbstractStockPortfolioTest {
  protected Portfolio createPortfolio(String portfolioName, String directory, ShareApi api) {
    return new StockPortfolioFlexible(portfolioName, directory, api);
  }

  @Test
  public void buySpecificDate() {
    Map<String, Log> expected = new HashMap<>();
    assertEquals(expected, portfolio.getComposition());

    String ticker;
    double quantity;
    LocalDate purchaseDate;
    double commission = 10;
    Set<Details> detailsSet;

    ticker = "META";
    quantity = 22.0;
    purchaseDate = LocalDate.parse("2022-10-10");
    detailsSet = newTreeSet();
    check2(ticker, quantity, purchaseDate, commission, detailsSet, expected);

    quantity = 115;
    purchaseDate = LocalDate.parse("2014-09-09");
    Details details = new Details(quantity, purchaseDate);
    portfolio.buy(ticker, details, commission);
    Map<String, Log> composition = portfolio.getComposition();
    details.setQuantity(quantity + 22);
    details.setPurchaseDate(LocalDate.parse("2022-10-10"));
    detailsSet = newTreeSet();
    detailsSet.add(details);
    expected.put(ticker.toUpperCase(), newLog(detailsSet));
    checkHashMapEquality(expected, composition);

    quantity = 10;
    purchaseDate = LocalDate.parse("2021-12-10");
    details = new Details(quantity, purchaseDate);
    portfolio.buy(ticker, details, commission);
    composition = portfolio.getComposition();
    details.setQuantity(quantity + 10);
    details.setPurchaseDate(LocalDate.parse("2022-10-10"));
    detailsSet = newTreeSet();
    detailsSet.add(details);
    expected.put(ticker.toUpperCase(), newLog(detailsSet));
    checkHashMapEquality(expected, composition);

    ticker = "gOoG";
    quantity = 254;
    purchaseDate = LocalDate.parse("2019-10-05");
    detailsSet = newTreeSet();
    check2(ticker, quantity, purchaseDate, commission, detailsSet, expected);

    ticker = "mSFt";
    quantity = 22;
    purchaseDate = LocalDate.parse("2021-10-07");
    detailsSet = newTreeSet();
    check2(ticker, quantity, purchaseDate, commission, detailsSet, expected);

    quantity = 13;
    purchaseDate = LocalDate.parse("2017-10-07");
    details = new Details(quantity, purchaseDate);
    portfolio.buy(ticker, details, commission);
    composition = portfolio.getComposition();
    details.setQuantity(quantity + 22);
    details.setPurchaseDate(LocalDate.parse("2021-10-07"));
    detailsSet = newTreeSet();
    detailsSet.add(details);
    expected.put(ticker.toUpperCase(), newLog(detailsSet));
    checkHashMapEquality(expected, composition);
  }

  @Test
  public void testBuyInvalid() {
    try {
      Details details = new Details(50, LocalDate.parse("2028-10-10"));
      portfolio.buy("META", details, 10);
      fail("Should fail but future date but did not");
    } catch (IllegalArgumentException e) {
      // passes
    }
  }


  @Test
  public void getValueSpecificDate() {
    portfolio.buy("META", new Details(22, LocalDate.parse("2022-10-10")), 10);
    portfolio.buy("GOOG", new Details(22, LocalDate.parse("2018-10-10")), 10);
    portfolio.buy("META", new Details(22, LocalDate.parse("2019-10-10")), 10);
    portfolio.buy("MSFT", new Details(22, LocalDate.parse("2017-10-10")), 10);
    double val = portfolio.getValue(LocalDate.parse("2019-05-15"));
    assertEquals(28385.06, val, 0);

    val = portfolio.getValue(LocalDate.parse("2015-01-01"));
    assertEquals(0, val, 0);

    val = portfolio.getValue(LocalDate.parse("1900-01-01"));
    assertEquals(0, val, 0);
  }

  @Test
  public void getCompositionSpecificDate() {
    portfolio.buy("META", new Details(22, LocalDate.parse("2022-10-10")), 10);
    portfolio.buy("GOOG", new Details(22, LocalDate.parse("2018-10-10")), 10);
    portfolio.buy("META", new Details(22, LocalDate.parse("2019-10-10")), 10);
    portfolio.buy("MSFT", new Details(22, LocalDate.parse("2017-10-10")), 10);

    Map<String, Log> composition = portfolio.getComposition(LocalDate.parse("2011-01-01"));
    Map<String, Log> expected = new HashMap<>();
    assertEquals(expected, composition);

    composition = portfolio.getComposition(LocalDate.parse("2019-11-15"));
    Set<Details> detailsSet = newTreeSet();
    detailsSet.add(new Details(22, LocalDate.parse("2019-10-10")));
    expected.put("META", newLog(detailsSet));
    detailsSet = newTreeSet();
    detailsSet.add(new Details(22, LocalDate.parse("2017-10-10")));
    expected.put("MSFT", newLog(detailsSet));
    detailsSet = newTreeSet();
    detailsSet.add(new Details(22, LocalDate.parse("2018-10-10")));
    expected.put("GOOG", newLog(detailsSet));

    checkHashMapEquality(expected, composition);

    composition = portfolio.getComposition(LocalDate.now());
    Log log = expected.get("META");
    log.getDetailsSet().clear();
    log.getDetailsSet().add(new Details(44, LocalDate.parse("2022-10-10")));
    expected.put("META", log);
    checkHashMapEquality(expected, composition);
  }

  protected void deleteLogAndCostBasisIfRequired() {
    File logFile = new File(this.directory + "logs/" + this.portfolioName + ".csv");
    File costBasisFile = new File(this.directory + "costbasis/"
            + this.portfolioName + ".csv");

    try {
      Scanner csvReader = new Scanner(logFile);
      while (csvReader.hasNext()) {
        csvReader.nextLine();
      }
      if (!logFile.delete()) {
        fail("Could not delete log csv but should be able to.");
      }

      csvReader = new Scanner(costBasisFile);
      while (csvReader.hasNext()) {
        csvReader.nextLine();
      }
      if (!costBasisFile.delete()) {
        fail("Could not delete cost basis csv but should be able to.");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void check2(String ticker, double quantity, LocalDate purchaseDate, double commission,
                      Set<Details> detailsSet, Map<String, Log> expected) {
    Details details = new Details(quantity, purchaseDate);
    portfolio.buy(ticker, details, commission);
    Map<String, Log> composition = portfolio.getComposition();

    detailsSet.add(new Details(quantity, purchaseDate));
    expected.put(ticker.toUpperCase(), newLog(detailsSet));

    checkHashMapEquality(expected, composition);
  }

  protected void getValueSomeStocksDifferently(double val) {
    assertEquals(0.0, val, 0.1);

    Details d = new Details(150, LocalDate.parse("2020-10-10"));
    portfolio.buy("META", d, 10);
    val = portfolio.getValue(LocalDate.parse("2021-07-15"));
    assertEquals(51669, val, 0.1);
  }

  protected LocalDate getPurchaseDate() {
    return LocalDate.parse("2020-12-12");
  }

  @Test
  public void getCompositionDateProvided() {
    Map<String, Log> expected = new HashMap<>();
    assertEquals(expected, portfolio.getComposition());

    Map<String, Log> shareDetails;
    Set<Details> detailsSet;

    portfolio.buy("META", 22.0);
    shareDetails = portfolio.getComposition(LocalDate.parse("2020-10-10"));
    checkHashMapEquality(expected, shareDetails);

    shareDetails = portfolio.getComposition(now);
    detailsSet = newTreeSet();
    detailsSet.add(new Details(22, now));
    expected.put("META", newLog(detailsSet));
    checkHashMapEquality(expected, shareDetails);

    portfolio.buy("META", new Details(100, LocalDate.parse("2021-10-10")), 130);
    portfolio.buy("META", new Details(100, LocalDate.parse("2022-10-10")), 130);
    shareDetails = portfolio.getComposition(LocalDate.parse("2021-10-20"));
    detailsSet = newTreeSet();
    detailsSet.add(new Details(100, LocalDate.parse("2021-10-10")));
    expected.put("META", newLog(detailsSet));
    checkHashMapEquality(expected, shareDetails);

    try {
      portfolio.getComposition(LocalDate.parse("2024-01-01"));
      fail("should fail for future date but did not");
    } catch (IllegalArgumentException e) {
      // passes
    }
  }

  @Test
  public void testCostBasis() {
    Details details = new Details(22, LocalDate.parse("2021-01-01"));
    portfolio.buy("META", details, 10);

    details = new Details(66, LocalDate.parse("2021-12-12"));
    portfolio.buy("GOOG", details, 44.7);

    details = new Details(22, LocalDate.parse("2022-10-10"));
    portfolio.buy("AAPL", details, 29);

    details = new Details(2, LocalDate.parse("2021-05-05"));
    portfolio.sell("META", details, 5.2);

    assertEquals(0, portfolio.getCostBasis(LocalDate.parse("1900-01-01")), 0);
    assertEquals(0, portfolio.getCostBasis(LocalDate.parse("2000-01-01")), 0);
    assertEquals(6019.52, portfolio.getCostBasis(LocalDate.parse("2021-01-01")), 0);
    assertEquals(6019.52, portfolio.getCostBasis(LocalDate.parse("2021-04-04")), 0);
    assertEquals(6024.72, portfolio.getCostBasis(LocalDate.parse("2021-05-05")), 0);
    assertEquals(6024.72, portfolio.getCostBasis(LocalDate.parse("2021-11-11")), 0);
    assertEquals(202320.42, portfolio.getCostBasis(LocalDate.parse("2021-12-12")), 0);
    assertEquals(202320.42, portfolio.getCostBasis(LocalDate.parse("2022-09-09")), 0);
    assertEquals(205438.66, portfolio.getCostBasis(LocalDate.parse("2022-10-10")), 0);
    assertEquals(205438.66, portfolio.getCostBasis(LocalDate.parse("2022-11-11")), 0);

    try {
      portfolio.sell("META", details, -5.2);
      fail("Should fail for negative commission but did not");
    } catch (IllegalArgumentException e) {
      // passes
    }
  }

  @Test
  public void testCostBasisInvalid() {
    Details details = new Details(22, LocalDate.parse("2021-01-01"));
    portfolio.buy("META", details, 10);

    try {
      portfolio.getCostBasis(LocalDate.parse("2028-10-10"));
    } catch (IllegalArgumentException e1) {
      assertEquals(0, portfolio.getCostBasis(LocalDate.parse("1900-10-10")), 0);
    }
  }

  @Test
  public void testSell() {
    Details details = new Details(50, LocalDate.parse("2020-10-10"));
    portfolio.buy("META", details, 10);

    try {
      details = new Details(10, LocalDate.parse("2028-10-10"));
      portfolio.sell("META", details, 20);
      fail("Should fail for future date but did not.");
    } catch (IllegalArgumentException e) {
      // passes
    }
  }

  @Test
  public void testSellInvalidateFutureTransactions() {
    Details details = new Details(5, LocalDate.parse("2014-05-05"));
    portfolio.buy("GOOG", details, 20.1);
    details = new Details(10, LocalDate.parse("2014-04-01"));
    portfolio.buy("AAPL", details, 10);
    details = new Details(25, LocalDate.parse("2022-05-01"));
    portfolio.buy("AAPL", details, 10.2);
    details = new Details(30, LocalDate.parse("2022-05-05"));
    portfolio.sell("AAPL", details, 35.3);
    try {
      details = new Details(10, LocalDate.parse("2022-05-04"));
      portfolio.sell("AAPL", details, 25.6);
      fail("Should fail for sell in date that invalidates future transactions but did not.");
    } catch (IllegalArgumentException e) {
      // passes
    }
  }

  @Test
  public void testSellInvalid() {
    Details details = new Details(50, LocalDate.parse("2020-10-10"));
    portfolio.buy("META", details, 10);

    try {
      details = new Details(1980, LocalDate.parse("2028-10-10"));
      portfolio.sell("META", details, 10);
      fail("Should fail but did not");
    } catch (IllegalArgumentException e) {
      // passes
    }
  }

  @Test
  public void testPerformance() {
    Details details = new Details(50, LocalDate.parse("2015-10-10"));
    portfolio.buy("GOOG", details, 10);

    LocalDate from = LocalDate.parse("2014-10-10");
    LocalDate to = LocalDate.parse("2022-10-10");
    Map<LocalDate, Double> performance = portfolio.getPortfolioPerformance(from, to);

    Map<LocalDate, Double> expected = new HashMap<>();
    expected.put(LocalDate.parse("2014-10-10"), 0.0);
    expected.put(LocalDate.parse("2015-05-06"), 0.0);
    expected.put(LocalDate.parse("2015-11-30"), 37130.0);
    expected.put(LocalDate.parse("2016-06-25"), 33761.0);
    expected.put(LocalDate.parse("2017-01-19"), 40108.75);
    expected.put(LocalDate.parse("2017-08-15"), 46111.0);
    expected.put(LocalDate.parse("2018-03-11"), 58002.0);
    expected.put(LocalDate.parse("2018-10-05"), 57867.49999999999);
    expected.put(LocalDate.parse("2019-05-01"), 58404.0);
    expected.put(LocalDate.parse("2019-11-25"), 65334.5);
    expected.put(LocalDate.parse("2020-06-20"), 71586.0);
    expected.put(LocalDate.parse("2021-01-14"), 87009.0);
    expected.put(LocalDate.parse("2021-08-10"), 138096.5);
    expected.put(LocalDate.parse("2022-03-06"), 132122.0);
    expected.put(LocalDate.parse("2022-10-10"), 4935.5);

    assertEquals(expected, performance);

    from = LocalDate.parse("2022-10-10");
    to = LocalDate.parse("2021-10-10");
    try {
      portfolio.getPortfolioPerformance(from, to);
      fail("Should fail for invalid from..to but did not");
    } catch (IllegalArgumentException e) {
      // passes
    }
  }

  protected void addValueToDetailsSet(Map<String, Log> expected) {
    Map<String, Log> test = portfolio.getComposition();
    Set<Details> detailsSet = newTreeSet();
    detailsSet.add(new Details(50, now));
    expected.put("META", new Log(detailsSet, now));
    checkHashMapEquality(expected, test);
  }

  @Test
  public void testDca() {

  }
}