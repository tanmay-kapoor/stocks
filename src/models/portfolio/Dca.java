package models.portfolio;

import java.sql.Time;
import java.util.Map;

public class Dca {
  private final double totalAmount;
  private final Map<String, Double> stocksWeightage;
  private final TimeLine timeLine;
  private final int interval;

  public Dca(double totalAmount, Map<String, Double> stocksWeightage, TimeLine timeLine, int interval) {
    this.totalAmount = totalAmount;
    this.stocksWeightage = stocksWeightage;
    this.timeLine = timeLine;
    this.interval = interval;
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

}
