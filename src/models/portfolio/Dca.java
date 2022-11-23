package models.portfolio;

import java.sql.Time;
import java.util.Map;

public class Dca {
  double amount;
  Map<String, Double> stocksWeightage;

  TimeLine timeLine;

  public Dca(double amount, Map<String, Double> stocksWeightage, TimeLine timeLine) {
    this.amount = amount;
    this.stocksWeightage = stocksWeightage;
    this.timeLine = timeLine;
  }

}
