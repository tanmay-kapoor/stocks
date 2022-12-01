package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import models.Details;
import models.Log;
import models.api.ShareApi;
import models.portfolio.Composition;
import models.portfolio.Dca;
import models.portfolio.Portfolio;
import models.portfolio.Txn;
import views.Menu;

/**
 * An abstract controller that implements methods that are supposed to be common between
 * two or more types of controllers across the program.
 */
abstract class AbstractController implements SpecificController {
  protected Scanner sc;

  protected final Menu menu;
  protected final ShareApi api;
  protected final String path;
  protected final List<String> allPortfolios;
  protected final Map<String, Portfolio> allPortfolioObjects;

  protected abstract Portfolio createPortfolio(String portfolioName);

  protected abstract Portfolio createPortfolio(String portfolioName,
                                               Map<String, Log> stocks,
                                               Map<LocalDate, Double> costBasisHistory,
                                               Map<String, Dca> dcaMap);

  protected abstract Map<String, LocalDate> readLastSoldDateFromCsv(File logFile)
          throws FileNotFoundException;

  protected abstract Map<LocalDate, Double> readStockBasisHistoryFromCsv(File costBasisFile)
          throws FileNotFoundException;

  protected abstract Map<String, Dca> readDcaFromCsv(File dcaFile) throws FileNotFoundException;

  protected abstract LocalDate getPurchaseDate();

  protected abstract char getLastOption();

  protected abstract double getCommissionFee();

  protected abstract void filterBasedOnFunction(Function function);

  protected abstract void handleMenuOptions(Portfolio portfolio, Function function);

  protected abstract boolean giveDateOptionsIfApplicable(Portfolio portfolio, Composition option);

  protected abstract boolean handleCreatePortfolioOption(char choice, Portfolio portfolio,
                                                         String portfolioName);

