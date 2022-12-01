package models.portfolio;

/**
 * Class to couple all portfolio performance related stuff together like valuation and number of
 * stars to represent on graph.
 */
public class Performance {
  private final String precisionAdjusted;
  private final int stars;

  /**
   * Constructor to initialize values to the ones specified.
   *
   * @param precisionAdjusted Valuation of portfolio.
   * @param stars             Number of stars that represent the valuation.
   */
  public Performance(String precisionAdjusted, int stars) {
    this.precisionAdjusted = precisionAdjusted;
    this.stars = stars;
  }

  /**
   * Get valuation of portfolio.
   *
   * @return String which is the valuation of the portfolio.
   */
  public String getPrecisionAdjusted() {
    return this.precisionAdjusted;
  }

  /**
   * get the number of stars used to represent a valuation.
   *
   * @return int value which is the number of stars used to represent valution.
   */
  public int getStars() {
    return this.stars;
  }
}
