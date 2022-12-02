package models.portfolio;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import models.Details;
import models.Log;
import models.api.DateDetails;
import models.api.ShareApi;

import static models.portfolio.Txn.Buy;
import static models.portfolio.Txn.Sell;

/**
 * A class for flexible portfolio. A flexible portfolio has the ability to buy and sell shares
 * after its creation.
 */
public class StockPortfolioFlexible extends AbstractPortfolio {

  /**
   * Constructor for the class that initializes the name of the portfolio,
   * date it was created and the API that it is supposed to use for the fetching relevant data.
   *
   * @param portfolioName name of the portfolio.
   * @param path          path of where the file is stored.
   * @param api           API is meant to be used.
   */
  public StockPortfolioFlexible(String portfolioName, String path, ShareApi api) {
    super(portfolioName, path, api);
    this.dcaMap = new HashMap<>();
  }

  public StockPortfolioFlexible(String portfolioName, String path, ShareApi api,
                                Map<String, Dca> dcaMap) {
    super(portfolioName, path, api);
    this.dcaMap = dcaMap;
  }

  /**
   * A constructor that initializes the object attributes of the portfolio when it is created.
   *
   * @param portfolioName    name of portfolio.
   * @param stocks           stocks to include in the portfolio.
   * @param path             path of where the portfolio is stored.
   * @param api              API used for fetching the data.
   * @param costBasisHistory cost based history log of the portfolio.
   */
  public StockPortfolioFlexible(String portfolioName, Map<String, Log> stocks, String path,
                                ShareApi api, Map<LocalDate, Double> costBasisHistory,
                                Map<String, Dca> dcaMap) {
    super(portfolioName, stocks, path, api, costBasisHistory, dcaMap);
  }

  protected boolean portfolioBasedSell(String ticker, Details details, double commissionFee) {
    double sellQty = details.getQuantity();
    LocalDate sellDate = details.getPurchaseDate();
    Log log = stocks.get(ticker);

    if (log.getLastSoldDate() != null
            && log.getLastSoldDate().compareTo(details.getPurchaseDate()) > 0) {
      throw new IllegalArgumentException("Please choose a time later than or equal to "
              + log.getLastSoldDate());
    }

    Set<Details> detailsSet = log.getDetailsSet();

    LocalDate firstPurchaseDate = detailsSet.iterator().next().getPurchaseDate();
    if (detailsSet.size() == 0 || firstPurchaseDate.compareTo(sellDate) > 0) {
      throw new IllegalArgumentException("Cannot sell shares if they do not "
              + "exist in the portfolio yet.");
    }

    double sharesAvailable = 0;
    boolean sharesBoughtOnSellDay = false;

    for (Details d : detailsSet) {
      if (d.getPurchaseDate().compareTo(sellDate) <= 0) {
        sharesAvailable = d.getQuantity();
      } else {
        break;
      }
    }
    if (sellQty > sharesAvailable) {
      throw new IllegalArgumentException("You cannot sell more stock than available. "
              + "Current quantity: " + sharesAvailable);
    }

    for (Details d : detailsSet) {
      if (d.getPurchaseDate().compareTo(sellDate) == 0) {
        sharesBoughtOnSellDay = true;
      }
      if (d.getPurchaseDate().compareTo(sellDate) >= 0) {
        d.setQuantity(d.getQuantity() - sellQty);
      }
    }

    //doing this so that we can store interim changes in share quantity
    if (!sharesBoughtOnSellDay) {
      detailsSet.add(new Details(sharesAvailable - sellQty, sellDate));
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
    }

    double costBasisTillNow = 0;
    for (LocalDate date : this.costBasisHistory.keySet()) {

      if (details.getPurchaseDate().compareTo(date) >= 0) {
        costBasisTillNow = costBasisHistory.get(date);
      } else {
        //adding txn cost to all the future dates
        costBasisHistory.put(date, costBasisHistory.get(date) + txnCost);
      }
    }

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
      csvWriter.append("date,dostBasis\n");
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
    Map<String, Double> shareDetails =
            api.getShareDetails(ticker, details.getPurchaseDate()).getValues();
    double price = shareDetails.get("close");

    return price * details.getQuantity() + commissionFee;
  }

  protected Map<LocalDate, Double> getPortfolioPerformanceIfApplicable(LocalDate from,
                                                                       LocalDate to) {
    long days = ChronoUnit.DAYS.between(from, to);

    Map<LocalDate, Double> performance = new TreeMap<>();
    int n = 15;
    long intervals = days < n ? 1 : (days / (n - 1));

    LocalDate i;
    int total = 1;
    LocalDate lastDateTillNow = from;
    for (i = from; i.compareTo(to) <= 0; i = i.plusDays(intervals), total++) {
      lastDateTillNow = i;
      performance.put(i, getValue(i));
    }

    long day_diff = ChronoUnit.DAYS.between(i, to);
    if (day_diff < intervals / 2) {
      performance.remove(lastDateTillNow);
    }

    if (!performance.containsKey(to)) {
      performance.put(to, getValue(to));
    }

    return performance;
  }

