package models.portfolio;

import java.time.LocalDate;
import java.util.Map;

import models.TimeLine;

/**
 * Class to couple all the details required to create dca strategy in one place.
 */
public class Dca {
  private final double totalAmount;
  private final Map<String, Double> stocksWeightage;
  private final TimeLine timeLine;
  private final int interval;
  private final double commission;
  private LocalDate lastBoughtDate;

  /**
   * Constructor to initialize dca object with specified values.
   *
   * @param totalAmount     Total amount to invest for this strategy.
   * @param stocksWeightage Weightage of each individual ticker in the dca strategy.
   * @param timeLine        TimeLine object representing start and end date of strategy.
   * @param interval        Interval between consecutive investments in days (>= 1 day).
   * @param commission      Commission fee for this transaction.
   */
  public Dca(double totalAmount, Map<String, Double> stocksWeightage,
             TimeLine timeLine, int interval, double commission) {
    this.totalAmount = totalAmount;
    this.stocksWeightage = stocksWeightage;
    this.timeLine = timeLine;
    this.interval = interval;
    this.commission = commission;
    this.lastBoughtDate = null;
  }

  /**
   * Constructor to initialize everything in the previous constructor along with lastBoughtDate
   *
   * @param totalAmount     Total amount to invest for this strategy.
   * @param stocksWeightage Weightage of each individual ticker in the dca strategy.
   * @param timeLine        TimeLine object representing start and end date of strategy.
   * @param interval        Interval between consecutive investments in days (>= 1 day).
   * @param commission      Commission fee for this transaction.
   * @param lastBoughtDate  Date on which the last investment happened.
   */
  public Dca(double totalAmount, Map<String, Double> stocksWeightage,
             TimeLine timeLine, int interval, double commission, LocalDate lastBoughtDate) {
    this(totalAmount, stocksWeightage, timeLine, interval, commission);
    this.lastBoughtDate = lastBoughtDate;
  }

  /**
   * Get the stock weightage distribution for a dca strategy.
   *
   * @return map of string corresponding to tickers and values corresponding to weightage.
   */
  public Map<String, Double> getStockWeightage() {
    return this.stocksWeightage;
  }

  /**
   * Get the total investment amount for the dca strategy.
   *
   * @return double value which is the investment amount.
   */
  public double getTotalAmount() {
    return this.totalAmount;
  }

  /**
   * Get the TimeLine object for a dca strategy.
   *
   * @return timeline representing start and end date of dca strategy.
   */
  public TimeLine getTimeLine() {
    return this.timeLine;
  }

  /**
   * Get the interval in days for dca strategy.
   *
   * @return int value representing time interval in days.
   */
  public int getInterval() {
    return this.interval;
  }

  /**
   * Get the commission fee for dca transaction
   *
   * @return double value which is the commission
   */
  public double getCommission() {
    return this.commission;
  }

  /**
   * get the date on which last buy transaction occurred.
   *
   * @return date of last purchase.
   */
  public LocalDate getLastBoughtDate() {
    return this.lastBoughtDate;
  }

  /**
   * Set the last bought date to specified value.
   *
   * @param date Date to change lastBoughtDate to.
   */
  public void setLastBought(LocalDate date) {
    this.lastBoughtDate = date;
  }
}
