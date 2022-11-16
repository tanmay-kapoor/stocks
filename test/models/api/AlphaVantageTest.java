package models.api;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Map;

import static java.util.Map.entry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

/**
 * A class that is used to test the methods implemented in the AlphaVantage class.
 */
public class AlphaVantageTest {
  private ShareApi api;

  @Before
  public void setUp() {
    api = new AlphaVantage();
  }

  @Test
  public void getShareDetails() {
    Map<String, Double> output = api.getShareDetails("aapl",
            LocalDate.parse("2022-10-10"));
    assertNotEquals(null, output);
    Map<String, Double> expected = Map.ofEntries(
            entry("open", 140.4200),
            entry("high", 141.8900),
            entry("low", 138.5729),
            entry("close", 140.4200),
            entry("volume", 74899002d)
    );

    for (String key : expected.keySet()) {
      assertEquals(expected.get(key), output.get(key));
    }

    output = api.getShareDetails("aApL", LocalDate.parse("2022-10-10"));
    for (String key : expected.keySet()) {
      assertEquals(expected.get(key), output.get(key));
    }

    output = api.getShareDetails("AAPL", LocalDate.parse("2022-10-10"));
    for (String key : expected.keySet()) {
      assertEquals(expected.get(key), output.get(key));
    }
  }

  @Test
  public void testGetShareDetailsDateAbsent() {
    Map<String, Double> output = api.getShareDetails("meta",
            LocalDate.parse("2021-11-07"));
    assertNotEquals(null, output);
    Map<String, Double> expected = api.getShareDetails("meta",
            LocalDate.parse("2021-11-05"));

    output = api.getShareDetails("meta",
            LocalDate.parse("2021-11-06"));
    assertNotEquals(null, output);
    expected = api.getShareDetails("meta",
            LocalDate.parse("2021-11-05"));

    for (String key : expected.keySet()) {
      assertEquals(expected.get(key), output.get(key));
    }
  }

  @Test
  public void getShareDetailsInvalid() {
    try {
      api.getShareDetails("ajbs",
              LocalDate.parse("2022-10-10"));
      fail("Should have failed with unknown ticker but did not");
    } catch (IllegalArgumentException e1) {
      try {
        api.getShareDetails("awef23jbs",
                LocalDate.parse("2001-05-19"));
        fail("Should have failed with too old of a date but did not");
      } catch (RuntimeException e2) {
        try {
          api.getShareDetails("META", LocalDate.parse("1900-01-01"));
        } catch (IllegalArgumentException e) {
          // passes
        }
      }
    }
  }

  @Test
  public void getShareDetailsFuture() {
    try {
      api.getShareDetails("AApL",
              LocalDate.parse("2023-10-10"));
      fail("Should have failed with future date but did not");
    } catch (IllegalArgumentException e1) {
      try {
        api.getShareDetails("MSFT",
                LocalDate.parse("3022-05-19"));
        fail("Should have failed with future date but did not");
      } catch (RuntimeException e2) {
        try {
          api.getShareDetails("META", LocalDate.parse("2900-01-01"));
        } catch (IllegalArgumentException e) {
          // passes
        }
      }
    }
  }
}