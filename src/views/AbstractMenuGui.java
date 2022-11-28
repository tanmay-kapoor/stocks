package views;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import controllers.Features;
import models.Details;
import models.portfolio.Performance;
import models.portfolio.Report;
import models.portfolio.Txn;

abstract class AbstractMenuGui extends JFrame implements Menu {
  private JComboBox<String> portfolioListCb;
  protected String portfolioName;
  protected JTextField dateTxtFiled;
  protected final Features features;
  private final JButton exitButton;
  private final JButton goBackButton;
  protected JButton backToP3Btn;
  private final JButton enterBtn;
  protected JTextField ticker;
  protected JTextField quantity;
  protected JTextField commission;
  private Txn txn_type;
  private JLabel text;
  private JLabel successMessage;
  protected CardLayout cl;
  private JPanel mainPanel;

  // panel 3 does the option chosen by user, e.g. create portfolio, get composition, etc.
  protected JPanel panel3;

  // panel 4 is for some uber specific requirements.
  protected JPanel panel4;

  protected abstract void displaySellPanel();

  protected abstract void getRestIfApplicable(JPanel panel2);

  protected AbstractMenuGui(Features features, String caption) {
    super(caption);
    this.features = features;

    setSize(600, 600);
    setLocation(900, 100);

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    enterBtn = new JButton("Enter");

    goBackButton = new JButton("Go back");
    goBackButton.addActionListener(evt -> {
      cl.show(mainPanel, "Portfolio Features");

      if (Objects.equals(this.getTitle(), "Create Portfolio")) {
        features.savePortfolio(portfolioName);
      }
      this.setTitle("<<Portfolio Type>>");
    });

    backToP3Btn = new JButton("back");
    backToP3Btn.addActionListener(evt -> cl.show(mainPanel, "panel 3"));

    exitButton = new JButton("Exit");
    exitButton.addActionListener(evt -> features.exitProgram());

    this.mainPanel = getMainPanel();
    this.cl = (CardLayout) (mainPanel.getLayout());
    this.cl.show(mainPanel, "Main Panel");

    this.add(mainPanel);

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
    cl.show(mainPanel, "panel 3");
    panel3.removeAll();

    panel3.add(new JLabel("Enter portfolio name"));

    JTextField portfolioNameTextField = new JTextField(10);
    panel3.add(portfolioNameTextField);

    panel3.add(enterBtn);

    enterBtn.addActionListener(evt -> {
      portfolioName = portfolioNameTextField.getText();

      if (portfolioName.equals("")) {
        printMessage("Name cannot be empty");
      }

      features.createPortfolio(portfolioName);
    });

    panel3.revalidate();
  }


  @Override
  public void printMessage(String msg) {
    if (text != null) {
      //don't always remove this or don't use printMessage() all the time
      panel3.remove(text);
      panel3.revalidate();
    }
    text = new JLabel(msg);
    panel3.add(text);
    panel3.revalidate();
  }


  @Override
  public void successMessage(String ticker, Details details, Txn txnType) {
    if (successMessage != null) {
      panel4.remove(successMessage);
      panel4.revalidate();
    }
    String txn = txnType == Txn.Buy ? "bought" : "sold";
    String msg = String.format("Successfully %s %s shares of %s on %s", txn, details.getQuantity(),
            ticker, details.getPurchaseDate());
    successMessage = new JLabel(msg);
    panel4.add(successMessage);
    panel4.revalidate();
  }

  @Override
  public void getCreatePortfolioThroughWhichMethod() {
    panel3.removeAll();

    JButton interfaceButton = new JButton("Interface");
    panel3.add(interfaceButton);
    interfaceButton.addActionListener(evt -> getPortfolioName());

    JButton uploadButton = new JButton("File upload");
    panel3.add(uploadButton);
    uploadButton.addActionListener(evt -> getFiles());

    panel3.add(goBackButton);

    panel3.revalidate();
    cl.show(mainPanel, "panel 3");
  }

