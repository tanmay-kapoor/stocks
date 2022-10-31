package views;

import java.io.IOException;

public interface Menu {
  char getMainMenuChoice() throws IOException;

  String getPortfolioName() throws IOException;

  void printMessage(String msg) throws IOException;

  char getCreatePortfolioChoice() throws IOException;

  String getTickerSymbol() throws IOException;

  double getQuantity() throws IOException;

  String getDateForCheckValue() throws IOException;
}
