package views;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.time.LocalDate;
import java.util.Map;

import models.portfolio.Performance;
import models.portfolio.Report;

public class LineChart {
  private Report report;
  private String scale;
  private String baseValue;
  private Map<LocalDate, Performance> dateWisePerformance;
  private String portfolioName;

  public LineChart(Report report, String portfolioName) {
    this.report = report;
    this.scale = report.getScale();
    this.baseValue = report.getBaseValue();
    this.dateWisePerformance = report.getPerformanceOnEachDate();
    this.portfolioName = portfolioName;
  }

  public ChartPanel getLineChart( ) {
    JFreeChart lineChart = ChartFactory.createLineChart(
            portfolioName + "'s performance from 2020-20-20 to 2022-20-20",
            "Years","Number of Schools",
            createDataset(),
            PlotOrientation.VERTICAL,
            true,true,false);

    ChartPanel chartPanel = new ChartPanel( lineChart );
    chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );

    return chartPanel;
  }

  private CategoryDataset createDataset() {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    for (LocalDate date : dateWisePerformance.keySet()) {
      dataset.addValue(dateWisePerformance.get(date).getStars(), portfolioName, date);
    }

    return dataset;
  }
}
