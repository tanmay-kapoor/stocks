package views;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.*;

import controllers.Features;
import models.Details;
import models.portfolio.Txn;

abstract class AbstractMenuGui extends JFrame implements Menu {
  private JComboBox<String> portfolioListCb;
  private String portfolioName;
  private JTextField dateTxtFiled;
  private final Features features;
  private JButton flexibleButton;
  private JButton inflexibleButton;
  private JButton exitButton;
  private JButton createPortfolioButton;
  private JButton getCompositionButton;
  private JButton getValueButton;
  private JButton goBackButton;

  private JButton enterBtn;
  private JTextField quantity;
  private JTextField datePicker;
  private JTextField commission;

  //  private JLabel portfolioNameLabel;
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

    enterBtn = new JButton("Enter");

    goBackButton = new JButton("Go back");
    goBackButton.addActionListener(evt -> {
      cl.previous(mainPanel);
      if (true) {
        features.savePortfolio(portfolioName);
      }
    });

    exitButton = new JButton("Exit");
    exitButton.addActionListener(evt -> features.exitProgram());

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

    panel3.add(enterBtn);

    panel3.add(enterBtn);

    enterBtn.addActionListener(evt -> {
      String frameTitle = this.getTitle();
      portfolioName = portfolioNameTextField.getText();

      if (portfolioName.equals("")) {
        printMessage("Name cannot be empty");
      }


      //CONVERT THIS TO SWITCH CASE
      if (Objects.equals(frameTitle, "Create Portfolio")) {
//        portfolioNameTextField.setText("");
        features.createPortfolio(portfolioName);
      }
    });

