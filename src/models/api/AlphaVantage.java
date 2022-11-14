package models.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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

//    if (tickerDetails.containsKey(tickerSymbol)) {
//      Map<LocalDate, Map<String, Double>> allDates = tickerDetails.get(tickerSymbol);
//      if (allDates.containsKey(dateAsked)) {
//        return allDates.get(dateAsked);
//      } else {
//        URL url;
//
//        try {
//          url = new URL("https://www.alphavantage"
//                  + ".co/query?function=TIME_SERIES_DAILY"
//                  + "&outputsize=full"
//                  + "&symbol"
//                  + "=" + tickerSymbol + "&apikey=" + apiKey + "&datatype=csv");
//
//          InputStream in = url.openStream();
//          Scanner sc = new Scanner(in);
//          sc.nextLine();
//
//          while (sc.hasNext()) {
//            String line = sc.nextLine();
//            String[] vals = line.split(",");
//            LocalDate thisDate = LocalDate.parse(vals[0]);
//            System.out.println(Arrays.toString(vals));
//            if (!allDates.containsKey(thisDate)) {
//              assignValuesToHashMap(tickerSymbol, allDates, vals, thisDate);
//            } else {
//              return allDates.get(dateAsked);
//            }
//          }
//
//        } catch (MalformedURLException e) {
//          throw new RuntimeException("the AlphaVantage API has either changed or "
//                  + "no longer works");
//        } catch (IOException e) {
//          throw new RuntimeException("No price data found for " + tickerSymbol);
//        }
//      }
//    }

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

      Map<LocalDate, Map<String, Double>> allDates = new HashMap<>();
      boolean found = false;
      for (String line : lines) {
        String[] vals = line.split(",");
        if (!vals[0].equals("timestamp")) {
          record = line;
          LocalDate rowDate = LocalDate.parse(vals[0]);

//          assignValuesToHashMap(tickerSymbol, allDates, vals, rowDate);

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

//  private void assignValuesToHashMap(String tickerSymbol, Map<LocalDate, Map<String, Double>> allDates, String[] vals, LocalDate thisDate) {
//    Map<String, Double> values = new HashMap<>();
//    values.put("open", Double.parseDouble(vals[1]));
//    values.put("high", Double.parseDouble(vals[2]));
//    values.put("low", Double.parseDouble(vals[3]));
//    values.put("close", Double.parseDouble(vals[4]));
//    values.put("volume", Double.parseDouble(vals[5]));
//    allDates.put(thisDate, values);
//    tickerDetails.put(tickerSymbol, allDates);
//  }
}
