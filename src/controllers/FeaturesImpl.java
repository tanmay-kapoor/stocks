package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import models.Details;
import models.Log;
import models.api.ShareApi;
import models.portfolio.Performance;
import models.portfolio.Portfolio;
import models.portfolio.Report;
import models.portfolio.StockPortfolioFlexible;
import models.portfolio.Txn;
import views.Menu;

import static java.lang.Math.abs;
import static java.lang.Math.round;

public class FeaturesImpl implements Features {

  private Menu menu;
  private final ShareApi api;
  private final String commonPath;
  private String path;
  private final List<String> allPortfolios;
  private final Map<String, Portfolio> allPortfolioObjects;
  private Portfolio portfolio;

  public FeaturesImpl(ShareApi api, String commonPath) {
    this.api = api;
    this.commonPath = commonPath;
    this.path = commonPath + "stocks/flexible/";

    try {
      Files.createDirectories(Paths.get(this.path));
      File directory = new File(this.path);
      File[] files = directory.listFiles();
      this.allPortfolios = new ArrayList<>();
      this.allPortfolioObjects = new HashMap<>();

      if (files != null) {
        for (File file : files) {
          if (!file.isDirectory()) {
            String name = file.getName();
            String extension = name.substring(name.lastIndexOf(".") + 1);
            name = name.substring(0, name.lastIndexOf("."));
            if (extension.equals("csv")) {
              allPortfolios.add(name);
            }
          }
        }
      }
    } catch (IOException e) {
      throw new RuntimeException("Something went wrong!");
    }
  }

  @Override
  public void setView(Menu menu) {
    this.menu = menu;
  }

  @Override
  public void handleFlexibleSelected() {
    this.path = this.commonPath + "stocks/flexible/";
    new StockControllerFlexibleGui(menu, api, path).start();
  }

  @Override
  public void handleInflexibleSelected() {
    this.path = this.commonPath + "stocks/inflexible/";
  }

  @Override
  public void exitProgram() {
    System.exit(0);
  }

  @Override
  public void createPortfolio(String portfolioName) {
    if (allPortfolios.stream().anyMatch(portfolioName::equalsIgnoreCase)) {
      menu.printMessage(String.format("\nPortfolio \"%s\" already exists.", portfolioName));
    } else {
      portfolio = new StockPortfolioFlexible(portfolioName, path, api);
//      menu.clearTextIfDisplayed();
      menu.getAddToPortfolioChoice();
//      boolean shouldExit;
//      menu.getAddToPortfolioChoice();
//      char option = getCharVal();
//      try {
//        shouldExit = this.handleCreatePortfolioOption(option, portfolio, portfolioName);
//      } catch (IllegalArgumentException e) {
//        menu.printMessage("\n" + e.getMessage());
//        shouldExit = false;
//      }
    }
  }

  @Override
  public void buyStock(String ticker, String quant, String date, String commission) {
    try {
      double quantity = Double.parseDouble(quant);
      double commissionFee = Double.parseDouble(commission);
      LocalDate purchaseDate = LocalDate.parse(date);

      if (!api.isTickerPresent(ticker)) {
        api.getShareDetails(ticker, LocalDate.now());
      }
      if (purchaseDate.compareTo(LocalDate.now()) > 0) {
        menu.printMessage("Cannot buy on a future date");
      } else {
        Details details = new Details(quantity, purchaseDate);
        portfolio.buy(ticker, details, commissionFee);
        menu.successMessage(ticker, details, Txn.Buy);
      }
    } catch (NumberFormatException e) {
      menu.printMessage("Invalid format for 1 or more fields");
    } catch (IllegalArgumentException e) {
      menu.printMessage("This ticker is not associated with any company");
    } catch (DateTimeParseException e) {
      menu.printMessage("Invalid Date format");
    }
  }

  @Override
  public void savePortfolio(String portfolioName) {
    boolean saved;
    saved = portfolio.savePortfolio();
    if (saved) {
      menu.printMessage(String.format("\nSaved portfolio \"%s\"!", portfolioName));
      allPortfolios.add(portfolioName);
      allPortfolioObjects.put(portfolioName, portfolio);
    }
  }

  @Override
  public List<String> getAllPortfolios() {
    return new ArrayList<>(allPortfolios);
  }

