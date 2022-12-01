package models.portfolio;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

import models.TimeLine;

/**
 * Class to couple all the portfolio performance related stuff together.
 */
public class Report {
  private final Map<LocalDate, Performance> performanceOnEachDate;
  private final String scale;
  private final String min;
  private final TimeLine timeLine;

  /**
   * Constructor to initialize all the values to specified ones.
   *
   * @param performanceOnEachDate Map representing valuation and number of stars for each date.
   * @param scale                 Scale to multiply with number of stars and get the approximate
   *                              valuation.
   * @param min                   Value of 1 star.
   * @param timeLine              Object to represent start and end date of portfolio performance.
   */
  public Report(Map<LocalDate, Performance> performanceOnEachDate, String scale, String min,
                TimeLine timeLine) {
    this.performanceOnEachDate = performanceOnEachDate;
    this.scale = scale;
    this.min = min;
    this.timeLine = timeLine;
  }

  /**
   * Get the current scale value.
   *
   * @return current scale value.
   */
  public String getScale() {
    return this.scale;
  }

  /**
   * Get the value that 1 star represents.
   *
   * @return base value as a String.
   */
  public String getBaseValue() {
    return this.min;
  }

  /**
   * get performance of portfolio on each date.
   *
   * @return map representing performance of portfolio on each date.
   */
  public Map<LocalDate, Performance> getPerformanceOnEachDate() {
    return new TreeMap<>(this.performanceOnEachDate);
  }

  /**
   * Get start and end date of performance as specified by the user.
   *
   * @return TimeLine object that holds start and end date of the performance.
   */
  public TimeLine getTimeLime() {
    return this.timeLine;
  }
}
