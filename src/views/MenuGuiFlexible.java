package views;

import javax.swing.*;

import controllers.Features;

public class MenuGuiFlexible extends AbstractMenuGui {

  public MenuGuiFlexible(Features features, String caption) {
    super(features, caption);
  }

  @Override
  public void getRestIfApplicable(JPanel panel2) {
    JButton dcaButton = new JButton("Create DCA");
    panel2.add(dcaButton);
    dcaButton.addActionListener(evt -> {
      this.setTitle("Create DCA");
      getDcaOptions();
    });

    JButton buySellButton = new JButton("Buy/Sell shares");
    panel2.add(buySellButton);
    buySellButton.addActionListener(evt -> {
      this.setTitle("Trade Shares");
      getBuySellChoice();
    });

    JButton seePerformance = new JButton("See portfolio performance");
    panel2.add(seePerformance);
    seePerformance.addActionListener(evt -> {
      this.setTitle("See Portfolio Performance");
      getPortfolioPerformanceOption();
    });

    JButton getCostBasisButton = new JButton("Get Cost basis");
    panel2.add(getCostBasisButton);
    getCostBasisButton.addActionListener(evt -> {
      this.setTitle("Portfolio Cost Basis");
      getPortfolioCostBasisOptions();
    });

  }


  protected void displaySellPanel() {
    panel4.removeAll();

    getTickerSymbol();
    getQuantity();
    getDateChoice();
    getCommissionFee();

    JButton addBtn = new JButton("Sell");
    panel4.add(addBtn);

    panel4.add(backToP3Btn);

    addBtn.addActionListener(e ->
            features.sellStock(
                    portfolioName,
                    ticker.getText(),
                    quantity.getText(),
                    dateTxtFiled.getText(),
                    commission.getText()
            ));

    panel4.revalidate();
  }


}
