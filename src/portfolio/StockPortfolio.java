package portfolio;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StockPortfolio implements Portfolio{

  private final String username;
  private Map<String, Double> stocks;

  public StockPortfolio() {
    username = "idk";
    stocks = new HashMap<>();
  }

  @Override
  public void addShare(String tickerSymbol, double quantity) throws IOException, IllegalArgumentException {
    if(stocks.containsKey(tickerSymbol)) {
      throw new IOException("This stock already exists in your portfolio.");
    }

    if(quantity < 1) {
      throw new IllegalArgumentException("Number of shares cannot be less than 1.");
    }

    stocks.put(tickerSymbol, quantity);
  }

  @Override
  public double getValue() {
    return 0;
  }

  @Override
  public double getValue(String date) {
    return 0;
  }

  @Override
  public Map<String, Double> getPortfolioComposition() {
    return new HashMap<String, Double> (stocks);
  }

  @Override
  public void savePortfolio() {

  }
}
