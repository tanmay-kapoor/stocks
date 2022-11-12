package models.portfolio;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import models.Details;
import models.Log;
import models.api.ShareApi;
import models.api.StockApi;

import static models.portfolio.Txn.Buy;
import static models.portfolio.Txn.Sell;

public class StockPortfolioFlexible extends AbstractPortfolio {
  /**
   * Constructor for the class that initializes the name of the portfolio,
   * date it was created and the API that it is supposed to use for the fetching relevant data.
   *
   * @param portfolioName name of the portfolio.
   * @param path
   * @param api           API is meant to be used.
   */
  public StockPortfolioFlexible(String portfolioName, String path, ShareApi api) {
    super(portfolioName, path, api);
  }

  public StockPortfolioFlexible(String portfolioName, Map<String, Log> stocks, String path,
                                ShareApi api, Map<LocalDate, Double> costBasisHistory) {
    super(portfolioName, stocks, path, api, costBasisHistory);
  }


  //can do return err msg
  protected boolean portfolioBasedSell(String ticker, Details details) {
    double sellQty = details.getQuantity();
    LocalDate sellDate = details.getPurchaseDate();

    Log log = stocks.get(ticker);

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
    storeCostBasis(ticker, details, 0.0, Sell);
    return true;
  }

  protected void storeCostBasis(String ticker, Details details, double commissionFee, Txn txn) {
    double costBasisTillNow = 0;
    for(LocalDate date : this.costBasisHistory.keySet()) {
      System.out.println("Date: " + date);
      if(details.getPurchaseDate().compareTo(date) >= 0) {
        System.out.println("Date entered is future.");
        costBasisTillNow = costBasisHistory.get(date);
      }
      else {
        break;
      }
    }
    System.out.println("Cost basis till now: " + costBasisTillNow);
    if(txn == Sell) {
      costBasisHistory.put(details.getPurchaseDate(), costBasisTillNow + commissionFee);
    }
    else if(txn == Buy) {
      ShareApi api = new StockApi();
      Map<String, Double> shareDetails = api.getShareDetails(ticker, details.getPurchaseDate());
      double price = shareDetails.get("close");
      double txnCost =  price * details.getQuantity() + commissionFee;
      costBasisHistory.put(details.getPurchaseDate(), costBasisTillNow + txnCost);
    }

    saveCostBasisLog();
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

  private void saveCostBasisLog() {
    try {
      String path_costbasis = this.path + "costbasis/";
      Files.createDirectories(Paths.get(path_costbasis));
      String fileName = String.format(path_costbasis + "%s.csv", portfolioName);
      FileWriter csvWriter = new FileWriter(fileName);
      csvWriter.append("Date, CostBasis\n");
      for (LocalDate date : costBasisHistory.keySet()) {
        csvWriter.append(date.toString()).append(",")
                .append(costBasisHistory.get(date).toString())
                .append("\n");
      }

      csvWriter.flush();
      csvWriter.close();
    } catch (IOException e) {
      throw new RuntimeException("Something went wrong in creating log!");
    }
  }

  protected Map<String, Log> getCompositionSpecificDate(LocalDate date) {
    return filterBasedOnDate(date);
  }
}
