import java.io.IOException;
import java.io.InputStreamReader;

import controllers.Controller;
import controllers.StockController;
import models.api.AlphaVantageDemo;
import models.api.ShareApi;
import views.Menu;
import views.StockMenu;

public class ProgramRunner {
  public static void main(String[] args) throws IOException {

    Menu menu = new StockMenu(new InputStreamReader(System.in), System.out);
    ShareApi api = new AlphaVantageDemo();
    String folder = "stocks";

    Controller controller = new StockController(menu, api, folder);
    controller.go();
  }
}
