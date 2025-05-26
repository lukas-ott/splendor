package de.spl12.ai_client.application;

import de.spl12.ai_client.utils.AiUser;
import de.spl12.domain.AiDifficulty;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

import java.util.logging.Logger;

/**
 * Controller for the join screen where AI users input a lobby code and join a game session.
 *
 * <p>This controller handles:
 *
 * <ul>
 *   <li>Connecting the AI user to a multiplayer game using a lobby code
 *   <li>Registering the AI player with the backend
 *   <li>Transitioning to the lobby screen upon success
 *   <li>Handling error scenarios when joining fails
 * </ul>
 *
 * Uses {@link AiUser} to store session-related state and difficulty, and {@link GameController} for
 * game session coordination.
 *
 *  @author jwaltea
 */
public class JoinController extends Controller {

  private static final Logger logger = Logger.getLogger(JoinController.class.getName());

  @FXML private TextField lobbycode;
  @FXML private Slider difficultySlider;

  private GameController gameController;

  /** Initializes the join screen and difficulty slider. */
  @FXML
  public void initialize() {
    difficultySlider.setMin(0);
    difficultySlider.setMax(2);
    difficultySlider.setMajorTickUnit(1);
    difficultySlider.setMinorTickCount(0);
    difficultySlider.setSnapToTicks(true);
    difficultySlider.setShowTickMarks(true);

    this.gameController = GameController.getInstance();
    this.gameController.setJoinController(this);
  }

  /**
   * Handles the "Join Lobby" button action.
   *
   * @param actionEvent the button click event
   */
  @FXML
  public void joinLobby(ActionEvent actionEvent) {
    try {
      Integer.parseInt(this.lobbycode.getText());
    } catch (NumberFormatException e) {
      logger.warning("Lobby Code must be numeric");
      return;
    }

    AiUser.getInstance().setLobbyCode(this.lobbycode.getText());
    AiUser.getInstance().setDifficulty(getDifficultyFromSlider());

    GameController.getInstance().joinGameSession(Integer.parseInt(this.lobbycode.getText()));
  }

  /** Handles a successful lobby join. */
  public void handleJoinSuccessful() {
    Platform.runLater(
        () -> {
          Controller sisc = changeScene("/FXML/lobby.fxml").getController();
          sisc.setStage(stage);
          sisc.stage.setOnCloseRequest(event -> GameController.getInstance().leaveGameSession());
        });
  }

  /**
   * Handles a failed attempt to join a game session.
   *
   * @param errorMsg the reason the join attempt failed
   */
  public void handleJoinUnsuccessful(String errorMsg) {
    Platform.runLater(() -> showError(errorMsg));
  }

  /**
   * Displays an error alert dialog with a given message.
   *
   * @param message the error message to be shown
   */
  private void showError(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  /**
   * Determines the AI difficulty setting from the current slider value.
   *
   * @return the selected {@link AiDifficulty} enum
   */
  private AiDifficulty getDifficultyFromSlider() {
    double sliderValue = difficultySlider.getValue();
    if (sliderValue == 0) {
      return AiDifficulty.EASY;
    } else if (sliderValue == 1) {
      return AiDifficulty.MEDIUM;
    } else {
      return AiDifficulty.HARD;
    }
  }
}
