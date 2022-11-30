package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
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
import models.TimeLine;
import models.api.ShareApi;
import models.portfolio.Dca;
import models.portfolio.Portfolio;
import models.portfolio.Report;
import models.portfolio.StockPortfolioFlexible;
import models.portfolio.Txn;
import views.Menu;

abstract class FeaturesImpl implements Features {
  protected double totalWeightage;
  protected Map<String, Double> stocksWeightage;
  protected Menu menu;
  protected final ShareApi api;
  protected final String path;
  protected final List<String> allPortfolios;
  private final Map<String, Portfolio> allPortfolioObjects;
  protected Portfolio portfolio;

  protected abstract Portfolio createPortfolioObject(String portfolioName);
  protected abstract Portfolio createPortfolioObject(String portfolioName, Map<String, Log> stocks,
                                            String path, ShareApi api,
                                            Map<LocalDate, Double> costBasisHistory,
                                            Map<String, Dca> dcaMap);

  protected abstract LocalDate getDate(String d);

  protected abstract double getCommissionFee(String commission);

  protected abstract void sellStockIfAllowed(String portfolioName, String ticker, String quant,
                                             String d, String commission);

  protected abstract Report getPortfolioPerformanceIfAllowed(String portfolioName, String f,
                                                             String t);

  protected abstract double getCostBasisIfAllowed(String portfolioName, String date);

  protected abstract void addTickerToStrategyIfAllowed(String ticker, String weightage);

  protected abstract void saveDcaIfAllowed(String portfolioName, String strategyName, String amt,
                                           String f, String t, String interval, String commission,
                                           Map<String, Double> stockWeightage);

  public FeaturesImpl(ShareApi api, String path) {
    this.api = api;
    this.path = path;

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
    menu.getMainMenuChoice();
  }

  @Override
  public void handleInflexibleSelected() {
  }

  @Override
  public void exitProgram() {
    System.exit(0);
  }

  @Override
  public void createPortfolio(String portfolioName) {
    if (allPortfolios.stream().anyMatch(portfolioName::equalsIgnoreCase)) {
      menu.errorMessage(String.format("\nPortfolio \"%s\" already exists.", portfolioName));
    } else {
      portfolio = createPortfolioObject(portfolioName);
      menu.getAddToPortfolioChoice();
    }
  }

  @Override
  public void buyStock(String portfolioName, String ticker, String quant, String d, String commission) {
    try {
      Portfolio portfolio;
      if (allPortfolios.contains(portfolioName)) {
        portfolio = findPortfolio(portfolioName);
      } else {
        portfolio = this.portfolio;
      }

      ticker = ticker.toUpperCase();
      double quantity = Double.parseDouble(quant);
      double commissionFee = getCommissionFee(commission);
      LocalDate date = getDate(d);

      if (!api.isTickerPresent(ticker)) {
        api.getShareDetails(ticker, LocalDate.now());
      }
      if (date.compareTo(LocalDate.now()) > 0) {
        menu.printMessage("Cannot buy on a future date");
      } else {
        Details details = new Details(quantity, date);
        portfolio.buy(ticker, details, commissionFee);
        portfolio.savePortfolio();
        menu.successMessage(ticker, details, Txn.Buy);
      }
    } catch (NumberFormatException e) {
      menu.errorMessage("Invalid format for 1 or more fields");
    } catch (IllegalArgumentException e) {
      menu.errorMessage("This ticker is not associated with any company");
    } catch (DateTimeParseException e) {
      menu.errorMessage("Invalid Date format");
    }
  }

  @Override
  public void sellStock(String portfolioName, String ticker, String quant, String d, String commission) {
    sellStockIfAllowed(portfolioName, ticker, quant, d, commission);
  }

