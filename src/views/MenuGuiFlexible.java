package views;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;

import controllers.Features;

/**
 * Class for user interaction through gui for flexible portfolio.
 */
public class MenuGuiFlexible extends AbstractMenuGui {

  /**
   * Supple controller features and caption for initial JFrame.
   *
   * @param features Controller features.
   * @param caption  Caption for first JFrame.
   */
  public MenuGuiFlexible(Features features, String caption) {
    super(features, caption);
  }

  @Override
  public void getRestIfApplicable(JPanel panel2) {
    JButton dcaButton = new JButton("Create DCA");
    panel2.add(dcaButton);
    dcaButton.addActionListener(evt -> {
      this.setTitle("Create DCA");
      getDcaOptions(false);
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
    gbc4.gridwidth = 2;
    panel4.add(new JLabel("Fill details to sell stock from " + portfolioName + " portfolio"),
            gbc4);

    gbc4.gridwidth = 1;
    getTickerSymbol();
    getQuantity();
    getDateChoice();
    getCommissionFee();

    gbc4Newline();
    JButton addBtn = new JButton("Sell");
    panel4.add(addBtn, gbc4);
    gbc4.gridx = 1;
    panel4.add(backToP3Btn, gbc4);

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
