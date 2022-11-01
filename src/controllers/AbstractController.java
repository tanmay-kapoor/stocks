package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

abstract class AbstractController implements Controller {
  private final Menu menu;
  protected final ShareApi api;
  protected final String path;
  private final List<String> allPortfolios;
  private final Map<String, Portfolio> allPortfolioObjects;

  protected abstract Portfolio createPortfolio(String portfolioName, LocalDate dateCreated);

  protected abstract Portfolio createPortfolio(String portfolioName, LocalDate dateCreated,
                                               Map<String, Details> stocks);

  protected AbstractController(Menu menu, ShareApi api, String folder) {
    this.menu = menu;
    this.api = api;

    this.path = String.format(System.getProperty("user.dir") + "/src/files/%s/", folder);
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
  }

  @Override
  public void go() throws IOException {
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
      }
    } while (choice >= '1' && choice <= '3');
  }

  private void handleCreatePortfolioChoice() throws IOException {
    char c;
    c = menu.getCreatePortfolioThroughWhichMethod();
    switch (c) {
      case '1':
        handleCreatePortfolioThroughInterface();
        break;

      case '2':
        handleCreatePortfolioThroughUpload();
        break;
    }
  }

  private void handleCreatePortfolioThroughInterface() throws IOException {
    boolean shouldContinue;

    do {
      shouldContinue = false;
      String portfolioName = menu.getPortfolioName();

      if(allPortfolios.stream().anyMatch(portfolioName::equalsIgnoreCase)) {
        shouldContinue = true;
        menu.printMessage(String.format("\nPortfolio \"%s\" already exists. " +
                "Please use a unique name.", portfolioName));
      }

      if (!shouldContinue) {
        Portfolio portfolio = createPortfolio(portfolioName, LocalDate.now());
        boolean shouldExit;
        do {
          char option = menu.getAddToPortfolioChoice();
          try {
            shouldExit = this.handleCreatePortfolioOption(option, portfolio, portfolioName);
          } catch (IllegalArgumentException | IOException e) {
            menu.printMessage("\n" + e.getMessage());
            shouldExit = false;
          }
        } while (!shouldExit);
      }
    } while (shouldContinue);
  }

  private void handleCreatePortfolioThroughUpload() throws IOException {
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
          menu.printMessage("\nExtension should be included with the file name.");
        } else {
          String portfolioName = fileName.substring(0, dotIndex);
          String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
          if (!extension.equals("csv")) {
            menu.printMessage("\nFile should be of csv extension.");
          } else if (allPortfolios.stream().anyMatch(portfolioName::equalsIgnoreCase)) {
            menu.printMessage(String.format("\n\"%s\" named portfolio already exists." +
                    " Please rename your file and try again!", fileName));
          } else {
            shouldContinue = false;
            Portfolio portfolio = getStocksFromCsv(file);
            savePortfolio(portfolioName, portfolio);
          }
        }
      } catch (InvalidPathException e) {
        menu.printMessage("\nInvalid file path.\n");
      } catch (NullPointerException e) {
        menu.printMessage("\nFile not found.\n");
      }
    } while (shouldContinue);
  }

  private Portfolio getStocksFromCsv(File file) throws FileNotFoundException {
    String fileName = file.getName();
    String pName = fileName.substring(0, fileName.lastIndexOf("."));
    return createPortfolioFromCsv(pName, file);
  }

  private void handleExistingPortfoliosOption() throws IOException {
    commonStuff(Function.Composition);
  }

  private void handlePortfolioValueOption() throws IOException {
    commonStuff(Function.GetValue);
  }

  private void commonStuff(Function function) throws IOException {
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
        if(allPortfolios.stream().anyMatch(name::equalsIgnoreCase)) {
          portfolio = createPortfolioFromCsv(name, new File(String.format("%s%s.csv", this.path, name)));
          allPortfolioObjects.put(name, portfolio);
        }
      }

      if (portfolio != null) {
        switch (function) {
          case Composition:
            menu.printMessage(portfolio.getComposition());
            break;

          case GetValue:
            boolean shouldContinue;
            do {
              shouldContinue = false;

              char choice;
                choice = menu.getDateChoice();
                String date;
                String val;
                try {
                  switch (choice) {
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
                  }
                } catch (DateTimeParseException e) {
                  shouldContinue = true;
                  menu.printMessage("\nInvalid date format");
                } catch (RuntimeException e) {
                  shouldContinue = true;
                  menu.printMessage("\n" + e.getMessage());
                }
            } while (shouldContinue);
            break;

          default:
            throw new IllegalArgumentException("Illegal value");
        }
      } else {
        menu.printMessage(String.format("\n\"%s\" named portfolio does not exist.", name));
      }
    }
  }

  private boolean handleCreatePortfolioOption(char choice, Portfolio portfolio,
                                              String portfolioName)
          throws RuntimeException, IOException {

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

  private void savePortfolio(String portfolioName, Portfolio portfolio) throws IOException {
    boolean saved = portfolio.savePortfolio();
    if (saved) {
      menu.printMessage(String.format("\nSaved portfolio \"%s\"!", portfolioName));
      allPortfolios.add(portfolioName);
      allPortfolioObjects.put(portfolioName, portfolio);
    }
  }

  private void displayAddStockStuff(Portfolio portfolio) throws IOException {
    String tickerSymbol = menu.getTickerSymbol();

    try {
      api.getShareDetails(tickerSymbol, LocalDate.now());
      double quantity = 0.0;

      boolean shouldExit;
      do {
        shouldExit = true;
        try {
          quantity = menu.getQuantity();
          if (quantity < 0.0 || quantity - Math.floor(quantity) != 0.0) {
            throw new IllegalArgumentException("\nNumber of shares must be an integer > 0.");
          }
        } catch (IllegalArgumentException e) {
          shouldExit = false;
          menu.printMessage(e.getMessage());
        }
      } while (!shouldExit);

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
    return createPortfolio(pName, dateCreated, stocks);
  }
}
