package models.portfolio;

import java.io.IOException;

public interface Portfolio {

  void addShare(String tickerSymbol, double quantity) throws RuntimeException;

  double getValue();

  double getValue(String date);

  String getComposition() throws IOException;

  void savePortfolio() throws RuntimeException, IOException;

}
