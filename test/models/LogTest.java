package models;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;

/**
 * Test class to check the methods implemented in Log class.
 */
public class LogTest {
  private Log log;

  @Before
  public void setup() {
    LocalDate lastSoldDate = LocalDate.now();

    Set<Details> detailsSet = new TreeSet<>(Comparator.comparing(Details::getPurchaseDate));
    detailsSet.add(new Details(10, LocalDate.parse("2020-10-10")));
    detailsSet.add(new Details(30, LocalDate.parse("2014-08-08")));
    detailsSet.add(new Details(90, LocalDate.parse("2019-01-12")));

    log = new Log(detailsSet, lastSoldDate);
  }

  @Test
  public void getDetailsSet() {
    Set<Details> detailsSet = new TreeSet<>(Comparator.comparing(Details::getPurchaseDate));
    detailsSet.add(new Details(10, LocalDate.parse("2020-10-10")));
    detailsSet.add(new Details(30, LocalDate.parse("2014-08-08")));
    detailsSet.add(new Details(90, LocalDate.parse("2019-01-12")));

    assertEquals(detailsSet, log.getDetailsSet());
  }

  @Test
  public void setDetailsSet() {
    Set<Details> detailsSet = new TreeSet<>(Comparator.comparing(Details::getPurchaseDate));
    detailsSet.add(new Details(10, LocalDate.parse("2020-10-10")));
    detailsSet.add(new Details(30, LocalDate.parse("2014-08-08")));
    detailsSet.add(new Details(90, LocalDate.parse("2019-01-12")));
    detailsSet.add(new Details(110, LocalDate.parse("2015-03-22")));

    log.setDetailsSet(detailsSet);
    assertEquals(detailsSet, log.getDetailsSet());
  }

  @Test
  public void getLastSold() {
    assertEquals(LocalDate.now(), log.getLastSoldDate());
  }

  @Test
  public void setLastSold() {
    LocalDate d = LocalDate.parse("2020-10-10");
    log.setLastSoldDate(d);
    assertEquals(d, log.getLastSoldDate());
  }
}