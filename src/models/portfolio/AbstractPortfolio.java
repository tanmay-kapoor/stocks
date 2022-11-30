package models.portfolio;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import models.Details;
import models.Log;
import models.api.DateDetails;
import models.api.ShareApi;

/**
 * A class that is an extension of <code>Portfolio</code> class. This class specifically
 * deals with the portfolio that store shares in the form of stocks listed in the NASDAQ.
 */
abstract class AbstractPortfolio implements Portfolio {
  protected final String portfolioName;
  protected Map<String, Log> stocks;
  protected final ShareApi api;
  final String path;
  protected Map<LocalDate, Double> costBasisHistory;
  protected Map<String, Dca> dcaMap;

  protected abstract boolean portfolioBasedSell(String ticker, Details details,
                                                double commissionFee);

  protected abstract void storeCostBasis(String ticker, Details details,
                                         double commissionFee, Txn txn);

  protected abstract void saveLastSoldLog();

  protected abstract void changePurchaseDateIfApplicable(Details details);

  protected abstract double changeCommissionFeeIfApplicable(double commissionFee);

  protected abstract LocalDate getSpecificDate(LocalDate date);

  protected abstract Map<LocalDate, Double> getPortfolioPerformanceIfApplicable(LocalDate from,
                                                                                LocalDate to);

  protected abstract void doDcaIfApplicable(String dcaName, Dca dca);

  protected abstract Map<String, Dca> getDcaStrategiesIfApplicable();

  /**
   * Constructor for the class that initializes the name of the portfolio,
   * date it was created and the API that it is supposed to use for the fetching relevant data.
   *
   * @param portfolioName name of the portfolio.
   * @param api           API is meant to be used.
   */
  protected AbstractPortfolio(String portfolioName, String path, ShareApi api) {
    this.portfolioName = portfolioName;
    this.api = api;
    this.path = path;
    this.stocks = new HashMap<>();
    this.costBasisHistory = new TreeMap<>(LocalDate::compareTo);
  }

  protected AbstractPortfolio(String portfolioName, Map<String, Log> stocks, String path,
                              ShareApi api, Map<LocalDate, Double> costBasisHistory ,
                              Map<String, Dca> dcaMap) {
    this.portfolioName = portfolioName;
    this.api = api;
    this.path = path;
    this.stocks = new HashMap<>(stocks);
    this.costBasisHistory = new TreeMap<>(costBasisHistory);
    this.dcaMap = new HashMap<>(dcaMap);
  }

  @Override
  public void buy(String ticker, double quantity) {
    buy(ticker, new Details(quantity, LocalDate.now()), 0);
  }

