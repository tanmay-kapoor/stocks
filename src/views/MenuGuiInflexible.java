package views;

import javax.swing.*;

import controllers.Features;

public class MenuGuiInflexible extends AbstractMenuGui {

  public MenuGuiInflexible(Features features, String caption) {
    super(features, caption);
  }

  protected void displaySellPanel() {
    throw new IllegalArgumentException("Cannot sell from Inflexible Portfolio.");
  }

  @Override
  protected void getRestIfApplicable(JPanel panel2) {

  }

}
