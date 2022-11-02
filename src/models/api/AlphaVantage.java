package models.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * An API by Alpha Vantage that is used to get necessary data points about stocks which are
 * registered on the NASDAQ. It is primarily used to fetch stock's OHCL and its traded
 * volume on a particular date.
 */
public class AlphaVantage implements ShareApi {
  private final String apiKey;

  /**
   * The API key that is going to be used by the API.
   */
  public AlphaVantage() {
    this.apiKey = "NAO61MQDSY9EPTN";
  }

  @Override
  public Map<String, Double> getShareDetails(String tickerSymbol, LocalDate dateAsked) {
    URL url;

    try {
      url = new URL("https://www.alphavantage"
              + ".co/query?function=TIME_SERIES_DAILY"
              + "&outputsize=compact"
              + "&symbol"
              + "=" + tickerSymbol + "&apikey=" + apiKey + "&datatype=csv");
    } catch (MalformedURLException e) {
      throw new RuntimeException("the AlphaVantage API has either changed or "
              + "no longer works");
    }

    String[] keys;
    String record = "";

    try {
      InputStream in = url.openStream();
      Scanner sc = new Scanner(in);

      StringBuilder response = new StringBuilder();
      while (sc.hasNext()) {
        response.append(sc.nextLine()).append("\n");
      }

      String res = response.toString();
      if (res.contains("Invalid API")) {
        throw new IllegalArgumentException("This ticker is not associated with a company.");
      }

      String[] lines = res.split("\n");
      keys = lines[0].split(",");

      boolean found = false;
      for (String line : lines) {
        String[] vals = line.split(",");
        if (!vals[0].equals("timestamp")) {
          record = line;
          LocalDate rowDate = LocalDate.parse(vals[0]);
          int diff = rowDate.compareTo(dateAsked);
          if (diff <= 0) {
            found = true;
            break;
          }
        }
      }
      if (!found) {
        throw new IllegalArgumentException("No price data found for " + dateAsked.toString());
      }

      Map<String, Double> shareDetails = new HashMap<>();
      String[] details = record.split(",");

      for (int i = 1; i < details.length; i++) {
        shareDetails.put(keys[i], Double.parseDouble(details[i]));
      }
      return shareDetails;
    } catch (IOException e) {
      throw new IllegalArgumentException("No price data found for " + tickerSymbol);
    }
  }
}
