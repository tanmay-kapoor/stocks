package models.portfolio;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

public class Report {
  private final Map<LocalDate, Performance> performanceOnEachDate;
  private final String scale;
  private final String min;

  public Report(Map<LocalDate, Performance> performanceOnEachDate, String scale, String min) {
    this.performanceOnEachDate = performanceOnEachDate;
    this.scale = scale;
    this.min = min;
  }

  public String getScale() {
    return this.scale;
  }

  public String getBaseValue() {
    return this.min;
  }

  public Map<LocalDate, Performance> getPerformanceOnEachDate() {
    return new TreeMap<>(this.performanceOnEachDate);
  }
}
