package models.portfolio;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import models.Details;
import models.api.ShareApi;

/**
 * A class that is an extension of <code>Portfolio</code> class. This class specifically
 * deals with the portfolio that store shares in the form of stocks listed in the NASDAQ.
 */
public class StockPortfolio implements Portfolio {
  private final String portfolioName;
  private Map<String, Details> stocks;
  private final LocalDate dateCreated;
  private final ShareApi api;
  private final String path;

  /**
   * Constructor for the class that initializes the name of the portfolio,
   * date it was created and the API that it is supposed to use for the fetching relevant data.
   *
   * @param portfolioName name of the portfolio.
   * @param dateCreated   creation date of the portfolio.
   * @param api           API is meant to be used.
   */
  public StockPortfolio(String portfolioName, LocalDate dateCreated, ShareApi api) {
    this.portfolioName = portfolioName;
    this.dateCreated = dateCreated;
    this.api = api;
    path = System.getProperty("user.dir") + "/src/files/stocks/%s.%s";
    this.stocks = new HashMap<>();
  }

  /**
   * Constructor for the class that initializes the name of the portfolio,
   * date it was created, the map that stores the details of the stocks fetched from the csv
   * and the API that it is supposed to use for the fetching relevant data.
   *
   * @param portfolioName name of the portfolio.
   * @param dateCreated   creation date of the portfolio.
   * @param stocks        details of the stocks in the portfolio.
   * @param api           API is meant to be used.
   */
  public StockPortfolio(String portfolioName, LocalDate dateCreated,
                        Map<String, Details> stocks, ShareApi api) {
    this(portfolioName, dateCreated, api);
    this.stocks = stocks;
  }

  @Override
  public void addShare(String tickerSymbol, double quantity) {
    tickerSymbol = tickerSymbol.toUpperCase();

    Details details;
    if (stocks.containsKey(tickerSymbol)) {
      details = stocks.get(tickerSymbol);
      details = new Details(details.getQuantity() + quantity, details.getDateCreated());
    } else {
      details = new Details(quantity, LocalDate.now());
    }
    stocks.put(tickerSymbol, details);
  }

  @Override
  public double getValue() {
    return getValue(LocalDate.now());
  }

  @Override
  public double getValue(LocalDate date) throws RuntimeException {
    double totalValue = 0.0;
    for (String tickerSymbol : stocks.keySet()) {
      Map<String, Double> shareDetails = api.getShareDetails(tickerSymbol, date);
      totalValue += (shareDetails.get("close") * stocks.get(tickerSymbol).getQuantity());
    }

    return totalValue;
  }

  @Override
  public String getComposition() {
    StringBuilder composition = new StringBuilder("\nshare,quantity,dateCreated");
    for (String share : this.stocks.keySet()) {
      Details details = stocks.get(share);
      composition
              .append("\n")
              .append(share)
              .append(",")
              .append(details.getQuantity())
              .append(",")
              .append(details.getDateCreated().toString());
    }
    return composition.toString();
  }

  @Override
  public boolean savePortfolio() throws IOException {
    if (stocks.size() == 0) {
      return false;
    }

    String fileName = String.format(path, portfolioName, "csv");
    FileWriter csvWriter = new FileWriter(fileName);
    csvWriter.append("share,quantity,dateCreated\n");
    for (String share : stocks.keySet()) {
      Details details = stocks.get(share);
      csvWriter.
              append(share).
              append(",")
              .append(String.valueOf(details.getQuantity()))
              .append(",")
              .append(details.getDateCreated().toString())
              .append("\n");
    }

    csvWriter.flush();
    csvWriter.close();
    return true;
  }
}
