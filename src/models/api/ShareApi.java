package models.api;

import java.time.LocalDate;
import java.util.Map;

/**
 * An interface to get details about any type of share as and when requested by the user.
 * A share can be of type stock, crypto, forex, commodity, etc.
 */
public interface ShareApi {
  /**
   * This method returns the price of the share on a particular date asked by the client.
   *
   * @param tickerSymbol a symbol to uniquely identify a share in the market.
   * @param dateAsked    date on which the client is requesting the value of the share.
   * @return a map object whose key is the ticker symbol of the share in the portfolio
   * and the value is the price of that share on the date requested by the client.
   */
  Map<String, Double> getShareDetails(String tickerSymbol, LocalDate dateAsked);
}
