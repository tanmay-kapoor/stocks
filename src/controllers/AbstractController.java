package controllers;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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

  protected abstract Portfolio createPortfolio(String portfolioName, LocalDate dateCreated, Map<String, Double> stocks);

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

        default:
          break;
      }
    } while (choice >= '1' && choice <= '3');
  }

  private void handleCreatePortfolioChoice() throws IOException {
    boolean shouldContinue = true;

    do {
      try {
        String portfolioName = menu.getPortfolioName();

        for (String existingPortfolio : allPortfolios) {
          if (portfolioName.equalsIgnoreCase(existingPortfolio)) {
            throw new IllegalArgumentException("This portfolio already exists, " +
                    "please use a unique name.");
          }
        }
        shouldContinue = false;
        Portfolio portfolio = createPortfolio(portfolioName, LocalDate.now());
        boolean shouldExit;
        do {
          char option = menu.getCreatePortfolioChoice();
          try {
            shouldExit = this.handleCreatePortfolioOption(option, portfolio, portfolioName);
          } catch (IllegalArgumentException | IOException e) {
            menu.printMessage("\n" + e.getMessage());
            shouldExit = false;
          }
        } while (!shouldExit);
      } catch (IllegalArgumentException e) {
        menu.printMessage("\n" + e.getMessage());
      }
    } while (shouldContinue);
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

      Portfolio p = null;
      if (allPortfolioObjects.containsKey(name) || allPortfolioObjects.containsKey(name.toLowerCase()) || allPortfolioObjects.containsKey(name.toUpperCase())) {
        p = allPortfolioObjects.get(name);
      } else {
        for (String pName : allPortfolios) {
          if (name.equalsIgnoreCase(pName)) {
            Scanner csvReader = new Scanner(new File(String.format("%s%s.csv", this.path, pName)));
            Map<String, Double> stocks = new HashMap<>();
            csvReader.nextLine();
            while (csvReader.hasNext()) {
              String[] vals = csvReader.nextLine().split(",");
              stocks.put(vals[0], Double.parseDouble(vals[1]));
            }
            p = createPortfolio(pName, LocalDate.now(), stocks);
            allPortfolioObjects.put(pName, p);
            break;
          }
        }
      }

      if (p != null) {
        switch (function) {
          case Composition:
            menu.printMessage(p.getComposition());
            break;

          case GetValue:
            boolean shouldContinue;
            do {
              shouldContinue = false;

              char choice;
              do {
                choice = menu.getDateChoice();
                String date = "";
                String val = "";
                try {
                  switch (choice) {
                    case '1':
                      date = LocalDate.now().toString();
                      val = String.valueOf(p.getValue());
                      break;

                    case '2':
                      date = menu.getDateForValue();
                      val = String.valueOf(p.getValue(LocalDate.parse(date)));
                      break;

                    default:
                      menu.printMessage("\nInvalid choice");
                  }
                  menu.printMessage(String.format("\nValue of portfolio on %s = %s", date, val));
                } catch (DateTimeParseException e) {
                  shouldContinue = true;
                  menu.printMessage("\nInvalid date format");
                } catch (RuntimeException e) {
                  shouldContinue = true;
                  menu.printMessage("\n" + e.getMessage());
                }
              } while (choice != '1' && choice != '2');
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
        portfolio.savePortfolio();
        allPortfolios.add(portfolioName);
        allPortfolioObjects.put(portfolioName, portfolio);
        return true;
      } catch (RuntimeException e) {
        menu.printMessage("\n" + e.getMessage());
      }
    }
    return false;
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
}
