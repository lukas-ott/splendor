package de.spl12.client.application;

import de.spl12.client.utils.MusicManager;
import de.spl12.client.utils.SoundManager;
import de.spl12.client.utils.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

/**
 * Controller for the home screen of the JavaFX application.
 * <p>
 * Manages navigation to settings, profile, singleplayer, and multiplayer screens,
 * initializes background music and user UI elements, and handles multiplayer lobby join events.
 * </p>
 *
 * <p>Integrates with {@link SoundManager}, {@link MusicManager}, and {@link GameController}.</p>
 * @author nmorali
 */


public class HomeScreenController extends Controller {

  @FXML private Button nametag;

  @FXML
  private void initialize() {
    soundManager = SoundManager.getInstance();
    musicManager = MusicManager.getInstance();
    musicManager.lobbyMusic();
    nametag.setText(" " + User.getInstance().getUserData().getUsername());
    GameController.getInstance().setHomeScreenController(this);
  }

  /**
   * Handles navigation to the settings screen. This method plays a cancellation sound, switches the
   * current scene to the settings screen, and initializes the SettingsScreenController with the
   * current stage and the previous screen path.
   *
   * @param event The action event triggered by the user interaction (e.g., clicking a button).
   */
  @FXML
  private void settings(ActionEvent event) {
    soundManager.playCancelSound();
    SettingsScreenController ssc = changeScene("/FXML/settings.fxml").getController();
    ssc.setStage(stage);
    ssc.setPrevious("/FXML/home.fxml");
  }

  /**
   * Handles the action for navigating to the profile screen. This method plays a click sound,
   * changes the current scene to the profile interface, and initializes the ProfileScreenController
   * with the current stage and the previous screen path.
   *
   * @param event The action event triggered by the user interaction (e.g., clicking a button).
   */
  @FXML
  private void profile(ActionEvent event) {
    soundManager.playClickSound();
    ProfileScreenController spc = changeScene("/FXML/profile.fxml").getController();
    spc.setStage(stage);
    spc.setPrevious("/FXML/home.fxml");
  }

  /**
   * Navigates the user to the single-player lobby screen. This method changes the current scene to
   * the single-player lobby interface, updates the relevant controller, and initializes a new
   * single-player game session.
   *
   * @param event The action event triggered by the user interaction (e.g., clicking a button).
   */
  @FXML
  private void goToSingleplayerLobby(ActionEvent event) {
    soundManager.playClickSound();
    GameController gameController = GameController.getInstance();
    gameController.createGameSession(false);
  }

  /**
   * Navigates the user to the multiplayer selection screen. This method plays a click sound,
   * triggers a scene change to the multiplayer selection interface, and updates the stage in the
   * relevant controller.
   *
   * @param event The action event triggered by the user interaction (e.g., clicking a button).
   */
  @FXML
  private void goToMultiplayerSelect(ActionEvent event) {
    soundManager.playClickSound();
    Controller mpssc = changeScene("/FXML/multiplayer_launch.fxml").getController();
    mpssc.setStage(stage);
  }

  /**
   * Handles a successful join to a multiplayer lobby.
   *
   * <p>Updates the UI to transition to the multiplayer lobby screen. Sets the stage, identifies
   * whether the local player is the host, and updates the session ID. Also sends a chat message
   * indicating the player joined.
   *
   * <p>This code is executed on the JavaFX Application Thread.
   */
  public void handleJoinSuccessful() {
    Platform.runLater(
        () -> {
          MultiplayerLobbyScreenController mplsc =
              changeScene("/FXML/multiplayer_lobby.fxml").getController();
          mplsc.setStage(stage);
          mplsc.stage.setOnCloseRequest(
              event -> {
                GameController.getInstance().leaveGameSession();
              });
          mplsc.setHost(GameController.getInstance().getPlayer().getSessionPlayerNumber() == 0);
          mplsc.setSessionId(GameController.getInstance().getSessionId());
          mplsc.setServerIP(GameController.getInstance().getServerIP());
          GameController.getInstance().sendChatMessage("joined");
        });
  }

  /**
   * Handles an unsuccessful attempt to join a game lobby.
   *
   * <p>Displays an error dialog with the provided error message.
   *
   * <p>This code is executed on the JavaFX Application Thread.
   *
   * @param errorMsg the error message describing why the join attempt failed
   */
  public void handleJoinUnsuccessful(String errorMsg) {
    Platform.runLater(
        () -> {
          showError(errorMsg);
        });
  }

  /**
   * Displays an error alert dialog with the specified message.
   *
   * @param message the message to display in the error dialog
   */
  private void showError(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }
}
