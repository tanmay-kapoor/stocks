package controllers;

/**
 * A controller that starts to program to work with a specific type of controller
 * as requested by the user. For now, it will choose to call the start method for Flexible or
 * Inflexible portfolio.
 */
public interface SpecificController {
  void start();
}