  @Override
  public void savePortfolio(String portfolioName) {
    Portfolio portfolio;
    if (allPortfolios.contains(portfolioName)) {
      portfolio = findPortfolio(portfolioName);
    } else {
      portfolio = this.portfolio;
    }
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
      LocalDate d = getDate(date);
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
      menu.errorMessage("Invalid date format");
      return null;
    }
    return new HashMap<>();
  }

  @Override
  public Map<String, Double> getPortfolioWeightage(String portfolioName, String date) {
    try {
      getDate(date);
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
      menu.errorMessage("Invalid date format");
    }
    return new HashMap<>();
  }

  @Override
  public double getPortfolioValue(String portfolioName, String date) {
    try {
      Portfolio portfolio = findPortfolio(portfolioName);
      LocalDate d = getDate(date);
      if (d.compareTo(LocalDate.now()) > 0) {
        menu.printMessage("Cannot get value for future dates");
      } else {
        return portfolio.getValue(d);
      }
    } catch (DateTimeParseException e) {
      menu.errorMessage("Invalid date format");
    }
    return -1;
  }

  @Override
  public Report getPortfolioPerformance(String portfolioName, String f, String t) {
    return getPortfolioPerformanceIfAllowed(portfolioName, f, t);
  }

  @Override
  public double getCostBasis(String portfolioName, String date) {
    return getCostBasisIfAllowed(portfolioName, date);
  }

  @Override
  public void resetTotalWeightage() {
    this.totalWeightage = 100.0;
    stocksWeightage = new HashMap<>();
  }

  @Override
  public double getWeightageLeft() {
    return this.totalWeightage;
  }

  @Override
  public void addTickerToStrategy(String ticker, String weightage) {
    addTickerToStrategyIfAllowed(ticker, weightage);
  }

  @Override
  public void saveDca(String portfolioName, String strategyName, String amt, String f, String t,
                      String interval, String commission, Map<String, Double> stockWeightage) {
    saveDcaIfAllowed(portfolioName, strategyName, amt, f, t, interval, commission, stockWeightage);
  }

  protected Portfolio findPortfolio(String name) {
    Portfolio portfolio;
    if (allPortfolioObjects.containsKey(name)) {
      portfolio = allPortfolioObjects.get(name);
    } else {
      try {
        String logPath = this.path + "logs/";
        String costBasisPath = this.path + "costbasis/";
        String dcaFilePath = this.path + "dca/";
        portfolio = createPortfolioFromCsv(name,
                new File(String.format("%s%s.csv", this.path, name)),
                new File(String.format("%s%s.csv", logPath, name)),
                new File(String.format("%s%s.csv", costBasisPath, name)),
                new File(String.format("%s%s.csv", dcaFilePath, name)));
        allPortfolioObjects.put(name, portfolio);
      } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
      }
    }
    return portfolio;
  }

  private Portfolio createPortfolioFromCsv(String pName, File file, File logFile,
                                           File costBasisFile, File dcaFile)
          throws FileNotFoundException {
    //implement try catch here
    Map<String, LocalDate> lastSoldDateList = readLastSoldDateFromCsv(logFile);
    Map<String, Log> stocks = readStocksFromCsv(file, lastSoldDateList);
    Map<LocalDate, Double> costBasisHistory = readStockBasisHistoryFromCsv(costBasisFile);
    Map<String, Dca> dcaMap = readDcaFromCsv(dcaFile);

    return createPortfolioObject(pName, stocks, path, api, costBasisHistory, dcaMap);
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

  private Map<String, Dca> readDcaFromCsv(File dcaFile) throws FileNotFoundException {
    Scanner csvReader = new Scanner(dcaFile);
    csvReader.nextLine();

    Map<String, Dca> dcaMap = new HashMap<>();

    while (csvReader.hasNext()) {
      String[] vals = csvReader.nextLine().split(",");

      Map<String, Double> stockWeightage = new HashMap<>();
      TimeLine timeLine;
      if(Objects.equals(vals[3], "null")) {
        timeLine = new TimeLine(LocalDate.parse(vals[2]), null);
      } else {
        timeLine = new TimeLine(LocalDate.parse(vals[2]), LocalDate.parse(vals[3]));
      }

      dcaMap.put(vals[0], new Dca(
              Double.parseDouble(vals[1]),
              stockWeightage,
              timeLine,
              Integer.parseInt(vals[4]),
              Double.parseDouble(vals[5]),
              LocalDate.parse(vals[6])
      ));
    }

    return dcaMap;
  }


  @Override
  public void handleCreatePortfolioThroughUpload(String filePath) {
    try {
      Paths.get(filePath);
      File file = new File(filePath);
      String fileName = file.getName();
      String portfolioName = fileName.substring(0, fileName.lastIndexOf("."));
      if (allPortfolios.stream().anyMatch(portfolioName::equalsIgnoreCase)) {
        menu.errorMessage(String.format("\n\"%s\" named portfolio already exists. "
                + "Portfolio names are case insensitive! Please rename your file "
                + "and try again!", portfolioName));
      } else {
        Portfolio portfolio = createPortfolioFromCsv(portfolioName, file);
        savePortfolio(portfolioName, portfolio);
        menu.printMessage("");
      }
    } catch (InvalidPathException e) {
      menu.errorMessage("Invalid file path");
    } catch (FileNotFoundException | NullPointerException e) {
      menu.errorMessage("File not found. Select file with proper path");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Portfolio createPortfolioFromCsv(String pName, File file) throws IOException {
    //creating log file
    File logFile = createCsvFile(pName, FileType.LogFile);

    //creating costBasis file
    File costBasisFile = createCsvFile(pName, FileType.CostBasisFile);

    File dcaFile = createCsvFile(pName, FileType.DcaFile);

    return createPortfolioFromCsv(pName, file, logFile, costBasisFile, dcaFile);
  }

  private File createCsvFile(String name, FileType type) throws IOException {
    String subFolder = "";
    String header = "";

    if (type == FileType.LogFile) {
      subFolder = "logs/";
      header = "Date, lastSellDate\n";
    } else if (type == FileType.DcaFile) {
      subFolder = "dca/";
      header = "strategy_name,investment_amount,start_date,end_date,interval," +
              "commission,last_purchase_date";
    } else {
      subFolder = "costbasis/";
      header = "Date, CostBasis\n";
    }
    String creationPath = this.path + subFolder;
    Files.createDirectories(Paths.get(creationPath));
    String fileName = String.format(creationPath + "%s.csv", name);
    FileWriter csvWriter = new FileWriter(fileName);
    csvWriter.append(header);
    csvWriter.close();

    String createdFilePath = creationPath + name + ".csv";

    return new File(createdFilePath);
  }

  protected void savePortfolio(String portfolioName, Portfolio portfolio) {
    boolean saved;
    saved = portfolio.savePortfolio();
    if (saved) {
      menu.printMessage(String.format("\nSaved portfolio \"%s\"!", portfolioName));
      allPortfolios.add(portfolioName);
      allPortfolioObjects.put(portfolioName, portfolio);
    }
  }
}
