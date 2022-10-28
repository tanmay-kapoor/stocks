package portfolio;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StockPortfolio implements Portfolio {

  private final String username;
  private final String name;
  private Map<String, Double> stocks;
  private final AlphaVantageDemo api;

  public StockPortfolio(String name) {
    username = "idk";
    this.name = name;
    stocks = new HashMap<>();
    api = new AlphaVantageDemo();
  }

  @Override
  public void addShare(String tickerSymbol, double quantity) throws IOException, IllegalArgumentException {
    if (stocks.containsKey(tickerSymbol)) {
      throw new IOException("This stock already exists in your portfolio. Please enter a new ticker.");
    }

    try {
      api.getShareDetails(tickerSymbol, "2022-10-26");
      if (quantity < 1) {
        throw new IllegalArgumentException("Number of shares cannot be less than 1. Please enter a valid quantity.");
      }
      stocks.put(tickerSymbol, quantity);
    } catch(IllegalArgumentException e) {
      throw new IllegalArgumentException("This ticker symbol is not associated with any company. Please enter a valid ticker.");
    }
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
