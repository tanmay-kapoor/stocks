package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import models.Log;
import models.api.ShareApi;
import models.portfolio.Composition;
import models.portfolio.Portfolio;
import models.portfolio.StockPortfolioInflexible;
import views.Menu;

/**
 /**
 * The StockControllerInflexible couples appropriate views and models that work specifically with
 * inflexible Portfolio.
 */

public class StockControllerInflexible extends AbstractController {
  public StockControllerInflexible(InputStream in, Menu menu, ShareApi api, String path) {
    super(in, menu, api, path);
    sc = new Scanner(this.in);
  }

  @Override
  protected void getWhatToDoOnStart() {
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

  @Override
  protected Portfolio createPortfolio(String portfolioName) {
    return new StockPortfolioInflexible(portfolioName, path, api);
  }

  @Override
  protected Portfolio createPortfolio(String portfolioName, Map<String, Log> stocks,
                                      Map<LocalDate, Double> costBasisHistory) {
    return new StockPortfolioInflexible(portfolioName, stocks, path, api, costBasisHistory);
  }

  @Override
  protected Map<String, LocalDate> readLastSoldDateFromCsv(File logFile)
          throws FileNotFoundException {
    return new HashMap<>();
  }

  @Override
  protected Map<LocalDate, Double> readStockBasisHistoryFromCsv(File costBasisFile)
          throws FileNotFoundException {
    return new HashMap<>();
  }

  @Override
  protected LocalDate getPurchaseDate() {
    return LocalDate.now();
  }

  @Override
  protected char getLastOption() {
    return '3';
  }

  @Override
  protected double getCommissionFee() {
    return 0.0;
  }

  @Override
  protected void filterBasedOnFunction(Function function) {
    switch (function) {
      case Composition:
      case GetValue:
        commonStuff(function);
        break;

      default:
        break;
    }
  }

  @Override
  protected void handleMenuOptions(Portfolio portfolio, Function function) {
    switch (function) {
      case Composition:
        handleGetPortfolioComposition(portfolio);
        break;

      case GetValue:
        handleGetPortfolioValue(portfolio);
        break;

      default:
        throw new IllegalArgumentException("Illegal Value");
    }
  }

  @Override
  protected boolean giveDateOptionsIfApplicable(Portfolio portfolio, Composition option) {
    getCompositionForToday(portfolio, option);
    return true;
  }

  @Override
  protected boolean handleCreatePortfolioOption(char choice, Portfolio portfolio,
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
}
