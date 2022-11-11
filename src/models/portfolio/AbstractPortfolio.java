package models.portfolio;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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

//  protected void updatePortfolio(String tickerSymbol, double quantity, LocalDate updateDate) {
//    //new
//    tickerSymbol = tickerSymbol.toUpperCase();
//    Set<Details> detailsList;
//
//    if (stocks.containsKey(tickerSymbol)) {
//      detailsList = stocks.get(tickerSymbol);
//      boolean purchaseDateExists = false;
//
//      //checking if we have purchased the stock on same date
//      for (Details details : detailsList) {
//        if (details.getPurchaseDate().equals(updateDate)) {
//          purchaseDateExists = true;
//          double updatedQty = details.getQuantity() + quantity;
//          if (quantity < 0) {
//            details.setLastSold(updateDate);
//          }
//          //if quantity becomes zero after selling then remove from the detailsList PQ
//          if (updatedQty == 0) {
//            detailsList.remove(details);
//          } else {
//            details.setQuantity(updatedQty);
////            details = new Details(updatedQty, details.getPurchaseDate());
//          }
//        }
//      }
//
//      //add new Details object to the list only if we have not purchased
//      // the stock on same date before
//      if (!purchaseDateExists) {
//        detailsList.add(new Details(quantity, updateDate));
//      }
//    } else {
//      detailsList = new TreeSet<>(
////              Comparator.comparing(Details::getPurchaseDate)
//              (a, b) -> a.getPurchaseDate().compareTo(b.getPurchaseDate())
//      );
//      detailsList.add(new Details(quantity, updateDate));
//    }
//
//    stocks.put(tickerSymbol, detailsList);
//  }

//  @Override
//  public void buy(String ticker, Details details) {
//    if (details.getQuantity() < 0.0) {
//      throw new IllegalArgumentException("Quantity should be grater than 0.");
//    }
//    this.updatePortfolio(ticker, details.getQuantity(), details.getPurchaseDate());
//  }


  @Override
  public void buy(String ticker, Details details) {
    if (details.getQuantity() < 0.0) {
      throw new IllegalArgumentException("Quantity should be grater than 0.");
    }

    //if ticker doesn't exist in the portfolio just add it
    if(!stocks.containsKey(ticker)) {
      Set<Details> detailsSet = new TreeSet<>(Comparator.comparing(Details::getPurchaseDate));
      detailsSet.add(details);
      Log log = new Log(detailsSet, null);

      stocks.put(ticker, log);
    }
    // add the existing Log of the stock
    else {
      Log log = stocks.get(ticker);
      Set<Details> detailsSet = log.getDetailsSet();

      for(Details d : detailsSet) {

        boolean haveBoughtBefore = false;
        //just add to the quantity if we've purchased stock on same date
        if(d.getPurchaseDate().compareTo(details.getPurchaseDate()) == 0) {

          haveBoughtBefore = true;
          double newQty = d.getQuantity() + details.getQuantity();
          d.setQuantity(newQty);
        }

        if(!haveBoughtBefore) {
          detailsSet.add(new Details(details.getQuantity(), details.getPurchaseDate()));
        }
      }

//      Set<Details> modifiedDetailsSet = new TreeSet<>(detailsSet);
      log.setDetailsSet(detailsSet);
    }
//    updateAllStocksAfter(ticker, details);
  }


  @Override
  public double getValue() {
    return getValue(LocalDate.now());
  }

  @Override
  public double getValue(LocalDate date) throws RuntimeException {
    double totalValue = 0.0;
    for (String tickerSymbol : stocks.keySet()) {
      Log log = stocks.get(tickerSymbol);

      Set<Details> detailsSet = log.getDetailsSet();

      for (Details d : detailsSet) {
        Map<String, Double> shareDetails = api.getShareDetails(tickerSymbol, date);
        totalValue += shareDetails.get("close") * d.getQuantity();
      }
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
          csvWriter.append(ticker).append(",")
                  .append(String.valueOf(d.getQuantity()))
                  .append(",")
                  .append(d.getPurchaseDate().toString())
                  .append("\n");
        }
      }

      csvWriter.flush();
      csvWriter.close();
      return true;
    } catch (IOException e) {
      throw new RuntimeException("Something went wrong!");
    }
  }



  private void updateAllStocksAfter(String ticker, Details details) {
    Log log = stocks.get(ticker);
    Set<Details> detailsSet = log.getDetailsSet();

    //update quantity if it was purchased after the date received in argument
    for(Details d : detailsSet) {
      if(d.getPurchaseDate().compareTo(details.getPurchaseDate()) == 1) {
        d.setQuantity(d.getQuantity() + details.getQuantity());
      }
    }

    log.setDetailsSet(detailsSet);
  }
}

