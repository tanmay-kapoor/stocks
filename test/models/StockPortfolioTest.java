package models;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import models.Details;
import models.api.StockApi;
import models.portfolio.Portfolio;
import models.portfolio.StockPortfolio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class StockPortfolioTest {

  private Portfolio portfolio;
  private String portfolioName;
  private LocalDate now;
  private String directory;

  @Before
  public void setUp() {
    directory = System.getProperty("user.dir") + "/test/";
    portfolioName = "test";
    now = LocalDate.now();

    try {
      portfolio = new StockPortfolio(portfolioName,
              LocalDate.parse("2022-10-63"),
              this.directory,
              new StockApi());
      fail("Program should've failed while parsing invalid data.");
    } catch (DateTimeParseException e) {
      portfolio = new StockPortfolio(portfolioName,
              LocalDate.parse("2022-10-01"),
              this.directory,
              new StockApi());
    }
  }

  @Test
  public void checkPortfolioEmptyAtStart() {
    assertEquals(new HashMap<>(), portfolio.getComposition());
  }

  @Test
  public void addShare() {
    Map<String, Details> expected = new HashMap<>();
    assertEquals(expected, portfolio.getComposition());

    portfolio.addShare("META", 22.0);
    Map<String, Details> shareDetails = portfolio.getComposition();
    expected.put("META", new Details(22.0, LocalDate.now()));
    checkHashMapEquality(expected, shareDetails);

    portfolio.addShare("gOoG", 34);
    shareDetails = portfolio.getComposition();
    expected.put("GOOG", new Details(34, LocalDate.now()));
    checkHashMapEquality(expected, shareDetails);

    portfolio.addShare("xyz", 33);
    shareDetails = portfolio.getComposition();
    expected.put("XYZ", new Details(33, LocalDate.now()));
    checkHashMapEquality(expected, shareDetails);

    portfolio.addShare("nflx", 299);
    shareDetails = portfolio.getComposition();
    expected.put("NFLX", new Details(299, LocalDate.now()));
    checkHashMapEquality(expected, shareDetails);

    portfolio.addShare("AMZN", 11.5);
    expected.put("AMZN", new Details(11.5, LocalDate.now()));
    checkHashMapEquality(expected, shareDetails);

    portfolio.addShare("AMZN", 22);
    expected.put("AMZN", new Details(33.5, LocalDate.now()));
    checkHashMapEquality(expected, shareDetails);
  }

  @Test
  public void addShareInvalid() {
    try {
      portfolio.addShare("GoOg", -55);
    } catch (IllegalArgumentException e1) {
      try {
        portfolio.addShare("amzn", 0);
      } catch(IllegalArgumentException e2) {
        try {
          portfolio.addShare("NFLX", -5.3);
        } catch(IllegalArgumentException e3) {
          //passes
        }
      }
    }
  }

  @Test
  public void getValue() {
    portfolio.addShare("AMZN", 27);
    portfolio.addShare("NFLX", 18);
    double val = portfolio.getValue();
    assertEquals(7774.83, val, 0);
  }

  @Test
  public void getValueDateProvided() {
    portfolio.addShare("AMZN", 27);
    portfolio.addShare("NFLX", 18);
    double val = portfolio.getValue(LocalDate.parse("2022-10-10"));
    assertEquals(7208.73, val, 0);

    try {
      portfolio.getValue(LocalDate.parse("2022/10/10"));
      fail("Should be invalid date");
    } catch (DateTimeParseException e1) {
      try {
        portfolio.getValue(LocalDate.parse("2003-07-15"));
        fail("Should not be able to get val for this date");
      } catch (RuntimeException e2) {
        // passes
      }
    }
  }

  @Test
  public void getComposition() {
    Map<String, Details> expected = new HashMap<>();
    assertEquals(expected, portfolio.getComposition());

    portfolio.addShare("META", 22.0);
    Map<String, Details> shareDetails = portfolio.getComposition();
    expected.put("META", new Details(22.0, LocalDate.now()));
    checkHashMapEquality(expected, shareDetails);

    portfolio.addShare("gOoG", 34);
    shareDetails = portfolio.getComposition();
    expected.put("GOOG", new Details(34, LocalDate.now()));
    checkHashMapEquality(expected, shareDetails);

    portfolio.addShare("xyz", 33);
    shareDetails = portfolio.getComposition();
    expected.put("XYZ", new Details(33, LocalDate.now()));
    checkHashMapEquality(expected, shareDetails);

    portfolio.addShare("nflx", 299);
    shareDetails = portfolio.getComposition();
    expected.put("NFLX", new Details(299, LocalDate.now()));
    checkHashMapEquality(expected, shareDetails);

    portfolio.addShare("AMZN", 11.5);
    expected.put("AMZN", new Details(11.5, LocalDate.now()));
    checkHashMapEquality(expected, shareDetails);

    portfolio.addShare("AMZN", 22);
    expected.put("AMZN", new Details(33.5, LocalDate.now()));
    checkHashMapEquality(expected, shareDetails);
  }

  @Test
  public void savePortfolio() {
    Set<String> expected = new HashSet<>();
    expected.add("share,quantity,dateCreated");

    portfolio.addShare("META", 22.0);
    expected.add(String.format("META,22.0,%s", now));

    portfolio.addShare("gOoG", 34);
    expected.add(String.format("GOOG,34.0,%s", now));

    portfolio.addShare("xyz", 33);
    expected.add(String.format("XYZ,33.0,%s", now));

    portfolio.addShare("nflx", 299);
    expected.add(String.format("NFLX,299.0,%s", now));

    portfolio.addShare("AMZN", 22);
    expected.add(String.format("AMZN,22.0,%s", now));

    boolean saved = portfolio.savePortfolio();
    assertTrue(saved);

    File file = new File(this.directory + this.portfolioName + ".csv");
    try {
      Scanner csvReader = new Scanner(file);
      String line = csvReader.nextLine();
      while (csvReader.hasNext()) {
        assertTrue(expected.contains(line));
        line = csvReader.nextLine();
      }

      if (!file.delete()) {
        fail("Could not delete csv but should be able to.");
      }
    } catch (FileNotFoundException e) {
      fail("File not found when it should.");
    }
  }

  @Test
  public void savePortfolioInvalid() {
    assertFalse(portfolio.savePortfolio());
    portfolio.addShare("nflx", 299);
    portfolio.savePortfolio();

    try {
      Files.createDirectories(Paths.get(this.directory));
      File file = new File(this.directory + this.portfolioName + ".csv");
        Scanner csvReader = new Scanner(file);
        String firstLine = csvReader.nextLine();
        assertNotEquals("idk,something,random", firstLine);
        if(!file.delete()) {
          fail("Could not delete csv but should be able to.");
        }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void checkHashMapEquality(Map<String, Details> expected,
                                    Map<String, Details> shareDetails) {
    for (String ticker : shareDetails.keySet()) {
      Details expectedDetails = expected.get(ticker);
      Details currDetails = shareDetails.get(ticker);
      assertEquals(expectedDetails.getQuantity(), currDetails.getQuantity(), 0);
      assertEquals(expectedDetails.getDateCreated(), currDetails.getDateCreated());
    }
  }
}