package models.portfolio;

import java.time.LocalDate;

public class TimeLine {
  private LocalDate startDate;
  private LocalDate endDate;

  public TimeLine (LocalDate startDate, LocalDate endDate) {
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public LocalDate getStartDate() {
    return this.startDate;
  }

  public LocalDate getEndDate() {
    return this.endDate;
  }

}