  protected AbstractController(InputStream in, Menu menu, ShareApi api, String path) {
    this.menu = menu;
    this.api = api;
    this.path = path;
    sc = new Scanner(in);

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
  public void start() {
    char choice;
    do {
      menu.getMainMenuChoice();
      choice = getCharVal();

      switch (choice) {
        case '1':
          handleCreatePortfolioChoice();
          break;

        case '2':
          filterBasedOnFunction(Function.Composition);
          break;

        case '3':
          filterBasedOnFunction(Function.GetValue);
          break;

        case '4':
          filterBasedOnFunction(Function.BuySell);
          break;

        case '5':
          filterBasedOnFunction(Function.SeePerformance);
          break;

        case '6':
          filterBasedOnFunction(Function.CostBasis);
          break;

        default:
          break;
      }
    }
    while (choice >= '1' && choice <= getLastOption());
  }

  protected void handleCreatePortfolioChoice() {
    menu.getCreatePortfolioThroughWhichMethod();
    char c = getCharVal();
    switch (c) {
      case '1':
        handleCreatePortfolioThroughInterface();
        break;

      case '2':
        handleCreatePortfolioThroughUpload();
        break;

      default:
        break;
    }
  }

  protected void handleCreatePortfolioThroughInterface() {
    boolean shouldContinue;

    do {
      shouldContinue = false;
      menu.getPortfolioName();
      String portfolioName = getSentenceVal();

      if (allPortfolios.stream().anyMatch(portfolioName::equalsIgnoreCase)) {
        shouldContinue = true;
        menu.errorMessage(String.format("\nPortfolio \"%s\" already exists. Portfolio names are "
                + "case insensitive! Please use a unique name.", portfolioName));
      }

      if (!shouldContinue) {
        Portfolio portfolio = createPortfolio(portfolioName);
        boolean shouldExit;
        do {
          menu.getAddToPortfolioChoice();
          char option = getCharVal();
          try {
            shouldExit = this.handleCreatePortfolioOption(option, portfolio, portfolioName);
          } catch (IllegalArgumentException e) {
            menu.printMessage("\n" + e.getMessage());
            shouldExit = false;
          }
        }
        while (!shouldExit);
      }
    }
    while (shouldContinue);
  }

  protected void handleCreatePortfolioThroughUpload() {
    boolean shouldContinue;
    do {
      shouldContinue = true;

      menu.getFilePath();
      String filePath = getSentenceVal();
      try {
        Paths.get(filePath);
        File file = new File(filePath);
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex == -1) {
          menu.printMessage("\nInvalid file. Please use a csv file");
        } else {
          //can use split here
          String portfolioName = fileName.substring(0, dotIndex);

          //last index already stored in variable
          String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
          if (!extension.equals("csv")) {
            menu.printMessage("\nInvalid file. Please use a csv file.");
          } else if (allPortfolios.stream().anyMatch(portfolioName::equalsIgnoreCase)) {
            menu.errorMessage(String.format("\n\"%s\" named portfolio already exists. "
                    + "Portfolio names are case insensitive! Please rename your file "
                    + "and try again!", portfolioName));
          } else {
            //need to pass correct files in 3rd and 4th argument.
            Portfolio portfolio = createPortfolioFromCsv(portfolioName, file);
            savePortfolio(portfolioName, portfolio);
            shouldContinue = false;
          }
        }
      } catch (InvalidPathException e) {
        menu.printMessage("\nInvalid file path.");
      } catch (FileNotFoundException | NullPointerException e) {
        menu.printMessage("\nFile not found. Please enter file with proper path.");
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    while (shouldContinue);
  }

  protected void commonStuff(Function function) {
    if (allPortfolios.size() == 0) {
      menu.printMessage("\nNo existing portfolios.");
    } else {
      Portfolio portfolio = findPortfolio();
      if (portfolio != null) {
        handleMenuOptions(portfolio, function);
      }
    }
  }

  protected Portfolio findPortfolio() {
    StringBuilder portfolioNames = new StringBuilder("\nAll existing portfolios :");
    for (String existingPortfolio : allPortfolios) {
      portfolioNames.append("\n").append(existingPortfolio);
    }
    menu.printMessage(portfolioNames.toString());
    menu.getPortfolioName();
    String name = getSentenceVal();

    Portfolio portfolio = null;
    if (allPortfolioObjects.containsKey(name)
            || allPortfolioObjects.containsKey(name.toLowerCase())
            || allPortfolioObjects.containsKey(name.toUpperCase())) {
      portfolio = allPortfolioObjects.get(name);
    } else {
      if (allPortfolios.stream().anyMatch(name::equalsIgnoreCase)) {
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
    }

    if (portfolio == null) {
      menu.printMessage(String.format("\n\"%s\" named portfolio does not exist.", name));
    }
    return portfolio;
  }

  protected void handleGetPortfolioComposition(Portfolio portfolio) {
    boolean didPerform;

    do {
      menu.getPortfolioCompositionOption();
      char choice = getCharVal();
      switch (choice) {
        case '1':
          didPerform = giveDateOptionsIfApplicable(portfolio, Composition.Contents);
          break;

        case '2':
          didPerform = giveDateOptionsIfApplicable(portfolio, Composition.Weightage);
          break;

        default:
          didPerform = true;
          break;
      }
    }
    while (!didPerform);
  }

  protected void handleGetPortfolioValue(Portfolio portfolio) {
    boolean shouldContinue;
    do {
      shouldContinue = false;

      menu.getDateChoice();
      char ch = getCharVal();
      LocalDate date;
      double val;
      try {
        switch (ch) {
          case '1':
            date = LocalDate.now();
            val = portfolio.getValue();
            menu.printMessage(String.format("\nValue of portfolio on %s = $%.2f", date, val));
            break;

          case '2':
            menu.getDateForValue();
            date = LocalDate.parse(getWordVal());
            val = portfolio.getValue(date);
            menu.printMessage(String.format("\nValue of portfolio on %s = $%.2f", date, val));
            break;

          default:
            break;
        }
      } catch (DateTimeParseException e) {
        shouldContinue = true;
        menu.printMessage("\nInvalid date format");
      } catch (RuntimeException e) {
        shouldContinue = true;
        menu.printMessage("\n" + e.getMessage());
      }
    }
    while (shouldContinue);
  }

  protected void getCompositionForToday(Portfolio portfolio, Composition option) {
    switch (option) {
      case Contents:
        menu.printMessage(getPortfolioContents(portfolio, LocalDate.now()));
        break;

      case Weightage:
        menu.printMessage(getPortfolioWeightage(portfolio, LocalDate.now()));
        break;

      default:
        break;
    }
  }

  protected String getPortfolioContents(Portfolio portfolio, LocalDate date) {
    Map<String, Log> portfolioContent = portfolio.getComposition(date);
    StringBuilder composition = new StringBuilder();

    if (portfolioContent.isEmpty()) {
      composition.append("\nNo stocks existed on ").append(date);
    } else {
      composition.append("\nshare\t\tquantity");
      for (String ticker : portfolioContent.keySet()) {
        Log log = portfolioContent.get(ticker);
        Set<Details> detailsSet = log.getDetailsSet();
        double quantity = 0.0;
        for (Details details : detailsSet) {
          quantity = details.getQuantity();
        }

        composition
                .append("\n")
                .append(ticker)
                .append("\t\t")
                .append(quantity);
      }
    }

    return composition.toString();
  }

  protected String getPortfolioWeightage(Portfolio portfolio, LocalDate date) {
    Map<String, Log> portfolioContent = portfolio.getComposition(date);
    StringBuilder composition = new StringBuilder();

    if (portfolioContent.isEmpty()) {
      composition.append("\nNo stocks existed on ").append(date);
    } else {
      composition.append("\nshare\t\tpercentage");

      Map<String, Double> shareQuantity = new HashMap<>();
      double totalShare = 0.0;
      for (String ticker : portfolioContent.keySet()) {
        Log log = portfolioContent.get(ticker);
        Set<Details> detailsSet = log.getDetailsSet();

        double tickerQuantity = 0.0;
        for (Details details : detailsSet) {
          tickerQuantity = details.getQuantity();
        }
        totalShare += tickerQuantity;
        shareQuantity.put(ticker, tickerQuantity);
      }

      for (String ticker : shareQuantity.keySet()) {
        double qty = shareQuantity.get(ticker);

        composition
                .append("\n")
                .append(ticker)
                .append("\t\t").append(String.format("%.02f", qty / totalShare * 100)).append("%")
                .append("\t\t\t");
      }
    }

    return composition.toString();
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

  protected boolean validateQuantity(double quantity) {
    if (quantity - Math.floor(quantity) != 0.0) {
      menu.printMessage("\nNumber of shares must be an integer > 0.\n");
      return false;
    }
    return true;
  }

  protected void displayAddStockStuff(Portfolio portfolio) {
    menu.getTickerSymbol();
    String tickerSymbol = getWordVal().toUpperCase();

    try {
      api.getShareDetails(tickerSymbol, LocalDate.now());
      double quantity;
      boolean shouldExit;

      do {
        menu.getQuantity();
        quantity = getDoubleVal();
        shouldExit = this.validateQuantity(quantity);
      }
      while (!shouldExit);

      do {
        shouldExit = true;
        try {
          LocalDate purchaseDate = getPurchaseDate();
          Details details = new Details(quantity, purchaseDate);
          api.getShareDetails(tickerSymbol, purchaseDate);
          portfolio.buy(tickerSymbol, details, getCommissionFee());
          menu.successMessage(tickerSymbol, details, Txn.Buy);
        } catch (DateTimeParseException e) {
          shouldExit = false;
          menu.printMessage("\nInvalid date format");
        }
      }
      while (!shouldExit);
    } catch (RuntimeException e) {
      menu.printMessage("\n" + e.getMessage());
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

  private Portfolio createPortfolioFromCsv(String pName,
                                           File file, File logFile, File costBasisFile,
                                           File dcaFile) throws FileNotFoundException {
    //implement try catch here
    Map<String, LocalDate> lastSoldDateList = readLastSoldDateFromCsv(logFile);
    Map<String, Log> stocks = readStocksFromCsv(file, lastSoldDateList);
    Map<LocalDate, Double> costBasisHistory = readStockBasisHistoryFromCsv(costBasisFile);
    Map<String, Dca> dcaMap = readDcaFromCsv(dcaFile);

    return createPortfolio(pName, stocks, costBasisHistory, dcaMap);
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

  private File createCsvFile(String name, FileType type) throws IOException {
    String subFolder = "";
    String header = "";

    if (type == FileType.LogFile) {
      subFolder = "logs/";
      header = "date,lastSellDate\n";
    } else if (type == FileType.DcaFile) {
      subFolder = "dca/";
      StringBuilder str = new StringBuilder("strategy_name,investment_amount,start_date,end_date")
              .append(",interval,commission,last_purchase_date");
      for(int i = 0; i<20; i++) {
        str.append(",stock").append(i+1).append(",weightage").append(i+1);
      }
      str.append("\n");
      header = str.toString();
    } else {
      subFolder = "costbasis/";
      header = "date,costBasis\n";
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

  protected char getCharVal() {
    char c = sc.next().charAt(0);
    sc.nextLine();
    return c;
  }

  protected String getSentenceVal() {
    return sc.nextLine();
  }

  protected String getWordVal() {
    String val = sc.next();
    sc.nextLine();
    return val;
  }

  protected double getDoubleVal() {
    double quantity = sc.nextDouble();
    sc.nextLine();
    return quantity;
  }

  protected int getIntVal() {
    int val = sc.nextInt();
    sc.nextLine();
    return val;
  }
}
