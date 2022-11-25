package views;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;

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
  private JTextField ticker;
  private JTextField quantity;
  private JTextField datePicker;
  private JTextField commission;

  private JLabel portfolioNameLabel;
  private JLabel text;

  protected abstract void getRestIfApplicable();

  protected AbstractMenuGui(Features features, String caption) {
    super(caption);
    this.features = features;

    setSize(500, 500);
    setLocation(200, 200);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JPanel mainPanel = new JPanel(new CardLayout());

    JPanel panel1 = new JPanel();
    flexibleButton = new JButton("Flexible");
    panel1.add(flexibleButton);

    inflexibleButton = new JButton("Inflexible");
//    this.add(inflexibleButton);
    panel1.add(inflexibleButton);

    exitButton = new JButton("Exit");
//    this.add(exitButton);
    panel1.add(exitButton);

    JPanel panel2 = new JPanel();
    panel2.add(new JButton("back"));

    mainPanel.add(panel1, "1");
    mainPanel.add(panel2, "2");
    CardLayout cl = (CardLayout) (mainPanel.getLayout());

    cl.show(mainPanel, "1");
//    cl.next(mainPanel);

    this.add(mainPanel);


//    flexibleButton.addActionListener(evt -> cl.show(mainPanel, "2"));
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
    createPortfolioButton.addActionListener(evt -> getCreatePortfolioThroughWhichMethod());

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
    enterButton.addActionListener(evt -> {
      String pName = portfolioName.getText();
      if (pName.equals("")) {
        printMessage("Name cannot be empty");
      } else {
        portfolioName.setText("");
        features.createPortfolio(pName);
      }
    });

    this.refresh();
  }

  @Override
  public void toggleColor() {
    if (this.portfolioNameLabel.getForeground().equals(Color.BLACK)) {
      this.portfolioNameLabel.setForeground(Color.RED);
    } else {
      this.portfolioNameLabel.setForeground(Color.BLACK);
    }
  }

  @Override
  public void printMessage(String msg) {
    clearTextIfDisplayed();
    text = new JLabel(msg);
    this.add(text);
    this.refresh();
  }

  @Override
  public void clearTextIfDisplayed() {
    if (text != null) {
      this.remove(text);
      this.refresh();
    }
  }

  @Override
  public void successMessage(String ticker, Details details, Txn txnType) {

  }

  @Override
  public void getCreatePortfolioThroughWhichMethod() {
    JButton interfaceButton = new JButton("Interface");
    this.add(interfaceButton);
    interfaceButton.addActionListener(evt -> getPortfolioName());

    JButton uploadButton = new JButton("File upload");
    this.add(uploadButton);

    JButton backButton = new JButton("Go back");
    this.add(backButton);

    this.refresh();
  }

  @Override
  public void getAddToPortfolioChoice() {
    JButton addShareBtn = new JButton("Add share");
    this.add(addShareBtn);
    addShareBtn.addActionListener(evt -> getTickerSymbol());

    JButton createStrategyBtn = new JButton("Create DCA strategy");
    this.add(createStrategyBtn);

    this.refresh();
  }

  @Override
  public void getFilePath() {

  }

  @Override
  public void getTickerSymbol() {
    JLabel msg = new JLabel("Ticker symbol : ");
    this.add(msg);

    ticker = new JTextField(10);
    this.add(ticker);

    getQuantity();
    getDateForValue();
    getCommissionFee();

    JButton addBtn = new JButton("Add");
    this.add(addBtn);
    addBtn.addActionListener(e ->
            features.buyStock(
                    ticker.getText(),
                    Double.parseDouble(quantity.getText().toUpperCase()),
                    LocalDate.now(),
                    Double.parseDouble(commission.getText())
            ));

    this.refresh();
  }

  @Override
  public void getQuantity() {
    JLabel msg = new JLabel("Number of shares : ");
    this.add(msg);

    quantity = new JTextField(10);
    this.add(quantity);
    quantity.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent key) {
        quantity.setEditable((key.getKeyChar() >= '0' && key.getKeyChar() <= '9')
                || key.getKeyCode() == 8);
      }
    });
  }

  @Override
  public void getDateChoice() {

  }

  @Override
  public void getDateForValue() {
    JLabel msg = new JLabel("Choose date : ");
    this.add(msg);
  }

  @Override
  public void getPortfolioCompositionOption() {

  }

  @Override
  public void getBuySellChoice() {

  }

  @Override
  public void getCommissionFee() {
    JLabel msg = new JLabel("Commission Fee : ");
    this.add(msg);

    commission = new JTextField(10);
    this.add(commission);
    commission.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent key) {
        commission.setEditable((key.getKeyChar() >= '0' && key.getKeyChar() <= '9')
                || key.getKeyCode() == 8 || key.getKeyChar() == '.');
      }
    });
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
