package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.TreeMap;

import models.Details;
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
    return '4';
  }

  @Override
  protected void handleBuySellOption() {
    commonStuff(Function.BuySell);
  }

  @Override
  protected void handleBuySellInPortfolio(String name) {
    Portfolio portfolio = allPortfolioObjects.get(name);
    Map<String, Log> portfolioComposition = portfolio.getComposition();

    char ch;
    do {
      ch = menu.getBuySellChoice();
      String ticker;

      switch (ch) {
        case '1':
          try {
            ticker = menu.getTickerSymbol().toUpperCase();
            api.getShareDetails(ticker, LocalDate.now());
            portfolio.buy(ticker, getDetails(), getCommissionPercent());
            portfolioComposition = portfolio.getComposition();
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
              portfolio.sell(ticker, getDetails(), getCommissionPercent());
              portfolioComposition = portfolio.getComposition();
            } catch (IllegalArgumentException e) {
              menu.printMessage("\n" + e.getMessage());
            }
          }
          break;

        default:
          portfolio.savePortfolio();
          break;
      }
    } while (ch >= '1' && ch <= '2');
  }

  protected double getCommissionPercent() {
    double commissionPercent;
    do {
      commissionPercent = menu.getCommissionPercent();
      if (commissionPercent < 0.0 || commissionPercent > 100.0) {
        menu.printMessage("\nCommission percentage must be between 0 and 100%");
      }
    } while (commissionPercent < 0.0 || commissionPercent > 100.0);
    return commissionPercent;
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
}
