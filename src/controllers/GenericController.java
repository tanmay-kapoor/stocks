package controllers;

import java.io.InputStream;
import java.io.PrintStream;

import models.api.ShareApi;
import models.api.StockApi;
import views.Menu;
import views.StockMenu;

public class GenericController implements Controller {
  private final Menu menu;
  private final String commonPath;

  public GenericController(InputStream in, PrintStream out, String commonPath) {
    this.menu = new StockMenu(in, out);
    this.commonPath = commonPath;
  }

  @Override
  public void start() {
    char choice;

    do {
      choice = menu.getPortfolioType();

      switch (choice) {
        case '1':
          callStockController(commonPath + "stocks/flexible/");
          break;

        case '2':
          callStockController(commonPath + "stocks/inflexible/");
          break;

        default:
          break;
      }
    } while (choice >= '1' && choice <= '2');
  }

  private void callStockController(String path) {
    ShareApi api = new StockApi();
    new StockController(menu, api, path).start();
  }
}
