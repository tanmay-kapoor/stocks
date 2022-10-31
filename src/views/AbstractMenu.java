package views;

import java.io.IOException;
import java.util.Scanner;

abstract class AbstractMenu implements Menu {
  protected final Readable in;
  protected final Appendable out;
  protected final Scanner sc;

  protected AbstractMenu(Readable in, Appendable out) {
    this.in = in;
    this.out = out;
    sc = new Scanner(this.in);
  }

  @Override
  public char getMainMenuChoice() throws IOException {
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
  public String getPortfolioName() throws IOException {
    this.print("\nEnter portfolio name : ");
    return sc.nextLine();
  }

  @Override
  public void printMessage(String msg) throws IOException {
    this.print(msg + "\n");
  }

  @Override
  public char getCreatePortfolioChoice() throws IOException {
    this.print("\n1. Add a share to your portfolio.\n" +
            "Press any other key to exit.\n" +
            "\nEnter your choice : ");
    char choice = sc.next().charAt(0);
    sc.nextLine();
    return choice;
  }

  @Override
  public String getTickerSymbol() throws IOException {
    this.print("\nEnter ticker symbol of the company you would like to add to this portfolio : ");
    String portfolioName = sc.next();
    sc.nextLine();
    return portfolioName;
  }

  @Override
  public double getQuantity() throws IOException {
    this.print("Enter the number of shares you would like to add : ");
    double quantity = sc.nextDouble();
    sc.nextLine();
    return quantity;
  }

  @Override
  public String getDateForCheckValue() throws IOException {
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

  private void print(String msg) throws IOException {
    this.out.append(msg);
  }
}
