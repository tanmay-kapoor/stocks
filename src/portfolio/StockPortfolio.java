package portfolio;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import api.ShareApi;

public class StockPortfolio implements Portfolio {

  private final String portfolioName;
  private final Map<String, Double> stocks;
  private final ShareApi api;
  private final String path;

  public StockPortfolio(String portfolioName, ShareApi api) {
    this.portfolioName = portfolioName;
    this.api = api;
    path = System.getProperty("user.dir") + "/src/files/%s.csv";
    this.stocks = new HashMap<>();
    ;
  }

  @Override
  public void addShare(String tickerSymbol, double quantity) throws RuntimeException {
    if (stocks.containsKey(tickerSymbol)) {
      throw new IllegalArgumentException("This stock already exists in your portfolio. Please enter a new ticker.");
    }

    stocks.put(tickerSymbol, quantity);
  }

  @Override
  public double getValue() {
    return getValue(java.time.LocalDate.now().toString());
  }

  @Override
  public double getValue(String date) {
    double totalValue = 0.0;

    for (String tickerSymbol : stocks.keySet()) {
      Map<String, Double> shareDetails = api.getShareDetails(tickerSymbol, date);
      totalValue = totalValue + (shareDetails.get("close") * stocks.get(tickerSymbol));
    }

    return totalValue;
  }

  @Override
  public Map<String, Double> getPortfolioComposition(String portfolioName) throws IOException {
    System.out.println();

    // check whether it exists in portfolio names file.

    Scanner csvReader = new Scanner(new File(String.format(path, portfolioName)));
    String row;
    while (csvReader.hasNext()) {
      System.out.println(csvReader.nextLine());
    }
    csvReader.close();
    return null;
  }

  @Override
  public void savePortfolio() throws IOException {
    String fileName = String.format(path, portfolioName);
    FileWriter csvWriter = new FileWriter(fileName);

    csvWriter.append("share,quantity\n");
    for (String share : stocks.keySet()) {
      csvWriter.append(share).append(",").append(String.valueOf(stocks.get(share))).append("\n");
    }

    csvWriter.flush();
    csvWriter.close();
  }
}
