package models;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class DetailsTest {

  private Details details;

  @Before
  public void setUp() {
    details = new Details(5, LocalDate.parse("2022-11-15"));
  }

  @Test
  public void getQuantity() {
    assertEquals(5.0, details.getQuantity(), 0);
  }

  @Test
  public void getDateCreated() {
    assertEquals(LocalDate.parse("2022-11-15"), details.getDateCreated());
  }
}