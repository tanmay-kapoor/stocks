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
    check2(ticker, quantity, purchaseDate, commission, detailsSet, expected);

    quantity = 10;
    purchaseDate = LocalDate.parse("2021-12-10");
    check2(ticker, quantity, purchaseDate, commission, detailsSet, expected);

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
    check2(ticker, quantity, purchaseDate, commission, detailsSet, expected);
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

  protected void addValueToDetailsSet(Map<String, Log> expected) {
    Map<String, Log> test = portfolio.getComposition();
    Set<Details> detailsSet = newTreeSet();
    detailsSet.add(new Details(50, now));
    expected.put("META", new Log(detailsSet, now));
    checkHashMapEquality(expected, test);
  }
}