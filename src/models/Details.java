package models;

import java.time.LocalDate;

public class Details {
  private final double quantity;
  private final LocalDate dateCreated;

  public Details(double quantity, LocalDate dateCreated) {
    this.quantity = quantity;
    this.dateCreated = dateCreated;
  }

  public double getQuantity() {
    return this.quantity;
  }

  public LocalDate getDateCreated() {
    return this.dateCreated;
  }
}
