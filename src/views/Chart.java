package views;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.time.LocalDate;
import java.util.Map;

import models.portfolio.Performance;
import models.portfolio.Report;

public class Chart {
  private static final long serialVersionUID = 1L;
  private Report report;
  private String scale;
  private String baseValue;
  private Map<LocalDate, Performance> dateWisePerformance;
  private String portfolioName;
  private CategoryDataset dataset;

  Chart(Report report, String portfolioName) {
    this.report = report;
    this.scale = report.getScale();
    this.baseValue = report.getBaseValue();
    this.dateWisePerformance = report.getPerformanceOnEachDate();
    this.portfolioName = portfolioName;
    this.dataset = createDataset();
  }

  public ChartPanel getBartChart() {

    JFreeChart barChart = ChartFactory.createBarChart(
            portfolioName + "'s performance from 2020-20-20 to 2022-20-20",
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

  public ChartPanel getLineChart( ) {

    JFreeChart lineChart = ChartFactory.createLineChart(
            portfolioName + "'s performance from 2020-20-20 to 2022-20-20",
            "Date",
            "Valuation",
            dataset,
            PlotOrientation.VERTICAL,
            true,true,false
    );

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
