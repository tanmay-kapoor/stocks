package models.portfolio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import models.Details;
import models.api.ShareApi;

/**
 * A class that is an extension of <code>Portfolio</code> class. This class specifically
 * deals with the portfolio that store shares in the form of stocks listed in the NASDAQ.
 */
abstract class AbstractPortfolio implements Portfolio {
  protected final String portfolioName;
  private final Map<String, ArrayList<Details>> stocks;
  private final ShareApi api;
  private final String path;

  /**
   * Constructor for the class that initializes the name of the portfolio,
   * date it was created and the API that it is supposed to use for the fetching relevant data.
   *
   * @param portfolioName name of the portfolio.
   * @param purchaseDate   creation date of the portfolio.
   * @param api           API is meant to be used.
   */
  protected AbstractPortfolio(String portfolioName, LocalDate purchaseDate, String path, ShareApi api) {
    this.portfolioName = portfolioName;
    this.api = api;
    this.path = path;
    this.stocks = new HashMap<>();
  }

  @Override
  public void addShare(String tickerSymbol, double quantity, LocalDate purchaseDate) {
    if (quantity < 0.0) {
      throw new IllegalArgumentException("Quantity should be grater than 0.");
    }

    //new
    tickerSymbol = tickerSymbol.toUpperCase();
    ArrayList<Details> detailsList;

    if(stocks.containsKey(tickerSymbol)) {
      detailsList = stocks.get(tickerSymbol);
      boolean purchaseDateExists = false;

      //checking if we have purchased the stock on same date
      for(Details details: detailsList) {
        if (details.getPurchaseDate() == purchaseDate) {
          details = new Details(details.getQuantity() + quantity,
                  details.getPurchaseDate());
          purchaseDateExists = true;
        }
      }

      //add new Details object to the list only if we have not purchased
      // the stock on same date before
      if(!purchaseDateExists) {
        detailsList.add(new Details(quantity, purchaseDate));
      }
    } else {
      detailsList = new ArrayList<>();
      detailsList.add(new Details(quantity, purchaseDate));
    }

    stocks.put(tickerSymbol, detailsList);
  }

  @Override
  public double getValue() {
    return getValue(LocalDate.now());
  }

  @Override
  public double getValue(LocalDate date) throws RuntimeException {
    double totalValue = 0.0;
    for (String tickerSymbol : stocks.keySet()) {
      ArrayList<Details> detailsList = stocks.get(tickerSymbol);

      for(Details details : detailsList) {
        Map<String, Double> shareDetails = api.getShareDetails(tickerSymbol, date);
        totalValue += shareDetails.get("close") * details.getQuantity();
      }
    }

    return totalValue;
  }

  @Override
  public Map<String, ArrayList<Details>> getComposition() {
    return new HashMap<>(stocks);
  }

  @Override
  public boolean savePortfolio() {
//    if (stocks.size() == 0) {
//      return false;
//    }
//
//    try {
//      Files.createDirectories(Paths.get(this.path));
//      String fileName = String.format(path + "%s.csv", portfolioName);
//      FileWriter csvWriter = new FileWriter(fileName);
//      csvWriter.append("share,quantity,purchaseDate\n");
//      for (String share : stocks.keySet()) {
//        Details details = stocks.get(share);
//        csvWriter.append(share)
//                .append(",")
//                .append(String.valueOf(details.getQuantity()))
//                .append(",")
//                .append(details.getPurchaseDate().toString())
//                .append("\n");
//      }
//
//      csvWriter.flush();
//      csvWriter.close();
//      return true;
//    } catch (IOException e) {
//      throw new RuntimeException("Something went wrong!");
//    }
    return false;
  }

}