  @Override
  public void getAddToPortfolioChoice() {
    panel3.removeAll();

    JButton addShareBtn = new JButton("Add share");
    addShareBtn.setPreferredSize(new Dimension(40, 40));
    addShareBtn.addActionListener(evt -> {
      cl.show(mainPanel, "panel 4");
      displayBuyPanel();
    });
    panel3.add(addShareBtn);


    JButton createStrategyBtn = new JButton("Create DCA strategy");
    createStrategyBtn.setPreferredSize(new Dimension(40, 40));
    panel3.add(createStrategyBtn);

    panel3.add(goBackButton);

    panel3.revalidate();
  }

  @Override
  public void getFilePath() {

  }

  @Override
  public void getTickerSymbol() {

    panel4.add(new JLabel("Ticker symbol : "));
    this.ticker = new JTextField(10);
    panel4.add(ticker);
  }

  @Override
  public void getQuantity() {
    panel4.add(new JLabel("Number of shares : "));

    this.quantity = new JTextField(10);
    panel4.add(quantity);
    quantity.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent key) {
        quantity.setEditable((key.getKeyChar() >= '0' && key.getKeyChar() <= '9')
                || key.getKeyCode() == 8);
      }
    });
  }

  @Override
  public void getDateChoice() {
    panel4.add(new JLabel("Date (YYYY-MM-DD) : "));

    this.dateTxtFiled = new JTextField(10);
    panel4.add(dateTxtFiled);
  }

  @Override
  public void getCommissionFee() {
    panel4.add(new JLabel("Commission Fee : "));

    commission = new JTextField(10);
    panel4.add(commission);
    commission.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent key) {
        commission.setEditable((key.getKeyChar() >= '0' && key.getKeyChar() <= '9')
                || key.getKeyCode() == 8 || key.getKeyChar() == '.');
      }
    });
  }

  @Override
  public void getInterval() {

  }

  @Override
  public void getWeightage() {

  }

  @Override
  public void getStrategyAmount() {

  }

  @Override
  public void getStrategyName() {

  }


  @Override
  public void getDateForValue() {
    printMessage("Choose date : ");

    JTextField dateTxtFiled = new JTextField(10);
    panel3.add(dateTxtFiled);
  }

  @Override
  public void getPortfolioCompositionOption() {
    cl.show(mainPanel, "panel 3");
    panel3.removeAll();

    getAllPortfolios();

    panel3.add(new JLabel("Enter Date (YYYY-MM-DD) : "));
    JTextField dateField = new JTextField(10);
    panel3.add(dateField);

    JButton getContentsBtn = new JButton("Get Contents");
    panel3.add(getContentsBtn);

    JButton getWeightageBtn = new JButton("Get Stock Weightage");
    panel3.add(getWeightageBtn);

    panel3.add(goBackButton);

    getContentsBtn.addActionListener(e -> {
      Map<String, Double> composition = features.getPortfolioContents(portfolioName, dateField.getText());
      if (!composition.isEmpty()) {
        //data cleaning
        String[][] data = new String[composition.size()][2];
        int count = 0;
        for (Map.Entry<String, Double> entry : composition.entrySet()) {
          data[count][0] = entry.getKey();
          data[count][1] = entry.getValue().toString();
          count++;
        }

        showTable(data);
      } else {
        printMessage("No stocks existed on this date");
      }
    });

    getWeightageBtn.addActionListener(e -> {
      Map<String, Double> weightage = features.getPortfolioWeightage(portfolioName, dateField.getText());
      if (weightage.isEmpty()) {
        printMessage("No stocks existed on this date");
      } else {
        //data cleaning
        String[][] data = new String[weightage.size()][2];
        int count = 0;
        for (Map.Entry<String, Double> entry : weightage.entrySet()) {
          data[count][0] = entry.getKey();
          data[count][1] = entry.getValue().toString() + "%";
          count++;
        }

        showTable(data);
      }
    });

    panel3.revalidate();
  }


  private void getPortfolioValueOptions() {
    cl.show(mainPanel, "panel 3");
    panel3.removeAll();

    getAllPortfolios();

    panel3.add(new JLabel("Enter Date (YYYY-MM-DD) : "));
    JTextField dateField = new JTextField(10);
    panel3.add(dateField);

    JButton getValueBtn = new JButton("Get Portfolio Value");
    panel3.add(getValueBtn);
    getValueBtn.addActionListener(evt -> {
      double value = features.getPortfolioValue(portfolioName, dateField.getText());
      //data cleaning
      if (value != -1) {
        printMessage("Value of " + portfolioName
                + " on " + dateField.getText()
                + " is: $" + value);
      }
    });

    panel3.add(goBackButton);

    panel3.revalidate();
  }

  protected void getPortfolioCostBasisOptions() {
    cl.show(mainPanel, "panel 3");
    panel3.removeAll();

    getAllPortfolios();

    panel3.add(new JLabel("Enter Date (YYYY-MM-DD) : "));
    JTextField dateField = new JTextField(10);
    panel3.add(dateField);

    JButton getCostBasisBtn = new JButton("Get Cost Basis");
    panel3.add(getCostBasisBtn);
    getCostBasisBtn.addActionListener(evt -> {
      double value = features.getCostBasis(portfolioName, dateField.getText());
      //data cleaning
      if (value != -1) {
        printMessage("Value of " + portfolioName
                + " on " + dateField.getText()
                + " is: $" + value);
      }
    });

    panel3.add(goBackButton);

    panel3.revalidate();
  }


  protected void getDcaOptions() {
    cl.show(mainPanel, "panel 3");
    panel3.removeAll();

    getAllPortfolios();

    panel3.add(new JLabel("Enter DCA strategy name"));
    JTextField strategy = new JTextField("");
    panel3.add(strategy);

    panel3.add(new JLabel("Start Date (YYYY-MM-DD): "));
    JTextField fromDate = new JTextField(10);
    panel3.add(fromDate);

    panel3.add(new JLabel("Optional End Date (YYYY-MM-DD): "));
    JTextField toDate = new JTextField(10);
    panel3.add(toDate);

    panel3.add(new JLabel("Enter investment interval (days): "));
    JTextField interval = new JTextField(10);
    panel3.add(interval);

    panel3.add(new JLabel("Enter investment amount ($): "));
    JTextField amount = new JTextField(10);
    panel3.add(amount);

    panel3.add(new JLabel("Enter commission: "));
    JTextField commission = new JTextField(10);
    panel3.add(commission);

    JButton addShareWeightageBtn = new JButton("Choose share weightage");

    addShareWeightageBtn.addActionListener(evt -> {
      if (strategy.getText() == "" || fromDate.getText() == ""
            || interval.getText() == "" || amount.getText() == "" || commission.getText() == "") {
        printMessage("Please Fill all the necessary fields.");
        // IDK WHAT TO DO HERE
      }

      cl.show(mainPanel, "panel 4");
      displayShareWeightage(strategy.getText(), LocalDate.parse(fromDate.getText()),
              LocalDate.parse(toDate.getText()), Integer.parseInt(interval.getText()),
              Double.parseDouble(amount.getText()), Double.parseDouble(commission.getText()));
    });
    panel3.add(addShareWeightageBtn);

    panel3.add(goBackButton);

    panel3.revalidate();

  }

  private void displayShareWeightage(String strategy, LocalDate from, LocalDate to,
                                     int interval, double amount, double commission) {
    double weightLeft = 100;
    panel4.removeAll();
//    features.resetWeightage()

    do {
      panel4.add(new JLabel("Weight left: "));
      panel4.add(new JLabel(weightLeft + "%"));

      panel4.add(new JLabel("Add ticker: "));
      JTextField ticker = new JTextField(10);
      panel4.add(ticker);

      JButton addBtn = new JButton("Add");
      panel4.add(addBtn);
      addBtn.addActionListener(evt -> {
//        weightLeft = weightLeft - 30;
//        features.dca();
      });
      weightLeft -= 30;
    }
    while (weightLeft > 0);

    panel4.revalidate();
  }

  @Override
  public void getBuySellChoice() {
    cl.show(mainPanel, "panel 3");
    panel3.removeAll();

    getAllPortfolios();

    JButton buyOptionBtn = new JButton("Buy Shares");
    JButton sellOptionBtn = new JButton("Sell Shares");

    panel3.add(buyOptionBtn);
    panel3.add(sellOptionBtn);
    panel3.add(goBackButton);

    panel3.revalidate();

    buyOptionBtn.addActionListener(evt -> {
      cl.show(mainPanel, "panel 4");
      displayBuyPanel();
    });
    sellOptionBtn.addActionListener(evt -> {
      cl.show(mainPanel, "panel 4");
      displaySellPanel();
    });

  }

  private void displayBuyPanel() {
    panel4.removeAll();

    getTickerSymbol();
    getQuantity();
    getDateChoice();
    getCommissionFee();

    JButton addBtn = new JButton("Buy");
    panel4.add(addBtn);

    panel4.add(backToP3Btn);

    addBtn.addActionListener(e ->
            features.buyStock(
                    portfolioName,
                    ticker.getText(),
                    quantity.getText(),
                    dateTxtFiled.getText(),
                    commission.getText()
            ));

    panel4.revalidate();
  }


  protected void getPortfolioPerformanceOption() {
    cl.show(mainPanel, "panel 3");
    panel3.removeAll();

    getAllPortfolios();

    panel3.add(new JLabel("Start Date (YYYY-MM-DD) : "));
    JTextField fromDate = new JTextField(10);
    panel3.add(fromDate);

    panel3.add(new JLabel("End Date (YYYY-MM-DD) : "));
    JTextField toDate = new JTextField(10);
    panel3.add(toDate);

    JButton getPerformanceBtn = new JButton("Get Performance");
    getPerformanceBtn.addActionListener(evt -> {
      Report performanceReport = features.getPortfolioPerformance(portfolioName,
              fromDate.getText(), toDate.getText());
      if (performanceReport != null) {
        showPerformanceGraph(performanceReport);
      }
    });
    panel3.add(getPerformanceBtn);

    panel3.add(goBackButton);

    panel3.revalidate();
  }


  private void getAllPortfolios() {

    List<String> portfolios = features.getAllPortfolios();
    if (portfolios.size() == 0) {
      printMessage("No Portfolios. Please Create atleast one and come back again.");
      panel3.add(goBackButton);
      panel3.revalidate();
      return;
    }

    panel3.add(new JLabel("Choose a portfolio from the list"));

    String[] portfolioList = Arrays.copyOf(portfolios.toArray(), portfolios.size(), String[].class);
    portfolioListCb = new JComboBox<>(portfolioList);
    portfolioName = portfolioListCb.getItemAt(0);
    portfolioListCb.addActionListener(evt ->
            portfolioName = Objects.requireNonNull(portfolioListCb.getSelectedItem()).toString());
    panel3.add(portfolioListCb);

    panel3.revalidate();
  }

  private void showTable(String[][] data) {
    panel3.removeAll();

    String[] colNames = {"Ticker", "Quantity"};
    JTable table = new JTable(data, colNames);
    JScrollPane sp = new JScrollPane(table);
    panel3.add(sp);
    panel3.add(goBackButton);

    panel3.revalidate();
  }

  private void getFiles() {
    panel3.removeAll();

    FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV only", "csv");

    panel3.add(new JLabel("Add Portfolio file"));
    JFileChooser portfolioJfc = new JFileChooser(FileSystemView.getFileSystemView());
    portfolioJfc.setFileFilter(filter);
    panel3.add(portfolioJfc);
    portfolioJfc.addActionListener(evt -> {
      System.out.println(portfolioJfc.getSelectedFile());
      features.handleCreatePortfolioThroughUpload(portfolioJfc.getSelectedFile().toString());
    });


    panel3.add(new JLabel("Add Dollar Cost Average file [Optional]"));
    JFileChooser dcaFfc = new JFileChooser(FileSystemView.getFileSystemView());
    dcaFfc.setFileFilter(filter);
    panel3.add(dcaFfc);
    dcaFfc.addActionListener(evt -> {
      int result = dcaFfc.showOpenDialog(this);
      if (result == JFileChooser.APPROVE_OPTION) {
        File selectedFile = dcaFfc.getSelectedFile();
        System.out.println("Selected file: " + selectedFile.getAbsolutePath());
      }
    });

    JButton createPortfolioBtn = new JButton("Create Portfolio");
    panel3.add(createPortfolioBtn);
//    createPortfolioBtn.addActionListener(evt -> features.gePortfolioFromCsv);

    panel3.revalidate();
  }

  private void showPerformanceGraph(Report report) {
    cl.show(mainPanel, "panel 4");
    panel4.removeAll();

    Map<LocalDate, Performance> dateWisePerformance = report.getPerformanceOnEachDate();
    String scale = report.getScale();
    String baseValue = report.getBaseValue();

    for (LocalDate date : dateWisePerformance.keySet()) {
      panel4.add(new JLabel(date.toString()));

      String precisionAdjusted = dateWisePerformance.get(date).getPrecisionAdjusted();
      int stars = dateWisePerformance.get(date).getStars();

      panel4.add(new JLabel(precisionAdjusted));
      panel4.add(new JLabel("" + stars));
    }

    panel4.add(new JLabel("scale: " + scale));
    panel4.add(new JLabel("base value: " + baseValue));

    panel4.add(goBackButton);
    panel4.revalidate();
  }


  //////////////////////////////////FRAME RELATED SHIT//////////////////////

  private JPanel getMainPanel() {
    JPanel mainPanel = new JPanel(new CardLayout());

    // panel 1 stores choice between flexible and inflexible menu
    JPanel panel1 = getPanel1();
    //panel 2 gives portfolio specific features options in the menu
    JPanel panel2 = getPanel2();
    this.panel3 = new JPanel(new GridLayout(0, 2, 10, 10));
    this.panel4 = new JPanel(new GridLayout(0, 2, 10, 10));

    mainPanel.add(panel1, "Main Menu");
    mainPanel.add(panel2, "Portfolio Features");
    mainPanel.add(panel3, "panel 3");
    mainPanel.add(panel4, "panel 4");

    return mainPanel;
  }

  private JPanel getPanel1() {
    JPanel panel1 = new JPanel();
    panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));

    JButton flexibleButton = new JButton("Flexible");
    panel1.add(flexibleButton);

    JButton inflexibleButton = new JButton("Inflexible");
    panel1.add(inflexibleButton);

    JButton backToTextUi = new JButton("Back To Text UI");
    panel1.add(backToTextUi);
    backToTextUi.addActionListener(e -> this.dispose());

    panel1.add(exitButton);

    flexibleButton.addActionListener(evt -> features.handleFlexibleSelected());
