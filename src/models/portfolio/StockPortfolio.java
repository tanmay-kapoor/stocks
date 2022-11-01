package models.portfolio;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import models.api.ShareApi;

public class StockPortfolio implements Portfolio {
  private final String portfolioName;
  private Map<String, Map<String, Object>> stocks;
  private final LocalDate dateCreated;
  private final ShareApi api;
  private final String path;

  public StockPortfolio(String portfolioName, LocalDate dateCreated, ShareApi api) {
    this.portfolioName = portfolioName;
    this.dateCreated = dateCreated;
    this.api = api;
    path = System.getProperty("user.dir") + "/src/files/stocks/%s.%s";
    this.stocks = new HashMap<>();
  }

  public StockPortfolio(String portfolioName, LocalDate dateCreated,
                        Map<String, Map<String, Object>> stocks, ShareApi api) {
    this(portfolioName, dateCreated, api);
    this.stocks = stocks;
  }

  @Override
  public void addShare(String tickerSymbol, double quantity) {
    tickerSymbol = tickerSymbol.toUpperCase();

    Map<String, Object> details;
    if (stocks.containsKey(tickerSymbol)) {
      details = stocks.get(tickerSymbol);
      details.put("quantity", (double) details.get("quantity") + quantity);
    } else {
      details = new HashMap<>();
      details.put("quantity", quantity);
      details.put("dateCreated", LocalDate.now());
    }
    stocks.put(tickerSymbol, details);
  }

  @Override
  public double getValue() {
    return getValue(LocalDate.now());
  }

  @Override
  public double getValue(LocalDate date) throws RuntimeException {
    if (date.compareTo(dateCreated) < 0) {
      throw new IllegalArgumentException(String.format("Cannot get value for " +
              "date that is before the portfolio's creation date. (%s)", dateCreated));
    }

    double totalValue = 0.0;
    for (String tickerSymbol : stocks.keySet()) {
      Map<String, Double> shareDetails = api.getShareDetails(tickerSymbol, date);
      totalValue = totalValue + (shareDetails.get("close") *
              (double) stocks.get(tickerSymbol).get("quantity"));
    }

    return totalValue;
  }

  @Override
  public String getComposition() {
    StringBuilder composition = new StringBuilder("\nshare,quantity,dateCreated");
    for (String share : this.stocks.keySet()) {
      Map<String, Object> details = stocks.get(share);
      composition
              .append("\n")
              .append(share)
              .append(",")
              .append(details.get("quantity"))
              .append(",")
              .append(details.get("dateCreated"));
    }
    return composition.toString();
  }

  @Override
  public boolean savePortfolio() throws IOException {
    if (stocks.size() == 0) {
      System.out.println(stocks.size() );
      return false;
    }

    String fileName = String.format(path, portfolioName, "csv");
    FileWriter csvWriter = new FileWriter(fileName);
    csvWriter.append("share,quantity,dateCreated\n");
    for (String share : stocks.keySet()) {
      Map<String, Object> details = stocks.get(share);
      csvWriter.
              append(share).
              append(",")
              .append(String.valueOf(details.get("quantity")))
              .append(",")
              .append(details.get("dateCreated").toString())
              .append("\n");
    }

    csvWriter.flush();
    csvWriter.close();
    return true;
  }
}
