package views;

import org.jfree.chart.ChartPanel;

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
import models.portfolio.Report;
import models.portfolio.Txn;

import static javax.swing.JOptionPane.showMessageDialog;

abstract class AbstractMenuGui extends JFrame implements Menu {
  private JComboBox<String> portfolioListCb;
  private final JButton enterBtn;
  protected String portfolioName;
  protected JTextField dateTxtFiled;
  protected JLabel text;
  protected final Features features;
  private final JButton exitButton;
  private final JButton goBackButton;
  protected JButton backToP3Btn;

  protected JTextField ticker;
  protected JTextField quantity;
  protected JTextField commission;
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

  public AbstractMenuGui(Features features, String caption) {
    super(caption);
    this.features = features;

    this.setSize(800, 600);
    this.setLocation(200, 100);

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    enterBtn = new JButton("Enter");

    goBackButton = new JButton("Go back");
    goBackButton.addActionListener(evt -> {
      cl.show(mainPanel, "Portfolio Features");

      if (Objects.equals(this.getTitle(), "Create Portfolio") && portfolioName != null) {
        features.savePortfolio(portfolioName);
      }
      this.setTitle("Flexible Portfolio");
    });

    backToP3Btn = new JButton("back");
    backToP3Btn.addActionListener(evt -> {
      this.setSize(1000, 600);
      cl.show(mainPanel, "panel 3");
    });

    exitButton = new JButton("Exit");
    exitButton.addActionListener(evt -> this.features.exitProgram());

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
    panel3.removeAll();
    resetGbc3();
    panel3.add(new JLabel("Enter portfolio name"), gbc3);
    gbc3.gridx = 1;
    JTextField portfolioNameTextField = new JTextField(10);
    panel3.add(portfolioNameTextField, gbc3);

    gbcNewline();
    panel3.add(enterBtn, gbc3);
    gbc3.gridx = 1;
    panel3.add(goBackButton, gbc3);

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
  public void errorMessage(String msg) {
    showMessageDialog(null, msg);
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

    gbc4Newline();
    gbc4.gridwidth = 2;
    panel4.add(successMessage, gbc4);

    resetFields();

    panel4.revalidate();
  }

  @Override
  public void getCreatePortfolioThroughWhichMethod() {
    cl.show(mainPanel, "panel 3");
    panel3.removeAll();
    resetGbc3();

    JButton interfaceButton = new JButton("Create through interface");
    panel3.add(interfaceButton, gbc3);
    interfaceButton.addActionListener(evt -> getPortfolioName());

    JButton uploadButton = new JButton("Create through  file upload");
    gbc3.gridx = 1;
    panel3.add(uploadButton, gbc3);
    uploadButton.addActionListener(evt -> getFiles());

    gbcNewline();
    gbc3.gridwidth = 2;
    panel3.add(goBackButton, gbc3);

    panel3.revalidate();
  }

  @Override
  public void getAddToPortfolioChoice() {
    panel3.remove(enterBtn);

    gbcNewline();
    JButton addShareBtn = new JButton("Add share");
    addShareBtn.addActionListener(evt -> displayBuyPanel());
    panel3.add(addShareBtn, gbc3);

    gbc3.gridx = 1;
    JButton createStrategyBtn = new JButton("Create DCA strategy");
    createStrategyBtn.addActionListener(evt -> {
      String chosenName = portfolioName;
      getDcaOptions();
      panel3.remove(0);
      panel3.remove(portfolioListCb);
      portfolioName = chosenName;
    });
    panel3.add(createStrategyBtn, gbc3);

    gbcNewline();
    panel3.add(goBackButton, gbc3);

    panel3.revalidate();
  }

  @Override
  public void getFilePath() {

  }

  @Override
  public void getTickerSymbol() {
    gbc4Newline();
    panel4.add(new JLabel("Ticker symbol : "), gbc4);
    this.ticker = new JTextField(10);
    gbc4.gridx = 1;
    panel4.add(ticker, gbc4);
  }

  @Override
  public void getQuantity() {
    gbc4Newline();
    panel4.add(new JLabel("Number of shares : "), gbc4);

    this.quantity = new JTextField(10);
    gbc4.gridx = 1;
    panel4.add(quantity, gbc4);
    quantity.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent key) {
        quantity.setEditable((key.getKeyChar() >= '0' && key.getKeyChar() <= '9')
                || key.getKeyCode() == 8);
      }
    });
  }

  @Override
  public void getDateChoice() {
    gbc4Newline();
    panel4.add(new JLabel("Date (YYYY-MM-DD) : "), gbc4);

    this.dateTxtFiled = new JTextField(10);
    gbc4.gridx = 1;
    panel4.add(dateTxtFiled, gbc4);
  }

  @Override
  public void getCommissionFee() {
    gbc4Newline();
    panel4.add(new JLabel("Commission Fee : "), gbc4);

    commission = new JTextField(10);
    gbc4.gridx = 1;
    panel4.add(commission, gbc4);
    commission.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent key) {
        commission.setEditable((key.getKeyChar() >= '0' && key.getKeyChar() <= '9')
                || key.getKeyCode() == 8 || key.getKeyChar() == '.');
      }
    });
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

    gbcNewline();
    panel3.add(goBackButton, gbc3);

    JScrollPane sp = new JScrollPane();

    getContentsBtn.addActionListener(e -> {
      setDateToNowIfEmpty(dateField);
      Map<String, Double> composition = features.getPortfolioContents(portfolioName,
              dateField.getText());
      if (composition != null) {
        if (!composition.isEmpty()) {
          //data cleaning
          String[][] data = new String[composition.size()][2];
          int count = 0;
          for (Map.Entry<String, Double> entry : composition.entrySet()) {
            data[count][0] = entry.getKey();
            data[count][1] = entry.getValue().toString();
            count++;
          }

          showTable(data, sp);

          gbcNewline();
          gbc3.gridwidth = 1;
          panel3.add(goBackButton);
        } else {
          printMessage("No stocks existed on this date");
        }
      }
    });

    getWeightageBtn.addActionListener(e -> {
      setDateToNowIfEmpty(dateField);
      Map<String, Double> weightage = features.getPortfolioWeightage(portfolioName,
              dateField.getText());
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

        showTable(data, sp);

        gbcNewline();
        gbc3.gridwidth = 1;
        panel3.add(goBackButton);
      }
    });

    panel3.revalidate();
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

    buyOptionBtn.addActionListener(evt -> displayBuyPanel());

    sellOptionBtn.addActionListener(evt -> {
      goToPanel4();
      displaySellPanel();
    });

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
                + " is: $" + String.format("%.2f", value));
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
    strategy.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent key) {
        strategy.setEditable(key.getKeyChar() != ',' || key.getKeyCode() == 8);
      }
    });
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
    interval.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent key) {
        interval.setEditable(key.getKeyChar() >= '0' && key.getKeyChar() <= '9' || key.getKeyCode() == 8);
      }
    });
    gbc3.gridx = 1;
    panel3.add(interval, gbc3);

    gbcNewline();
    panel3.add(new JLabel("Enter investment amount ($): "), gbc3);
    JTextField amount = new JTextField(10);
    amount.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent key) {
        amount.setEditable(key.getKeyChar() >= '0' && key.getKeyChar() <= '9' ||
                key.getKeyChar() == '.' || key.getKeyCode() == 8);
      }
    });
    gbc3.gridx = 1;
    panel3.add(amount, gbc3);

    gbcNewline();
    panel3.add(new JLabel("Enter commission: "), gbc3);
    JTextField commission = new JTextField(10);
    commission.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent key) {
        commission.setEditable((key.getKeyChar() >= '0' && key.getKeyChar() <= '9')
                || key.getKeyCode() == 8 || key.getKeyChar() == '.');
      }
    });
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
        goToPanel4();
        addTickerPanel(stockWeightage, tickerChosen, weightage, addBtn);
      } else {
        features.saveDca(portfolioName, strategy.getText(), amount.getText(), fromDate.getText(),
                toDate.getText(), interval.getText(), commission.getText(), stockWeightage);
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
      } else {
        goToPanel4();
        addTickerPanel(stockWeightage, tickerChosen, weightage, addBtn);
      }

    });
    panel3.add(addShareWeightageBtn, gbc3);

    gbc3.gridx = 1;
    panel3.add(goBackButton, gbc3);

    panel3.revalidate();

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

    JLabel warning = new JLabel("Please wait, generating performance report " +
            "may take some time.");
    warning.setForeground(Color.red);

    gbcNewline();
    JButton getBarChartBtn = new JButton("Get Bar Chart");
    getBarChartBtn.addActionListener(evt -> {
      panel3.add(warning, gbc3);
      panel3.revalidate();
      Report performanceReport = features.getPortfolioPerformance(portfolioName,
              fromDate.getText(), toDate.getText());
      if (performanceReport != null) {
        showPerformanceGraph(performanceReport, ChartType.BAR_CHART);
      }
    });
    panel3.add(getBarChartBtn, gbc3);

    JButton getLineChartBtn = new JButton("Get Line Chart");
    getLineChartBtn.addActionListener(evt -> {
      panel3.add(warning, gbc3);
      panel3.revalidate();
      Report performanceReport = features.getPortfolioPerformance(portfolioName,
              fromDate.getText(), toDate.getText());
      if (performanceReport != null) {
        showPerformanceGraph(performanceReport, ChartType.LINE_CHART);
      }
    });
    gbc3.gridx = 1;
    panel3.add(getLineChartBtn, gbc3);

    gbcNewline();
    panel3.add(goBackButton, gbc3);

    gbcNewline();
    gbc3.gridwidth = 2;

    panel3.revalidate();
  }


  protected void gbcNewline() {
    gbc3.gridy += 1;
    gbc3.gridx = 0;
  }

  protected void gbc4Newline() {
    gbc4.gridy += 1;
    gbc4.gridx = 0;
  }

  protected void resetGbc3() {
    gbc3 = new GridBagConstraints();
    gbc3.fill = GridBagConstraints.HORIZONTAL;
    gbc3.ipadx = 25;
    gbc3.ipady = 20;
    gbcNewline();
  }

  protected void resetGbc4() {
    gbc4 = new GridBagConstraints();
    gbc4.fill = GridBagConstraints.HORIZONTAL;
    gbc4.ipadx = 25;
    gbc4.ipady = 20;
    gbc4Newline();
  }


  //////////////////////////////////FRAME RELATED SHIT//////////////////////

  private JPanel getMainPanel() {
    JPanel mainPanel = new JPanel(new CardLayout());

    JPanel panel1 = getPanel1();
    JPanel panel2 = getPanel2();
    this.panel3 = new JPanel(new GridBagLayout());
    this.panel4 = new JPanel(new GridBagLayout());

    mainPanel.add(panel1, "Main Menu");
    mainPanel.add(panel2, "Portfolio Features");
    mainPanel.add(panel3, "panel 3");
    mainPanel.add(panel4, "panel 4");

    return mainPanel;
  }

  private JPanel getPanel1() {
    JPanel panel1 = new JPanel();
    panel1.setLayout(new GridLayout(0, 1, 20, 20));
    panel1.setBorder(new EmptyBorder(100, 200, 100, 200));

    JButton flexibleButton = new JButton("Flexible Portfolio");
    panel1.add(flexibleButton);

    JButton inflexibleButton = new JButton("Inflexible Portfolio");
    panel1.add(inflexibleButton);

    JButton backToTextUi = new JButton("Back To Text UI");
    panel1.add(backToTextUi);
    backToTextUi.addActionListener(e -> this.dispose());

    panel1.add(exitButton);

    flexibleButton.addActionListener(evt -> {
      this.setTitle("Flexible Portfolio");
      features.handleFlexibleSelected();
    });

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


  private void resetFields() {
    ticker.setText("");
    quantity.setText("");
    dateTxtFiled.setText("");
    commission.setText("");
  }



  private void getPortfolioValueOptions() {
    if (showP3()) return;

    gbcNewline();
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
                + " closing was: $" + String.format("%.2f", value));
      }
    });

    gbc3.gridx = 1;
    panel3.add(goBackButton, gbc3);

    panel3.revalidate();
  }

  private boolean showP3() {
    cl.show(mainPanel, "panel 3");
    panel3.removeAll();
    resetGbc3();

    boolean res = getAllPortfolios();
    return !res;
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

  private void showTable(String[][] data, JScrollPane sp) {
    panel3.remove(6);
    String[] colNames = {"Ticker", "Quantity"};
    JTable table = new JTable(data, colNames);
    sp = new JScrollPane(table);

    gbcNewline();
    gbc3.gridwidth = 2;
    gbc3.ipady = 250;
    panel3.add(sp, gbc3);

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

    panel3.revalidate();
  }

  private void showPerformanceGraph(Report report, ChartType chartType) {
    Chart chart = new Chart(report, portfolioName);
    ChartPanel cp;

    JLabel scaleLabel = new JLabel("");

    if (chartType == ChartType.BAR_CHART) {
      cp = chart.getBartChart();
      scaleLabel.setText("Scale: 1 unit on x axis ~ $" + report.getScale()
              + " relative to the base value of $" + report.getBaseValue());
    } else {
      cp = chart.getLineChart();
      scaleLabel.setText("Scale: 1 unit on y axis ~ $" + report.getScale()
              + " relative to the base value of $" + report.getBaseValue());
    }

    goToPanel4();
    gbc4.weightx = 1;
    panel4.add(cp, gbc4);

    gbc4Newline();
    gbc4.weightx = 0;
    panel4.add(scaleLabel, gbc4);

    gbc4Newline();
    panel4.add(backToP3Btn, gbc4);

    panel4.revalidate();
  }

  private void addTickerPanel(Map<String, Double> stockWeightage, JTextField tickerChosen,
                              JTextField weightage, JButton addBtn) {
    tickerChosen.setText("");
    weightage.setText("");

    goToPanel4();

    gbc4Newline();
    for (String ticker : stockWeightage.keySet()) {
      panel4.add(new JLabel(ticker), gbc4);
      gbc4.gridx = 1;
      panel4.add(new JLabel(stockWeightage.get(ticker).toString() + "%"), gbc4);
      gbc4Newline();
    }

    panel4.add(new JLabel("Add ticker: "), gbc4);
    gbc4.gridx = 1;
    panel4.add(tickerChosen, gbc4);

    gbc4Newline();
    panel4.add(new JLabel("Choose Weightage: (" + features.getWeightageLeft()
            + "% left)"), gbc4);
    gbc4.gridx = 1;
    weightage.addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent key) {
        weightage.setEditable((key.getKeyChar() >= '0' && key.getKeyChar() <= '9')
                || key.getKeyCode() == 8 || key.getKeyChar() == '.');
      }
    });
    panel4.add(weightage, gbc4);

    gbc4Newline();
    panel4.add(addBtn, gbc4);
    gbc4.gridx = 1;
    panel4.add(backToP3Btn, gbc4);

    panel4.revalidate();
  }



  private void displayBuyPanel() {
    goToPanel4();

    gbc4.gridwidth = 2;
    panel4.add(new JLabel("Fill details to buy stock in " + portfolioName + " portfolio"),
            gbc4);

    gbc4.gridwidth = 1;
    getTickerSymbol();
    getQuantity();
    getDateChoice();
    getCommissionFee();

    gbc4Newline();
    JButton addBtn = new JButton("Buy");
    panel4.add(addBtn, gbc4);
    gbc4.gridx = 1;
    panel4.add(backToP3Btn, gbc4);

    addBtn.addActionListener(e ->
            features.buyStock(
                    portfolioName,
                    ticker.getText(),
                    quantity.getText(),
                    dateTxtFiled.getText(),
                    commission.getText()
            )
    );

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
}
