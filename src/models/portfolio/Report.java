package models.portfolio;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

import models.TimeLine;

public class Report {
  private final Map<LocalDate, Performance> performanceOnEachDate;
  private final String scale;
  private final String min;
  private final TimeLine timeLine;

  public Report(Map<LocalDate, Performance> performanceOnEachDate, String scale, String min, TimeLine timeLine) {
    this.performanceOnEachDate = performanceOnEachDate;
    this.scale = scale;
    this.min = min;
    this.timeLine = timeLine;
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

  public TimeLine getTimeLime() {
    return this.timeLine;
  }
}
