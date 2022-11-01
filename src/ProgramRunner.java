import controllers.Controller;
import controllers.StockController;
import models.api.AlphaVantageDemo;
import models.api.ShareApi;
import views.Menu;
import views.StockMenu;

/**
 * This is where the1
 */
public class ProgramRunner {
  public static void main(String[] args) {

    Menu menu = new StockMenu(System.in, System.out);
    ShareApi api = new AlphaVantageDemo();
    String folder = "stocks";

    Controller controller = new StockController(menu, api, folder);
    controller.go();
  }
}
