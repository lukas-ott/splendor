package de.spl12.client.application;

// test
import de.spl12.client.utils.MusicManager;
import de.spl12.client.utils.SoundManager;
import de.spl12.client.utils.User;
import de.spl12.domain.AbstractPlayer;
import de.spl12.domain.ChatMessage;
import java.util.List;
import java.util.logging.Logger;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Controller for the multiplayer lobby screen.
 * <p>
 * Manages player list, chat interface, leaderboard, sound/music settings,
 * and coordination of lobby actions such as starting games and reacting to host status.
 * </p>
 *
 * @author nmorali
 */


public class MultiplayerLobbyScreenController extends Controller {

  private static final Logger LOGGER =
      Logger.getLogger(MultiplayerLobbyScreenController.class.getName());

  @FXML Pane player1;
  @FXML Text player1_name;
  @FXML Pane player2;
  @FXML Text player2_name;
  @FXML Pane player3;
  @FXML Text player3_name;
  @FXML Pane player4;
  @FXML Text player4_name;

  @FXML Pane player1_leaderboard;
  @FXML Text player1_leaderboard_name;
  @FXML Pane player2_leaderboard;
  @FXML Text player2_leaderboard_name;
  @FXML Pane player3_leaderboard;
  @FXML Text player3_leaderboard_name;
  @FXML Pane player4_leaderboard;
  @FXML Text player4_leaderboard_name;

  @FXML Pane start_game;
  @FXML Text wait_for_host;

  @FXML ImageView open_chat;
  @FXML ImageView open_leaderboard;

  @FXML private AnchorPane rootPane;

  @FXML Pane leaderboard;
  @FXML Pane settings;
  @FXML Pane chat;
  @FXML TextField messageField;
  @FXML ScrollPane chatHistory;
  @FXML VBox chat_container;

  @FXML Text game_code;
  @FXML Text server_ip;

  @FXML Pane loadingPane;

  @FXML private Slider soundSlider;
  @FXML private Slider musicSlider;
  @FXML private CheckBox soundMuteBox;
  @FXML private CheckBox musicMuteBox;
  PauseTransition pause = new PauseTransition(Duration.seconds(2));

  private GameController gameController;
  private boolean isHost;