  @Override
  public Map<String, Double> getPortfolioContents(String portfolioName, String date) {
    Portfolio portfolio = findPortfolio(portfolioName);
    try {
      LocalDate d = LocalDate.parse(date);
      if (d.compareTo(LocalDate.now()) > 0) {
        menu.printMessage("Cannot get composition on future dates");
      } else {
        Map<String, Log> composition = portfolio.getComposition(d);
        Map<String, Double> vals = new HashMap<>();
        for (String ticker : composition.keySet()) {
          double quantity = 0.0;
          for (Details details : composition.get(ticker).getDetailsSet()) {
            quantity = details.getQuantity();
          }
          vals.put(ticker, quantity);
        }
        return vals;
      }
    } catch (DateTimeParseException e) {
      menu.printMessage("Invalid date format");
    }
    return new HashMap<>();
  }

  @Override
  public Map<String, Double> getPortfolioWeightage(String portfolioName, String date) {
    try {
      LocalDate.parse(date);
      Map<String, Double> vals = getPortfolioContents(portfolioName, date);
      double total = 0.0;
      for (String ticker : vals.keySet()) {
        total += vals.get(ticker);
      }
      for (String ticker : vals.keySet()) {
        double weightage = Math.round((vals.get(ticker) / total) * 10000) / 100.0;
        vals.put(ticker, weightage);
      }
      return vals;
    } catch (DateTimeParseException e) {
      menu.printMessage("Invalid date format");
    }
    return new HashMap<>();
  }

  @Override
  public double getPortfolioValue(String portfolioName, String date) {
    try {
      Portfolio portfolio = findPortfolio(portfolioName);
      LocalDate d = LocalDate.parse(date);
      if (d.compareTo(LocalDate.now()) > 0) {
        menu.printMessage("Cannot get value for future dates");
      } else {
        return portfolio.getValue(d);
      }
    } catch (DateTimeParseException e) {
      menu.printMessage("Invalid date format");
    }
    return -1;
  }

  @Override
  public Report getPortfolioPerformance(String portfolioName, String f, String t) {
    try {
      Portfolio portfolio = findPortfolio(portfolioName);
      LocalDate from = LocalDate.parse(f);
      LocalDate to = LocalDate.parse(t);
      if (from.compareTo(LocalDate.now()) > 0 || to.compareTo(LocalDate.now()) > 0) {
        menu.printMessage("Cannot get performance for future dates");
      } else if (from.compareTo(to) > 0) {
        menu.printMessage("Start date must be before end date");
      } else {
        Map<LocalDate, Double> performance = portfolio.getPortfolioPerformance(from, to);
        Map<LocalDate, Performance> performanceOnEachDate = new TreeMap<>();

        //scale performance
        Double min = Collections.min(performance.values());
        Double max = Collections.max(performance.values());

        int count = 0;
        double prevVal = 0.00;
        int prevStars = 0;
        double valueDiffSum = 0;

        for (LocalDate date : performance.keySet()) {
          double valueOnDate = performance.get(date);
          int scaled = (int) round(scaleBetween(valueOnDate, min, max));
          int stars = scaled == 0 ? 1 : scaled;

          if (prevStars != 0) {
            int starDiff = abs(stars - prevStars);
            if (starDiff != 0) {
              double avg_star_val = (abs(valueOnDate - prevVal) / starDiff) * stars;
              valueDiffSum += avg_star_val;
              count += stars;
            }
          }

          prevVal = valueOnDate;
          prevStars = stars;
          String precisionAdjusted = String.format("%.2f", performance.get(date));
          performanceOnEachDate.put(date, new Performance(precisionAdjusted, stars));
        }
        Double scale_val = Double.isNaN(valueDiffSum / count) ? 0 : (valueDiffSum / count);
        return new Report(performanceOnEachDate, String.format("%.02f", scale_val), String.format("%.02f", min));
      }
    } catch (DateTimeParseException e) {
      menu.printMessage("Invalid date format");
    }
    return null;
  }

  private double scaleBetween(double x, double min, double max) {
    double minAllowed = 1;
    double maxAllowed = 50;

    return (maxAllowed - minAllowed) * (x - min) / (max - min) + minAllowed;
  }

