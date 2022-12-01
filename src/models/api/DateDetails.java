package models.api;

import java.util.HashMap;
import java.util.Map;

/**
 * Class
 */
public class DateDetails {
  private final Map<String, Double> values;
  private final boolean isHoliday;

  public DateDetails(Map<String, Double> values, boolean isHoliday) {
    this.values = values;
    this.isHoliday = isHoliday;
  }

  public Map<String, Double> getValues() {
    return new HashMap<>(this.values);
  }

  public boolean getIsHoliday() {
    return this.isHoliday;
  }
}
