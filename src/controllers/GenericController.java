package controllers;

import java.io.InputStream;
import java.io.PrintStream;

import models.api.AlphaVantage;
import models.api.ShareApi;
import views.StockMenuFlexible;
import views.MainMenu;
import views.MainMenuImpl;
import views.Menu;
import views.StockMenuInflexible;

/**
 * A class that decides weather to create a Portfolio object for Flexible portfolio or
 * inflexible portfolio. It is also the initial controller that `ProgramRunner` uses to start
 * the program.
 */
public class GenericController implements Controller {
  private final InputStream in;
  private final PrintStream out;
  private Menu menu;
  private final String commonPath;

  /**
   * A controller for the class to initialize input and output streams and path where all the
   * portfolio files are going to be accessed and saved.
   *
   * @param in         Input Stream
   * @param out        Output Stream
   * @param commonPath path to access and store portfolio files.
   */
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
    }
    while (choice >= '1' && choice <= '2');
  }
}
