package models;

import java.time.LocalDate;
import java.util.Set;

public class Log {
  private Set<Details> detailsSet;
  private LocalDate lastSoldDate;

  public Log(Set<Details> detailsSet, LocalDate lastSoldDate) {
    this.detailsSet = detailsSet;
    this.lastSoldDate = lastSoldDate;
  }

  public Set<Details> getDetailsSet() {
    return this.detailsSet;
  }

  public LocalDate getLastSoldDate() {
    return this.lastSoldDate;
  }

  public void setDetailsSet(Set<Details> detailsSet) {
    this.detailsSet = detailsSet;
  }

  public void setLastSoldDate(LocalDate lastSoldDate) {
    this.lastSoldDate = lastSoldDate;
  }
}
