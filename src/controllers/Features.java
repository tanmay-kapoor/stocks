package controllers;

import views.Menu;

public interface Features {
  void setView(Menu view);

  void handleFlexibleSelected();
  void handleInflexibleSelected();
  void exitProgram();
  void handleCreatePortfolioThroughUpload();
  void createPortfolio(String portfolioName);
}
