package de.spl12.client.application;

import de.spl12.client.utils.MusicManager;
import de.spl12.client.utils.SoundManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;

/**
 * Controller for the settings screen.
 * <p>
 * Manages volume and mute settings for sound and music, and allows navigation
 * to the profile or previous screen. Uses sliders and checkboxes to control managers.
 * </p>
 *
 * @author nmorali
 */

public class SettingsScreenController extends Controller {

  @FXML private Slider soundSlider;
  @FXML private Slider musicSlider;
  @FXML private CheckBox soundMuteBox;
  @FXML private CheckBox musicMuteBox;

  private String previous;

  public void setPrevious(String s) {
    previous = s;
  }

  /**
   * Initializes the settings screen by configuring the volume sliders and mute checkboxes for both
   * sound and music. The method retrieves instances of `SoundManager` and `MusicManager` and
   * synchronizes their current states with the UI components. It also adds listeners to handle
   * changes in slider values and mute states, updating the managers and enabling or disabling the
   * sliders accordingly.
   */
  @FXML
  public void initialize() {
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
  }

  /**
   * Handles the action of navigating back to the previous scene. This method plays a cancel sound,
   * switches to the previous scene using the path stored in the `previous` field, and sets the
   * stage of the new scene's controller to the current stage.
   *
   * @param event The ActionEvent triggered by the user's interaction with the back control (e.g.,
   *     button click).
   */
  @FXML
  public void back(ActionEvent event) {
    soundManager.playCancelSound();
    Controller smsc = changeScene(previous).getController();
    smsc.setStage(stage);
  }

  /**
   * Handles the action of navigating to the profile screen.
   *
   * @param event
   */
  @FXML
  public void profile(ActionEvent event) {
    soundManager.playClickSound();
    ProfileScreenController spc = changeScene("/FXML/profile.fxml").getController();
    spc.setStage(stage);
    spc.setPrevious(previous);
  }

  /**
   * Handles the action of logging out of the application.
   *
   * @param event
   */
  @FXML
  public void logout(ActionEvent event) {
    soundManager.playClickSound();
    Controller ssc = changeScene("/FXML/start.fxml").getController();
    ssc.setStage(stage);
  }
}
