package controllers;

import java.io.IOException;

/**
 * An interface that state all the methods that are supposed to be implemented by all
 * controllers of the program.
 * A <code>Controller</code> object is meant to be used to produce a view to the client,
 * take requests from them, perform data manipulations/cleanup on the inputs and delegate
 * necessary functionality to the model of the program.
 */
public interface Controller {
  /**
   * A method that starts the program. It produces all the necessary views that are
   * meant for the client and accepts necessary inputs for further operations.
   *
   * @throws IOException when there is a problem with the <code>Appendable</code>  object's
   *                     append method.
   */
  void go() throws IOException;
}
