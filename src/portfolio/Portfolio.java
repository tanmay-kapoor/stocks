package portfolio;

import java.io.IOException;
import java.util.Map;

public interface Portfolio {

  void addShare(String tickerSymbol, double quantity) throws IOException, IllegalArgumentException;

  double getValue();

  double getValue(String date);

  Map<String, Double> getPortfolioComposition();

  void savePortfolio();

}
