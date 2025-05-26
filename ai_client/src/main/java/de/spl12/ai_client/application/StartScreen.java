package de.spl12.ai_client.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.Image;
import java.awt.Taskbar;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * The main JavaFX entry point for the application.
 *
 * <p>Responsibilities include:
 * <ul>
 *   <li>Loading the FXML layout</li>
 *   <li>Setting the application stage and scene</li>
 *   <li>Configuring window properties and taskbar icon (if supported)</li>
 * </ul>
 *
 * Future extensions may include background music or interactive effects.
 */
public class StartScreen extends Application {

    /**
     * Starts the main application and initializes the graphical user interface (GUI).
     *
     * @param primaryStage the primary window for this application
     * @throws Exception if the FXML file cannot be loaded or the scene cannot be created
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Set the taskbar icon (Java 9+ and OS support required)
        if (Taskbar.isTaskbarSupported()) {
            Taskbar taskbar = Taskbar.getTaskbar();
            try (InputStream stream = getClass().getResourceAsStream("/images/Icon.png")) {
                if (stream != null) {
                    Image awtImage = ImageIO.read(stream);
                    taskbar.setIconImage(awtImage);
                }
            } catch (Exception e) {
                System.err.println(
                        "Taskleisten-Icon konnte nicht gesetzt werden: " + e.getMessage());
            }
        }

        // Initialize and display the join screen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/join.fxml"));
        Parent root = loader.load();
        JoinController controller = loader.getController();
        controller.setStage(primaryStage);

        primaryStage.setTitle("Splendor");
        primaryStage.setResizable(false);
        primaryStage.setFullScreen(false);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args command-line arguments (not used)
     */
    public static void launchGui(String[] args) {
        launch(args);
    }
}
