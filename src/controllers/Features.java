package controllers;

import java.time.LocalDate;
import java.util.List;

import views.Menu;

public interface Features {
  void setView(Menu view);

  void handleFlexibleSelected();
  void handleInflexibleSelected();
  void exitProgram();
  void handleCreatePortfolioThroughUpload();
  void createPortfolio(String portfolioName);
  void buyStock(String ticker, String quant, String purchaseDate, String commission);
  void savePortfolio(String portfolioName);
  List<String> getAllPortfolios();
  void getContents(String portfolioName, String date);
}
