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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import models.Details;
import models.api.ShareApi;
import models.portfolio.Portfolio;
import views.Menu;

/**
 * An abstract controller that implements methods that are supposed to be common between
 * two or more types of controllers across the program.
 */
abstract class AbstractController implements Controller {
  private final Menu menu;
  protected final ShareApi api;
  protected final String path;
  private final List<String> allPortfolios;
  private final Map<String, Portfolio> allPortfolioObjects;

  protected abstract Portfolio createPortfolio(String portfolioName, LocalDate dateCreated);

  protected AbstractController(Menu menu, ShareApi api, String path) {
    this.menu = menu;
    this.api = api;
    this.path = path;

    try {
      Files.createDirectories(Paths.get(this.path));
      File directory = new File(path);
      File[] files = directory.listFiles();
      this.allPortfolios = new ArrayList<>();
      this.allPortfolioObjects = new HashMap<>();

      if (files != null) {
        for (File file : files) {
          String name = file.getName();
          String extension = name.substring(name.lastIndexOf(".") + 1);
          name = name.substring(0, name.lastIndexOf("."));
          if (extension.equals("csv")) {
            allPortfolios.add(name);
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

        default:
          break;
      }
    }
    while (choice >= '1' && choice <= '3');
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
            Portfolio portfolio = createPortfolioFromCsv(portfolioName, file);
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

  private void commonStuff(Function function) {
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
            break;

          case GetValue:
            boolean shouldContinue;
            do {
              shouldContinue = false;

              char ch;
              ch = menu.getDateChoice();
              String date;
              String val;
              try {
                switch (ch) {
                  case '1':
                    date = LocalDate.now().toString();
                    val = String.valueOf(portfolio.getValue());
                    menu.printMessage(String.format("\nValue of portfolio on %s = %s",
                            date, val));
                    break;

                  case '2':
                    date = menu.getDateForValue();
                    val = String.valueOf(portfolio.getValue(LocalDate.parse(date)));
                    menu.printMessage(String.format("\nValue of portfolio on %s = %s",
                            date, val));
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
            break;

          default:
            throw new IllegalArgumentException("Illegal value");
        }
      } else {
        menu.printMessage(String.format("\n\"%s\" named portfolio does not exist.", name));
      }
    }
  }

  private String getPortfolioContents(Portfolio portfolio) {
    Map<String, Details> shareDetails = portfolio.getComposition();
    StringBuilder composition = new StringBuilder("\nshare\t\tquantity\t\tdateCreated");
    for (String share : shareDetails.keySet()) {
      Details details = shareDetails.get(share);
      composition
              .append("\n")
              .append(share)
              .append("\t\t")
              .append(details.getQuantity())
              .append("\t\t\t")
              .append(details.getDateCreated().toString());
    }
    return composition.toString();
  }

  private String getPortfolioWeightage(Portfolio portfolio) {
    Map<String, Details> shareDetails = portfolio.getComposition();
    StringBuilder composition = new StringBuilder("\nshare\t\tpercentage");

    long totalShare = 0;
    for (String share : shareDetails.keySet()) {
      Details details = shareDetails.get(share);
      totalShare += details.getQuantity();
    }

    for (String share : shareDetails.keySet()) {
      Details details = shareDetails.get(share);
      composition
              .append("\n")
              .append(share)
              .append("\t\t")
              .append((String.format("%.02f", details.getQuantity() / totalShare * 100) + "%"))
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

  private void displayAddStockStuff(Portfolio portfolio) {
    String tickerSymbol = menu.getTickerSymbol();

    try {
      api.getShareDetails(tickerSymbol, LocalDate.now());
      double quantity;
      boolean shouldExit;

      do {
        shouldExit = true;
        quantity = menu.getQuantity();
        if (quantity - Math.floor(quantity) != 0.0) {
          shouldExit = false;
          menu.printMessage("\nNumber of shares must be an integer.\n");
        }
      }
      while (!shouldExit);

      portfolio.addShare(tickerSymbol, quantity);
      menu.printMessage("\nSuccess!");
    } catch (RuntimeException e) {
      menu.printMessage("\n" + e.getMessage());
    }
  }

  private Portfolio createPortfolioFromCsv(String pName, File file) throws FileNotFoundException {
    Scanner csvReader = new Scanner(file);
    csvReader.nextLine();

    Map<String, Details> stocks = new HashMap<>();
    boolean isFirstRecord = true;
    LocalDate dateCreated = LocalDate.now();

    while (csvReader.hasNext()) {
      String[] vals = csvReader.nextLine().split(",");
      double quantity = Double.parseDouble(vals[1]);
      LocalDate dateCreatedForRecord = LocalDate.parse(vals[2]);
      Details details = new Details(quantity, dateCreatedForRecord);
      stocks.put(vals[0], details);

      if (isFirstRecord) {
        dateCreated = LocalDate.parse(vals[2]);
        isFirstRecord = false;
      }
    }
    csvReader.close();
    Portfolio p = createPortfolio(pName, dateCreated);
    for (String stock : stocks.keySet()) {
      p.addShare(stock, stocks.get(stock).getQuantity());
    }
    return p;
  }
}
