package portfolio;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import api.AlphaVantageDemo;
import api.ShareApi;

public class StockPortfolio implements Portfolio {

  private final String username;
  private final String portfolioName;
  private final Map<String, Double> stocks;
  private final ShareApi api;

  public StockPortfolio(String username, String portfolioName, ShareApi api) {
    this.username = username;
    this.portfolioName = portfolioName;
    this.api = api;
    this.stocks = new HashMap<>();
  }

  @Override
  public void addShare(String tickerSymbol, double quantity) throws RuntimeException {
    if (stocks.containsKey(tickerSymbol)) {
      throw new IllegalArgumentException("This stock already exists in your portfolio. Please enter a new ticker.");
    }

    try {
      api.getShareDetails(tickerSymbol, "2022-10-26");
      if (quantity < 1) {
        throw new IllegalArgumentException("Number of shares cannot be less than 1. Please enter a valid quantity.");
      }
      stocks.put(tickerSymbol, quantity);
    } catch (Exception e) {
      throw e;
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
    return new HashMap<>(stocks);
  }

  @Override
  public void savePortfolio() throws IOException {
    try {
      FileWriter csvWriter = new FileWriter(String.format("%s.csv", portfolioName));

      csvWriter.append("username,share,quantity\n");
      for (String share : stocks.keySet()) {
        csvWriter.append(username).append(",").append(share).append(",").append(String.valueOf(stocks.get(share))).append("\n");
      }

      csvWriter.flush();
      csvWriter.close();
    } catch (IOException e) {
      throw e;
    }
  }
}
