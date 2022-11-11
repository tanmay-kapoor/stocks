package models.portfolio;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import models.Details;
import models.Log;
import models.api.ShareApi;

/**
 * A class that is an extension of <code>Portfolio</code> class. This class specifically
 * deals with the portfolio that store shares in the form of stocks listed in the NASDAQ.
 */
abstract class AbstractPortfolio implements Portfolio {
  protected final String portfolioName;
  protected Map<String, Log> stocks;
  private final ShareApi api;
  private final String path;

  abstract boolean PortfolioBasedSell(String ticker, Details details);

  /**
   * Constructor for the class that initializes the name of the portfolio,
   * date it was created and the API that it is supposed to use for the fetching relevant data.
   *
   * @param portfolioName name of the portfolio.
   * @param purchaseDate  creation date of the portfolio.
   * @param api           API is meant to be used.
   */
  protected AbstractPortfolio(String portfolioName, LocalDate purchaseDate,
                              String path, ShareApi api) {
    this.portfolioName = portfolioName;
    this.api = api;
    this.path = path;
    this.stocks = new HashMap<>();
  }

  protected AbstractPortfolio(String portfolioName, LocalDate purchaseDate, Map<String, Log> stocks,
                              String path, ShareApi api) {
    this.portfolioName = portfolioName;
    this.api = api;
    this.path = path;
    this.stocks = new HashMap<>(stocks);
  }


  @Override
  public void buy(String ticker, Details details) {
    if (details.getQuantity() < 0.0) {
      throw new IllegalArgumentException("Quantity should be grater than 0.");
    }
    ticker = ticker.toUpperCase();
    //if ticker doesn't exist in the portfolio just add it
    if (!stocks.containsKey(ticker)) {
      Set<Details> detailsSet = new TreeSet<>(Comparator.comparing(Details::getPurchaseDate));
      detailsSet.add(details);
      Log log = new Log(detailsSet, null);

      stocks.put(ticker, log);
    }
    // add the existing Log of the stock
    else {
      Log log = stocks.get(ticker);
      Set<Details> detailsSet = log.getDetailsSet();
      boolean haveBoughtBefore = false;
      double prevRowQty = 0;

      for (Details d : detailsSet) {

        if(d.getPurchaseDate().compareTo(details.getPurchaseDate()) < 0) {
          prevRowQty = d.getQuantity();
        }
        else if(d.getPurchaseDate().compareTo(details.getPurchaseDate()) == 0) {
          haveBoughtBefore = true;
          System.out.println("Date" +d.getPurchaseDate() + "Adding on: " + d.getQuantity() +" "+ details.getQuantity());
          d.setQuantity(d.getQuantity() + details.getQuantity());
        }
        else {
          System.out.println("Date" +d.getPurchaseDate() + "Adding on: " + d.getQuantity() +" "+ details.getQuantity());
          d.setQuantity(d.getQuantity() + details.getQuantity());
          System.out.println("=="  + d.getQuantity());
        }
      }

      if (!haveBoughtBefore) {
        detailsSet.add(new Details(details.getQuantity() + prevRowQty, details.getPurchaseDate()));
      }

      log.setDetailsSet(detailsSet);
    }
  }


  @Override
  public boolean sell(String ticker, Details details) {
    return PortfolioBasedSell(ticker, details);
  }


  @Override
  public double getValue() {
    return getValue(LocalDate.now());
  }

  @Override
  public double getValue(LocalDate date) throws RuntimeException {
    if(date.compareTo(LocalDate.now()) > 0) {
      throw new IllegalArgumentException("Cannot get value for a future date.");
    }

    double totalValue = 0.0;
    for (String tickerSymbol : stocks.keySet()) {
      Log log = stocks.get(tickerSymbol);

      Set<Details> detailsSet = log.getDetailsSet();
      double quantity = 0.0;
      for (Details d : detailsSet) {
        quantity = d.getQuantity();
      }
      Map<String, Double> shareDetails = api.getShareDetails(tickerSymbol, date);
      totalValue += shareDetails.get("close") * quantity;
      System.out.println(shareDetails.get("close") + " " + quantity);
    }

    return totalValue;
  }

  @Override
  public Map<String, Log> getComposition(LocalDate purchaseDate) {
    Map<String, Log> filteredStocks = new HashMap<>();
    for(String stock : stocks.keySet()) {
      Log log = stocks.get(stock);
      Set<Details> d = new TreeSet<>(Comparator.comparing(Details::getPurchaseDate));
      for(Details details : log.getDetailsSet()) {
        if(details.getPurchaseDate().compareTo(purchaseDate) <= 0) {
          d.add(details);
        }
      }
      if(d.size() > 0) {
        Log logCopy = new Log(d, log.getLastSoldDate());
        filteredStocks.put(stock, logCopy);
      }
    }
//    return new HashMap<>(stocks);
    return filteredStocks;
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
      csvWriter.append("share,quantity,purchaseDate\n");
      for (String ticker : stocks.keySet()) {
        Log log = stocks.get(ticker);

        Set<Details> detailsSet = log.getDetailsSet();

        for (Details d : detailsSet) {
          csvWriter.append(ticker.toUpperCase()).append(",")
                  .append(String.valueOf(d.getQuantity()))
                  .append(",")
                  .append(d.getPurchaseDate().toString())
                  .append("\n");
        }
      }

      csvWriter.flush();
      csvWriter.close();
      saveLastSoldLog();
      return true;
    } catch (IOException e) {
      throw new RuntimeException("Something went wrong!");
    }
  }

  private void saveLastSoldLog() {
    try {
      String path_log = this.path + "logs/";
      System.out.println(path_log);
      Files.createDirectories(Paths.get(path_log));
      String fileName = String.format(path_log + "%s.csv", portfolioName);
      FileWriter csvWriter = new FileWriter(fileName);
      csvWriter.append("share,lastSellDate\n");
      for (String ticker : stocks.keySet()) {
        Log log = stocks.get(ticker);

        csvWriter.append(ticker.toUpperCase()).append(",")
                .append(String.valueOf(log.getLastSoldDate()))
                .append("\n");

      }

      csvWriter.flush();
      csvWriter.close();
    } catch (IOException e) {
      throw new RuntimeException("Something went wrong in creating log!");
    }
  }

}

