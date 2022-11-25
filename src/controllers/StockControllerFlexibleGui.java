package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Scanner;

import models.Log;
import models.api.ShareApi;
import models.portfolio.Composition;
import models.portfolio.Portfolio;
import views.Menu;

public class StockControllerFlexibleGui extends AbstractController {
  public StockControllerFlexibleGui(Menu menu, ShareApi api, String path) {
    super(null, menu, api, path);
    sc = null;
  }

  @Override
  protected void getWhatToDoOnStart() {
    menu.getMainMenuChoice();
  }

  @Override
  protected Portfolio createPortfolio(String portfolioName) {
    return null;
  }

  @Override
  protected Portfolio createPortfolio(String portfolioName, Map<String, Log> stocks, Map<LocalDate, Double> costBasisHistory) {
    return null;
  }

  @Override
  protected Map<String, LocalDate> readLastSoldDateFromCsv(File logFile) throws FileNotFoundException {
    return null;
  }

  @Override
  protected Map<LocalDate, Double> readStockBasisHistoryFromCsv(File costBasisFile) throws FileNotFoundException {
    return null;
  }

  @Override
  protected LocalDate getPurchaseDate() {
    return null;
  }

  @Override
  protected char getLastOption() {
    return 0;
  }

  @Override
  protected double getCommissionFee() {
    return 0;
  }

  @Override
  protected void filterBasedOnFunction(Function function) {

  }

  @Override
  protected void handleMenuOptions(Portfolio portfolio, Function function) {

  }

  @Override
  protected boolean giveDateOptionsIfApplicable(Portfolio portfolio, Composition option) {
    return false;
  }

  @Override
  protected boolean handleCreatePortfolioOption(char choice, Portfolio portfolio, String portfolioName) {
    return false;
  }
}
