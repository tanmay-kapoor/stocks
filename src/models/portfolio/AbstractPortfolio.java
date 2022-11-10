package models.portfolio;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import models.Details;
import models.api.ShareApi;

/**
 * A class that is an extension of <code>Portfolio</code> class. This class specifically
 * deals with the portfolio that store shares in the form of stocks listed in the NASDAQ.
 */
abstract class AbstractPortfolio implements Portfolio {
  protected final String portfolioName;
  protected Map<String, Queue<Details>> stocks;
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

  protected void updatePortfolio(String tickerSymbol, double quantity, LocalDate updateDate) {
    //new
    tickerSymbol = tickerSymbol.toUpperCase();
    Queue<Details> detailsList;

    if (stocks.containsKey(tickerSymbol)) {
      detailsList = stocks.get(tickerSymbol);
      boolean purchaseDateExists = false;

      //checking if we have purchased the stock on same date
      for (Details details : detailsList) {
        if (details.getPurchaseDate() == updateDate) {
          purchaseDateExists = true;
          double updatedQty = details.getQuantity() + quantity;
          if(quantity < 0) {
            details.setLastSold(updateDate);
          }
          //if quantity becomes zero after selling then remove from the detailsList PQ
          if(updatedQty == 0) {
            detailsList.remove(details);
          }
          else {
            details = new Details(updatedQty, details.getPurchaseDate());
          }
        }
      }

      //add new Details object to the list only if we have not purchased
      // the stock on same date before
      if (!purchaseDateExists) {
        detailsList.add(new Details(quantity, updateDate));
      }
    } else {
      detailsList = new PriorityQueue<>(
              Comparator.comparing(Details::getPurchaseDate)
      );
      detailsList.add(new Details(quantity, updateDate));
    }

    stocks.put(tickerSymbol, detailsList);
  }

  public void buy(String ticker, double quantity, LocalDate purchaseDate) {
    if (quantity < 0.0) {
      throw new IllegalArgumentException("Quantity should be grater than 0.");
    }
    this.updatePortfolio(ticker, quantity, purchaseDate);
  }

  @Override
  public double getValue() {
    return getValue(LocalDate.now());
  }

  @Override
  public double getValue(LocalDate date) throws RuntimeException {
    double totalValue = 0.0;
    for (String tickerSymbol : stocks.keySet()) {
      Queue<Details> detailsList = stocks.get(tickerSymbol);

      for (Details details : detailsList) {
        Map<String, Double> shareDetails = api.getShareDetails(tickerSymbol, date);
        totalValue += shareDetails.get("close") * details.getQuantity();
      }
    }

    return totalValue;
  }

  @Override
  public Map<String, Queue<Details>> getComposition() {
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
      csvWriter.append("share,quantity,purchaseDate\n");
      for (String ticker : stocks.keySet()) {
        Queue<Details> detailsList = stocks.get(ticker);

        for (Details details : detailsList) {
          csvWriter.append(ticker).append(",")
                  .append(String.valueOf(details.getQuantity()))
                  .append(",")
                  .append(details.getPurchaseDate().toString())
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


//  public void updatePortfolio(Txn txn, String tickerSymbol, double quantity, LocalDate updateDate) {
//    if(txn == Txn.Buy) {
//
//    } else {
//
//    }
//  }

}

