package models.portfolio;

import java.time.LocalDate;
import java.util.Map;

import models.TimeLine;

public class Dca {
  private final double totalAmount;
  private final Map<String, Double> stocksWeightage;
  private final TimeLine timeLine;
  private final int interval;
  private final double commission;
  private LocalDate lastBought;

  public Dca(double totalAmount, Map<String, Double> stocksWeightage,
             TimeLine timeLine, int interval, double commission) {
    this.totalAmount = totalAmount;
    this.stocksWeightage = stocksWeightage;
    this.timeLine = timeLine;
    this.interval = interval;
    this.commission = commission;
  }

  public Map<String, Double> getStockWeightage() {
    return this.stocksWeightage;
  }

  public double getTotalAmount() {
    return this.totalAmount;
  }

  public TimeLine getTimeLine() {
    return this.timeLine;
  }

  public int getInterval() {
    return this.interval;
  }

  public double getCommission() {
    return this.commission;
  }

  public LocalDate getLastBought() {
    return this.lastBought;
  }

  public void setLastBought(LocalDate date) {
    this.lastBought = date;
  }
}
