package de.spl12.ai_client.application;

import de.spl12.ai_client.utils.AiUser;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

/**
 * Controller for the lobby screen in the AI client application.
 *
 * <p>This controller manages the lobby interface for an AI player, including displaying the user's
 * name and status, handling logout actions, responding to player exits, and syncing game state
 * information.
 *
 * <p>Utilizes {@link AiUser} for session data and {@link GameController} for game state management.
 *
 * @author lmelodia
 */
public class LobbyController extends Controller {

  private GameController gameController;

  @FXML private Label nametag;
  @FXML private Label status;

  /**
   * Initializes the lobby screen by setting the name label and status, and registering this
   * controller with the {@link GameController}.
   */
  @FXML
  public void initialize() {
    this.gameController = GameController.getInstance();
    this.gameController.setLobbyController(this);
    nametag.setText(AiUser.getInstance().getName());
    status.setText("In Lobby");
  }

  /**
   * Handles logout action when the logout button is clicked.
   *
   * <p>Clears the lobby code, leaves the current game session, and navigates back to the join
   * screen.
   *
   * @param event the action event triggered by clicking the logout button
   */
  @FXML
  public void handleLogout(ActionEvent event) {
    this.gameController.leaveGameSession();
    AiUser.getInstance().setLobbyCode("");
    Controller sisc = changeScene("/FXML/join.fxml").getController();
    sisc.setStage(stage);
  }

  /**
   * Updates the name and status labels based on the current game state.
   *
   * <p>This method is executed on the JavaFX Application Thread using {@code Platform.runLater}.
   */
  public void updateUI() {
    Platform.runLater(
        () -> {
          this.nametag.setText(AiUser.getInstance().getName());
          if (this.gameController.getGameState().isRunning()) {
            this.status.setText("Playing");
          } else {
            this.status.setText("In Lobby");
          }
        });
  }

  /**
   * Handles the situation when another player leaves the game.
   *
   * <p>Clears the lobby code, navigates back to the join screen, and logs the outcome. This method
   * is called when the host leaves or the session is otherwise interrupted. This method is also
   * executed on the JavaFX Application Thread.
   */
  public void handlePlayerExit() {
    Platform.runLater(
        () -> {
          AiUser.getInstance().setLobbyCode("");
          Controller sisc = changeScene("/FXML/join.fxml").getController();
          sisc.setStage(stage);
        });
  }

  /**
   * Displays an informational alert with a given message.
   *
   * @param message the message to show in the alert
   */
  private void showInfo(String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Information");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }
}
