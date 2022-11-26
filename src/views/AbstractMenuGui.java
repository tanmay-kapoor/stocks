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
  private JButton flexibleButton;
  private JButton inflexibleButton;
  private JButton exitButton;
  private JButton createPortfolioButton;
  private JButton getCompositionButton;
  private JButton getValueButton;
  private JButton goBackButton;
  private JTextField quantity;
  private JTextField datePicker;
  private JTextField commission;

//  private JLabel portfolioNameLabel;
  private JTextField portfolioName;
  private JLabel text;

  private CardLayout cl;
  private JPanel mainPanel;

  // panel 1 stores choice between flexible and inflexible menu
  private JPanel panel1;

  //panel 2 gives portfolio specific features options in the menu
  private JPanel panel2;

  // panel 3 does the option chosen by user, e.g. create portfolio, get composition, etc.
  protected JPanel panel3;

  protected abstract void displaySellPanel();


  protected abstract void getRestIfApplicable(JPanel panel2);

  protected AbstractMenuGui(Features features, String caption) {
    super(caption);
    this.features = features;

    setSize(400, 500);
    setLocation(900, 100);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    this.mainPanel = getMainPanel();
    this.cl = (CardLayout) (mainPanel.getLayout());
    this.cl.show(mainPanel, "Main Panel");

    this.add(mainPanel);

//    inflexibleButton.addActionListener(evt -> features.handleInflexibleSelected());

//    getCompositionButton.addActionListener(evt ->);
//    getValueButton.addActionListener(evt -> );

//    this.pack();
    setVisible(true);
  }

  @Override
  public void getMainMenuChoice() {
    cl.next(mainPanel);
  }

  private void refresh() {
    this.revalidate();
    this.repaint();
  }

  @Override
  public void getPortfolioName() {
    panel3.removeAll();

    JLabel portfolioNameLabel = new JLabel("Enter portfolio name");
    panel3.add(portfolioNameLabel);

    JTextField portfolioNameTextField = new JTextField(10);
    panel3.add(portfolioNameTextField);

    JButton enterButton = new JButton("Enter");
    panel3.add(enterButton);

    panel3.add(goBackButton);

    enterButton.addActionListener(evt -> {
      String pName = portfolioNameTextField.getText();
      if (pName.equals("")) {
        printMessage("Name cannot be empty");
      } else {
        portfolioNameTextField.setText("");
        features.createPortfolio(pName);
      }
    });

    panel3.revalidate();
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
    panel3.removeAll();

    JButton interfaceButton = new JButton("Interface");
    panel3.add(interfaceButton);
    interfaceButton.addActionListener(evt -> getPortfolioName());

    JButton uploadButton = new JButton("File upload");
    panel3.add(uploadButton);

    panel3.add(goBackButton);

    panel3.revalidate();
    cl.show(mainPanel, "panel 3");
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
    panel3.add(msg);

    JTextField ticker = new JTextField("some text", 10);
    panel3.add(ticker);
    System.out.println("in getTickerSymbol");
    getQuantity();
    getDateForValue();
    getCommissionFee();

    JButton addBtn = new JButton("Add");
    panel3.add(addBtn);
    panel3.add(goBackButton);
    panel3.revalidate();

    addBtn.addActionListener(e ->
            features.buyStock(
                    ticker.getText(),
                    Double.parseDouble(quantity.getText().toUpperCase()),
                    LocalDate.now(),
                    Double.parseDouble(commission.getText())
            ));
  }

  @Override
  public void getQuantity() {
    JLabel msg = new JLabel("Number of shares : ");
    panel3.add(msg);

    quantity = new JTextField(10);
    panel3.add(quantity);
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
    panel3.add(msg);
  }

  @Override
  public void getPortfolioCompositionOption() {

  }
  @Override
  public void getBuySellChoice() {
    panel3.removeAll();

    JButton buyOptionBtn = new JButton("Buy");
    JButton sellOptionBtn = new JButton("Sell");

    panel3.add(buyOptionBtn);
    panel3.add(sellOptionBtn);
    panel3.add(goBackButton);

    panel3.revalidate();

    buyOptionBtn.addActionListener(evt -> displayBuyPanel());
    sellOptionBtn.addActionListener(evt -> displaySellPanel());

  }

  protected void displayBuyPanel() {
    panel3.removeAll();
    getTickerSymbol();

  }

  @Override
  public void getCommissionFee() {
    JLabel msg = new JLabel("Commission Fee : ");
    panel3.add(msg);

    commission = new JTextField(10);
    panel3.add(commission);
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













  //////////////////////////////////JFRAME RELATED SHIT//////////////////////

  private JPanel getMainPanel() {
    JPanel mainPanel = new JPanel(new CardLayout());

    this.panel1 = getPanel1();
    this.panel2 = getPanel2();
    this.panel3 = new JPanel(new GridLayout(0, 2, 10, 80));

    mainPanel.add(panel1, "Main Menu");
    mainPanel.add(panel2, "2");
    mainPanel.add(panel3, "panel 3");

    goBackButton.addActionListener(evt -> cl.previous(mainPanel));
    exitButton.addActionListener(evt -> features.exitProgram());

    return mainPanel;
  }

  private JPanel getPanel1() {
    JPanel panel1 = new JPanel();

    flexibleButton = new JButton("Flexible");
    panel1.add(flexibleButton);

    inflexibleButton = new JButton("Inflexible");
    panel1.add(inflexibleButton);

    exitButton = new JButton("Exit");
    panel1.add(exitButton);

    flexibleButton.addActionListener(evt -> features.handleFlexibleSelected());
//    inflexibleButton.addActionListener(evt -> cl.show(mainPanel, "2"));

    return panel1;
  }

  private JPanel getPanel2() {
    JPanel panel2 = new JPanel();

    createPortfolioButton = new JButton("Create portfolio");
    panel2.add(createPortfolioButton);
    createPortfolioButton.addActionListener(evt -> getCreatePortfolioThroughWhichMethod());

    getCompositionButton = new JButton("See portfolio composition");
    panel2.add(getCompositionButton);

    getValueButton = new JButton("Check portfolio value");
    panel2.add(getValueButton);

    goBackButton = new JButton("Go back");
    panel2.add(goBackButton);

    //has to be called by controller and be implemented in MenuGuiFlexible and MenuGuiInflexible
    getRestIfApplicable(panel2);

    return panel2;
  }

}
