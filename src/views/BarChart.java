package views;

import javax.swing.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

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
  BarChart(Report report) {
    this.report = report;
    this.scale = report.getScale();
    this.baseValue = report.getBaseValue();
    this.dateWisePerformance = report.getPerformanceOnEachDate();
  }

  public ChartPanel getChart() {
//    JPanel panel = new JPanel();
    // Create Dataset
    CategoryDataset dataset = createDataset();

    //Create chart
    JFreeChart chart=ChartFactory.createBarChart(
            "Portfolio Performance", //Chart Title
            "Year", // Category axis
            "Population in Million", // Value axis
            dataset,
            PlotOrientation.VERTICAL,
            true,true,false
    );

    ChartPanel chartPanel=new ChartPanel(chart);
//    panel.add(chartPanel);

    return chartPanel;
  }

  private CategoryDataset createDataset() {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    for (LocalDate date : dateWisePerformance.keySet()) {
      dataset.addValue(dateWisePerformance.get(date).getStars(), "USA", date);
    }

    return dataset;
  }

//  public static void main(String[] args) throws Exception {
//    SwingUtilities.invokeAndWait(()->{
//      BarChart example=new BarChart("Bar Chart Window");
//      example.setSize(800, 400);
//      example.setLocationRelativeTo(null);
//      example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//      example.setVisible(true);
//    });
//  }
}