    panel3.revalidate();
    cl.show(mainPanel, "panel 3");
  }


  @Override
  public void printMessage(String msg) {
    clearTextIfDisplayed();
    text = new JLabel(msg);
    panel3.add(text);
    panel3.revalidate();
  }

  @Override
  public void clearTextIfDisplayed() {
    if (text != null) {
      panel3.remove(text);
      panel3.revalidate();
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
    panel3.removeAll();

    JButton addShareBtn = new JButton("Add share");
    panel3.add(addShareBtn);
    addShareBtn.addActionListener(evt -> getTickerSymbol());

    JButton createStrategyBtn = new JButton("Create DCA strategy");
    panel3.add(createStrategyBtn);

    panel3.revalidate();
  }

  @Override
  public void getFilePath() {

  }

  @Override
  public void getTickerSymbol() {
    panel3.removeAll();

    JLabel msg = new JLabel("Ticker symbol : ");
    panel3.add(msg);

    JTextField ticker = new JTextField(10);
    panel3.add(ticker);
    System.out.println("in getTickerSymbol");
    getQuantity();
    getDateChoice();
    getCommissionFee();

    JButton addBtn = new JButton("Add");
    panel3.add(addBtn);
    panel3.add(goBackButton);
    panel3.revalidate();

    addBtn.addActionListener(e ->
            features.buyStock(
                    ticker.getText(),
                    quantity.getText(),
                    dateTxtFiled.getText(),
                    commission.getText()
            ));

    panel3.revalidate();
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
    JLabel msg = new JLabel("Date (YYYY-MM-DD) : ");
    dateTxtFiled = new JTextField(10);
    panel3.add(msg);
    panel3.add(dateTxtFiled);
  }


  @Override
  public void getDateForValue() {
    JLabel msg = new JLabel("Choose date : ");
    JTextField dateTxtFiled = new JTextField(10);
    panel3.add(msg);
    panel3.add(dateTxtFiled);
  }

  @Override
  public void getPortfolioCompositionOption() {
    cl.show(mainPanel, "panel 3");

    getDateChoice();

    JButton getContentsBtn = new JButton("Get Contents");
    panel3.add(getContentsBtn);

    JButton getWeightageBtn = new JButton("Get Stock Weightage");
    panel3.add(getWeightageBtn);

    panel3.add(goBackButton);
    panel3.revalidate();

    getContentsBtn.addActionListener(e -> {
      Map<String, Double> composition = features.getPortfolioContents(portfolioName, dateTxtFiled.getText());
      //data cleaning
      String[][] data = new String[composition.size()][2];
      int count = 0;
      for(Map.Entry<String,Double> entry : composition.entrySet()){
        data[count][0] = entry.getKey();
        data[count][1] = entry.getValue().toString();
        count++;
      }

      showTable(data);
    });

    getWeightageBtn.addActionListener(e -> {
      Map<String, Double> weightage = features.getPortfolioWeightage(portfolioName, dateTxtFiled.getText());
      //data cleaning
      String[][] data = new String[weightage.size()][2];
      int count = 0;
      for(Map.Entry<String,Double> entry : weightage.entrySet()){
        data[count][0] = entry.getKey();
        data[count][1] = entry.getValue().toString();
        count++;
      }

      showTable(data);
    });


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

  protected void getAllPortfolios() {
    cl.show(mainPanel, "panel 3");
    panel3.removeAll();

    List<String> portfolios = features.getAllPortfolios();
    if (portfolios.size() == 0) {
      panel3.add(new JLabel("No Portfolios. Please Create atleast one and come back again."));
      panel3.add(goBackButton);
      panel3.revalidate();
      return;
    }

    JLabel msg = new JLabel("Choose a portfolio from the list");
    panel3.add(msg);

    String[] portfolioList = Arrays.copyOf(portfolios.toArray(), portfolios.size(), String[].class);
    portfolioListCb = new JComboBox<>(portfolioList);
    portfolioListCb.setEditable(true);
    portfolioName = portfolioListCb.getItemAt(0);
    portfolioListCb.addActionListener(evt -> {
      portfolioName = portfolioListCb.getSelectedItem().toString();
      System.out.println(portfolioName);
      String selectedPortfolio = portfolioListCb.getSelectedItem().toString();
    });
    panel3.add(portfolioListCb);

    switch (this.getTitle()) {
      case "Portfolio Composition":
        getPortfolioCompositionOption();
      case "Portfolio Value":
        //call portfolio function
      case "Portfolio Cost Basis":
        //call relevant method
      case "Portfolio Performance":
        // call relevant methods
//        showgraph();
    }

    panel3.revalidate();

  }

  private void showTable (String[][] data) {
    panel3.removeAll();

    String[] colNames = {"Ticker", "Quantity"};
    JTable table = new JTable(data, colNames);
    panel3.add(table);
//    panel3.add(goBackButton);

    panel3.revalidate();
  }


  //////////////////////////////////FRAME RELATED SHIT//////////////////////

  private JPanel getMainPanel() {
    JPanel mainPanel = new JPanel(new CardLayout());

    this.panel1 = getPanel1();
    this.panel2 = getPanel2();
    this.panel3 = new JPanel(new GridLayout(0, 2, 10, 80));

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

    JButton backToTextUi = new JButton("Back To Text UI");
    panel1.add(backToTextUi);

    panel1.add(exitButton);

    flexibleButton.addActionListener(evt -> features.handleFlexibleSelected());
//    inflexibleButton.addActionListener(evt -> cl.show(mainPanel, "2"));

    return panel1;
  }

  private JPanel getPanel2() {
    JPanel panel2 = new JPanel();

    createPortfolioButton = new JButton("Create portfolio");
    panel2.add(createPortfolioButton);
    createPortfolioButton.addActionListener(evt -> {
      this.setTitle("Create Portfolio");
      getCreatePortfolioThroughWhichMethod();
    });

    getCompositionButton = new JButton("See portfolio composition");
    panel2.add(getCompositionButton);
    getCompositionButton.addActionListener(evt -> {
      this.setTitle("Portfolio Composition");
      getAllPortfolios();
    });

    getValueButton = new JButton("Check portfolio value");
    panel2.add(getValueButton);
    getValueButton.addActionListener(evt -> {
      this.setTitle("Portfolio Value");
      getAllPortfolios();
    });

    panel2.add(goBackButton);

    //has to be called by controller and be implemented in MenuGuiFlexible and MenuGuiInflexible
    getRestIfApplicable(panel2);

    return panel2;
  }

}
