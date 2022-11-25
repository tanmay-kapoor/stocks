package views;

import java.awt.*;

import javax.swing.*;

import controllers.Features;

public class GuiImpl extends JFrame implements Gui {
  private JFrame mainFrame;

  private JButton exitButton;

  public GuiImpl(){

    mainFrame.setSize(400,400);

    CardLayout cl = new CardLayout();

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(cl);

    JPanel panel1 = new JPanel();
    JPanel panel2 = new JPanel();
    JPanel panel3 = new JPanel();

    exitButton = new JButton("Exit");
    panel1.add(exitButton);
    mainPanel.add(panel1, "1");
    mainPanel.add(panel2, "2");

    cl.show(mainPanel, "some21231");


    mainFrame.add(mainPanel);
    mainFrame.setVisible(true);
  }
}
