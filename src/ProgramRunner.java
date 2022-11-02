import controllers.Controller;
import controllers.StockController;
import models.api.ShareApi;
import models.api.StockApi;
import views.Menu;
import views.StockMenu;

/**
 * This is where the starter code lies. When the user starts the program, the main method of this
 * class is called. It initialises the configurations required to run the program.
 */
public class ProgramRunner {
  /**
   * Starter of the program.
   *
   * @param args boilerplate code to run main. Used for command line argument if any.
   */
  public static void main(String[] args) {

    Menu menu = new StockMenu(System.in, System.out);
    ShareApi api = new StockApi();
    String path = System.getProperty("user.dir") + "/src/files/stocks/";

    Controller controller = new StockController(menu, api, path);
    controller.start();
  }
}
