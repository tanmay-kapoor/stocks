import org.junit.Test;

import java.io.IOException;

import portfolio.Portfolio;
import portfolio.StockPortfolio;

import static org.junit.Assert.*;

public class StockPortfolioTest {
  private Portfolio portfolio;

  @Test
  public void testGetValue() {
    portfolio = new StockPortfolio();
    try {
      portfolio.addShare("GOOG", 1);
      portfolio.addShare("MSFT", 2);
    } catch(IOException e1) {
      fail("should have passed");
    } catch(IllegalArgumentException e2) {
      fail("should have passed too");
    }
    assertEquals(546.1, portfolio.getValue("2022-10-27"), 0);
    assertEquals(546.1, portfolio.getValue(), 0);
  }
}