  /**
   * Initializes the multiplayer lobby screen by setting up controllers, configuring UI elements,
   * and defining event handlers.
   *
   * <p>The method connects the controller to the {@link GameController} and configures settings for
   * sound and music, including sliders for volume and mute toggles. It also sets up bidirectional
   * bindings for player names in the leaderboard and chat views.
   *
   * <p>Various event handlers are registered to handle key presses, such as toggling the chat,
   * leaderboard, and sending messages. Listeners on sliders and toggle boxes manage the behavior of
   * sound and music settings dynamically. The UI configurations for chat history and leaderboard
   * are also finalized in this method.
   *
   * <p>This method is invoked automatically by the FXMLLoader when the associated FXML file is
   * loaded.
   */
  @FXML
  public void initialize() {
    this.gameController = GameController.getInstance();
    this.gameController.setMultiplayerLobbyScreenController(this);
    this.isHost = false;
    soundManager = SoundManager.getInstance();
    musicManager = MusicManager.getInstance();
    soundSlider.setValue(soundManager.getVolume() * 100);
    soundMuteBox.setSelected(soundManager.isMuted());

    musicSlider.setValue(musicManager.getVolume() * 100);
    musicMuteBox.setSelected(musicManager.isMuted());

    // === EventHandler verbinden ===
    soundSlider
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              soundManager.setVolume(newVal.doubleValue() / 100);
            });

    musicSlider
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              musicManager.setVolume(newVal.doubleValue() / 100);
            });

    soundMuteBox
        .selectedProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              soundManager.setMuted(newVal);
              soundSlider.setDisable(newVal);
            });

    musicMuteBox
        .selectedProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              musicManager.setMuted(newVal);
              musicSlider.setDisable(newVal);
            });
    if (soundMuteBox.isSelected()) {
      soundSlider.setDisable(true);
    }
    if (musicMuteBox.isSelected()) {
      musicSlider.setDisable(true);
    }
    game_code.setText("N/A");
    server_ip.setText("N/A");
    settings.setVisible(false);
    chat.setVisible(false);
    disable_start();
    loadingPane.setVisible(false);
    rootPane.setOnKeyPressed(
        event -> {
          if (event.getCode() == KeyCode.T) {
            openChat();
          }
          if (event.getCode() == KeyCode.L) {
            if (!leaderboard.isVisible()) {
              openLeaderboard();
            } else {
              closeLeaderboard();
            }
          }
        });
    chat.setOnKeyPressed(
        event -> {
          if (event.getCode() == KeyCode.ESCAPE) {
            closeChat();
          }
        });
    messageField.setOnKeyPressed(
        event -> {
          if (event.getCode() == KeyCode.ENTER) {
            sendMessage();
          }
        });
    chatHistory.setStyle(
        "-fx-background-color: transparent;" + "-fx-background-insets: 0;" + "-fx-padding: 0;");
    Platform.runLater(
        () -> {
          Node viewport = chatHistory.lookup(".viewport");
          if (viewport != null) {
            viewport.setStyle("-fx-background-color: transparent;");
          }
        });
    chatHistory.setFitToWidth(true);
    chatHistory.setFitToHeight(false);
    player1_leaderboard_name.textProperty().bindBidirectional(player1_name.textProperty());
    player2_leaderboard_name.textProperty().bindBidirectional(player2_name.textProperty());
    player3_leaderboard_name.textProperty().bindBidirectional(player3_name.textProperty());
    player4_leaderboard_name.textProperty().bindBidirectional(player4_name.textProperty());
    pause.setOnFinished(
        event -> {
          Controller gc = changeScene("/FXML/game.fxml").getController();
          gc.setStage(stage);
          gc.stage.setOnCloseRequest(
              closeEvent -> {
                GameController.getInstance().leaveGameSession();
              });
        });
  }

  /**
   * Updates the UI elements of the multiplayer lobby screen to reflect the current game state.
   *
   * <p>This method synchronizes the UI with data retrieved from the {@link GameController},
   * including: - Updating the session ID and server IP displayed on the screen. - Setting the names
   * and visibility of up to four players in the lobby. - Adjusting the opacity of player slots
   * based on the number of players in the lobby. - Enabling or disabling the start button based on
   * the host's status and player count. - Loading chat messages into the chat pane.
   *
   * <p>If the player is the host and the required number of players are present, the start button
   * is enabled; otherwise, it is disabled. Additionally, the method logs a message indicating that
   * the UI update has been completed.
   */
  public void updateUI() {
    this.game_code.setText(Integer.toString(this.gameController.getSessionId()));
    this.server_ip.setText(this.gameController.getServerIP());
    List<AbstractPlayer> players = this.gameController.getGameState().getPlayers();
    if (!players.isEmpty()) {
      this.player1_name.setText(players.getFirst().getName());
      this.player1.setOpacity(1);
    } else {
      this.player1.setOpacity(0.5);
    }
    if (players.size() >= 2) {
      this.player2_name.setText(players.get(1).getName());
      this.player2.setOpacity(1);
      ((SepiaTone) player2.getEffect()).setLevel(0);
    } else {
      this.player2_name.setText("");
      this.player2.setOpacity(0.5);
      ((SepiaTone) player2.getEffect()).setLevel(0.6);
    }
    if (players.size() >= 3) {
      this.player3_name.setText(players.get(2).getName());
      this.player3.setOpacity(1);
      ((SepiaTone) player3.getEffect()).setLevel(0);
    } else {
      this.player3_name.setText("");
      this.player3.setOpacity(0.5);
      ((SepiaTone) player3.getEffect()).setLevel(0.6);
    }
    if (players.size() >= 4) {
      this.player4_name.setText(players.get(3).getName());
      this.player4.setOpacity(1);
      ((SepiaTone) player4.getEffect()).setLevel(0);
      if (this.isHost) {
        this.enable_start();
      }
    } else {
      this.player4_name.setText("");
      this.player4.setOpacity(0.5);
      ((SepiaTone) player4.getEffect()).setLevel(0.6);
      if (this.isHost) {
        this.disable_start();
      }
    }
    this.loadChat();
    LOGGER.info("Updating UI");
  }

  /**
   * Loads and updates the chat messages in the UI container.
   *
   * <p>This method is executed on the JavaFX Application Thread using {@code Platform.runLater} to
   * ensure thread safety. It first removes existing text nodes from the chat container, then
   * iterates through the list of chat messages retrieved from the {@code GameController}'s game
   * state. Each message is formatted and styled before being added to the chat container.
   *
   * <p>The method also ensures that the vertical scroll position of the chat history is set to the
   * bottom, enabling users to view the latest messages.
   */
  private void loadChat() {
    Platform.runLater(
        () -> {
          chat_container.getChildren().removeIf(node -> node instanceof Text);
          for (ChatMessage chatMessage :
              this.gameController.getGameState().getChat().getMessageList()) {
            Text text = new Text("[" + chatMessage.getSender() + "] " + chatMessage.getMessage());
            text.setStyle(
                "-fx-font-family: 'Songti SC'; -fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: #432700;");
            chat_container.getChildren().add(text);
          }
          chatHistory.setVvalue(1.0);
        });
  }

  public void setHost(boolean isHost) {
    this.isHost = isHost;
    if (isHost) {
      player1_name.setText(User.getInstance().getUserData().getUsername());
      wait_for_host.setVisible(false);
      start_game.setVisible(true);
    } else {
      player1_name.setText("Host");
      player2_name.setText(User.getInstance().getUserData().getUsername());
      player2.setOpacity(1);
      ((SepiaTone) player2.getEffect()).setLevel(0);
    }
  }

  // Method to DISCONNECT from the lobby
  @FXML
  public void exit_lobby() {
    this.gameController.leaveGameSession();
    soundManager.playCancelSound();
    MultiplayerLaunchScreenController mls =
        changeScene("/FXML/multiplayer_launch.fxml").getController();
    mls.setStage(stage);
  }

  /**
   * Handles the situation when the host leaves the lobby.
   *
   * <p>Plays a cancel sound and navigates the user back to the multiplayer launch screen. If the
   * current player is not the host, an informational dialog is shown indicating the session was
   * closed.
   *
   * <p>This code is executed on the JavaFX Application Thread.
   */
  public void handleHostExit() {
    Platform.runLater(
        () -> {
          if (GameController.getInstance().getPlayer().getSessionPlayerNumber() != 0) {
            MultiplayerLaunchScreenController mls =
                changeScene("/FXML/multiplayer_launch.fxml").getController();
            mls.setStage(stage);
            soundManager.playCancelSound();
            showInfo("Host left -> game session is closed");
          }
        });
  }

  /**
   * Displays an informational alert dialog with the specified message.
   *
   * @param message the message to display in the information dialog
   */
  private void showInfo(String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Information");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  @FXML
  public void settings() {
    settings.setVisible(true);
  }

  @FXML
  public void back() {
    settings.setVisible(false);
  }

  @FXML
  public void openChat() {
    open_chat.setVisible(false);
    chat.setVisible(true);
    messageField.clear();
    soundManager.playChatOpen();
    Platform.runLater(() -> messageField.requestFocus());
  }

  @FXML
  public void sendMessage() {
    String message = messageField.getText();
    this.gameController.sendChatMessage(message);
    messageField.clear();
  }

  @FXML
  public void closeChat() {
    chat.setVisible(false);
    soundManager.playChatClose();
    open_chat.setVisible(true);
  }

  @FXML
  public void openLeaderboard() {
    soundManager.playChatOpen();
    leaderboard.setVisible(true);
    open_leaderboard.setVisible(false);
  }

  @FXML
  public void closeLeaderboard() {
    soundManager.playChatClose();
    leaderboard.setVisible(false);
    open_leaderboard.setVisible(true);
  }

  @FXML
  public void settings(Event event) {
    soundManager.playClickSound();
    settings.setVisible(true);
  }

  @FXML
  public void back(Event event) {
    soundManager.playCancelSound();
    settings.setVisible(false);
  }

  private void disable_start() {
    start_game.setOpacity(0.5);
    start_game.setDisable(true);
  }

  private void enable_start() {
    this.start_game.setOpacity(1);
    this.start_game.setDisable(false);
  }

  @FXML
  public void startGame() {
    this.gameController.startGame();
    this.gameStarted();
  }

  public void gameStarted() {
    this.soundManager.playStartSound();
    this.loadingPane.setVisible(true);
    this.pause.play();
  }

  public void setSessionId(int sessionId) {
    this.game_code.setText(sessionId + "");
  }

  public void setServerIP(String serverIP) {
    this.server_ip.setText(serverIP);
  }
}