  @Override
  public void buy(String ticker, Details details, double commissionFee) {
    if (details.getQuantity() == 0) {
      return;
    }

    if (details.getPurchaseDate().compareTo(LocalDate.now()) > 0) {
      throw new IllegalArgumentException("Cannot buy shares on future date.");
    }

    if (details.getQuantity() < 0) {
      throw new IllegalArgumentException("Quantity cannot be negative.");
    }

    if (commissionFee < 0.0) {
      throw new IllegalArgumentException("Commission Fee cannot be negative.");
    }

    commissionFee = changeCommissionFeeIfApplicable(commissionFee);
    changePurchaseDateIfApplicable(details);

    if (details.getQuantity() < 0.0) {
      throw new IllegalArgumentException("Quantity should be greater than 0.");
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

        if (d.getPurchaseDate().compareTo(details.getPurchaseDate()) < 0) {
          prevRowQty = d.getQuantity();
        } else if (d.getPurchaseDate().compareTo(details.getPurchaseDate()) == 0) {
          haveBoughtBefore = true;
          d.setQuantity(d.getQuantity() + details.getQuantity());
        } else {
          d.setQuantity(d.getQuantity() + details.getQuantity());
        }
      }

      if (!haveBoughtBefore) {
        detailsSet.add(new
                Details(details.getQuantity() + prevRowQty, details.getPurchaseDate()));
      }

      log.setDetailsSet(detailsSet);
    }
    storeCostBasis(ticker, details, commissionFee, Txn.Buy);
  }


  @Override
  public boolean sell(String ticker, Details details, double commissionFee) {
    if (details.getQuantity() < 0) {
      throw new IllegalArgumentException("Quantity cannot be negative.");
    }

    if (commissionFee < 0.0) {
      throw new IllegalArgumentException("Commission Fee cannot be negative.");
    }

    if (details.getPurchaseDate().compareTo(LocalDate.now()) > 0) {
      throw new IllegalArgumentException("Cannot sell shares on future dates.");
    }
    return portfolioBasedSell(ticker, details, commissionFee);
  }


  @Override
  public double getCostBasis() {
    return getCostBasis(LocalDate.now());
  }


  @Override
  public double getCostBasis(LocalDate dateReq) {
    if (dateReq.compareTo(LocalDate.now()) > 0) {
      throw new IllegalArgumentException("Cannot request cost basis for future dates.");
    }

    double costBasis = 0.0;
    for (LocalDate date : this.costBasisHistory.keySet()) {
      if (date.compareTo(dateReq) > 0) {
        break;
      }
      costBasis = costBasisHistory.get(date);
    }

    return costBasis;
  }


  @Override
  public double getValue() {
    return getValue(LocalDate.now());
  }

  @Override
  public double getValue(LocalDate date) throws RuntimeException {
    if (date.compareTo(LocalDate.now()) > 0) {
      throw new IllegalArgumentException("Cannot get value for a future date.");
    }

    date = getSpecificDate(date);

    double totalValue = 0.0;
    for (String tickerSymbol : stocks.keySet()) {
      Log log = stocks.get(tickerSymbol);

      Set<Details> detailsSet = log.getDetailsSet();
      double quantity = 0.0;
      for (Details d : detailsSet) {
        if (d.getPurchaseDate().compareTo(date) <= 0) {
          quantity = d.getQuantity();
        } else {
          break;
        }
      }

      if (quantity != 0.0) {
        try {
          DateDetails shareDetails = api.getShareDetails(tickerSymbol, date);
          totalValue += (shareDetails.getValues().get("close") * quantity);
        } catch (IllegalArgumentException e) {
          totalValue += 0;
        }
      }
    }

    return totalValue;
  }

  @Override
  public Map<String, Log> getComposition() {
    return getComposition(LocalDate.now());
  }

  @Override
  public Map<String, Log> getComposition(LocalDate date) {
    if (date.compareTo(LocalDate.now()) > 0) {
      throw new IllegalArgumentException("Cannot get composition of future dates.");
    }

    date = getSpecificDate(date);

    Map<String, Log> filteredStocks = new HashMap<>();
    for (String stock : stocks.keySet()) {
      Log log = stocks.get(stock);
      Set<Details> d = new TreeSet<>(Comparator.comparing(Details::getPurchaseDate));
      Details latest = null;
      for (Details details : log.getDetailsSet()) {
        if (details.getPurchaseDate().compareTo(date) <= 0) {
          latest = details;
        }
      }
      if (latest != null) {
        d.add(latest);
        Log logCopy = new Log(d, log.getLastSoldDate());
        filteredStocks.put(stock, logCopy);
      }
    }

    return filteredStocks;
  }

  @Override
  public Map<LocalDate, Double> getPortfolioPerformance(LocalDate from, LocalDate to) {
    if (to.compareTo(from) < 0) {
      throw new IllegalArgumentException("Please choose a date later than start date.");
    } else if (to.compareTo(LocalDate.now()) > 0) {
      throw new IllegalArgumentException("You cannot choose a future date. "
              + "Please choose today's or a date "
              + "from the past.");
    }

    return getPortfolioPerformanceIfApplicable(from, to);

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

  @Override
  public void doDca(String dcaName, Dca dca) {
    doDcaIfApplicable(dcaName, dca);
  }

  @Override
  public Map<String, Dca> getDcaStrategies() {
    return getDcaStrategiesIfApplicable();
  }
}

