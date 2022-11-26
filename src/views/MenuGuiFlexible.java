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

    JButton seePerformance = new JButton("See portfolio performance");
    panel2.add(seePerformance);

    JButton getCostBasisButton = new JButton("Get Cost basis");
    panel2.add(getCostBasisButton);

    buySellButton.addActionListener(evt -> getBuySellChoice());
  }


  protected void displaySellPanel() {
    panel3.removeAll();
    getTickerSymbol();
  }

}
