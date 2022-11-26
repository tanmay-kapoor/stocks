package controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Details;
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
//      menu.clearTextIfDisplayed();
//      menu.getAddToPortfolioChoice();
      menu.getTickerSymbol();
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
  public void buyStock(String ticker, double quantity, LocalDate purchaseDate, double commissionFee) {
    System.out.println(ticker + " " +
            quantity + " " +
            purchaseDate + " " +
            commissionFee);
    try {
      if (!api.isTickerPresent(ticker)) {
        api.getShareDetails(ticker, LocalDate.now());
      }
      Details details = new Details(quantity, purchaseDate);
      portfolio.buy(ticker, details, commissionFee);
      menu.successMessage(ticker, details, Txn.Buy);
    } catch (IllegalArgumentException e) {
      menu.printMessage("This ticker is not associated with any company");
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
  public void handleCreatePortfolioThroughUpload() {

  }
}
