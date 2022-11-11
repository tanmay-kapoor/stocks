package models.portfolio;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import models.Details;
import models.Log;
import models.api.ShareApi;
import models.api.StockApi;

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

  public StockPortfolioFlexible(String portfolioName, LocalDate purchaseDate, Map<String, Log> stocks,
                                String path, ShareApi api) {
    super(portfolioName, purchaseDate, stocks, path, api);
  }


  //can do return err msg
  public boolean PortfolioBasedSell(String ticker, Details details) {
    double sellQty = details.getQuantity();
    LocalDate sellDate = details.getPurchaseDate();

    Log log = stocks.get(ticker);
    Set<Details> detailsSet = log.getDetailsSet();
    double sharesAvailable = getShareQuantityTillDate(detailsSet, sellDate);

    if (sharesAvailable < sellQty) {
      return false;
    }

    for(Details d : detailsSet) {
      if(d.getPurchaseDate().compareTo(sellDate) >= 0) {
        d.setQuantity(d.getQuantity() - sellQty);
      }
    }

    log.setDetailsSet(detailsSet);
    log.setLastSoldDate(sellDate);

    return true;
  }

  double getTxnCommission(String ticker, Details details, double commissionPercent) {
    ShareApi api = new StockApi();

    Map<String, Double> shareDetails = api.getShareDetails(ticker, details.getPurchaseDate());
    double price = shareDetails.get("Close");

    return price * details.getQuantity() * commissionPercent / 100;
  }



  private double getShareQuantityTillDate(Set<Details> detailsSet, LocalDate date) {
    double qtyAvailable = 0;

    for(Details details: detailsSet) {
      if(details.getPurchaseDate().compareTo(date) > 0) {
        break;
      }
      qtyAvailable = details.getQuantity();
    }

    return qtyAvailable;
  }
}
