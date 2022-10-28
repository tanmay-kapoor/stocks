package views;

public class Menu implements MainMenu {

  public void printMainMenu() {
    System.out.print("\n[1] Create Portfolio.\n" +
            "[2] See portfolio composition.\n" +
            "[3] Check portfolio value.\n" +
            "[4] Exit\n" +
            "Enter your choice : ");
  }

  @Override
  public void printCheckPortfolioValueMenu() {

  }

  @Override
  public void printCreatePortfolioMenu() {
    System.out.print("\n1. Add share to your portfolio.\n" +
            "2. Save this portfolio\n" +
            "Enter your choice : ");
  }
}
