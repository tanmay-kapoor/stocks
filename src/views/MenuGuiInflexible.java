package views;

import javax.swing.JPanel;

import controllers.Features;

/**
 * Class for user interaction through gui for inflexible portfolio.
 */
public class MenuGuiInflexible extends AbstractMenuGui {

  /**
   * Supple controller features and caption for initial JFrame.
   *
   * @param features Controller features.
   * @param caption  Caption for first JFrame.
   */
  public MenuGuiInflexible(Features features, String caption) {
    super(features, caption);
  }

  protected void displaySellPanel() {
    throw new IllegalArgumentException("Cannot sell from Inflexible Portfolio.");
  }

  @Override
  protected void getRestIfApplicable(JPanel panel2) {
    // implement later
  }
}
