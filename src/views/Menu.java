package views;

import java.io.IOException;

public interface Menu {
  char getMainMenuChoice() throws IOException;

  String getPortfolioName() throws IOException;

  void printMessage(String msg) throws IOException;

  char getCreatePortfolioChoice() throws IOException;

  String getTickerSymbol() throws IOException;

  double getQuantity() throws IOException;

  char getDateChoice() throws IOException;

  String getDateForValue() throws IOException;
}
