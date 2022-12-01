package views;

import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JButton;

/**
 * Implementation class for Main Menu interface to interact with the user in GUI.
 */
public class MainMenuImplGui extends JFrame implements MainMenu {

  /**
   * Constructor that initializes the caption of the JFrame
   *
   * @param caption Caption to display on the JFrame.
   */
  public MainMenuImplGui(String caption) {
    super(caption);
    setSize(500, 500);
    setLocation(200, 200);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setLayout(new FlowLayout());

    JButton flexibleButton = new JButton("Flexible");
    flexibleButton.setActionCommand("1");
    this.add(flexibleButton);

    JButton inflexibleButton = new JButton("Inflexible");
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
