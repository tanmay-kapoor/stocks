package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
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
import models.api.ShareApi;
import models.portfolio.Portfolio;
import models.portfolio.StockPortfolioFlexible;
import models.portfolio.Txn;
import views.Menu;

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
      menu.clearTextIfDisplayed();
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
    try {
      Portfolio portfolio = findPortfolio(portfolioName);
      LocalDate d = LocalDate.parse(date);
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
    } catch (DateTimeParseException e) {
      menu.printMessage("Invalid date format");
    }
    return null;
  }

  @Override
  public Map<String, Double> getPortfolioWeightage(String portfolioName, String date) {
    try {
      LocalDate.parse(date);
      Map<String, Double> composition = getPortfolioContents(portfolioName, date);
      double total = 0.0;
      for (String ticker : composition.keySet()) {
        total += composition.get(ticker);
      }
      for (String ticker : composition.keySet()) {
        composition.put(ticker, Math.round(composition.get(ticker) / (total + 0.0)) * 100.0);
      }
      return composition;
    } catch (DateTimeParseException e) {
      menu.printMessage("Invalid date format");
    }
    return null;
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
