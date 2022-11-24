package views;

import models.Details;
import models.portfolio.Txn;

public class StockMenuFlexibleGui implements Menu {
  @Override
  public char getMainMenuChoice() {
    return 0;
  }

  @Override
  public String getPortfolioName() {
    return null;
  }

  @Override
  public void printMessage(String msg) {

  }

  @Override
  public void successMessage(String ticker, Details details, Txn txnType) {

  }

  @Override
  public char getCreatePortfolioThroughWhichMethod() {
    return 0;
  }

  @Override
  public char getAddToPortfolioChoice() {
    return 0;
  }

  @Override
  public String getFilePath() {
    return null;
  }

  @Override
  public String getTickerSymbol() {
    return null;
  }

  @Override
  public double getQuantity() {
    return 0;
  }

  @Override
  public char getDateChoice() {
    return 0;
  }

  @Override
  public String getDateForValue() {
    return null;
  }

  @Override
  public char getPortfolioCompositionOption() {
    return 0;
  }

  @Override
  public char getBuySellChoice() {
    return 0;
  }

  @Override
  public double getCommissionFee() {
    return 0;
  }

  @Override
  public String getStrategyName() {
    return null;
  }

  @Override
  public double getWeightage() {
    return 0;
  }

  @Override
  public double getStrategyAmount() {
    return 0;
  }

  @Override
  public int getInterval() {
    return 0;
  }
}
