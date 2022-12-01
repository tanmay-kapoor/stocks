package views;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.Color;
import java.time.LocalDate;
import java.util.Map;

import models.portfolio.Performance;
import models.portfolio.Report;

/**
 * Class to create bar and line chart.
 */
public class Chart {
  private final Report report;
  private final Map<LocalDate, Performance> dateWisePerformance;
  private final String portfolioName;
  private final CategoryDataset dataset;

  /**
   * Initialize variables for chart creation.
   *
   * @param report        Report of performance to create chart of.
   * @param portfolioName Name of portfolio to create chart of.
   */
  public Chart(Report report, String portfolioName) {
    this.report = report;
    this.dateWisePerformance = report.getPerformanceOnEachDate();
    this.portfolioName = portfolioName;
    this.dataset = createDataset();
  }

  /**
   * Method to create bar chart of the performance report.
   *
   * @return ChartPanel that shows that bar chart.
   */
  public ChartPanel getBartChart() {

    JFreeChart barChart = ChartFactory.createBarChart(
            portfolioName + "'s performance from " + report.getTimeLime().getStartDate()
                    + " to " + report.getTimeLime().getEndDate(),
            "Date",
            "Valuation",
            dataset,
            PlotOrientation.HORIZONTAL,
            true, true, false
    );

    CategoryPlot plot = barChart.getCategoryPlot();
    BarRenderer renderer = (BarRenderer) plot.getRenderer();

    Color color = new Color(79, 129, 189);
    renderer.setSeriesPaint(0, color);

    return new ChartPanel(barChart);
  }

  /**
   * Method to create line chart of the performance report.
   *
   * @return ChartPanel that shows the line chart.
   */
  public ChartPanel getLineChart() {

    JFreeChart lineChart = ChartFactory.createLineChart(
            portfolioName + "'s performance from " + report.getTimeLime().getStartDate()
                    + " to " + report.getTimeLime().getEndDate(),
            "Date",
            "Valuation",
            dataset,
            PlotOrientation.VERTICAL,
            true, true, false
    );

    return new ChartPanel(lineChart);
  }

  private CategoryDataset createDataset() {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    for (LocalDate date : dateWisePerformance.keySet()) {
      dataset.addValue(dateWisePerformance.get(date).getStars(), portfolioName, date);
    }

    return dataset;
  }
}
