package models.portfolio;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import models.api.ShareApi;

public class StockPortfolio implements Portfolio, Serializable {
  private static final long serialVersionUID = 6529685098267757690L;

  private final String portfolioName;
  private Map<String, Double> stocks;
  private final ShareApi api;
  private final String path;

  public StockPortfolio(String portfolioName, ShareApi api) {
    this.portfolioName = portfolioName;
    this.api = api;
    path = System.getProperty("user.dir") + "/src/files/stocks/%s.%s";
    this.stocks = new HashMap<>();
  }

  public StockPortfolio(String portfolioName, Map<String, Double> stocks, ShareApi api) {
    this(portfolioName, api);
    this.stocks = stocks;
  }

  @Override
  public void addShare(String tickerSymbol, double quantity) throws RuntimeException {
    tickerSymbol = tickerSymbol.toUpperCase();

    if (stocks.containsKey(tickerSymbol)) {
      stocks.put(tickerSymbol, stocks.get(tickerSymbol) + quantity);
    } else {
      stocks.put(tickerSymbol, quantity);
    }
  }

  @Override
  public double getValue() {
    return getValue(java.time.LocalDate.now().toString());
  }

  @Override
  public double getValue(String date) throws NullPointerException {
    double totalValue = 0.0;

    for (String tickerSymbol : stocks.keySet()) {
      Map<String, Double> shareDetails = api.getShareDetails(tickerSymbol, date);
      totalValue = totalValue + (shareDetails.get("close") * stocks.get(tickerSymbol));
    }

    return totalValue;
  }

  @Override
  public String getComposition() {
    StringBuilder composition = new StringBuilder("\nshare,quantity");
    for (String share : this.stocks.keySet()) {
      composition.append("\n").append(share).append(",").append(stocks.get(share));
    }
    return composition.toString();
  }

  @Override
  public void savePortfolio() throws RuntimeException, IOException {
    if (stocks.size() == 0) {
      throw new RuntimeException("You must add at least 1 share to save a portfolio!");
    }

    String fileName = String.format(path, portfolioName, "csv");
    FileWriter csvWriter = new FileWriter(fileName);
    csvWriter.append("share,quantity\n");
    for (String share : stocks.keySet()) {
      csvWriter.append(share).append(",").append(String.valueOf(stocks.get(share))).append("\n");
    }

    csvWriter.flush();
    csvWriter.close();
  }
}
