package views;

import javax.swing.*;

import controllers.Features;

public class MenuGuiFlexible extends AbstractMenuGui {
  private Features features;

  public MenuGuiFlexible(Features features, String caption) {
    super(features, caption);
    this.features = features;
  }

  @Override
  public void getRestIfApplicable(JPanel panel2) {
    JButton buySellButton = new JButton("Buy/Sell shares");
    panel2.add(buySellButton);
    buySellButton.addActionListener(evt -> {
      this.setTitle("Trade Shares");
      getAllPortfolios();
    });

    JButton seePerformance = new JButton("See portfolio performance");
    panel2.add(seePerformance);
    seePerformance.addActionListener(evt -> {
      this.setTitle("See PortfolioPerformance");
      getAllPortfolios();
    });

    JButton getCostBasisButton = new JButton("Get Cost basis");
    panel2.add(getCostBasisButton);
    getCostBasisButton.addActionListener(evt -> {
      this.setTitle("Portfolio Cost Basis");
      getAllPortfolios();
    });

  }


  protected void displaySellPanel() {
    panel3.removeAll();
    getTickerSymbol();
  }

}