  private List<LocalDate> getBuyDates(Dca dca) {
    LocalDate startDate = dca.getTimeLine().getStartDate();
    LocalDate endDate = dca.getTimeLine().getEndDate();
    if (endDate == null) {
      endDate = LocalDate.now();
    }

    List<LocalDate> buyOnDates = new ArrayList<>();
    for (LocalDate i = startDate; i.compareTo(endDate) <= 0; i = i.plusDays(dca.getInterval())) {
      if (i.compareTo(LocalDate.now()) <= 0) {
        buyOnDates.add(i);
      }
    }

    return buyOnDates;
  }

  private void buyOnDates(Dca dca, List<LocalDate> buyOnDates) {
    double totalAmount = dca.getTotalAmount();
    Map<String, Double> stockWeightage = dca.getStockWeightage();

    for (LocalDate date : buyOnDates) {
      for (String ticker : stockWeightage.keySet()) {
        DateDetails shareDetails = api.getShareDetails(ticker, date);

        while (shareDetails.getIsHoliday()) {
          date = date.plusDays(1);
          shareDetails = api.getShareDetails(ticker, date);
        }

        if (date.compareTo(buyOnDates.get(buyOnDates.size() - 1)) <= 0) {
          double stockPrice = shareDetails.getValues().get("close");
          double stockBudget = totalAmount / stockWeightage.get(ticker);
          double quantityToBuy = stockBudget / stockPrice;
          buy(ticker, new Details(quantityToBuy, date), dca.getCommission());
        }
      }

      dca.setLastBought(date);
    }
  }

  @Override
  protected void doDcaIfApplicable(String dcaName, Dca dca) {
    if (getDcaStrategies().containsKey(dcaName)) {
      throw new IllegalArgumentException(dcaName + " strategy already exists in this portfolio");
    }
    dcaMap.put(dcaName, dca);

    if (dca.getTotalAmount() <= 0) {
      throw new IllegalArgumentException("Investment amount must be > 0");
    }

    if (dca.getInterval() < 1) {
      throw new IllegalArgumentException("Interval must be >= 1 day");
    }

    Map<String, Double> stocksWeightage = dca.getStockWeightage();
    double w = 0.0;
    for (String ticker : stocksWeightage.keySet()) {
      double weight = stocksWeightage.get(ticker);
      if (weight <= 0.0) {
        throw new IllegalArgumentException("Weightage of each stock must be > 0");
      }
      w += weight;
    }
    if (w != 100.0) {
      throw new IllegalArgumentException("Total weightage of all stocks should be 100%");
    }

    LocalDate startDate = dca.getTimeLine().getStartDate();
    LocalDate endDate = dca.getTimeLine().getEndDate();
    if (startDate.compareTo(LocalDate.now()) > 0) {
      throw new IllegalArgumentException("Start Date cannot be in the future");
    } else if (endDate != null && startDate.compareTo(endDate) > 0) {
      throw new IllegalArgumentException("Start date must be before end date");
    }

    List<LocalDate> buyOnDates = getBuyDates(dca);
    buyOnDates(dca, buyOnDates);

    saveDcaLog();
  }

  private void saveDcaLog() {
    try {
      String pathDca = this.path + "dca/";
      Files.createDirectories(Paths.get(pathDca));
      String fileName = String.format(pathDca + "%s.csv", portfolioName);

      FileWriter csvWriter = new FileWriter(fileName);
      StringBuilder header = new StringBuilder("strategy_name,investment_amount,start_date,"
              + "end_date,interval,commission,last_purchase_date");

      for (int i = 0; i < 20; i++) {
        header.append(",stock").append(i + 1).append(",weightage").append(i + 1);
      }
      header.append("\n");

      csvWriter.append(header.toString());

      for (String strategy : dcaMap.keySet()) {
        Dca dca = dcaMap.get(strategy);
        String stockWeightageAsString = getStockWeightageAsString(dca);

        LocalDate endDate = dca.getTimeLine().getEndDate();
        csvWriter.append(strategy).append(",")
                .append(String.valueOf(dca.getTotalAmount())).append(",")
                .append(dca.getTimeLine().getStartDate().toString()).append(",")
                .append(endDate == null ? null : endDate.toString()).append(",")
                .append(String.valueOf(dca.getInterval())).append(",")
                .append(String.valueOf(dca.getCommission())).append(",")
                .append(dca.getLastBoughtDate().toString()).append(",")
                .append(stockWeightageAsString).append("\n");
      }

      csvWriter.flush();
      csvWriter.close();
      this.savePortfolio();
    } catch (IOException e) {
      throw new RuntimeException("Something went wrong in creating DCA log!");
    }
  }

  private String getStockWeightageAsString(Dca dca) {
    StringBuilder res = new StringBuilder();
    Map<String, Double> stockWeightage = dca.getStockWeightage();
    for (String ticker : stockWeightage.keySet()) {
      res.append(ticker).append(",").append(stockWeightage.get(ticker)).append(",");
    }

    return res.length() == 0 ? "" : res.substring(0, res.length() - 1);
  }

  @Override
  protected Map<String, Dca> getDcaStrategiesIfApplicable() {
    return new HashMap<>(this.dcaMap);
  }
}
