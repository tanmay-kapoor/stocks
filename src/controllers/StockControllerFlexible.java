package controllers;

import java.sql.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import models.Details;
import models.Log;
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
  protected char getLastOption() {
    return '4';
  }

  @Override
  protected void handleBuySellOption() {
    commonStuff(Function.BuySell);
  }

  @Override
  protected void handleBuySellInPortfolio(String name) {
    Portfolio portfolio = allPortfolioObjects.get(name);
    Map<String, Log> portfolioComposition = portfolio.getComposition(LocalDate.now());

    char ch;
    do {
      ch = menu.getBuySellChoice();
      String ticker;
      Details d;

      switch (ch) {
        case '1':
          try {
            ticker = menu.getTickerSymbol().toUpperCase();
            api.getShareDetails(ticker, LocalDate.now());
            d = getDetails();
            portfolio.buy(ticker, d);
          } catch (IllegalArgumentException e) {
            menu.printMessage("\n" + e.getMessage());
          }
          break;

        case '2':
          ticker = menu.getTickerSymbol().toUpperCase();
          if (!portfolioComposition.containsKey(ticker)) {
            menu.printMessage("\nCannot sell ticker that is not in portfolio");
          } else {
            d = getDetails();
            System.out.println("\nnow do sell stuff");
          }
          break;

        default:
          portfolio.savePortfolio();
          break;
      }
    } while (ch >= '1' && ch <= '2');
  }
}
