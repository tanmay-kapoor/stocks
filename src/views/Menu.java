package views;

public interface Menu {
  char getMainMenuChoice();

  String getPortfolioName();

  void printMessage(String msg);

  char getCreatePortfolioChoice();

  String getTickerSymbol();

  double getQuantity();

  String getDateForCheckValue();
}
