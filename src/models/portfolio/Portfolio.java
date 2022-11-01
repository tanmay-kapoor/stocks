package models.portfolio;

import java.io.IOException;
import java.time.LocalDate;

public interface Portfolio {
  void addShare(String tickerSymbol, double quantity) throws RuntimeException;

  double getValue();

  double getValue(LocalDate date);

  String getComposition() throws IOException;

  void savePortfolio() throws RuntimeException, IOException;

}
