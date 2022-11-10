package controllers;

import java.io.InputStream;
import java.io.PrintStream;

import models.api.AlphaVantage;
import models.api.ShareApi;
import models.api.StockApi;
import views.StockMenuFlexible;
import views.MainMenu;
import views.MainMenuImpl;
import views.Menu;
import views.StockMenuInflexible;

public class GenericController implements Controller {
  private final InputStream in;
  private final PrintStream out;
  private Menu menu;
  private final String commonPath;

  public GenericController(InputStream in, PrintStream out, String commonPath) {
    this.in = in;
    this.out = out;
    this.commonPath = commonPath;
  }

  @Override
  public void start() {
    ShareApi api;
    String path;
    MainMenu mainMenu = new MainMenuImpl(this.in, this.out);

    char choice;
    do {
      choice = mainMenu.getPortfolioType();

      switch (choice) {
        case '1':
          this.menu = new StockMenuFlexible(this.in, this.out);
          api = new AlphaVantage();
          path = this.commonPath + "stocks/flexible/";
          new StockControllerFlexible(menu, api, path).start();
          break;

        case '2':
          this.menu = new StockMenuInflexible(this.in, this.out);
          api = new AlphaVantage();
          path = this.commonPath + "stocks/inflexible/";
          new StockControllerInflexible(menu, api, path).start();
          break;

        default:
          break;
      }
    } while (choice >= '1' && choice <= '2');
  }
}
