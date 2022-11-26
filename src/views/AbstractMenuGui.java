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
  private JTextField ticker;
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
  private JPanel panel3;


  protected abstract void getRestIfApplicable(JPanel panel2);

  protected AbstractMenuGui(Features features, String caption) {
    super(caption);
    this.features = features;

    setSize(400, 500);
    setLocation(200, 200);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    this.mainPanel = getMainPanel();
    this.cl = (CardLayout) (mainPanel.getLayout());
    this.cl.show(mainPanel, "Main Panel");

    this.add(mainPanel);

    flexibleButton.addActionListener(evt -> features.handleFlexibleSelected());
//    inflexibleButton.addActionListener(evt -> features.handleInflexibleSelected());
    inflexibleButton.addActionListener(evt -> cl.show(mainPanel, "2"));
    goBackButton.addActionListener(evt -> cl.previous(mainPanel));
    exitButton.addActionListener(evt -> features.exitProgram());
    createPortfolioButton.addActionListener(evt -> getPortfolioName());
//    getCompositionButton.addActionListener(evt ->);
//    getValueButton.addActionListener(evt -> );

//    pack();
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
    this.panel3.removeAll();

    JLabel portfolioNameLabel = new JLabel("Enter portfolio name");
    panel3.add(portfolioNameLabel);

    portfolioName = new JTextField(10);
    panel3.add(portfolioName);

    JButton enterButton = new JButton("Enter");
    panel3.add(enterButton);
    enterButton.addActionListener(evt -> {
      String pName = portfolioName.getText();
      if (pName.equals("")) {
        printMessage("Name cannot be empty");
      } else {
        portfolioName.setText("");
        features.createPortfolio(pName);
      }
    });

    panel3.add(goBackButton);

    cl.show(mainPanel, "panel 3");
//    cl.next(mainPanel);

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













  //////////////////////////////////JFRAME RELATED SHIT//////////////////////

  private JPanel getMainPanel() {
    JPanel mainPanel = new JPanel(new CardLayout());

    this.panel1 = getPanel1();
    this.panel2 = getPanel2();
    this.panel3 = new JPanel();

    mainPanel.add(panel1, "Main Menu");
    mainPanel.add(panel2, "2");
    mainPanel.add(panel3, "panel 3");

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
