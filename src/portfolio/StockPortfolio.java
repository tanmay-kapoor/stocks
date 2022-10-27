package portfolio;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StockPortfolio implements Portfolio {

  private final String username;
  private Map<String, Double> stocks;
  private final AlphaVantageDemo api;

  public StockPortfolio() {
    username = "idk";
    stocks = new HashMap<>();
    api = new AlphaVantageDemo();
  }

  @Override
  public void addShare(String tickerSymbol, double quantity) throws IOException, IllegalArgumentException {
    if (stocks.containsKey(tickerSymbol)) {
      throw new IOException("This stock already exists in your portfolio.");
    }

    if (quantity < 1) {
      throw new IllegalArgumentException("Number of shares cannot be less than 1.");
    }

    stocks.put(tickerSymbol, quantity);
  }

  @Override
  public double getValue() {
    return getValue(java.time.LocalDate.now().toString());
  }

  @Override
  public double getValue(String date) {
    double totalValue = 0.0;

    for (String tickerSymbol : stocks.keySet()) {
      Map<String, Double> shareDetails = api.getShareDetails(tickerSymbol, date);
      totalValue = totalValue + (shareDetails.get("close") * stocks.get(tickerSymbol));
    }

    return totalValue;
  }

  @Override
  public Map<String, Double> getPortfolioComposition() {
    return new HashMap<String, Double>(stocks);
  }

  @Override
  public void savePortfolio() {

  }
}
