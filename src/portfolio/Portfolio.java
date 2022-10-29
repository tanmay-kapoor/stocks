package portfolio;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

public interface Portfolio {

  void addShare(String tickerSymbol, double quantity) throws RuntimeException;

  double getValue();

  double getValue(String date);

  Map<String, Double> getPortfolioComposition(String portfolioName) throws IOException;

  void savePortfolio() throws IOException;

}
