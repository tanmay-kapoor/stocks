package models;

import java.time.LocalDate;

/**
 * A helper class that is used to store the quantity of a particular stock
 * and the date on which it was added to portfolio.
 */
public class Details {
  private final double quantity;
  private final LocalDate dateCreated;

  /**
   * constructor that initializes the quantity and creation date.
   *
   * @param quantity    number of shares.
   * @param dateCreated date it was added to the portfolio.
   */
  public Details(double quantity, LocalDate dateCreated) {
    this.quantity = quantity;
    this.dateCreated = dateCreated;
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
  public LocalDate getDateCreated() {
    return this.dateCreated;
  }
}
