import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import api.AlphaVantageDemo;
import api.ShareApi;
import portfolio.Portfolio;
import portfolio.StockPortfolio;
import views.Menu;

public class MainController {
  private final Scanner sc;
  private final Menu menu;
  private Portfolio portfolio;
  private final ShareApi api;

  private final File path;
  private File[] allPortfolios;

  public MainController() {
    sc = new Scanner(System.in);
    menu = new Menu();
    api = new AlphaVantageDemo();
    path = new File(System.getProperty("user.dir") + "/src/files/");
    allPortfolios = path.listFiles();
  }

  void handleMainMenuChoice() throws IOException {
    char choice;

    do {
      menu.printMainMenu();
      choice = sc.next().charAt(0);
      sc.nextLine();

      switch (choice) {
        case '1':
          boolean shouldContinue = true;
          String portfolioName;
          do {
            try {
              System.out.print("\nEnter portfolio name : ");
              portfolioName = sc.nextLine();

              // check if this portfolio for this user exists or not
              for (File file : allPortfolios) {
                if (file.getName().equals(portfolioName + ".csv")) {
                  throw new IllegalArgumentException("This portfolio already exists, please use a unique name.");
                }
              }
              shouldContinue = false;

              portfolio = new StockPortfolio(portfolioName, api);
              boolean shouldExit;
              do {
                menu.printCreatePortfolioMenu();
                try {
                  shouldExit = this.handleCreatePortfolioChoice();
                } catch (IllegalArgumentException | IOException e) {
                  System.out.println("\n" + e.getMessage());
                  shouldExit = false;
                }
              } while (!shouldExit);
            } catch (IllegalArgumentException e) {
              System.out.println("\n" + e.getMessage());
            }
          } while(shouldContinue);
          break;

        case '2':
          shouldContinue = true;

          do {
            System.out.print("Enter portfolio name : ");
            portfolioName = sc.nextLine();
            portfolio = new StockPortfolio("idk", new AlphaVantageDemo());

            try {
              portfolio.getPortfolioComposition(portfolioName);
              shouldContinue = false;
            } catch (FileNotFoundException e) {
              System.out.println(e.getMessage() + "\n");
            }
          } while (shouldContinue);
          break;

        case '3':
          break;

        default:
          break;
      }
    } while (choice == '1' || choice == '2' || choice == '3');
  }

  boolean handleCreatePortfolioChoice() throws RuntimeException, IOException {
    char choice = sc.next().charAt(0);

    if (choice == '1') {
      System.out.print("\nEnter ticker symbol of the company you would like to add to this portfolio : ");
      String tickerSymbol = sc.next();

      try {
        api.getShareDetails(tickerSymbol, "2022-10-26");

        System.out.print("Enter the number of shares you would like to add : ");
        int quantity = sc.nextInt();

        if (quantity < 1) {
          throw new IllegalArgumentException("Number of shares cannot be less than 1. Please enter a valid quantity.");
        }
        portfolio.addShare(tickerSymbol, quantity);
        System.out.println("\nSuccess!");
        return false;
      } catch (RuntimeException e) {
        System.out.println("\n" + e.getMessage());
      }
    } else {
      portfolio.savePortfolio();
      allPortfolios = path.listFiles();
      return true;
    }
    return false;
  }

  public static void main(String[] args) throws IOException {
    MainController mc = new MainController();
    mc.handleMainMenuChoice();
  }
}




