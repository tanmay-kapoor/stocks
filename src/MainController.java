import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
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

  public MainController() {
    sc = new Scanner(System.in);
    menu = new Menu();
  }

  void handleMainMenuChoice() throws IOException {
    char choice;

    do {
      menu.printMainMenu();
      choice = sc.next().charAt(0);
      sc.nextLine();

      switch (choice) {
        case '1':
          System.out.print("\nEnter portfolio name : ");
          String portfolioName = sc.nextLine();
          String username = "idk";
          ShareApi api = new AlphaVantageDemo();

          // check if this portfolio for this user exists or not

          portfolio = new StockPortfolio(username, portfolioName, api);
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
          break;

        case '2':
          System.out.print("Enter portfolio name : ");
          portfolioName = sc.nextLine();
          System.out.println();

          // check whether it exists in portfolio names file.

          BufferedReader csvReader = new BufferedReader(new FileReader(String.format("%s.csv", portfolioName)));
          String row;
          while ((row = csvReader.readLine()) != null) {
            System.out.println(row);
          }
          csvReader.close();
          break;

        case '3':
          break;

        default:
          break;
      }
    } while (choice == '1' || choice == '2' || choice == '3');
  }

  boolean handleCreatePortfolioChoice() throws IOException {
    char choice = sc.next().charAt(0);

    if (choice == '1') {
      System.out.print("\nEnter ticker symbol of the company you would like to add to this portfolio : ");
      String tickerSymbol = sc.next();
      System.out.print("Enter the number of shares you would like to add : ");
      int quantity = sc.nextInt();
      try {
        portfolio.addShare(tickerSymbol, quantity);
        System.out.println("\nSuccess!");
        return false;
      } catch (Exception e) {
        System.out.println("\n" + e.getMessage());
      }
    } else {
      portfolio.savePortfolio();
      return true;
    }
    return false;
  }

  public static void main(String[] args) throws IOException {
    MainController mc = new MainController();
    mc.handleMainMenuChoice();
  }
}




