package de.spl12.client.application;

import de.spl12.client.utils.MusicManager;
import de.spl12.client.utils.SoundManager;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**Abstract base class for JavaFX controllers that provides a mechanism for scene switching.
 *@author nmorali*/
public abstract class Controller {
  protected Stage stage;
  protected SoundManager soundManager;
  protected MusicManager musicManager;

  public void setStage(Stage stage) {
    this.stage = stage;
  }

  /**
   * Changes the current scene of the application to the one specified by the provided FXML file.
   * This method loads the new FXML file, sets its root to the current scene, and returns the
   * FXMLLoader instance for additional configurations if needed.
   *
   * @param fxmlFile The path to the FXML file that defines the new scene.
   * @return The FXMLLoader instance used to load the new scene, or null if an IOException occurs.
   */
  protected FXMLLoader changeScene(String fxmlFile) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
      Parent newRoot = loader.load();
      Scene currentScene = stage.getScene();
      currentScene.setRoot(newRoot);
      return loader;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}
