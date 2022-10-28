package api;

import java.util.Map;

public interface ShareApi {
  Map<String, Double> getShareDetails(String tickerSymbol, String timestamp);
}
