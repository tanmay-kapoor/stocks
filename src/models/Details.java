package models;

import java.time.LocalDate;

/**
 * A helper class that is used to store the quantity of a particular stock
 * and the date on which it was added to portfolio.
 */
public class Details {
  private final double quantity;
  private final LocalDate purchaseDate;
  private LocalDate lastSold;

  /**
   * constructor that initializes the quantity and creation date.
   *
   * @param quantity    number of shares.
   * @param purchaseDate date it was added to the portfolio.
   */
  public Details(double quantity, LocalDate purchaseDate) {
    this.quantity = quantity;
    this.purchaseDate = purchaseDate;
    this.lastSold = null;
  }

  /**
   * A method to get the quantity of a particular stock.
   *
   * @return number of shares of the stock.
   */
  public double getQuantity() {
    return this.quantity;
  }

  /**
   * A method to get the date the stock was first added to the portfolio.
   *
   * @return the date it was first added to the portfolio.
   */
  public LocalDate getPurchaseDate() {
    return this.purchaseDate;
  }

  public void setLastSold(LocalDate sellDate) {
    this.lastSold = sellDate;
  }
}
