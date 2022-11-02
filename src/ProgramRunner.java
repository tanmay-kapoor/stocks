import controllers.Controller;
import controllers.StockController;
import models.api.ShareApi;
import models.api.StockApi;
import views.Menu;
import views.StockMenu;

/**
 * This is where the1
 */
public class ProgramRunner {
  public static void main(String[] args) {

    Menu menu = new StockMenu(System.in, System.out);
    ShareApi api = new StockApi();
    String path = System.getProperty("user.dir") + "/src/files/stocks/";

    Controller controller = new StockController(menu, api, path);
    controller.go();
  }
}
