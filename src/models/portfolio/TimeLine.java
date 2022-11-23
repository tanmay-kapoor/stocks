package models.portfolio;

import java.time.LocalDate;

public class TimeLine {
  LocalDate startDate;
  LocalDate endDate;

  public TimeLine (LocalDate startDate, LocalDate endDate) {
    this.startDate = startDate;
    this.endDate = endDate;
  }

}
