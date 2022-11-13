package models.portfolio;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import models.Details;
import models.Log;
import models.api.AlphaVantage;
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

  protected boolean portfolioBasedSell(String ticker, Details details, double commissionFee) {
    double sellQty = details.getQuantity();
    LocalDate sellDate = details.getPurchaseDate();
    Log log = stocks.get(ticker);

    if (log.getLastSoldDate() != null &&
            log.getLastSoldDate().compareTo(details.getPurchaseDate()) > 0) {
      throw new IllegalArgumentException
              ("Please choose a time later than or equal to " + log.getLastSoldDate());
    }

    Set<Details> detailsSet = log.getDetailsSet();
    double sharesAvailable = 0;
    boolean sharesBoughtOnSellDay = false;

    for (Details d : detailsSet) {
      if (d.getPurchaseDate().compareTo(sellDate) < 0) {
        sharesAvailable = d.getQuantity();
      } else {
        if (sharesAvailable < 0) {
          throw new IllegalArgumentException("You cannot sell more stock than available. "
                  + "Current quantity: " + sharesAvailable);
        }

        if (d.getPurchaseDate().compareTo(sellDate) == 0) {
          sharesBoughtOnSellDay = true;
        }
        d.setQuantity(d.getQuantity() - sellQty);
      }
    }

    //doing this so that we can store interim changes in share quantity
    if (!sharesBoughtOnSellDay) {
      detailsSet.add(new Details(sharesAvailable - details.getQuantity(),
              details.getPurchaseDate()));
    }

    log.setDetailsSet(detailsSet);
    log.setLastSoldDate(sellDate);
    storeCostBasis(ticker, details, commissionFee, Sell);
    return true;
  }

  protected void storeCostBasis(String ticker, Details details, double commissionFee, Txn txn) {
    double txnCost = 0;
    if (txn == Sell) {
      txnCost = commissionFee;
    } else if (txn == Buy) {
      txnCost = getTxnCost(ticker, details, commissionFee);
    } else {
      // here if other txn type, just adding for special sell cases
    }

    double costBasisTillNow = 0;
    for (LocalDate date : this.costBasisHistory.keySet()) {
      System.out.println("Date: " + date);
      if (details.getPurchaseDate().compareTo(date) >= 0) {
        System.out.println("Date entered is future.");
        costBasisTillNow = costBasisHistory.get(date);
      } else {
        //adding txn cost to all the future dates
        costBasisHistory.put(date, costBasisHistory.get(date) + txnCost);
      }
    }
    System.out.println("Cost basis till now: " + costBasisTillNow);
    costBasisHistory.put(details.getPurchaseDate(), costBasisTillNow + txnCost);
    saveCostBasisLog();
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

  protected void changePurchaseDateIfApplicable(Details details) {
    return;
  }

  protected double changeCommissionFeeIfApplicable(double commissionFee) {
    return commissionFee;
  }

  protected LocalDate getSpecificDate(LocalDate date) {
    return date;
  }

  private void saveCostBasisLog() {
    try {
      String pathCostBasis = this.path + "costbasis/";
      Files.createDirectories(Paths.get(pathCostBasis));
      String fileName = String.format(pathCostBasis + "%s.csv", portfolioName);
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

  private double getTxnCost(String ticker, Details details, double commissionFee) {
    Map<String, Double> shareDetails = api.getShareDetails(ticker, details.getPurchaseDate());
    double price = shareDetails.get("close");
    System.out.println(price);
    return price * details.getQuantity() + commissionFee;
  }
}
