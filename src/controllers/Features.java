package controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import models.portfolio.Report;
import views.Menu;

public interface Features {
  void setView(Menu view);

  void handleFlexibleSelected();

  void handleInflexibleSelected();

  void exitProgram();

  void handleCreatePortfolioThroughUpload(String filePath);

  void createPortfolio(String portfolioName);

  void buyStock(String portfolioName, String ticker, String quant, String purchaseDate, String commission);

  void sellStock(String portfolioName, String ticker, String quant, String d, String commission);

  void savePortfolio(String portfolioName);

  List<String> getAllPortfolios();

  Map<String, Double> getPortfolioContents(String portfolioName, String date);

  Map<String, Double> getPortfolioWeightage(String portfolioName, String date);

  double getPortfolioValue(String portfolioName, String date);

  Report getPortfolioPerformance(String portfolioName, String f, String t);

  double getCostBasis(String portfolioName, String date);

  void resetTotalWeightage();

  void addTickerToStrategy(String ticker, String weightage);

  void saveDca(String portfolioName, String strategyName, String amt, String f, String t,
               String interval, String commission, Map<String, Double> stockWeightage);

  double getWeightageLeft();

  void createEmptyDcaLog(String pName) throws IOException;
}
