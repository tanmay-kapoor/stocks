import controllers.Controller;
import controllers.StockController;
import models.api.AlphaVantageDemo;
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
//    ShareApi api = new AlphaVantageDemo();
    ShareApi api = new StockApi();
    String folder = "stocks";

    Controller controller = new StockController(menu, api, folder);
    controller.go();
  }
}
