package models.api;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to couple share details and whether a date is a holiday or not.
 */
public class DateDetails {
  private final Map<String, Double> values;
  private final boolean isHoliday;

  /**
   * Initialize values (high, open, low, close, volume) for a date and whether this date is a
   * holiday or not.
   *
   * @param values    map representing share details.
   * @param isHoliday boolean indicating whether date corresponding to this
   *                  object is a holiday or not.
   */
  public DateDetails(Map<String, Double> values, boolean isHoliday) {
    this.values = values;
    this.isHoliday = isHoliday;
  }

  /**
   * Get map of values (high, open, low, close, volume).
   *
   * @return map of values fetched by the api.
   */
  public Map<String, Double> getValues() {
    return new HashMap<>(this.values);
  }

  /**
   * Get whether corresponding date is a holiday or not.
   *
   * @return boolean true for holiday false otherwise.
   */
  public boolean getIsHoliday() {
    return this.isHoliday;
  }
}
