package models.portfolio;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import models.Details;
import models.api.ShareApi;

public class StockPortfolioFlexible extends AbstractPortfolio {
  /**
   * Constructor for the class that initializes the name of the portfolio,
   * date it was created and the API that it is supposed to use for the fetching relevant data.
   *
   * @param portfolioName name of the portfolio.
   * @param purchaseDate  creation date of the portfolio.
   * @param path
   * @param api           API is meant to be used.
   */
  public StockPortfolioFlexible(String portfolioName, LocalDate purchaseDate,
                                String path, ShareApi api) {
    super(portfolioName, purchaseDate, path, api);
  }


  //can do return err msg
  public boolean sell(String ticker, double quantity, LocalDate sellDate) {
    if (stocks.containsKey(ticker)) {
      return false;
    }

    TreeSet<Details> detailsList = stocks.get(ticker);
    double sharesAvailable = getShareQuantityTillDate(detailsList, sellDate);

    if (sharesAvailable < quantity) {
      return false;
    }

    //preform all checks above, only then do update portfolio
    this.updatePortfolio(ticker, quantity * -1, sellDate);
    return true;
  }


  double getShareQuantityTillDate(TreeSet<Details> detailsList, LocalDate date) {
    double qtyAvailable = 0;

    for(Details details: detailsList) {
      if(details.getPurchaseDate().compareTo(date) > 0) {
        break;
      }
      qtyAvailable += details.getQuantity();
    }

    return qtyAvailable;
  }
}
