package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.api.ShareApi;
import models.portfolio.Portfolio;
import models.portfolio.StockPortfolio;
import views.Menu;

abstract class AbstractController implements Controller {
  private final Menu menu;
  private Portfolio portfolio;
  private String portfolioName;
  private final ShareApi api;
  private final List<String> allPortfolios;
  private final Map<String, Portfolio> allPortfolioObjects;

  protected AbstractController(Menu menu, ShareApi api, String folder) throws IOException, ClassNotFoundException {
    this.menu = menu;
    this.api = api;

    String path = String.format(System.getProperty("user.dir") + "/src/files/%s/", folder);
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
        } else {
          FileInputStream fi = new FileInputStream(file);
          ObjectInputStream oi = new ObjectInputStream(fi);
          allPortfolioObjects.put(name, (Portfolio) oi.readObject());
          oi.close();
          fi.close();
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
    } while (choice == '1' || choice == '2' || choice == '3');
  }

  private void handleCreatePortfolioChoice() {
    boolean shouldContinue = true;

    do {
      try {
        this.portfolioName = menu.getPortfolioName();

        for (String existingPortfolio : allPortfolios) {
          if (this.portfolioName.equalsIgnoreCase(existingPortfolio)) {
            throw new IllegalArgumentException("This portfolio already exists, " +
                    "please use a unique name.");
          }
        }
        shouldContinue = false;
        portfolio = new StockPortfolio(this.portfolioName, api);
        boolean shouldExit;
        do {
          char option = menu.getCreatePortfolioChoice();
          try {
            shouldExit = this.handleCreatePortfolioOption(option);
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

  public void commonStuff(Function function) throws IOException {
    if (allPortfolios.size() == 0) {
      menu.printMessage("\nNo existing portfolios.");
    } else {
      StringBuilder portfolioNames = new StringBuilder("\nAll existing portfolios :");
      for (String existingPortfolio : allPortfolios) {
        portfolioNames.append("\n").append(existingPortfolio);
      }
      menu.printMessage(portfolioNames.toString());
      String name = menu.getPortfolioName();

      for (String pName : allPortfolioObjects.keySet()) {
        if (name.equalsIgnoreCase(pName)) {
          Portfolio p = allPortfolioObjects.get(pName);
          switch (function) {
            case Composition:
              menu.printMessage(p.getComposition());
              break;

            case GetValue:
              String date = menu.getDateForCheckValue();
              String value;
              if (date.equals("today")) {
                value = String.valueOf(p.getValue());
              } else {
                value = String.valueOf(p.getValue(date));
              }
              menu.printMessage(value);
              break;

            default:
              throw new IllegalArgumentException("Illegal value");
          }
          return;
        }
      }

      menu.printMessage(String.format("\n\"%s\" named portfolio does not exist.", name));
    }
  }

  private boolean handleCreatePortfolioOption(char choice) throws RuntimeException, IOException {
    if (choice == '1') {
      displayAddStockStuff(portfolio);
    } else {
      try {
        portfolio.savePortfolio();
        allPortfolios.add(this.portfolioName);
        allPortfolioObjects.put(this.portfolioName, this.portfolio);
        return true;
      } catch (RuntimeException e) {
        menu.printMessage("\n" + e.getMessage());
      }
    }
    return false;
  }

  private void displayAddStockStuff(Portfolio portfolio) {
    String tickerSymbol = menu.getTickerSymbol();

    try {
      api.getShareDetails(tickerSymbol, "2022-10-26");
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
