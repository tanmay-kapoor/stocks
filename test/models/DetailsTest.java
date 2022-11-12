package models;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * A test class to check all the methods implemented in the Details class.
 */
public class DetailsTest {
  private Details details;

  @Before
  public void setUp() {
    try {
      details = new Details(5, LocalDate.parse("2022-02-31"));
      fail("Program should've failed while parsing invalid data.");
    } catch (DateTimeParseException e) {
      details = new Details(5, LocalDate.parse("2022-11-15"));
    }
  }

  @Test
  public void getQuantity() {
    assertEquals(5.0, details.getQuantity(), 0);
  }

  @Test
  public void getPurchaseDate() {
    assertEquals(LocalDate.parse("2022-11-15"), details.getPurchaseDate());
  }

  @Test
  public void setQuantity() {
    details.setQuantity(55);
    assertEquals(55.0, details.getQuantity(), 0);
  }
}