package de.spl12.client.application;

import de.spl12.client.utils.MusicManager;
import de.spl12.client.utils.SoundManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * Controller for the start screen of the application.
 * <p>
 * Manages transitions to login and register screens and initializes background music.
 * </p>
 *
 * @author nmorali
 */

public class StartScreenController extends Controller {

  /**
   * Initializes the StartScreenController by setting up the sound and music managers. The method
   * retrieves instances of SoundManager and MusicManager, ensuring they are properly initialized.
   * Additionally, it starts playing the background music associated with the start screen.
   */
  @FXML
  public void initialize() {
    soundManager = SoundManager.getInstance();
    musicManager = MusicManager.getInstance();
    musicManager.startingMusic();
  }

  /**
   * Handles the login button action in the start screen. When triggered, it performs the following:
   * 1. Plays a click sound effect using the SoundManager. 2. Switches the current scene to the
   * login scene defined in "/FXML/login.fxml". 3. Retrieves the controller of the login scene and
   * assigns the current stage to it.
   *
   * @param event The action event triggered by the login button.
   */
  @FXML
  private void handleLogin(ActionEvent event) {
    soundManager.playClickSound();
    Controller sisc = changeScene("/FXML/login.fxml").getController();
    sisc.setStage(stage);
  }

  /**
   * Handles the register button action in the start screen. When triggered, this method performs
   * the following actions: 1. Plays a click sound effect using the SoundManager. 2. Switches the
   * current scene to the register scene specified in "/FXML/register.fxml". 3. Retrieves the
   * controller of the newly loaded register scene and assigns the current stage to it.
   *
   * @param event The action event triggered by the register button.
   */
  @FXML
  private void handleRegister(ActionEvent event) {
    soundManager.playClickSound();
    Controller rsc = changeScene("/FXML/register.fxml").getController();
    rsc.setStage(stage);
  }
}
