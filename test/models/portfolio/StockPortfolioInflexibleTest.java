package models.portfolio;

import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import models.Details;
import models.Log;
import models.api.ShareApi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * A test class that check the methods implemented foe inflexible portfolio.
 */
public class StockPortfolioInflexibleTest extends AbstractStockPortfolioTest {
  protected Portfolio createPortfolio(String portfolioName, String directory, ShareApi api) {
    return new StockPortfolioInflexible(portfolioName, directory, api);
  }

  protected void deleteLogAndCostBasisIfRequired() {
    return;
  }

  @Test
  public void getValueDateProvided() {
    portfolio.buy("AMZN", 27);
    portfolio.buy("NFLX", 18);
    double val = portfolio.getValue(LocalDate.parse("2022-10-10"));
    double expected = (27 * api.getShareDetails("AMZN", now).get("close"))
            + (18 * api.getShareDetails("NFLX", now).get("close"));
    assertEquals(expected, val, 0.1);

    try {
      portfolio.getValue(LocalDate.parse("2022/10/10"));
      fail("Should be invalid date");
    } catch (DateTimeParseException e1) {
      val = portfolio.getValue(LocalDate.parse("1900-07-15"));
      expected = (27 * api.getShareDetails("AMZN", now).get("close"))
              + (18 * api.getShareDetails("NFLX", now).get("close"));
      assertEquals(expected, val, 0.1);
    }
  }

  protected void getValueSomeStocksDifferently(double val) {
    double expected = (27 * api.getShareDetails("AMZN", now).get("close"))
            + (18 * api.getShareDetails("NFLX", now).get("close"));
    assertEquals(expected, val, 0.1);
  }

  protected LocalDate getPurchaseDate() {
    return LocalDate.now();
  }

  // get composition for current date even who date provided is not today.
  @Test
  public void getCompositionDateProvided() {
    Map<String, Log> expected = new HashMap<>();
    assertEquals(expected, portfolio.getComposition());

    Map<String, Log> shareDetails;
    Set<Details> detailsSet;

    portfolio.buy("META", 22.0);
    shareDetails = portfolio.getComposition(LocalDate.parse("2020-10-10"));
    detailsSet = newTreeSet();
    detailsSet.add(new Details(22.0, now));
    expected.put("META", newLog(detailsSet));
    checkHashMapEquality(expected, shareDetails);
  }

  protected void addValueToDetailsSet(Map<String, Log> expected) {
    Map<String, Log> test = portfolio.getComposition();
    Set<Details> detailsSet = newTreeSet();
    detailsSet.add(new Details(100, now));
    expected.put("META", newLog(detailsSet));
    checkHashMapEquality(expected, test);
  }
}