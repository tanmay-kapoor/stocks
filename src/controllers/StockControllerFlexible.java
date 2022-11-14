package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.TreeMap;

import models.Log;
import models.api.ShareApi;
import models.portfolio.Portfolio;
import models.portfolio.StockPortfolioFlexible;
import views.Menu;

public class StockControllerFlexible extends AbstractController {
  protected StockControllerFlexible(Menu menu, ShareApi api, String path) {
    super(menu, api, path);
  }

  @Override
  protected Portfolio createPortfolio(String portfolioName) {
    return new StockPortfolioFlexible(portfolioName, path, api);
  }

  @Override
  protected Portfolio createPortfolio(String portfolioName, Map<String, Log> stocks,
                                      Map<LocalDate, Double> costBasisHistory) {
    return new StockPortfolioFlexible(portfolioName, stocks, path, api, costBasisHistory);
  }

  @Override
  protected LocalDate getPurchaseDate() {
    return LocalDate.parse(menu.getDateForValue());
  }

  @Override
  protected char getLastOption() {
    return '5';
  }

  @Override
  protected void handleBuySellOption() {
    commonStuff(Function.BuySell);
  }

  @Override
  protected void handleBuySellInPortfolio(Portfolio portfolio) {
    Map<String, Log> portfolioComposition = portfolio.getComposition();

    char ch;
    boolean shouldSave = false;
    do {
      ch = menu.getBuySellChoice();
      String ticker;

      switch (ch) {
        case '1':
          try {
            ticker = menu.getTickerSymbol().toUpperCase();
            api.getShareDetails(ticker, LocalDate.now());
            portfolio.buy(ticker, getDetails(), getCommissionFee());
            portfolioComposition = portfolio.getComposition();
            shouldSave = true;
          } catch (IllegalArgumentException e) {
            menu.printMessage("\n" + e.getMessage());
          }
          break;

        case '2':
          ticker = menu.getTickerSymbol().toUpperCase();
          if (!portfolioComposition.containsKey(ticker)) {
            menu.printMessage("\nCannot sell ticker that is not in portfolio");
          } else {
            try {
              portfolio.sell(ticker, getDetails(), getCommissionFee());
              portfolioComposition = portfolio.getComposition();
              shouldSave = true;
            } catch (IllegalArgumentException e) {
              menu.printMessage("\n" + e.getMessage());
            }
          }
          break;

        default:
          if (shouldSave) {
            portfolio.savePortfolio();
          }
          break;
      }
    } while (ch >= '1' && ch <= '2');
  }

  protected double getCommissionFee() {
    double commissionFee;
    do {
      commissionFee = menu.getCommissionFee();
      if (commissionFee < 0.0) {
        menu.printMessage("\nCommission fee must be non-negative");
      }
    } while (commissionFee < 0.0);
    return commissionFee;
  }

  protected Map<String, LocalDate> readLastSoldDateFromCsv(File logFile)
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

  protected Map<LocalDate, Double> readStockBasisHistoryFromCsv(File costBasisFile)
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

  protected void handleGetPortfolioPerformanceOption() {
    commonStuff(Function.SeePerformance);
  }

  @Override
  protected void handleGetPortfolioPerformance(Portfolio portfolio) {
    LocalDate from = getDate("Start Date");
    LocalDate to = getDate("End Date");
    Map<LocalDate, Double> performance = portfolio.getPortfolioPerformance(from, to);

    for (LocalDate date : performance.keySet()) {
      System.out.println(date + " " + performance.get(date));
    }
  }

  private LocalDate getDate(String msg) {
    LocalDate date;
    boolean isValidDate;

    do {
      date = LocalDate.now();
      isValidDate = true;
      try {
        menu.printMessage("\n" + msg);
        date = LocalDate.parse(menu.getDateForValue());
      } catch (DateTimeParseException e) {
        isValidDate = false;
        menu.printMessage("\nInvalid Date. Please enter again.");
      }
    } while (!isValidDate);

    return date;
  }
}
