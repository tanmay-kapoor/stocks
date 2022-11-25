package views;

import com.sun.source.tree.NewArrayTree;

import java.awt.*;

import javax.swing.*;

import controllers.Features;
import models.Details;
import models.portfolio.Txn;

abstract class AbstractMenuGui extends JFrame implements Menu, GuiAbilities {
  private final Features features;
  private final JButton flexibleButton;
  private final JButton inflexibleButton;
  private final JButton exitButton;
  private JButton createPortfolioButton;
  private JButton getCompositionButton;
  private JButton getValueButton;
  private JButton goBackButton;

  private JLabel portfolioNameLabel;

  protected abstract void getRestIfApplicable();

  protected AbstractMenuGui(Features features, String caption) {
    super(caption);
    this.features = features;

    setSize(500, 500);
    setLocation(200, 200);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setLayout(new FlowLayout());

    flexibleButton = new JButton("Flexible");
    this.add(flexibleButton);

    inflexibleButton = new JButton("Inflexible");
    this.add(inflexibleButton);

    exitButton = new JButton("Exit");
    this.add(exitButton);

    flexibleButton.addActionListener(evt -> features.handleFlexibleSelected());
    inflexibleButton.addActionListener(evt -> features.handleInflexibleSelected());
    exitButton.addActionListener(evt -> features.exitProgram());

//    pack();
    setVisible(true);
  }
  @Override
  public void getMainMenuChoice() {
    createPortfolioButton = new JButton("Create portfolio");
    this.add(createPortfolioButton);
    createPortfolioButton.addActionListener(evt -> features.handleCreatePortfolioChoice());

    getCompositionButton = new JButton("See portfolio composition");
    this.add(getCompositionButton);

    getValueButton = new JButton("Check portfolio value");
    this.add(getValueButton);

    goBackButton = new JButton("Go back");
    this.add(goBackButton);

    getRestIfApplicable();

    this.refresh();
  }

  private void refresh() {
    this.revalidate();
    this.repaint();
  }

  @Override
  public void getPortfolioName() {
    portfolioNameLabel = new JLabel("Enter portfolio name");
    this.add(portfolioNameLabel);

    JTextField portfolioName = new JTextField(10);
    this.add(portfolioName);

    JButton enterButton = new JButton("Enter");
    this.add(enterButton);
//    enterButton.addActionListener(evt -> features.findPortfolio());

    this.refresh();
  }

  @Override
  public void toggleColor() {
    if(this.portfolioNameLabel.getForeground().equals(Color.BLACK)){
      this.portfolioNameLabel.setForeground(Color.RED);
    } else {
      this.portfolioNameLabel.setForeground(Color.BLACK);
    }
  }

  @Override
  public void printMessage(String msg) {

  }

  @Override
  public void successMessage(String ticker, Details details, Txn txnType) {

  }

  @Override
  public void getCreatePortfolioThroughWhichMethod() {
    JButton interfaceButton = new JButton("Interface");
    this.add(interfaceButton);
    interfaceButton.addActionListener(evt -> features.handleCreatePortfolioThroughInterface());

    JButton uploadButton = new JButton("File upload");
    this.add(uploadButton);

    JButton backButton = new JButton("Go back");
    this.add(backButton);

    this.refresh();
  }

  @Override
  public void getAddToPortfolioChoice() {

  }

  @Override
  public void getFilePath() {

  }

  @Override
  public void getTickerSymbol() {

  }

  @Override
  public void getQuantity() {

  }

  @Override
  public void getDateChoice() {

  }

  @Override
  public void getDateForValue() {

  }

  @Override
  public void getPortfolioCompositionOption() {

  }

  @Override
  public void getBuySellChoice() {

  }

  @Override
  public void getCommissionFee() {

  }

  @Override
  public void getStrategyName() {

  }

  @Override
  public void getWeightage() {

  }

  @Override
  public void getStrategyAmount() {

  }

  @Override
  public void getInterval() {

  }
}
