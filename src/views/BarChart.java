package views;

import javax.swing.*;

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

public class BarChart {

  private static final long serialVersionUID = 1L;
  private Report report;
  private String scale;
  private String baseValue;
  private Map<LocalDate, Performance> dateWisePerformance;
  private String portfolioName;

  BarChart(Report report, String portfolioName) {
    this.report = report;
    this.scale = report.getScale();
    this.baseValue = report.getBaseValue();
    this.dateWisePerformance = report.getPerformanceOnEachDate();
    this.portfolioName = portfolioName;
  }

  public ChartPanel getChart() {
    CategoryDataset dataset = createDataset();

    //Create chart
    JFreeChart chart = ChartFactory.createBarChart(
            portfolioName + "'s performance from 2020-20-20 to 2022-20-20", //Chart Title
            "Date", // Category axis
            "Valuation", // Value axis
            dataset,
            PlotOrientation.HORIZONTAL,
            true, true, false
    );

    CategoryPlot plot = chart.getCategoryPlot();
    BarRenderer renderer = (BarRenderer) plot.getRenderer();

    Color color = new Color(79, 129, 189);
    renderer.setSeriesPaint(0, color);

    return new ChartPanel(chart);
  }

  private CategoryDataset createDataset() {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    for (LocalDate date : dateWisePerformance.keySet()) {
      dataset.addValue(dateWisePerformance.get(date).getStars(), portfolioName, date);
    }

    return dataset;
  }

}