package models.portfolio;

public class Performance {
  private final String precisionAdjusted;
  private final int stars;

  public Performance(String precisionAdjusted, int stars) {
    this.precisionAdjusted = precisionAdjusted;
    this.stars = stars;
  }

  public String getPrecisionAdjusted() {
    return this.precisionAdjusted;
  }

  public int getStars() {
    return this.stars;
  }
}
