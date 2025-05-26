package de.spl12.client.application;

// test
import de.spl12.client.utils.SoundManager;
import de.spl12.client.utils.User;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * Controller for the multiplayer launch screen.
 * <p>
 * Manages UI for joining or hosting a multiplayer game, session ID input,
 * navigation to profile and other screens, and interaction with the GameController.
 * </p>
 *
 * @author nmorali
 */


public class MultiplayerLaunchScreenController extends Controller {

  private static final Logger LOGGER =
      Logger.getLogger(MultiplayerLaunchScreenController.class.getName());
  @FXML public TextField sessionIdInput;
  @FXML private Button nametag;

  /**
   * Initializes the MultiplayerLaunchScreenController.
   *
   * <p>This method sets up the necessary components for the multiplayer launch screen, including
   * initializing the nametag with the current user's username, retrieving the instance of the sound
   * manager, and assigning this controller to the GameController's multiplayer launch screen
   * controller.
   *
   * <p>The method performs the following: 1. Updates the nametag label with the user's username
   * obtained from the singleton User instance. 2. Initializes the sound manager by obtaining its
   * singleton instance. 3. Links this controller instance to the GameController's multiplayer
   * launch screen controller.
   */
  @FXML
  public void initialize() {
    nametag.setText(" " + User.getInstance().getUserData().getUsername());
    soundManager = SoundManager.getInstance();
    GameController.getInstance().setMultiplayerLaunchScreenController(this);
  }

  /**
   * Navigates to the profile screen when triggered by the associated UI action. Plays a click
   * sound, switches the current scene to the profile screen, and initializes the
   * ProfileScreenController with the current stage and the path to the previous screen.
   *
   * @param event the action event triggered by the UI component, such as a button click
   */
  @FXML
  private void profile(ActionEvent event) {
    soundManager.playClickSound();
    ProfileScreenController spc = changeScene("/FXML/profile.fxml").getController();
    spc.setStage(stage);
    spc.setPrevious("/FXML/multiplayer_launch.fxml");
  }

  /**
   * Navigates to the home screen when triggered by the associated UI action.
   *
   * @param event
   */
  @FXML
  private void back(ActionEvent event) {
    soundManager.playClickSound();
    Controller spc = changeScene("/FXML/home.fxml").getController();
    spc.setStage(stage);
  }

  /**
   * Navigates to the singleplayer lobby screen when triggered by the associated UI action. Plays a
   * click sound, changes the current scene to the singleplayer lobby screen, and initializes the
   * corresponding controller with the current stage. Additionally, creates a new game session in
   * singleplayer mode.
   *
   * @param event the action event triggered by the UI component, such as a button click
   */
  @FXML
  private void goToSingleplayerLobby(ActionEvent event) {
    soundManager.playClickSound();
    GameController gameController = GameController.getInstance();
    gameController.createGameSession(false);
  }

  /**
   * Handles the action when the user clicks to join a lobby.
   *
   * <p>Plays a click sound and sends a join request using the session ID entered in the input
   * field.
   *
   * @param actionEvent the event triggered by the button click
   */
  public void joinLobby(ActionEvent actionEvent) {
    soundManager.playClickSound();
    try {
      Integer.parseInt(this.sessionIdInput.getText());
    } catch (NumberFormatException e) {
      LOGGER.warning("Lobby Code must be numeric");
      return;
    }
    GameController.getInstance().joinGameSession(Integer.parseInt(this.sessionIdInput.getText()));
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

  /**
   * Handles the action when the user clicks to host a lobby.
   *
   * <p>Plays a click sound and initiates the creation of a new game session.
   *
   * @param actionEvent the event triggered by the button click
   */
  public void hostLobby(ActionEvent actionEvent) {
    soundManager.playClickSound();
    GameController gameController = GameController.getInstance();
    gameController.createGameSession(true);
  }

  @FXML
  public void settings() {
    soundManager.playClickSound();
    SettingsScreenController spc = changeScene("/FXML/settings.fxml").getController();
    spc.setStage(stage);
    spc.setPrevious("/FXML/multiplayer_launch.fxml");
  }
}
