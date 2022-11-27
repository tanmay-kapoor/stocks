package models.portfolio;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

public class Report {
  private final String scale;
  private final String min;

  public Report(Map<LocalDate, Performance> performance, String scale, String min) {
    this.scale = scale;
    this.min = min;
  }

  public String getScale() {
    return this.scale;
  }

  public String getBaseValue() {
    return this.min;
  }
}
