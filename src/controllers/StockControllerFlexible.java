package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.TreeMap;

import models.Details;
import models.Log;
import models.api.ShareApi;
import models.portfolio.Composition;
import models.portfolio.Portfolio;
import models.portfolio.StockPortfolioFlexible;
import models.portfolio.Txn;
import views.Menu;

import static java.lang.Math.abs;
import static java.lang.Math.round;

/**
 * The StockControllerInflexible couples appropriate views and models that work specifically with
 * Flexible Portfolio.
 */
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
    LocalDate date = null;
    boolean isValidDate;

    do {
      isValidDate = true;
      try {
        date = LocalDate.parse(menu.getDateForValue());
      } catch (DateTimeParseException e) {
        isValidDate = false;
        menu.printMessage("\nInvalid Date format\n");
      }
    }
    while (!isValidDate);

    return date;
  }

  @Override
  protected char getLastOption() {
    return '6';
  }

  private void handleBuySellInPortfolio(Portfolio portfolio) {
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
            if (!api.isTickerPresent(ticker)) {
              api.getShareDetails(ticker, LocalDate.now());
            }

            Details details = getDetails();
            if (api.hasPrice(ticker, details.getPurchaseDate())) {
              portfolio.buy(ticker, details, getCommissionFee());
              portfolioComposition = portfolio.getComposition();
              shouldSave = true;
              menu.successMessage(ticker, details, Txn.Buy);
            } else {
              menu.printMessage("\nNo price data found for " + details.getPurchaseDate());
            }
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
              Details details = getDetails();
              portfolio.sell(ticker, details, getCommissionFee());
              portfolioComposition = portfolio.getComposition();
              shouldSave = true;
              menu.successMessage(ticker, details, Txn.Sell);
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
    }
    while (ch >= '1' && ch <= '2');
  }

  @Override
  protected double getCommissionFee() {
    double commissionFee;
    do {
      commissionFee = menu.getCommissionFee();
      if (commissionFee < 0.0) {
        menu.printMessage("\nCommission fee must be non-negative\n");
      }
    }
    while (commissionFee < 0.0);
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

  private void handleGetPortfolioPerformance(Portfolio portfolio) {
    LocalDate from;
    LocalDate to;
    Map<LocalDate, Double> performance;
    boolean isValidGap;

    do {
      isValidGap = true;
      menu.printMessage("\nChoose a start date");
      from = getDate();

      menu.printMessage("\nChoose an end date");
      to = getDate();

      if (from.compareTo(to) > 0) {
        menu.printMessage("\nPlease choose a start date before the end date");
        isValidGap = false;
      } else if (to.compareTo(LocalDate.now()) > 0) {
        isValidGap = false;
        menu.printMessage("\nEnd date cannot be in the future.");
      } else {
        try {
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

          StringBuilder performanceReport = new StringBuilder();
          performanceReport.append("Date\t\t\t\t\t\t\tPortfolio Valuation ($)"
                  + "\t\t\t\t\tRelative Change");

          for (LocalDate date : performance.keySet()) {
            double valueOnDate = performance.get(date);
            int scaled = (int) round(scaleBetween(valueOnDate, min, max));
            int stars = scaled == 0 ? 1 : scaled;

            if (prevStars != 0) {
              int starDiff = abs(stars - prevStars);
              if (starDiff != 0) {
                double avg_star_val = (abs(valueOnDate - prevVal) / starDiff) * stars;
                valueDiffSum += avg_star_val;
                count += stars;
              }
            }

            prevVal = valueOnDate;
            prevStars = stars;
            String precisionAdjusted = String.format("%.2f", performance.get(date));
            performanceReport
                    .append("\n")
                    .append(date)
                    .append("\t\t\t\t\t\t\t")
                    .append(precisionAdjusted)
                    .append(" ".repeat(7 - precisionAdjusted.length() + 35))
                    .append("*".repeat(stars));

          }
          Double scale_val = Double.isNaN(valueDiffSum / count) ? 0 : (valueDiffSum / count);
          performanceReport
                  .append("\n\nScale: * ~ $")
                  .append(String.format("%.2f", scale_val))
                  .append(" relative to the base value of $")
                  .append(String.format("%.2f", min))
                  .append("\n");


          menu.printMessage(performanceReport.toString());
        } catch (IllegalArgumentException e) {
          menu.printMessage(e.getMessage());
        }
      }

    }
    while (!isValidGap);
  }

  private void handleGetCostBasis(Portfolio portfolio) {
    boolean isProblematic;
    do {
      char ch = menu.getDateChoice();
      LocalDate date;
      double costBasis;
      isProblematic = false;
      try {
        switch (ch) {
          case '1':
            costBasis = portfolio.getCostBasis();
            menu.printMessage("\nCost Basis on " + LocalDate.now()
                    + " = $" + String.format("%.2f", costBasis));
            break;

          case '2':
            date = LocalDate.parse(menu.getDateForValue());
            costBasis = portfolio.getCostBasis(date);
            menu.printMessage("\nCost Basis on " + date
                    + " = $" + String.format("%.2f", costBasis));
            break;

          default:
            break;
        }
      } catch (IllegalArgumentException e) {
        isProblematic = true;
        menu.printMessage("\n" + e.getMessage());
      } catch (DateTimeParseException e) {
        isProblematic = true;
        menu.printMessage("\nInvalid date format");
      }
    }
    while (isProblematic);
  }

  private LocalDate getDate() {
    LocalDate date;
    boolean isValidDate;

    do {
      date = LocalDate.now();
      isValidDate = true;
      try {
        date = LocalDate.parse(menu.getDateForValue());

        if (date.compareTo(LocalDate.now()) > 0) {
          menu.printMessage("\nCannot perform action for a future date.\n");
          isValidDate = false;
        }
      } catch (DateTimeParseException e) {
        isValidDate = false;
        menu.printMessage("\nInvalid date format.\n");
      }
    }
    while (!isValidDate);

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
    }
    while (!isValid);

    LocalDate purchaseDate = getDate();
    return new Details(quantity, purchaseDate);
  }

  @Override
  protected void filterBasedOnFunction(Function function) {
    commonStuff(function);
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

  @Override
  protected boolean giveDateOptionsIfApplicable(Portfolio portfolio, Composition option) {
    boolean isFutureDate;

    do {
      isFutureDate = false;
      char ch = menu.getDateChoice();

      switch (ch) {
        case '1':
          getCompositionForToday(portfolio, option);
          return true;

        case '2':
          LocalDate date;
          switch (option) {
            case Contents:
              try {
                date = getPurchaseDate();
                menu.printMessage(getPortfolioContents(portfolio, date));
              } catch (IllegalArgumentException e) {
                isFutureDate = true;
                menu.printMessage("\n" + e.getMessage());
              }
              break;

            case Weightage:
              try {
                date = getPurchaseDate();
                menu.printMessage(getPortfolioWeightage(portfolio, date));
              } catch (IllegalArgumentException e) {
                isFutureDate = true;
                menu.printMessage("\n" + e.getMessage());
              }
              break;

            default:
              return false;
          }
          break;

        default:
          return false;
      }
    }
    while (isFutureDate);
    return true;
  }
}
