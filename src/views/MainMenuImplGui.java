package views;

import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.*;

public class MainMenuImplGui extends JFrame implements MainMenu {
  private JButton flexibleButton;
  private JButton inflexibleButton;
  public MainMenuImplGui(String caption) {
    super(caption);
    setSize(500, 500);
    setLocation(200, 200);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setLayout(new FlowLayout());

    flexibleButton = new JButton("Flexible");
    flexibleButton.setActionCommand("1");
    this.add(flexibleButton);

    inflexibleButton = new JButton("Inflexible");
    flexibleButton.setActionCommand("2");
    this.add(inflexibleButton);

    pack();
    setVisible(true);
  }

  @Override
  public char getPortfolioType() {
    return '1';
  }
}
