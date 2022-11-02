package models.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class StockApi implements ShareApi {

  private final String path;
  private final Map<String, Double> shareDetails;
  private final List<String> supportedStocks;

  public StockApi() {
    this.shareDetails = new HashMap<>();
    supportedStocks = new ArrayList<>();

    this.path = String.format("%s/src/models/api/supported_stocks/", System.getProperty("user.dir"));
    File dir = new File(path);
    File[] stockFiles = dir.listFiles();
    for (File stockFile : Objects.requireNonNull(stockFiles)) {
      String stockName = stockFile.getName();
      int dotIndex = stockName.lastIndexOf(".");
      String extension = stockName.substring(dotIndex + 1);
      if (extension.equals("csv")) {
        supportedStocks.add(stockName.substring(0, dotIndex));
      }
    }
  }

  @Override
  public Map<String, Double> getShareDetails(String tickerSymbol, LocalDate dateAsked) {
    String supportedTicker = null;

    for (String supportedStock : supportedStocks) {
      if (supportedStock.equalsIgnoreCase(tickerSymbol)) {
        supportedTicker = supportedStock;
        break;
      }
    }

    if (supportedTicker == null) {
      throw new IllegalArgumentException("This ticker is not associated with any company.");
    }

    try {
      Scanner csvReader = new Scanner(new File(String.format("%s%s.csv", this.path, supportedTicker)));
      String[] keys = csvReader.nextLine().split(",");

      String record = null;
      while(csvReader.hasNext()) {
        String line = csvReader.nextLine();
        String[] vals = line.split(",");
        LocalDate rowDate = LocalDate.parse(vals[0]);
        int diff = rowDate.compareTo(dateAsked);
        if(diff <= 0) {
          record = line;
          break;
        }
      }

      if(record == null) {
        throw new RuntimeException("No price data found for " + dateAsked.toString());
      }

      String[] details = record.split(",");
      for(int i = 1; i<keys.length; i++) {
        shareDetails.put(keys[i], Double.parseDouble(details[i]));
      }
      return shareDetails;
    } catch(FileNotFoundException e) {
      throw new IllegalArgumentException("File not found");
    }
  }
}
