package models;

import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

public class Log {
  private Set<Details> detailsSet;
  private LocalDate lastSoldDate;

  public Log(Set<Details> detailsSet) {
    this.detailsSet = detailsSet;
    this.lastSoldDate = null;
  }

  public void setDetailsSet(Set<Details> detailsSet) {
    this.detailsSet = detailsSet;
  }

  public Set<Details> getDetailsSet() {
    return this.detailsSet;
  }

  public void setLastSoldDate(LocalDate lastSoldDate) {
    this.lastSoldDate = lastSoldDate;
  }

  public LocalDate getLastSoldDate() {
    return this.lastSoldDate;
  }
}
