package de.spl12.client.application;

import java.awt.Image;
import java.awt.Taskbar;
import java.io.InputStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

public class StartScreen extends Application {

  /**
   * Starts the main application and initializes the graphical user interface (GUI). This method is
   * called when the JavaFX application is launched via App.java. Notes: - Currently, the
   * functionality is limited to displaying the GUI, but future extensions could include music or
   * other interactive features (to be implemented via an instance-based approach).
   *
   * @author nmorali
   * @param primaryStage The main application window (Stage) where the GUI is displayed.
   * @throws Exception If loading the FXML file or creating the scene fails.
   */
  @Override
  public void start(Stage primaryStage) throws Exception {

    // Set the taskbar icon (if supported by the operating system and the Java version 9+).
    if (Taskbar.isTaskbarSupported()) {
      Taskbar taskbar = Taskbar.getTaskbar();
      try (InputStream stream = getClass().getResourceAsStream("/images/Icon.png")) {
        if (stream != null) {
          Image awtImage = ImageIO.read(stream);
          taskbar.setIconImage(awtImage);
        }
      } catch (Exception e) {
        System.err.println("Taskbar icon could not be set: " + e.getMessage());
      }
    }

    // Initialize the GUI
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/start.fxml"));
    Parent root = loader.load();
    StartScreenController controller = loader.getController();
    controller.setStage(primaryStage);
    primaryStage.setTitle("Splendor");
    primaryStage.setResizable(false);
    primaryStage.setFullScreen(false);
    Scene scene = new Scene(root);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void launchGui(String[] args) {
    launch(args);
  }
}
