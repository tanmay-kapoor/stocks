package models.portfolio;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
  public StockPortfolio(String portfolioName, LocalDate dateCreated, String path, ShareApi api) {
    this.portfolioName = portfolioName;
    this.dateCreated = dateCreated;
    this.api = api;
    this.path = path;
    this.stocks = new HashMap<>();
  }

  @Override
  public void addShare(String tickerSymbol, double quantity) {
    if (quantity < 0.0) {
      throw new IllegalArgumentException("Quantity should be grater than 0.");
    }

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
  public Map<String, Details> getComposition() {
    return new HashMap<>(stocks);
  }

  @Override
  public boolean savePortfolio() {
    if (stocks.size() == 0) {
      return false;
    }

    try {
      Files.createDirectories(Paths.get(this.path));
      String fileName = String.format(path + "%s.csv", portfolioName);
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
    } catch (IOException e) {
      throw new RuntimeException("Something went wrong!");
    }
  }
}
