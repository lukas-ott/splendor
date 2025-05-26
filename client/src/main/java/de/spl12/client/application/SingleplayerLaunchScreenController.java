package de.spl12.client.application;

// test
import de.spl12.client.utils.SoundManager;
import de.spl12.client.utils.User;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Controller for the singleplayer game launch screen.
 * <p>
 * Displays player info, starts the game with a delay, and allows navigation
 * to settings and profile screens. Initializes UI elements and session.
 * </p>
 *
 * @author nmorali
 */


public class SingleplayerLaunchScreenController extends Controller {

  @FXML private Pane loadingPane;

  @FXML private Button nametag;

  @FXML private Text player1_name;

  @FXML private Pane start_game;

  PauseTransition pause = new PauseTransition(Duration.seconds(3));

  /** Initializes the SingleplayerLaunchScreenController. */
  @FXML
  public void initialize() {
    soundManager = SoundManager.getInstance();
    nametag.setText(" " + User.getInstance().getUserData().getUsername());
    ObservableList<String> levels =
        FXCollections.observableArrayList("Not Playing", "Easy", "Medium", "Hard");

    nametag.setText(" " + User.getInstance().getUserData().getUsername());
    player1_name.setText(User.getInstance().getUserData().getUsername());

    pause.setOnFinished(
        event -> {
          Controller gc = changeScene("/FXML/game.fxml").getController();
          gc.setStage(stage);
        });

    loadingPane.setVisible(false);
    // disable_start();
  }

  private void disable_start() {
    start_game.setOpacity(0.5);
    start_game.setDisable(true);
  }

  private void enable_start() {
    start_game.setOpacity(1);
    start_game.setDisable(false);
  }

  @FXML
  public void startGame(ActionEvent event) {
    soundManager.playStartSound();
    loadingPane.setVisible(true);
    pause.play();
  }

  @FXML
  public void back(ActionEvent actionEvent) {
    soundManager.playCancelSound();
    Controller smpsc = changeScene("/FXML/home.fxml").getController();
    smpsc.setStage(stage);
  }

  // Unused method
  @FXML
  private void settings(ActionEvent event) {
    soundManager.playClickSound();
    SettingsScreenController spc = changeScene("/FXML/settings.fxml").getController();
    spc.setStage(stage);
    spc.setPrevious("/FXML/singleplayer_launch.fxml");
  }

  /**
   * Navigates to the profile screen when triggered by the associated UI action.
   *
   * @param event
   */
  @FXML
  private void profile(ActionEvent event) {
    soundManager.playClickSound();
    ProfileScreenController spc = changeScene("/FXML/profile.fxml").getController();
    spc.setStage(stage);
    spc.setPrevious("/FXML/singleplayer_launch.fxml");
  }
}
