package models.api;

import java.time.LocalDate;
import java.util.Map;

/**
 * An interface to get details about any type of share as and when requested by the user.
 * A share can be of type stock, crypto, forex, commodity, etc.
 */
public interface ShareApi {
  /**
   *
   * @param tickerSymbol
   * @param dateAsked
   * @return
   */
  Map<String, Double> getShareDetails(String tickerSymbol, LocalDate dateAsked);
}
