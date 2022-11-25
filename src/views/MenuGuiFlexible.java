package views;

import javax.swing.*;

import controllers.Features;

public class MenuGuiFlexible extends AbstractMenuGui {
  JButton buySellButton;
  JButton seePerformance;
  JButton getCostBasisButton;
  JButton goBackButton;

  public MenuGuiFlexible(Features features, String caption) {
    super(features, caption);
  }

  @Override
  public void getRestIfApplicable() {
    buySellButton = new JButton("Buy/Sell shares");
    this.add(buySellButton);

    seePerformance = new JButton("See portfolio performance");
    this.add(seePerformance);

    getCostBasisButton = new JButton("Get Cost basis");
    this.add(getCostBasisButton);

    goBackButton = new JButton("Go back");
    this.add(goBackButton);
  }
}
