import java.io.IOException;
import java.util.Scanner;

import portfolio.Portfolio;
import portfolio.StockPortfolio;
import views.Menu;

public class MainController {
  private final Scanner sc;
  private final Menu menu;
  private Portfolio portfolio;

  public MainController() {
    sc = new Scanner(System.in);
    menu = new Menu();
  }

  void handleMainMenuChoice() {
    int choice;

    do {
      menu.printMainMenu();
      choice = sc.nextInt();
      sc.nextLine();

      switch (choice) {
        case 1:
          System.out.print("\nEnter portfolio name : ");
          String portfolioName = sc.nextLine();
          String username = "idk";
          // check if this portfolio for this user exists or not
          portfolio = new StockPortfolio(username, portfolioName);
          do {
            menu.printCreatePortfolioMenu();
          } while (!this.handleCreatePortfolioChoice());
          break;

        case 2:
          break;

        case 3:
          break;

        case 4:
          break;

        default:
          throw new IllegalArgumentException("Invalid choice. Please choose again");
      }
    } while (choice != 4);
  }

  boolean handleCreatePortfolioChoice() {
    int choice = sc.nextInt();
    boolean shouldExit = false;

    switch (choice) {
      case 1:
        System.out.print("\nEnter ticker symbol of the company you would like to add to this portfolio : ");
        String tickerSymbol = sc.next();
        System.out.print("Enter the number of shares you would like to add : ");
        int quantity = sc.nextInt();
        try {
          portfolio.addShare(tickerSymbol, quantity);
          System.out.println("\nSuccess!");
          break;
        } catch (IOException | IllegalArgumentException e) {
          System.out.println("\n" + e.getMessage());
        }
        break;

      case 2:
        shouldExit = true;
        break;

      default:
        throw new IllegalArgumentException("Invalid choice. Please choose again");
    }
    return shouldExit;
  }

  public static void main(String[] args) {
    MainController mc = new MainController();
    mc.handleMainMenuChoice();
  }
}




