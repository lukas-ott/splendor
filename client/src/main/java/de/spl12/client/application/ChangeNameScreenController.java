package de.spl12.client.application;

import de.spl12.client.utils.ConstantsManager;
import de.spl12.client.utils.SoundManager;
import de.spl12.client.utils.User;
import de.spl12.client.utils.UserData;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

/**
 * Controller class for managing the screen where users can change their username. This class
 * inherits from the base Controller and handles the necessary functionality to transition screens,
 * play sounds, and interact with a backend server.
 * @author nmorali
 */
public class ChangeNameScreenController extends Controller {
  private static final Logger LOGGER = Logger.getLogger(ChangeNameScreenController.class.getName());

  private String previous;

  @FXML private TextField newUsernameField;

  public void setPrevious(String previous) {
    this.previous = previous;
  }

  @FXML
  public void initialize() {
    soundManager = SoundManager.getInstance();
  }

  @FXML
  public void back(ActionEvent event) {
    soundManager.playCancelSound();
    ProfileScreenController psc = changeScene("/FXML/profile.fxml").getController();
    psc.setStage(stage);
    psc.setPrevious(previous);
  }

  /**
   * Handles the confirmation action for changing the username. This involves sending a PUT request
   * to the server with the old username, new username, and the user's password. Updates the user's
   * data upon success or displays error alerts for failures.
   *
   * @param event The event triggered by the confirm button.
   */
  @FXML
  public void confirm(ActionEvent event) {
    LOGGER.info("Username change initiated");
    soundManager.playClickSound();
    User user = User.getInstance();
    UserData userData = user.getUserData();
    String oldUsername = userData.getUsername();
    String newUsername = newUsernameField.getText();
    String password = userData.getPassword();

    LOGGER.info("Attempting to change username for user: " + oldUsername);

    try {
      URL url = new URL("http://" + ConstantsManager.HOST + ":8081/api/user/change-username");
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("PUT");
      connection.setDoOutput(true);
      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

      String urlParameters =
          "oldUsername=" + oldUsername + "&newUsername=" + newUsername + "&password=" + password;
      try (OutputStream os = connection.getOutputStream()) {
        byte[] input = urlParameters.getBytes(StandardCharsets.UTF_8);
        os.write(input, 0, input.length);
      }

      int responseCode = connection.getResponseCode();
      LOGGER.info("Username change request response code: " + responseCode);

      if (responseCode == 200) {
        LOGGER.info("Username changed successfully");
        userData = new UserData(userData.getId(), newUsername, password, userData.getAge());
        user.setUserData(userData);
        ProfileScreenController psc = changeScene("/FXML/profile.fxml").getController();
        psc.setPrevious(previous);
        psc.setStage(stage);
      } else {
        if (responseCode == 404) {
          LOGGER.severe("Endpoint not found (404): " + url);
          showError("Server endpoint not found (404). Contact administrator.");
        } else {
          LOGGER.severe("Username change failed with code: " + responseCode);
          showError("Username already taken.");
        }
      }
    } catch (IOException ioe) {
      LOGGER.severe("IO Exception during username change request: " + ioe.getMessage());
      showError("Connection error: " + ioe.getMessage());
    }
  }

  /**
   * Displays an error alert with the specified message.
   *
   * @param message
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
