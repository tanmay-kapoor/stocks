package models.portfolio;

import java.time.LocalDate;
import java.util.Set;

import models.Details;
import models.Log;
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

    Log log = stocks.get(ticker);
    Set<Details> detailsSet = log.getDetailsSet();
    double sharesAvailable = getShareQuantityTillDate(detailsSet, sellDate);

    if (sharesAvailable < quantity) {
      return false;
    }

    for(Details d : detailsSet) {
      if(d.getPurchaseDate().compareTo(sellDate) >= 0) {
        d.setQuantity(d.getQuantity() - quantity);
      }
    }

    log.setDetailsSet(detailsSet);
    log.setLastSoldDate(sellDate);

    return true;
  }


  double getShareQuantityTillDate(Set<Details> detailsSet, LocalDate date) {
    double qtyAvailable = 0;

    for(Details details: detailsSet) {
      if(details.getPurchaseDate().compareTo(date) > 0) {
        break;
      }
      qtyAvailable += details.getQuantity();
    }



    return qtyAvailable;
  }
}
