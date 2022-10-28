import org.junit.Test;

import java.io.IOException;

import portfolio.Portfolio;
import portfolio.StockPortfolio;

import static org.junit.Assert.*;

public class StockPortfolioTest {

  @Test
  public void testGetValue() {
    Portfolio portfolio = new StockPortfolio("idk", "random");

    try {
      portfolio.addShare("GOOG", 1);
      portfolio.addShare("MSFT", 2);
    } catch(IOException | IllegalArgumentException e) {
      fail(e.getMessage());
    }

    assertEquals(546.1, portfolio.getValue("2022-10-27"), 0);
//    assertEquals(546.1, portfolio.getValue(), 0);
  }
}