package models.api;

import java.time.LocalDate;
import java.util.Map;

public interface ShareApi {
  Map<String, Double> getShareDetails(String tickerSymbol, LocalDate dateAsked);
}