  @Override
  public double getCostBasis(String portfolioName, String date) {
    try {
      Portfolio portfolio = findPortfolio(portfolioName);
      LocalDate d = LocalDate.parse(date);
      if (d.compareTo(LocalDate.now()) > 0) {
        menu.printMessage("Cannot get value for future dates");
      } else {
        return portfolio.getCostBasis(d);
      }
    } catch (DateTimeParseException e) {
      menu.printMessage("Invalid date format");
    }
    return -1;
  }

  private Portfolio findPortfolio(String name) {
    System.out.println(name);
    Portfolio portfolio;
    if (allPortfolioObjects.containsKey(name)) {
      portfolio = allPortfolioObjects.get(name);
    } else {
      try {
        String logPath = this.path + "logs/";
        String costBasisPath = this.path + "costbasis/";
        portfolio = createPortfolioFromCsv(name,
                new File(String.format("%s%s.csv", this.path, name)),
                new File(String.format("%s%s.csv", logPath, name)),
                new File(String.format("%s%s.csv", costBasisPath, name)));
        allPortfolioObjects.put(name, portfolio);
      } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
      }
    }
    return portfolio;
  }

  private Portfolio createPortfolioFromCsv(String pName, File file, File logFile,
                                           File costBasisFile) throws FileNotFoundException {
    //implement try catch here
    Map<String, LocalDate> lastSoldDateList = readLastSoldDateFromCsv(logFile);
    Map<String, Log> stocks = readStocksFromCsv(file, lastSoldDateList);
    Map<LocalDate, Double> costBasisHistory = readStockBasisHistoryFromCsv(costBasisFile);

    return new StockPortfolioFlexible(pName, stocks, path, api, costBasisHistory);
  }

  private Map<String, LocalDate> readLastSoldDateFromCsv(File logFile)
          throws FileNotFoundException {
    Scanner csvReader = new Scanner(logFile);
    csvReader.nextLine();

    Map<String, LocalDate> lastDateSoldList = new HashMap<>();

    while (csvReader.hasNext()) {
      String[] vals = csvReader.nextLine().split(",");
      if (Objects.equals(vals[1], "null")) {
        lastDateSoldList.put(vals[0], null);
      } else {
        lastDateSoldList.put(vals[0], LocalDate.parse(vals[1]));
      }
    }
    return lastDateSoldList;
  }

  private Map<String, Log> readStocksFromCsv(File file, Map<String, LocalDate> lastSoldDateList)
          throws FileNotFoundException {
    Scanner csvReader = new Scanner(file);
    csvReader.nextLine();

    Map<String, Log> stocks = new HashMap<>();

    while (csvReader.hasNext()) {
      String[] vals = csvReader.nextLine().split(",");
      String ticker = vals[0];
      double quantity = Double.parseDouble(vals[1]);
      LocalDate purchaseDateForRecord = LocalDate.parse(vals[2]);
      Details details = new Details(quantity, purchaseDateForRecord);
      LocalDate lastSoldDate = lastSoldDateList.get(ticker);

      //refine this ....common methods outside both loops
      if (!stocks.containsKey(vals[0])) {
        Set<Details> detailsList = new TreeSet<>(Comparator.comparing(Details::getPurchaseDate));
        detailsList.add(details);
        Log log = new Log(detailsList, lastSoldDate);
        stocks.put(ticker, log);
      } else {
        Log log = stocks.get(ticker);
        Set<Details> detailsSet = log.getDetailsSet();
        detailsSet.add(details);
        log.setDetailsSet(detailsSet);
        stocks.put(ticker, log);
      }
    }
    csvReader.close();
    return stocks;
  }

  private Map<LocalDate, Double> readStockBasisHistoryFromCsv(File costBasisFile)
          throws FileNotFoundException {
    Scanner csvReader = new Scanner(costBasisFile);
    csvReader.nextLine();

    Map<LocalDate, Double> costBasisHistory = new TreeMap<>();

    while (csvReader.hasNext()) {
      String[] vals = csvReader.nextLine().split(",");
      costBasisHistory.put(LocalDate.parse(vals[0]), Double.parseDouble(vals[1]));
    }

    return costBasisHistory;
  }

  @Override
  public void handleCreatePortfolioThroughUpload() {

  }
}
