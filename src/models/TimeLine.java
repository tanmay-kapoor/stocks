package models;

import java.time.LocalDate;

/**
 * Class to store start date and end date of the dca in once place
 */
public class TimeLine {
  private final LocalDate startDate;
  private final LocalDate endDate;

  /**
   * Constructor to initialize start date and end date with the specified values.
   *
   * @param startDate Start date of dca strategy.
   * @param endDate   End date of dca strategy.
   */
  public TimeLine(LocalDate startDate, LocalDate endDate) {
    this.startDate = startDate;
    this.endDate = endDate;
  }

  /**
   * Gets the current value of start date.
   *
   * @return LocalDate representing start date of dca strategy.
   */
  public LocalDate getStartDate() {
    return this.startDate;
  }

  /**
   * Gets the current value of end date.
   *
   * @return LocalDate representing end date of dca strategy.
   */
  public LocalDate getEndDate() {
    return this.endDate;
  }

}
