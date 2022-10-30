import java.io.IOException;

import controllers.Controller;
import controllers.StockController;
import models.api.AlphaVantageDemo;
import models.api.ShareApi;
import views.Menu;
import views.StockMenu;

public class ProgramRunner {
  public static void main(String[] args) throws IOException {

    Menu menu = new StockMenu();
    ShareApi api = new AlphaVantageDemo();
    String folder = "stocks";

    Controller controller = new StockController(menu, api, folder);
    controller.go();
  }
}
