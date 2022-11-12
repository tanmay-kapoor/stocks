package models.portfolio;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.Details;
import models.Log;
import models.api.ShareApi;
import models.api.StockApi;

public class StockPortfolioFlexible extends AbstractPortfolio {
  /**
   * Constructor for the class that initializes the name of the portfolio,
   * date it was created and the API that it is supposed to use for the fetching relevant data.
   *
   * @param portfolioName name of the portfolio.
   * @param purchaseDate  creation date of the portfolio.
   * @param path
   * @param api           API is meant to be used.
   */
  public StockPortfolioFlexible(String portfolioName, LocalDate purchaseDate,
                                String path, ShareApi api) {
    super(portfolioName, purchaseDate, path, api);
  }

  public StockPortfolioFlexible(String portfolioName, LocalDate purchaseDate, Map<String, Log> stocks,
                                String path, ShareApi api) {
    super(portfolioName, purchaseDate, stocks, path, api);
  }


  //can do return err msg
  protected boolean PortfolioBasedSell(String ticker, Details details) {
    double sellQty = details.getQuantity();
    LocalDate sellDate = details.getPurchaseDate();

    Log log = stocks.get(ticker);
    System.out.println(log.getLastSoldDate());
    //this never gets executed
    if(log.getLastSoldDate() != null &&
            log.getLastSoldDate().compareTo(details.getPurchaseDate()) > 0) {
      throw new IllegalArgumentException("Please choose a time later than or equal to " + log.getLastSoldDate());
    }

    Set<Details> detailsSet = log.getDetailsSet();
    double sharesAvailable = getShareQuantityTillDate(detailsSet, sellDate);

    if (sharesAvailable < sellQty) {
      throw  new IllegalArgumentException("You cannot sell more stock than available. Current quantity: " + sharesAvailable);
    }

    List<Details> detailsToRemove = new ArrayList<>();

    for(Details d : detailsSet) {
      if(d.getPurchaseDate().compareTo(sellDate) >= 0) {
        d.setQuantity(d.getQuantity() - sellQty);

        //causes concurrent modification error
        if(d.getQuantity() == 0) {
          detailsToRemove.add(d);
        }
      }
    }

    for (Details value : detailsToRemove) {
      detailsSet.remove(value);
    }

    log.setDetailsSet(detailsSet);
    log.setLastSoldDate(sellDate);
    storeCostBasis(ticker, details, 0.0, Txn.Sell);
    return true;
  }

  protected void storeCostBasis(String ticker, Details details, double commissionFee, Txn txn) {
    double costBasisTillNow = 0;
    for(LocalDate date : this.costBasisHistory.keySet()) {
      if(details.getPurchaseDate().compareTo(date) >= 0) {
        costBasisTillNow = costBasisHistory.get(date);
      }
      else {
        break;
      }
    }

    if(txn == Txn.Sell) {
      costBasisHistory.put(details.getPurchaseDate(), costBasisTillNow + commissionFee);
    }
    else {
      ShareApi api = new StockApi();
      Map<String, Double> shareDetails = api.getShareDetails(ticker, details.getPurchaseDate());
      double price = shareDetails.get("close");
      double txnCost =  price * details.getQuantity() * commissionFee;
      costBasisHistory.put(details.getPurchaseDate(), costBasisTillNow + txnCost);
    }

  }


  private double getShareQuantityTillDate(Set<Details> detailsSet, LocalDate date) {
    double qtyAvailable = 0;

    for(Details details: detailsSet) {
      if(details.getPurchaseDate().compareTo(date) > 0) {
        break;
      }
      qtyAvailable = details.getQuantity();
    }

    return qtyAvailable;
  }

  protected void saveLastSoldLog() {
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
