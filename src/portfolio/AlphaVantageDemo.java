package portfolio;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AlphaVantageDemo {
  Map<String, Double> getShareDetails(String tickerSymbol, String timestamp) {
    //the API key needed to use this web service.
    //Please get your own free API key here: https://www.alphavantage.co/
    //Please look at documentation here: https://www.alphavantage.co/documentation/
    String apiKey = "NAO61MQDSY9EPTN";
    String stockSymbol = tickerSymbol; //ticker symbol for Google
    URL url = null;

    try {
      /*
      create the URL. This is the query to the web service. The query string
      includes the type of query (DAILY stock prices), stock symbol to be
      looked up, the API key and the format of the returned
      data (comma-separated values:csv). This service also supports JSON
      which you are welcome to use.
       */
      url = new URL("https://www.alphavantage"
              + ".co/query?function=TIME_SERIES_DAILY"
              + "&outputsize=compact"
              + "&symbol"
              + "=" + stockSymbol + "&apikey=" + apiKey + "&datatype=csv");
    } catch (MalformedURLException e) {
      throw new RuntimeException("the alphavantage API has either changed or "
              + "no longer works");
    }

    InputStream in = null;
    StringBuilder output = new StringBuilder();

    try {
      /*
      Execute this query. This returns an InputStream object.
      In the csv format, it returns several lines, each line being separated
      by commas. Each line contains the date, price at opening time, highest
      price for that date, lowest price for that date, price at closing time
      and the volume of trade (no. of shares bought/sold) on that date.

      This is printed below.
       */
      in = url.openStream();
      int b = in.read();
      while (b != 10) {
        b = in.read();
      }

      while (b != -1) {
        b = in.read();
        if(b == 10) {
          String date = output.toString().split(",")[0];
          if(timestamp.equals(date)) {
            break;
          } else {
            output = new StringBuilder();
          }
        } else {
          output.append((char) b);
        }
      }
    } catch (IOException e) {
      throw new IllegalArgumentException("No price data found for " + stockSymbol);
    }

    Map<String, Double> shareDetails = new HashMap<>();
    String[] details = output.toString().split(",");
    String[] keys = {"timestamp", "open", "high", "low", "close", "volume"};

    for(int i = 1; i<details.length; i++) {
      shareDetails.put(keys[i], Double.parseDouble(details[i]));
    }
    return shareDetails;
  }
}
