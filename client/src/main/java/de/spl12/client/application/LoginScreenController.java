package de.spl12.client.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.spl12.client.utils.ConstantsManager;
import de.spl12.client.utils.SoundManager;
import de.spl12.client.utils.User;
import de.spl12.client.utils.UserData;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

/**
 * Controller for the login screen of the application.
 * <p>
 * Handles user login via HTTP, navigation to registration and start screens,
 * and initializes key UI interactions like Enter-key handling.
 * </p>
 *
 * @author nmorali
 */


public class LoginScreenController extends Controller {
  private final ObjectMapper objectMapper = new ObjectMapper();
  private static final Logger LOGGER = Logger.getLogger(LoginScreenController.class.getName());

  @FXML private TextField usernameField;

  @FXML private TextField passwordField;

  @FXML private AnchorPane rootPane;

  /**
   * Initializes the LoginScreenController by setting up necessary event handlers and services. This
   * method is automatically invoked upon the loading of the associated FXML.
   *
   * <ul>
   *   - Initializes the SoundManager instance to handle audio-related functionality. - Adds a key
   *   event handler to the root pane that triggers the confirm() method when the Enter key is
   *   pressed.
   * </ul>
   */
  @FXML
  public void initialize() {
    soundManager = SoundManager.getInstance();
    rootPane.setOnKeyPressed(
        event -> {
          if (event.getCode() == KeyCode.ENTER) {
            confirm();
          }
        });
  }

  /**
   * Handles the action triggered when the "back" button is clicked in the Login screen. This method
   * performs the following actions: 1. Plays a cancellation sound using the SoundManager. 2.
   * Switches the current scene to the "start" screen. 3. Sets the current stage to the controller
   * associated with the "start" screen.
   *
   * @param event The ActionEvent triggered by the user's interaction with the "back" button.
   */
  @FXML
  private void back(ActionEvent event) {
    soundManager.playCancelSound();
    Controller ssc = changeScene("/FXML/start.fxml").getController();
    ssc.setStage(stage);
  }

  /**
   * Handles the action triggered when the "register" button is clicked in the Login screen.
   *
   * @param event
   */
  @FXML
  private void handleRegister(ActionEvent event) {
    soundManager.playClickSound();
    Controller rsc = changeScene("/FXML/register.fxml").getController();
    rsc.setStage(stage);
  }

  /** Handles the action triggered when the "confirm" button is clicked in the Login screen. */
  @FXML
  private void confirm() {
    soundManager.playLoginSound();
    String username = usernameField.getText();
    String password = passwordField.getText();

    LOGGER.info("Attempting to log in user: " + username);
    try {
      URL url = new URL("http://" + ConstantsManager.HOST + ":8081/api/user/login");
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      conn.setDoOutput(true);

      String requestBody = "username=" + username + "&password=" + password;
      try (OutputStream os = conn.getOutputStream()) {
        os.write(requestBody.getBytes(StandardCharsets.UTF_8));
      }

      int responseCode = conn.getResponseCode();
      LOGGER.info("Login request response code: " + responseCode);

      if (responseCode == 200) {
        UserData userData = objectMapper.readValue(conn.getInputStream(), UserData.class);
        userData =
            new UserData(userData.getId(), userData.getUsername(), password, userData.getAge());
        User user = User.getInstance();
        user.setUserData(userData);

        LOGGER.info("Login successful for user: " + username);
        HomeScreenController smsc = changeScene("/FXML/home.fxml").getController();
        smsc.setStage(stage);
      } else {
        LOGGER.severe("Login failed with code: " + responseCode);
        showError("Login failed! Please check your credentials.");
      }
    } catch (Exception e) {
      LOGGER.severe("Exception during login request: " + e.getMessage());
      showError("Connection error: " + e.getMessage());
    }
  }

  /**
   * Displays an error message in an alert dialog box.
   *
   * @param message The error message to be displayed in the alert dialog.
   */
  @FXML
  private void showError(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }
}
