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
  public void getRestIfApplicable(JPanel panel2) {
    buySellButton = new JButton("Buy/Sell shares");
    panel2.add(buySellButton);

    seePerformance = new JButton("See portfolio performance");
    panel2.add(seePerformance);

    getCostBasisButton = new JButton("Get Cost basis");
    panel2.add(getCostBasisButton);

  }
}
