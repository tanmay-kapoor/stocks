package models;

import java.time.LocalDate;
import java.util.Set;

/**
 * A class the keeps the log about all stocks in portfolio.
 * A log stores the quantity of stock in portfolio, date of purchase, date it was last sold.
 */
public class Log {
  private Set<Details> detailsSet;
  private LocalDate lastSoldDate;

  /**
   * Constructor for the class that initializes set of details and last sold date.
   *
   * @param detailsSet   details of the stock.
   * @param lastSoldDate date the stock was last sold.
   */
  public Log(Set<Details> detailsSet, LocalDate lastSoldDate) {
    this.detailsSet = detailsSet;
    this.lastSoldDate = lastSoldDate;
  }

  /**
   * Fetches the set of details for a stock.
   *
   * @return set containing the details about the stock.
   */
  public Set<Details> getDetailsSet() {
    return this.detailsSet;
  }

  /**
   * fetches the date the stock was last sold from the portfolio.
   *
   * @return date it was last sold.
   */
  public LocalDate getLastSoldDate() {
    return this.lastSoldDate;
  }

  /**
   * Changes the details about the stock in portfolio.
   *
   * @param detailsSet new `Details` object to be stored.
   */
  public void setDetailsSet(Set<Details> detailsSet) {
    this.detailsSet = detailsSet;
  }

  /**
   * sets the date stock was last sold.
   *
   * @param lastSoldDate last sold date.
   */
  public void setLastSoldDate(LocalDate lastSoldDate) {
    this.lastSoldDate = lastSoldDate;
  }
}
