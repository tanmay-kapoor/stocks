package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
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

import static java.lang.Math.abs;
import static java.lang.Math.round;

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
    return '6';
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
            Details details = getDetails();
            api.getShareDetails(ticker, details.getPurchaseDate());
            portfolio.buy(ticker, details, getCommissionFee());
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

  @Override
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

  @Override
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

  @Override
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

  @Override
  protected void handleGetPortfolioPerformance(Portfolio portfolio) {
    LocalDate from;
    LocalDate to;
    Map<LocalDate, Double> performance;
    boolean isValidGap;

    do {
      from = getDate("Choose a start date");
      to = getDate("Choose an end date");
      long days = ChronoUnit.DAYS.between(from, to);

      isValidGap = true;

      if (to.compareTo(from) < 0) {
        menu.printMessage("Please choose a date later than start date.");
        isValidGap = false;
      }
      else if (to.compareTo(LocalDate.now()) > 0) {
        menu.printMessage("You cannot choose a future date. Please choose today's or a date "
                + "from the past.");
        isValidGap = false;
      }
      else {

        menu.printMessage("\nPlease wait while performance report is being generated! "
                + "This may take some time..\n");
        performance = portfolio.getPortfolioPerformance(from, to);

        //scale performance
        Double min = Collections.min(performance.values());
        Double max = Collections.max(performance.values());

        int count = 0;
        double prevVal = 0.00;
        int prevStars = 0;
        double valueDiffSum = 0;

        menu.printMessage("Date\t\t\t\tPortfolio Valuation\t\t");

        for (LocalDate date : performance.keySet()) {
          double valueOnDate = performance.get(date);
          int scaled = (int) round(scaleBetween(valueOnDate, min, max));
          int stars = scaled == 0 ? 1 : scaled;
//          System.out.println(stars);
          if (prevStars != 0) {
            int starDiff = abs(stars - prevStars);
            if (starDiff != 0) {
//              double avg_star_val = (abs(valueOnDate - prevVal) / starDiff) * stars;
              double avg_star_val = (abs(valueOnDate - prevVal) / starDiff);
              valueDiffSum += avg_star_val;
//              count += stars;
              count++;
            }
          }

          prevVal = valueOnDate;
          prevStars = stars;

          menu.printMessage(date + "\t\t\t\t"
                  + String.format("%.2f", performance.get(date)) + "\t\t\t\t\t\t"
                  + "*".repeat(stars));

        }
        Double scale_val = Double.isNaN(valueDiffSum / count) ? 0 : (valueDiffSum / count);
        menu.printMessage("\nScale: * ~ " + String.format("%.2f", scale_val)
                + " relative to the base value of " + min + "\n");

      }
    } while (!isValidGap);
  }

  @Override
  protected void handleGetCostBasis(Portfolio portfolio) {
    char ch = menu.getDateChoice();
    LocalDate date;
    double costBasis;
    boolean isProblematic;
    do {
      isProblematic = false;
      try {
        switch (ch) {
          case '1':
            costBasis = portfolio.getCostBasis();
            menu.printMessage("Cost Basis on " + LocalDate.now() + " = " + costBasis);
            break;

          case '2':
            date = LocalDate.parse(menu.getDateForValue());
            costBasis = portfolio.getCostBasis(date);
            menu.printMessage("Cost Basis on " + date + " = " + costBasis);
            break;

          default:
            break;
        }
      } catch (DateTimeParseException e) {
        isProblematic = true;
        menu.printMessage("\nInvalid date format");
      }
    } while (isProblematic);
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

        if(date.compareTo(LocalDate.now()) > 0) {
          menu.printMessage("\nCannot get performance for a fuure date.");
          isValidDate = false;
        }
      } catch (DateTimeParseException e) {
        isValidDate = false;
        menu.printMessage("\nInvalid Date. Please enter again.");
      }
    } while (!isValidDate);

    return date;
  }

  private double scaleBetween(double x, double min, double max) {
    double minAllowed = 1;
    double maxAllowed = 50;

    return (maxAllowed - minAllowed) * (x - min) / (max - min) + minAllowed;
  }

  private Details getDetails() {
    boolean isValid;
    double quantity;
    do {
      quantity = menu.getQuantity();
      isValid = this.validateQuantity(quantity);
    } while (!isValid);

    LocalDate purchaseDate = getDate("");
    return new Details(quantity, purchaseDate);
  }

  @Override
  protected void filterBasedOnFunction(Function function) {
    commonStuff(function);
  }

  @Override
  protected void handleMenuOptions(Portfolio portfolio, Function function) {
    switch(function) {
      case Composition:
        handleGetPortfolioComposition(portfolio);
        break;

      case GetValue:
        handleGetPortfolioValue(portfolio);
        break;

      case BuySell:
        handleBuySellInPortfolio(portfolio);
        break;

      case SeePerformance:
        handleGetPortfolioPerformance(portfolio);
        break;

      case CostBasis:
        handleGetCostBasis(portfolio);
        break;

      default:
        throw new IllegalArgumentException("Illegal value");
    }
  }
}
