package controllers;

import java.io.InputStream;
import java.io.PrintStream;

import models.api.ShareApi;
import views.Gui;
import views.MainMenu;
import views.MainMenuImpl;
import views.Menu;
import views.MenuGuiFlexible;
import views.StockMenuFlexible;
import views.StockMenuInflexible;
import views.UiOption;
import views.UiOptionImpl;

/**
 * A class that decides weather to create a Portfolio object for Flexible portfolio or
 * inflexible portfolio. It is also the initial controller that `ProgramRunner` uses to start
 * the program.
 */
public class GenericController implements Controller {
  private final InputStream in;
  private final PrintStream out;
  private final ShareApi api;
  private final String commonPath;

  /**
   * A controller for the class to initialize input and output streams and path where all the
   * portfolio files are going to be accessed and saved.
   *
   * @param in         Input Stream
   * @param out        Output Stream
   * @param commonPath path to access and store portfolio files.
   */
  public GenericController(InputStream in, PrintStream out, ShareApi api, String commonPath) {
    this.in = in;
    this.out = out;
    this.api = api;
    this.commonPath = commonPath;
  }

  @Override
  public void start() {
    UiOption uiOption;
    MainMenu mainMenu;

    char ch;
    do {
      uiOption = new UiOptionImpl(this.in, this.out);
      ch = uiOption.getUiOption();
      char choice;

      switch (ch) {
//        case '1':
//          mainMenu = new MainMenuImplGui("Choose Portfolio Type");
//          do {
//            choice = mainMenu.getPortfolioType();
//            switch (choice) {
//              case '1':
//                handleFlexibleSelected(new StockMenuFlexibleGui());
//                break;
//
//              case '2':
//                handleInflexibleSelected(new StockMenuInflexible(this.in, this.out));
//                break;
//
//              default:
//                break;
//            }
//          } while (choice >= '1' && choice <= '2');
//          break;

        case '1':
          Features controller = new FeaturesImpl(this.api, this.commonPath);
          Menu menuGui = new MenuGuiFlexible(controller, "Choose portfolio type");
          controller.setView(menuGui);

        case '2':
          mainMenu = new MainMenuImpl(this.in, this.out);
          do {
            choice = mainMenu.getPortfolioType();
            switch (choice) {
              case '1':
                handleFlexibleSelected(new StockMenuFlexible(this.in, this.out));
                break;

              case '2':
                handleInflexibleSelected(new StockMenuInflexible(this.in, this.out));
                break;

              default:
                break;
            }
          }
          while (choice >= '1' && choice <= '2');
          break;

        default:
          break;
      }
    } while (ch >= '1' && ch <= '2');
  }

  private void handleFlexibleSelected(Menu menu) {
    String path = this.commonPath + "stocks/flexible/";
    new StockControllerFlexible(in, menu, api, path).start();
  }

  private void handleInflexibleSelected(Menu menu) {
    String path = this.commonPath + "stocks/inflexible/";
    new StockControllerInflexible(in, menu, api, path).start();
  }
}
