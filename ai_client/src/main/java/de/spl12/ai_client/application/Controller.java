package de.spl12.ai_client.application;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Abstract base class for JavaFX controllers that provides a mechanism for scene switching.
 *
 * <p>Subclasses can use {@link #changeScene(String)} to load and switch to a different FXML view,
 * and {@link #setStage(Stage)} to associate the JavaFX {@link Stage} with the controller.
 *
 * <p>This class assumes the {@link Scene} is already initialized on the provided stage.
 *
 * @author lmelodia
 */
public abstract class Controller {

  /** The primary stage used to display scenes. */
  protected Stage stage;

  /**
   * Sets the primary JavaFX stage for this controller.
   *
   * @param stage the JavaFX {@link Stage} to associate with this controller
   */
  public void setStage(Stage stage) {
    this.stage = stage;
  }

  /**
   * Changes the current scene to the one defined by the specified FXML file.
   *
   * <p>This method loads the given FXML layout and replaces the current scene's root node.
   *
   * @param fxmlFile the path to the FXML file (relative to the classpath)
   * @return the {@link FXMLLoader} used to load the FXML, or {@code null} if an error occurred
   */
  protected FXMLLoader changeScene(String fxmlFile) {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
      Parent newRoot = loader.load();
      Scene currentScene = stage.getScene();
      currentScene.setRoot(newRoot);
      return loader;
    } catch (IOException e) {
      System.err.println("Failed to load FXML: " + fxmlFile);
      e.printStackTrace();
      return null;
    }
  }
}
