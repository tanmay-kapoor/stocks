package views;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
  private JLabel text;
  private JLabel successMessage;
  protected CardLayout cl;
  private JPanel mainPanel;

  // panel 3 does the option chosen by user, e.g. create portfolio, get composition, etc.
  protected JPanel panel3;

  // panel 4 is for some uber specific requirements.
  protected JPanel panel4;
  protected GridBagConstraints gbc3;
  protected GridBagConstraints gbc4;

  protected abstract void displaySellPanel();

  protected abstract void getRestIfApplicable(JPanel panel2);

  protected AbstractMenuGui(Features features, String caption) {
    super(caption);
    this.features = features;

    setSize(1000, 600);
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

    gbc3 = new GridBagConstraints();
    gbc4 = new GridBagConstraints();

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
    gbcNewline();
    gbc3.gridwidth = 2;
    gbc3.ipady = 60;
    if (text != null) {
      //don't always remove this or don't use printMessage() all the time
      panel3.remove(text);
      panel3.revalidate();
    }
    text = new JLabel(msg);
    panel3.add(text, gbc3);

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
    if (showP3()) return;

    gbcNewline();
    panel3.add(new JLabel("Enter Date (YYYY-MM-DD) : "), gbc3);
    JTextField dateField = new JTextField(10);
    gbc3.gridx = 1;
    panel3.add(dateField, gbc3);

    gbcNewline();
    JButton getContentsBtn = new JButton("Get Contents");
    panel3.add(getContentsBtn, gbc3);

    JButton getWeightageBtn = new JButton("Get Stock Weightage");
    gbc3.gridx = 1;
    panel3.add(getWeightageBtn, gbc3);
    gbc3.gridx = 2;
    panel3.add(goBackButton, gbc3);

    getContentsBtn.addActionListener(e -> {
      setDateToNowIfEmpty(dateField);
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
      setDateToNowIfEmpty(dateField);
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
    if (showP3()) return;

    setGbcXY(0, 1);
    panel3.add(new JLabel("Enter Date (YYYY-MM-DD) : "), gbc3);
    JTextField dateField = new JTextField(10);
    gbc3.gridx = 1;
    panel3.add(dateField, gbc3);

    gbcNewline();
    JButton getValueBtn = new JButton("Get Portfolio Value");
    panel3.add(getValueBtn, gbc3);
    getValueBtn.addActionListener(evt -> {
      setDateToNowIfEmpty(dateField);
      double value = features.getPortfolioValue(portfolioName, dateField.getText());
      //data cleaning
      if (value != -1) {
        printMessage("Value of " + portfolioName
                + " as of " + dateField.getText()
                + " closing was: $" + value);
      }
    });

    setGbcXY(1, 2);
    panel3.add(goBackButton, gbc3);

    panel3.revalidate();
  }

  private boolean showP3() {
    cl.show(mainPanel, "panel 3");
    panel3.removeAll();
    resetGbc3();
    panel3.setLayout(new GridBagLayout());
//    setGbcSize(25,20);

    boolean res = getAllPortfolios();
    return !res;
  }


  protected void getPortfolioCostBasisOptions() {
    if (showP3()) return;

    gbcNewline();
    panel3.add(new JLabel("Enter Date (YYYY-MM-DD) : "), gbc3);
    JTextField dateField = new JTextField(10);
    gbc3.gridx = 1;
    panel3.add(dateField, gbc3);

    gbcNewline();
    JButton getCostBasisBtn = new JButton("Get Cost Basis");
    panel3.add(getCostBasisBtn, gbc3);
    getCostBasisBtn.addActionListener(evt -> {
      setDateToNowIfEmpty(dateField);
      double value = features.getCostBasis(portfolioName, dateField.getText());
      //data cleaning
      if (value != -1) {
        printMessage("Cost basis of " + portfolioName
                + " on " + dateField.getText()
                + " is: $" + value);
      }
    });

    gbc3.gridx = 1;
    panel3.add(goBackButton, gbc3);

    panel3.revalidate();
  }


  protected void getDcaOptions() {
    features.resetTotalWeightage();

    if (showP3()) return;

    Map<String, Double> stockWeightage = new HashMap<>();

    gbcNewline();
    panel3.add(new JLabel("Enter DCA strategy name"), gbc3);
    JTextField strategy = new JTextField("");
    gbc3.gridx = 1;
    panel3.add(strategy, gbc3);

    gbcNewline();
    panel3.add(new JLabel("Start Date (YYYY-MM-DD): "), gbc3);
    JTextField fromDate = new JTextField(10);
    gbc3.gridx = 1;
    panel3.add(fromDate, gbc3);

    gbcNewline();
    panel3.add(new JLabel("Optional End Date (YYYY-MM-DD): "), gbc3);
    JTextField toDate = new JTextField(10);
    gbc3.gridx = 1;
    panel3.add(toDate, gbc3);

    gbcNewline();
    panel3.add(new JLabel("Enter investment interval (days): "), gbc3);
    JTextField interval = new JTextField(10);
    gbc3.gridx = 1;
    panel3.add(interval, gbc3);

    gbcNewline();
    panel3.add(new JLabel("Enter investment amount ($): "), gbc3);
    JTextField amount = new JTextField(10);
    gbc3.gridx = 1;
    panel3.add(amount, gbc3);

    gbcNewline();
    panel3.add(new JLabel("Enter commission: "), gbc3);
    JTextField commission = new JTextField(10);
    gbc3.gridx = 1;
    panel3.add(commission, gbc3);

    gbcNewline();
    JTextField tickerChosen = new JTextField(10);
    JTextField weightage = new JTextField(10);
    JButton addBtn = new JButton("Add to strategy");
    addBtn.addActionListener(evt -> {
      //might throw error
      features.addTickerToStrategy(tickerChosen.getText(), weightage.getText());
      stockWeightage.put(tickerChosen.getText(), Double.parseDouble(weightage.getText()));

      if (features.getWeightageLeft() > 0) {
        cl.show(mainPanel, "panel 4");
        addTickerPanel(stockWeightage, tickerChosen, weightage, addBtn);
      } else {
        System.out.println(portfolioName);
        features.saveDca(portfolioName, strategy.getText(), amount.getText(), fromDate.getText(),
                toDate.getText(), interval.getText(), commission.getText());
        features.savePortfolio(portfolioName);
      }
    });

    JButton addShareWeightageBtn = new JButton("Choose share weightage");
    addShareWeightageBtn.addActionListener(evt -> {
      if (strategy.getText().equals("") || fromDate.getText().equals("")
              || interval.getText().equals("") || amount.getText().equals("")
              || commission.getText().equals("")) {
        printMessage("Please Fill all the necessary fields.");
        // IDK WHAT TO DO HERE
      }

      cl.show(mainPanel, "panel 4");
      addTickerPanel(stockWeightage, tickerChosen, weightage, addBtn);
    });
    panel3.add(addShareWeightageBtn, gbc3);

    gbc3.gridx = 1;
    panel3.add(goBackButton, gbc3);

    panel3.revalidate();

  }

  private void addTickerPanel(Map<String, Double> stockWeightage, JTextField tickerChosen,
                              JTextField weightage, JButton addBtn) {
    panel4.removeAll();
    tickerChosen.setText("");
    weightage.setText("");
    resetGbc4();
    panel4.setLayout(new GridBagLayout());

    for (String ticker : stockWeightage.keySet()) {
      gbc4Newline();
      panel4.add(new JLabel(ticker), gbc4);
      gbc4.gridx = 1;
      panel4.add(new JLabel(stockWeightage.get(ticker).toString() + "%"), gbc4);
    }

    gbc4Newline();
    panel4.add(new JLabel("Add ticker: "), gbc4);
    gbc4.gridx = 1;
    panel4.add(tickerChosen, gbc4);

    gbc4Newline();
    panel4.add(new JLabel("Choose Weightage: (" + features.getWeightageLeft() + "% left)"), gbc4);
    gbc4.gridx = 1;
    panel4.add(weightage, gbc4);

    gbc4Newline();
    panel4.add(addBtn, gbc4);
    gbc4.gridx = 1;
    panel4.add(backToP3Btn, gbc4);
    panel4.revalidate();
  }


  @Override
  public void getBuySellChoice() {
    if (showP3()) return;

    JButton buyOptionBtn = new JButton("Buy Shares");
    JButton sellOptionBtn = new JButton("Sell Shares");

    gbcNewline();
    panel3.add(buyOptionBtn, gbc3);
    gbc3.gridx = 1;
    panel3.add(sellOptionBtn, gbc3);

    gbcNewline();
    gbc3.gridwidth = 2;
    panel3.add(goBackButton, gbc3);

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
    if (showP3()) return;

    gbcNewline();
    panel3.add(new JLabel("Start Date (YYYY-MM-DD) : "), gbc3);
    JTextField fromDate = new JTextField(10);
    gbc3.gridx = 1;
    panel3.add(fromDate, gbc3);

    gbcNewline();
    panel3.add(new JLabel("End Date (YYYY-MM-DD) : "), gbc3);
    JTextField toDate = new JTextField(10);
    gbc3.gridx = 1;
    panel3.add(toDate, gbc3);

    gbcNewline();
    JButton getPerformanceBtn = new JButton("Get Performance");
    getPerformanceBtn.addActionListener(evt -> {
      Report performanceReport = features.getPortfolioPerformance(portfolioName,
              fromDate.getText(), toDate.getText());
      if (performanceReport != null) {
        showPerformanceGraph(performanceReport);
      }
    });
    panel3.add(getPerformanceBtn, gbc3);

    gbc3.gridx = 1;
    panel3.add(goBackButton, gbc3);

    panel3.revalidate();
  }


  private boolean getAllPortfolios() {

    List<String> portfolios = features.getAllPortfolios();

    if (portfolios.size() == 0) {
      printMessage("No Portfolios. Please Create atleast one and come back again.");
      gbcNewline();
      panel3.add(goBackButton, gbc3);
      panel3.revalidate();
      return false;
    }

    panel3.add(new JLabel("Choose a portfolio from the list"), gbc3);

    gbc3.gridx = 1;
    gbc3.gridy = 0;

    String[] portfolioList = Arrays.copyOf(portfolios.toArray(), portfolios.size(), String[].class);
    portfolioListCb = new JComboBox<>(portfolioList);
    portfolioName = portfolioListCb.getItemAt(0);
    portfolioListCb.addActionListener(evt ->
            portfolioName = Objects.requireNonNull(portfolioListCb.getSelectedItem()).toString());
    panel3.add(portfolioListCb, gbc3);

    panel3.revalidate();
    return true;
  }

  private void showTable(String[][] data) {
    gbcNewline();
    String[] colNames = {"Ticker", "Quantity"};
    panel3.remove(6);
    JTable table = new JTable(data, colNames);
    JScrollPane sp = new JScrollPane(table);
    gbc3.gridwidth = 2;
    gbc3.ipady = 250;
    panel3.add(sp, gbc3);

//    gbcNewline();
//    gbc3.ipady = 30;
//    panel3.add(goBackButton, gbc3);
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
    resetGbc4();

    BarChart barChart = new BarChart(report, portfolioName);
    panel4.add(barChart.getChart());

//    LineChart lineChart = new LineChart(report, portfolioName);
//    panel4.add(lineChart.getLineChart());


//    Map<LocalDate, Performance> dateWisePerformance = report.getPerformanceOnEachDate();

//    for (LocalDate date : dateWisePerformance.keySet()) {
//      panel4.add(new JLabel(date.toString()));
//
//      String precisionAdjusted = dateWisePerformance.get(date).getPrecisionAdjusted();
//      int stars = dateWisePerformance.get(date).getStars();
//
//      panel4.add(new JLabel(precisionAdjusted));
//      panel4.add(new JLabel("" + stars));
//    }


//    panel4.add(goBackButton);
    panel4.revalidate();
  }

  private void goToPanel4() {
    cl.show(mainPanel, "panel 4");
    panel4.removeAll();
    resetGbc4();
  }

  private void setDateToNowIfEmpty(JTextField dateField) {
    if (dateField.getText().equals("")) {
      dateField.setText(LocalDate.now().toString());
    }
  }


  //////////////////////////////////FRAME RELATED SHIT//////////////////////

  private JPanel getMainPanel() {
    JPanel mainPanel = new JPanel(new CardLayout());

    JPanel panel1 = getPanel1();
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
    panel1.setLayout(new GridLayout(0, 1, 20, 20));
    panel1.setBorder(new EmptyBorder(100,200,100,200));

    JButton flexibleButton = new JButton("Flexible");
    panel1.add(flexibleButton);

    JButton inflexibleButton = new JButton("Inflexible");
    panel1.add(inflexibleButton);

    JButton backToTextUi = new JButton("Back To Text UI");
    panel1.add(backToTextUi);
    backToTextUi.addActionListener(e -> this.dispose());

    panel1.add(exitButton);

    flexibleButton.addActionListener(evt -> features.handleFlexibleSelected());

    return panel1;
  }

  private JPanel getPanel2() {
    JPanel panel2 = new JPanel();
    panel2.setLayout(new GridLayout(0, 2, 10, 10));
    panel2.setBorder(new EmptyBorder(7, 10, 7, 10));

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

  protected void setGbcXY(int c, int r) {
    gbc3.gridx = c;
    gbc3.gridy = r;
  }

  protected void setGbcSize(int w, int h) {
    gbc3.ipadx = w;
    gbc3.ipady = h;
  }

  protected void gbcNewline() {
    gbc3.gridy += 1;
    gbc3.gridx = 0;
  }

  protected void gbc4Newline() {
    gbc4.gridy += 1;
    gbc4.gridx = 0;
  }

  public void resetGbc3() {
    gbc3 = new GridBagConstraints();
    gbc3.fill = GridBagConstraints.HORIZONTAL;
    setGbcSize(25,20);
  }

  public void resetGbc4() {
    gbc4 = new GridBagConstraints();
    gbc4.fill = GridBagConstraints.HORIZONTAL;
    gbc4.ipadx = 25;
    gbc4.ipady = 20;
  }
}
