package views;

import java.util.Scanner;

abstract class AbstractMenu implements Menu {
  protected final Scanner sc;

  protected AbstractMenu() {
    sc = new Scanner(System.in);
  }

  @Override
  public char getMainMenuChoice() {
    this.print("\n1. Create Portfolio.\n" +
            "2. See portfolio composition.\n" +
            "3. Check portfolio value.\n" +
            "Press any other key to exit.\n" +
            "\nEnter your choice : ");
    char choice = sc.next().charAt(0);
    sc.nextLine();
    return choice;
  }

  @Override
  public String getPortfolioName() {
    this.print("\nEnter portfolio name : ");
    return sc.nextLine();
  }

  @Override
  public void printMessage(String msg) {
    this.print(msg + "\n");
  }

  @Override
  public char getCreatePortfolioChoice() {
    this.print("\n1. Add a share to your portfolio.\n" +
            "Press any other key to exit.\n" +
            "\nEnter your choice : ");
    char choice = sc.next().charAt(0);
    sc.nextLine();
    return choice;
  }

  @Override
  public String getTickerSymbol() {
    this.print("\nEnter ticker symbol of the company you would like to add to this portfolio : ");
    String portfolioName = sc.next();
    sc.nextLine();
    return portfolioName;
  }

  @Override
  public double getQuantity() {
    this.print("Enter the number of shares you would like to add : ");
    double quantity = sc.nextDouble();
    sc.nextLine();
    return quantity;
  }

  @Override
  public String getDateForCheckValue() {
    String date = "today";
    char choice;
    do {
      this.print("\nCheck value for\n1. Today\n2. Custom date\nEnter choice : ");
      choice = sc.next().charAt(0);
      sc.nextLine();
      switch (choice) {
        case '1':
          break;

        case '2':
          this.print("\nEnter date in YYYY-MM-DD format : ");
          date = sc.next();
          sc.nextLine();
          break;

        default:
          this.print("\nInvalid choice. Choose again\n");
      }
    } while (choice != '1' && choice != '2');
    return date;
  }

  private void print(String msg) {
    System.out.print(msg);
  }
}
