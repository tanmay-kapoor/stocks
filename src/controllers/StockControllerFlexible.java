package controllers;

import java.time.LocalDate;

import models.api.ShareApi;
import models.portfolio.Portfolio;
import models.portfolio.StockPortfolioFlexible;
import views.Menu;

public class StockControllerFlexible extends AbstractController {
  protected StockControllerFlexible(Menu menu, ShareApi api, String path) {
    super(menu, api, path);
  }

  @Override
  protected Portfolio createPortfolio(String portfolioName, LocalDate purchaseDate) {
    return new StockPortfolioFlexible(portfolioName, purchaseDate, path, api);
  }

  @Override
  protected LocalDate getPurchaseDate() {
    return LocalDate.parse(menu.getDateForValue());
  }

  @Override
  protected void handleBuySellOption() {
    char choice;
    do {
      choice = menu.getBuySellChoice();
      switch (choice) {
        case '1':
          break;

        case '2':
          break;

        default:
          break;
      }
    } while (choice >= '1' && choice <= '2');
  }

  @Override
  protected char getLastOption() {
    return '4';
  }
}
