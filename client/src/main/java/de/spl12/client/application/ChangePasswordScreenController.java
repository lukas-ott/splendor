package de.spl12.client.application;

import de.spl12.client.utils.ConstantsManager;
import de.spl12.client.utils.SoundManager;
import de.spl12.client.utils.User;
import de.spl12.client.utils.UserData;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;

/**
 * Controller class for the "Change Password" screen in the JavaFX application.
 * <p>
 * This class handles user interactions related to changing the password,
 * including validating user input, making an HTTP request to the backend API,
 * and navigating back to the profile screen upon success or failure.
 * </p>
 *
 * @author nmorali
 */

public class ChangePasswordScreenController extends Controller {
  private static final Logger LOGGER =
      Logger.getLogger(ChangePasswordScreenController.class.getName());

  private String previous;

  @FXML private PasswordField oldPasswordField;

  @FXML private PasswordField newPasswordField;

  @FXML private PasswordField confirmNewPasswordField;

  public void setPrevious(String previous) {
    this.previous = previous;
  }

  @FXML
  public void initialize() {
    soundManager = SoundManager.getInstance();
  }

  /**
   * Handles the back button action by playing a cancel sound, changing the scene to the profile
   * screen, and passing the previous scene information.
   *
   * @param event The ActionEvent that triggered the method call.
   */
  @FXML
  public void back(ActionEvent event) {
    soundManager.playCancelSound();
    ProfileScreenController psc = changeScene("/FXML/profile.fxml").getController();
    psc.setStage(stage);
    psc.setPrevious(previous);
  }

  /**
   * Handles the confirmation of a password change request. This method validates the new password
   * and confirmation password fields, initiates the password change API call, and handles the
   * response. If the process is successful, the scene is transitioned to the profile screen. Errors
   * during the process are logged and displayed to the user.
   *
   * @param event The ActionEvent that triggered the method call.
   */
  @FXML
  public void confirm(ActionEvent event) {
    if (!newPasswordField.getText().equals(confirmNewPasswordField.getText())) {
      showError("New Passwords do not match");
      return;
    }

    LOGGER.info("Password change initiated");
    soundManager.playClickSound();
    User user = User.getInstance();
    UserData userData = user.getUserData();
    String username = userData.getUsername();
    String oldPassword = oldPasswordField.getText();
    String newPassword = newPasswordField.getText();

    LOGGER.info("Attempting to change password for user: " + username);
    try {
      URL url = new URL("http://" + ConstantsManager.HOST + ":8081/api/user/change-password");
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("PUT");
      connection.setDoOutput(true);
      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

      String urlParameters =
          "username=" + username + "&password=" + oldPassword + "&newPassword=" + newPassword;
      try (OutputStream os = connection.getOutputStream()) {
        byte[] input = urlParameters.getBytes(StandardCharsets.UTF_8);
        os.write(input, 0, input.length);
      }

      int responseCode = connection.getResponseCode();
      LOGGER.info("Password change request response code: " + responseCode);

      if (responseCode == 200) {
        LOGGER.info("Password changed successfully");
        userData = new UserData(userData.getId(), username, newPassword, userData.getAge());
        user.setUserData(userData);
        ProfileScreenController psc = changeScene("/FXML/profile.fxml").getController();
        psc.setPrevious(previous);
        psc.setStage(stage);
      } else {
        String errorMessage = "";
        try (InputStream errorStream = connection.getErrorStream()) {
          if (errorStream != null) {
            try (Scanner scanner = new Scanner(errorStream, StandardCharsets.UTF_8.name())) {
              errorMessage = scanner.useDelimiter("\\A").next();
            } catch (Exception e) {
              LOGGER.warning("Failed to read error stream: " + e.getMessage());
            }
          }
        } catch (IOException e) {
          LOGGER.warning("Failed to read error stream: " + e.getMessage());
        }

        if (responseCode == 404) {
          LOGGER.severe("Endpoint not found (404): " + url.toString());
          showError("Server endpoint not found (404). Contact administrator.");
        } else {
          showError("Wrong password");
        }
      }

    } catch (IOException ioe) {
      LOGGER.severe("IO Exception during password change request: " + ioe.getMessage());
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
