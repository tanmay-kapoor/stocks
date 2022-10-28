package views;

public class Menu implements MainMenu {

  public void printMainMenu() {
    System.out.print("\n1. Create Portfolio.\n" +
            "2. See portfolio composition.\n" +
            "3. Check portfolio value.\n" +
            "Press any other key to exit.\n" +
            "\nEnter your choice : ");
  }

  @Override
  public void printCheckPortfolioValueMenu() {

  }

  @Override
  public void printCreatePortfolioMenu() {
    System.out.print("\n1. Add a share to your portfolio.\n" +
            "Press any other key to exit.\n" +
            "\nEnter your choice : ");
  }
}
