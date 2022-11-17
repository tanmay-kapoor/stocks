package models.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * An API by Alpha Vantage that is used to get necessary data points about stocks which are
 * registered on the NASDAQ. It is primarily used to fetch stock's OHLC and its traded
 * volume on a particular date.
 */
public class AlphaVantage implements ShareApi {
  private final String apiKey;
  private final Map<String, Map<LocalDate, Map<String, Double>>> tickerDetails;

  /**
   * The API key that is going to be used by the API.
   */
  public AlphaVantage() {
    this.apiKey = "XXT841KPRC9FYXD1";
    this.tickerDetails = new HashMap<>();
  }

  @Override
  public Map<String, Double> getShareDetails(String tickerSymbol, LocalDate dateAsked) {

    if (dateAsked.compareTo(LocalDate.now()) > 0) {
      throw new IllegalArgumentException("Cannot get value for a future date!");
    }

    if (tickerDetails.containsKey(tickerSymbol)) {
      if (tickerDetails.get(tickerSymbol).containsKey(dateAsked)) {
        return tickerDetails.get(tickerSymbol).get(dateAsked);
      }
    }

    URL url;

    try {
      url = new URL("https://www.alphavantage"
              + ".co/query?function=TIME_SERIES_DAILY"
              + "&outputsize=full"
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
      String prevLine = null;
      for (int i = 1; i < lines.length; i++) {
        String line = lines[i];
        String[] vals = line.split(",");
        record = line;
        LocalDate rowDate = LocalDate.parse(vals[0]);
        if (tickerDetails.containsKey(tickerSymbol)
                && tickerDetails.get(tickerSymbol).containsKey(rowDate)) {
          if (i == 1 && rowDate.compareTo(dateAsked) < 0) {
            found = true;
            break;
          }
          continue;
        }

        Map<String, Double> values = new HashMap<>();
        assignValues(vals, values);
        Map<LocalDate, Map<String, Double>> allDates;
        if (tickerDetails.containsKey(tickerSymbol)) {
          allDates = tickerDetails.get(tickerSymbol);
        } else {
          allDates = new HashMap<>();
        }
        allDates.put(rowDate, values);
        tickerDetails.put(tickerSymbol, allDates);

        int diff = rowDate.compareTo(dateAsked);

        if (prevLine != null) {
          String[] v = prevLine.split(",");
          LocalDate prevDate = LocalDate.parse(v[0]);
          if (DAYS.between(rowDate, prevDate) > 1) {
            LocalDate curr = rowDate.plusDays(1);
            while (!curr.equals(prevDate)) {
              allDates.put(curr, values);
              curr = curr.plusDays(1);
            }
            tickerDetails.put(tickerSymbol, allDates);
          }
        }

        if (diff <= 0) {
          found = true;
          break;
        }
        prevLine = line;
      }
      if (!found) {
        throw new IllegalArgumentException("No price data found for " + dateAsked);
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

  private void assignValues(String[] vals, Map<String, Double> values) {
    values.put("open", Double.parseDouble(vals[1]));
    values.put("high", Double.parseDouble(vals[2]));
    values.put("low", Double.parseDouble(vals[3]));
    values.put("close", Double.parseDouble(vals[4]));
    values.put("volume", Double.parseDouble(vals[5]));
  }
}
