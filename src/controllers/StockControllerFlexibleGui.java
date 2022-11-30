package controllers;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import models.Details;
import models.Log;
import models.TimeLine;
import models.api.ShareApi;
import models.portfolio.Dca;
import models.portfolio.Performance;
import models.portfolio.Portfolio;
import models.portfolio.Report;
import models.portfolio.StockPortfolioFlexible;
import models.portfolio.Txn;

import static java.lang.Math.abs;
import static java.lang.Math.round;

public class StockControllerFlexibleGui extends FeaturesImpl {

  public StockControllerFlexibleGui(ShareApi api, String path) {
    super(api, path);
  }

  @Override
  protected Portfolio createPortfolioObject(String portfolioName) {
    return new StockPortfolioFlexible(portfolioName, path, api);
  }

  @Override
  protected LocalDate getDate(String d) {
    if (d.equals("")) {
      return LocalDate.now();
    }
    return LocalDate.parse(d);
  }

  @Override
  protected double getCommissionFee(String commission) {
    if (commission.equals("")) {
      commission = "0";
    }
    return Double.parseDouble(commission);
  }

  @Override
  protected void sellStockIfAllowed(String portfolioName, String ticker, String quant,
                                    String d, String commission) {
    try {
      Portfolio portfolio = findPortfolio(portfolioName);
      ticker = ticker.toUpperCase();
      double quantity = Double.parseDouble(quant);
      double commissionFee = Double.parseDouble(commission);
      LocalDate date = LocalDate.parse(d);
      Map<String, Log> portfolioComposition = portfolio.getComposition();
      if (!portfolioComposition.containsKey(ticker)) {
        menu.printMessage(ticker + " is not in this portfolio");
      } else {
        Details details = new Details(quantity, date);
        portfolio.sell(ticker, details, commissionFee);
        portfolio.savePortfolio();
        menu.successMessage(ticker, details, Txn.Sell);
      }

    } catch (NumberFormatException e) {
      menu.printMessage("Invalid format for 1 or more fields");
    } catch (IllegalArgumentException e) {
      menu.printMessage(e.getMessage());
    } catch (DateTimeParseException e) {
      menu.printMessage("Invalid Date format");
    }
  }

  @Override
  protected Report getPortfolioPerformanceIfAllowed(String portfolioName, String f, String t) {
    try {
      Portfolio portfolio = findPortfolio(portfolioName);
      LocalDate from = LocalDate.parse(f);
      LocalDate to = LocalDate.parse(t);
      if (from.compareTo(LocalDate.now()) > 0 || to.compareTo(LocalDate.now()) > 0) {
        menu.printMessage("Cannot get performance for future dates");
      } else if (from.compareTo(to) > 0) {
        menu.printMessage("Start date must be before end date");
      } else {
        Map<LocalDate, Double> performance = portfolio.getPortfolioPerformance(from, to);
        Map<LocalDate, Performance> performanceOnEachDate = new TreeMap<>();

        //scale performance
        Double min = Collections.min(performance.values());
        Double max = Collections.max(performance.values());

        int count = 0;
        double prevVal = 0.00;
        int prevStars = 0;
        double valueDiffSum = 0;

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
          performanceOnEachDate.put(date, new Performance(precisionAdjusted, stars));
        }
        Double scale_val = Double.isNaN(valueDiffSum / count) ? 0 : (valueDiffSum / count);
        return new Report(performanceOnEachDate, String.format("%.02f", scale_val), String.format("%.02f", min),
                new TimeLine(LocalDate.parse(f), LocalDate.parse(t)));
      }
    } catch (DateTimeParseException e) {
      menu.printMessage("Invalid date format");
    }
    return null;
  }

  @Override
  protected double getCostBasisIfAllowed(String portfolioName, String date) {
    try {
      Portfolio portfolio = findPortfolio(portfolioName);
      LocalDate d = LocalDate.parse(date);
      if (d.compareTo(LocalDate.now()) > 0) {
        menu.printMessage("Cannot get value for future dates");
      } else {
        return portfolio.getCostBasis(d);
      }
    } catch (DateTimeParseException e) {
      menu.printMessage("Invalid date format");
    }
    return -1;
  }

  private double scaleBetween(double x, double min, double max) {
    double minAllowed = 1;
    double maxAllowed = 50;

    return (maxAllowed - minAllowed) * (x - min) / (max - min) + minAllowed;
  }

  private boolean satisfiesWeightageTotal(double val) {
    return totalWeightage - val >= 0.0;
  }

  @Override
  protected void addTickerToStrategyIfAllowed(String ticker, String weightage) {
    try {
      ticker = ticker.toUpperCase();
      if (!api.isTickerPresent(ticker)) {
        api.getShareDetails(ticker, LocalDate.now());
      }
      double w = Double.parseDouble(weightage);
      if (w <= 0) {
        menu.printMessage("Weightage of a ticker must be > 0");
      } else if (!satisfiesWeightageTotal(w)) {
        menu.printMessage("Total weightage should be < 100%");
      } else {
        if (!stocksWeightage.containsKey(ticker)) {
          stocksWeightage.put(ticker, w);
        } else {
          stocksWeightage.put(ticker, stocksWeightage.get(ticker) + w);
        }
        totalWeightage -= w;

        if (totalWeightage != 0) {
          menu.printMessage("Successfully added ticker to strategy");
        } else {
          menu.printMessage("100% weightage completed");
        }
      }
    } catch (NumberFormatException e) {
      menu.printMessage("Invalid format for 1 or more fields");
    } catch (IllegalArgumentException e) {
      menu.printMessage("Invalid ticker");
    }
  }

  @Override
  protected void saveDcaIfAllowed(String portfolioName, String strategyName, String amt, String f, String t,
                                  String interval, String commission) {
    try {
      Portfolio portfolio;
      if (allPortfolios.contains(portfolioName)) {
        portfolio = findPortfolio(portfolioName);
      } else {
        portfolio = this.portfolio;
      }
      double amount = Double.parseDouble(amt);
      LocalDate from = LocalDate.parse(f);
      LocalDate to = !t.equals("") ? LocalDate.parse(t) : LocalDate.parse("2100-12-31");

      if (from.compareTo(to) > 0) {
        menu.printMessage("Start date should be before end date");
      } else {
        TimeLine timeline = new TimeLine(from, to);
        int intervalVal = Integer.parseInt(interval);
        if (intervalVal < 1) {
          menu.printMessage("Interval should be at least 1 day");
        } else {
          double commissionFee = Double.parseDouble(commission);
          doDca(portfolio, strategyName, amount, stocksWeightage,
                  timeline, intervalVal, commissionFee);
        }
      }
    } catch (NumberFormatException e) {
      menu.printMessage("Invalid format for 1 or more fields");
    } catch (DateTimeParseException e) {
      menu.printMessage("Invalid date format");
    } catch (IllegalArgumentException e) {
      menu.printMessage(e.getMessage());
    }
  }

  private void doDca(Portfolio portfolio, String strategyName, double amount,
                     Map<String, Double> stocksWeightage, TimeLine timeline,
                     int interval, double commission) {

    if (this.totalWeightage != 0) {
      menu.printMessage("Total weightage is not 100%");
    } else {
      Dca dca = new Dca(amount, stocksWeightage, timeline, interval, commission);
      portfolio.doDca(strategyName, dca);
      menu.printMessage("Success!!!!!!!");
    }
  }
}