//    inflexibleButton.addActionListener(evt -> cl.show(mainPanel, "2"));

    return panel1;
  }

  private JPanel getPanel2() {
    JPanel panel2 = new JPanel();
    panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));

    JButton createPortfolioButton = new JButton("Create portfolio");
    panel2.add(createPortfolioButton);
    createPortfolioButton.addActionListener(evt -> {
      this.setTitle("Create Portfolio");
      getCreatePortfolioThroughWhichMethod();
    });

    JButton getCompositionButton = new JButton("See portfolio composition");
    panel2.add(getCompositionButton);
    getCompositionButton.addActionListener(evt -> {
      this.setTitle("Portfolio Composition");
      getPortfolioCompositionOption();
    });

    JButton getValueButton = new JButton("Check portfolio value");
    panel2.add(getValueButton);
    getValueButton.addActionListener(evt -> {
      this.setTitle("Portfolio Value");
      getPortfolioValueOptions();
    });

    //has to be called by controller and be implemented in MenuGuiFlexible and MenuGuiInflexible
    getRestIfApplicable(panel2);

    JButton backP1Btn = new JButton("Back to Main Menu");
    backP1Btn.addActionListener(evt -> cl.show(mainPanel, "Main Menu"));
    panel2.add(backP1Btn);

    return panel2;
  }

}
