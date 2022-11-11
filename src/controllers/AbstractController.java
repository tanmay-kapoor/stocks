package controllers;

import java.io.File;
import java.io.FileNotFoundException;
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
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import models.Details;
import models.Log;
import models.api.ShareApi;
import models.portfolio.Portfolio;
import views.Menu;

/**
 * An abstract controller that implements methods that are supposed to be common between
 * two or more types of controllers across the program.
 */
abstract class AbstractController implements SpecificController {
  protected final Menu menu;
  protected final ShareApi api;
  protected final String path;
  protected final List<String> allPortfolios;
  protected final Map<String, Portfolio> allPortfolioObjects;

  protected abstract Portfolio createPortfolio(String portfolioName, LocalDate purchaseDate);

  protected abstract Portfolio createPortfolio(String portfolioName, LocalDate purchaseDate, Map<String, Log> stocks);

  protected abstract LocalDate getPurchaseDate();

  protected abstract void handleBuySellOption();

  protected abstract char getLastOption();

  protected abstract void handleBuySellInPortfolio(String name);

  protected AbstractController(Menu menu, ShareApi api, String path) {
    this.menu = menu;
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
  public void start() {
    char choice;

    do {
      choice = menu.getMainMenuChoice();

      switch (choice) {
        case '1':
          handleCreatePortfolioChoice();
          break;

        case '2':
          handleExistingPortfoliosOption();
          break;

        case '3':
          handlePortfolioValueOption();
          break;

        case '4':
          handleBuySellOption();
          break;

        default:
          break;
      }
    }
    while (choice >= '1' && choice <= getLastOption());
  }

  private void handleCreatePortfolioChoice() {
    char c;
    c = menu.getCreatePortfolioThroughWhichMethod();
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

  private void handleCreatePortfolioThroughInterface() {
    boolean shouldContinue;

    do {
      shouldContinue = false;
      String portfolioName = menu.getPortfolioName();

      if (allPortfolios.stream().anyMatch(portfolioName::equalsIgnoreCase)) {
        shouldContinue = true;
        menu.printMessage(String.format("\nPortfolio \"%s\" already exists. Portfolio names are "
                + "case insensitive! Please use a unique name.", portfolioName));
      }

      if (!shouldContinue) {
        Portfolio portfolio = createPortfolio(portfolioName, LocalDate.now());
        boolean shouldExit;
        do {
          char option = menu.getAddToPortfolioChoice();
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

  private void handleCreatePortfolioThroughUpload() {
    boolean shouldContinue;
    do {
      shouldContinue = true;

      String filePath = menu.getFilePath();
      try {
        Paths.get(filePath);
        File file = new File(filePath);
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex == -1) {
          menu.printMessage("\nInvalid file. Please use a csv file");
        } else {
          String portfolioName = fileName.substring(0, dotIndex);
          String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
          if (!extension.equals("csv")) {
            menu.printMessage("\nInvalid file. Please use a csv file.");
          } else if (allPortfolios.stream().anyMatch(portfolioName::equalsIgnoreCase)) {
            menu.printMessage(String.format("\n\"%s\" named portfolio already exists. "
                    + "Portfolio names are case insensitive! Please rename your file "
                    + "and try again!", portfolioName));
          } else {
            //need to change this 3rd argument later
            Portfolio portfolio = createPortfolioFromCsv(portfolioName, file, file);
            savePortfolio(portfolioName, portfolio);
            shouldContinue = false;
          }
        }
      } catch (InvalidPathException e) {
        menu.printMessage("\nInvalid file path.");
      } catch (FileNotFoundException | NullPointerException e) {
        menu.printMessage("\nFile not found. Please enter file with proper path.");
      }
    }
    while (shouldContinue);
  }

  private void handleExistingPortfoliosOption() {
    commonStuff(Function.Composition);
  }

  private void handlePortfolioValueOption() {
    commonStuff(Function.GetValue);
  }

  protected void commonStuff(Function function) {
    if (allPortfolios.size() == 0) {
      menu.printMessage("\nNo existing portfolios.");
    } else {
      StringBuilder portfolioNames = new StringBuilder("\nAll existing portfolios :");
      for (String existingPortfolio : allPortfolios) {
        portfolioNames.append("\n").append(existingPortfolio);
      }
      menu.printMessage(portfolioNames.toString());
      String name = menu.getPortfolioName();

      Portfolio portfolio = null;
      if (allPortfolioObjects.containsKey(name)
              || allPortfolioObjects.containsKey(name.toLowerCase())
              || allPortfolioObjects.containsKey(name.toUpperCase())) {
        portfolio = allPortfolioObjects.get(name);
      } else {
        if (allPortfolios.stream().anyMatch(name::equalsIgnoreCase)) {
          try {
            portfolio = createPortfolioFromCsv(name,
                    new File(String.format("%s%s.csv", this.path, name)),
                    new File(String.format("%s%s.csv", this.path, name)));
            allPortfolioObjects.put(name, portfolio);
          } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
          }
        }
      }

      if (portfolio != null) {
        switch (function) {
          case Composition:
            handleGetPortfolioComposition(portfolio);
            break;

          case GetValue:
            handleGetPortfolioValue(portfolio);
            break;

          case BuySell:
            handleBuySellInPortfolio(name);
            break;

          default:
            throw new IllegalArgumentException("Illegal value");
        }
      } else {
        menu.printMessage(String.format("\n\"%s\" named portfolio does not exist.", name));
      }
    }
  }

  private void handleGetPortfolioComposition(Portfolio portfolio) {
    char choice = menu.getPortfolioCompositionOption();
    switch (choice) {
      case '1':
        menu.printMessage(getPortfolioContents(portfolio));
        break;

      case '2':
        menu.printMessage(getPortfolioWeightage(portfolio));
        break;

      default:
        break;
    }
  }

  private void handleGetPortfolioValue(Portfolio portfolio) {
    boolean shouldContinue;
    do {
      shouldContinue = false;

      char ch;
      ch = menu.getDateChoice();
      String date;
      double val;
      try {
        switch (ch) {
          case '1':
            date = LocalDate.now().toString();
            val = portfolio.getValue();
            menu.printMessage(String.format("\nValue of portfolio on %s = $%.2f", date, val));
            break;

          case '2':
            date = menu.getDateForValue();
            val = portfolio.getValue(LocalDate.parse(date));
            menu.printMessage(String.format("\nValue of portfolio on %s = %.2f", date, val));
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

  protected Details getDetails() {
    boolean isValid;
    double quantity;
    do {
      quantity = menu.getQuantity();
      isValid = this.validateQuantity(quantity);
    } while (!isValid);

    LocalDate purchaseDate;
    do {
      isValid = true;
      purchaseDate = LocalDate.now();
      try {
        purchaseDate = LocalDate.parse(menu.getDateForValue());
      } catch (DateTimeParseException e) {
        isValid = false;
        menu.printMessage("Invalid date format\n");
      }
    } while (!isValid);
    return new Details(quantity, purchaseDate);
  }

  private String getPortfolioContents(Portfolio portfolio) {
    Map<String, Log> portfolioContent = portfolio.getComposition(getPurchaseDate());
    StringBuilder composition = new StringBuilder("\nshare\t\tquantity\t\tpurchaseDate");

    for (String ticker : portfolioContent.keySet()) {
      Log log = portfolioContent.get(ticker);
      Set<Details> detailsSet = log.getDetailsSet();
      int quantity = 0;
      for (Details details : detailsSet) {
        quantity += details.getQuantity();
      }

      composition
              .append("\n")
              .append(ticker)
              .append("\t\t")
              .append(quantity)
              .append("\t\t\t")
              .append("IDK WHAT DATE TO PUT HERE!!");
    }
    return composition.toString();
  }

  private String getPortfolioWeightage(Portfolio portfolio) {
    Map<String, Log> portfolioContent = portfolio.getComposition(getPurchaseDate());

    StringBuilder composition = new StringBuilder("\nshare\t\tpercentage");
    Map<String, Double> shareQuantity = new HashMap<>();

    long totalShare = 0;
    for (String ticker : portfolioContent.keySet()) {
      Log log = portfolioContent.get(ticker);
      Set<Details> detailsSet = log.getDetailsSet();
      double tickerQuantity = 0;

      for (Details details : detailsSet) {
        double n = details.getQuantity();
        totalShare += n;
        tickerQuantity += n;
      }
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
    return composition.toString();
  }

  private boolean handleCreatePortfolioOption(char choice, Portfolio portfolio,
                                              String portfolioName) {

    if (choice == '1') {
      displayAddStockStuff(portfolio);
    } else {
      try {
        savePortfolio(portfolioName, portfolio);
        return true;
      } catch (RuntimeException e) {
        menu.printMessage("\n" + e.getMessage());
      }
    }
    return false;
  }

  private void savePortfolio(String portfolioName, Portfolio portfolio) {
    boolean saved;
    saved = portfolio.savePortfolio();
    if (saved) {
      menu.printMessage(String.format("\nSaved portfolio \"%s\"!", portfolioName));
      allPortfolios.add(portfolioName);
      allPortfolioObjects.put(portfolioName, portfolio);
    }
  }

  private boolean validateQuantity(double quantity) {
    if (quantity < 0 || (quantity - Math.floor(quantity) != 0.0)) {
      menu.printMessage("\nNumber of shares must be an integer > 0.\n");
      return false;
    }
    return true;
  }

  private void displayAddStockStuff(Portfolio portfolio) {
    String tickerSymbol = menu.getTickerSymbol();

    try {
      api.getShareDetails(tickerSymbol, LocalDate.now());
      double quantity;
      boolean shouldExit;

      do {
        quantity = menu.getQuantity();
        shouldExit = this.validateQuantity(quantity);
      }
      while (!shouldExit);

      do {
        shouldExit = true;
        try {
          LocalDate purchaseDate = getPurchaseDate();
          Details details = new Details(quantity, purchaseDate);
          portfolio.buy(tickerSymbol, details);
          menu.printMessage("\nSuccess!");
        } catch (DateTimeParseException e) {
          shouldExit = false;
          menu.printMessage("\nInvalid date format");
        }
      } while (!shouldExit);
    } catch (RuntimeException e) {
      menu.printMessage("\n" + e.getMessage());
    }
  }

  private Portfolio createPortfolioFromCsv(String pName, File file, File logFile) throws FileNotFoundException {
    Scanner csvReader = new Scanner(file);
    csvReader.nextLine();

    Map<String, Log> stocks = new HashMap<>();
    boolean isFirstRecord = true;
    LocalDate purchaseDate = LocalDate.now();

    while (csvReader.hasNext()) {
      String[] vals = csvReader.nextLine().split(",");
      String ticker = vals[0];
      double quantity = Double.parseDouble(vals[1]);
      LocalDate purchaseDateForRecord = LocalDate.parse(vals[2]);
      Details details = new Details(quantity, purchaseDateForRecord);

      //need to fetch this from the log file
      LocalDate lastDateSold = null;
      if (!stocks.containsKey(vals[0])) {
        Set<Details> detailsList = new TreeSet<>(Comparator.comparing(Details::getPurchaseDate));
        detailsList.add(details);
        Log log = new Log(detailsList, lastDateSold);
        stocks.put(ticker, log);
//        stocks.put(vals[0], detailsList);
      } else {
        Log log = stocks.get(ticker);
        Set<Details> detailsSet = log.getDetailsSet();
        detailsSet.add(details);
        log.setDetailsSet(detailsSet);
        stocks.put(ticker, log);
      }

      if (isFirstRecord) {
        purchaseDate = LocalDate.parse(vals[2]);
        isFirstRecord = false;
      }
    }
    csvReader.close();
    return createPortfolio(pName, purchaseDate, stocks);
  }
}
