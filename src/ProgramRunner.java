import controllers.Controller;
import controllers.GenericController;
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

    String rootPath = System.getProperty("user.dir");
    String[] temp = rootPath.split("/");

    String commonPath = !temp[temp.length - 1].equals("res")
            ? System.getProperty("user.dir") + "/src/files/" :
            "../res/files/";

    Controller controller = new GenericController(System.in, System.out, commonPath);
    controller.start();
  }
}
