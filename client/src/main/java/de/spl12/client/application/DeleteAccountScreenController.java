package de.spl12.client.application;

import de.spl12.client.utils.ConstantsManager;
import de.spl12.client.utils.SoundManager;
import de.spl12.client.utils.User;
import de.spl12.client.utils.UserData;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

/**
 * Controller for the "Delete Account" screen in the JavaFX application.
 * <p>
 * Handles user input for account deletion, validates the password, sends a DELETE request
 * to the server, and manages navigation based on the operation's success or failure.
 * </p>
 *
 * <p>Uses {@link SoundManager}, {@link User}, and {@link ConstantsManager} for auxiliary tasks.</p>
 * @author nmorali
 */
public class DeleteAccountScreenController extends Controller {
  private static final Logger LOGGER =
      Logger.getLogger(DeleteAccountScreenController.class.getName());

  private String previous;
  @FXML private TextField passwordField;

  public void setPrevious(String previous) {
    this.previous = previous;
  }

  @FXML
  public void initialize() {
    soundManager = SoundManager.getInstance();
  }

  /**
   * Handles the action triggered when the "Back" button is pressed. Plays a cancel sound, switches
   * the scene to the profile screen (profile.fxml), and passes the current stage and previous
   * screen reference to the new controller.
   *
   * @param event The ActionEvent triggered by the back button press.
   */
  @FXML
  public void back(ActionEvent event) {
    soundManager.playCancelSound();
    ProfileScreenController psc = changeScene("/FXML/profile.fxml").getController();
    psc.setStage(stage);
    psc.setPrevious(previous);
  }

  /**
   * Handles the account deletion process when triggered. This method validates the user-provided
   * password and sends a request to the server to delete the account. If the password is invalid,
   * an error is displayed. If the server request fails, appropriate error messages are shown. The
   * method also updates the application scene upon successful account deletion.
   *
   * @param event The ActionEvent triggered by the confirmation button press.
   */
  @FXML
  public void confirm(ActionEvent event) {
    LOGGER.info("Account deletion initiated");
    soundManager.playClickSound();
    User user = User.getInstance();
    UserData userData = user.getUserData();
    String username = userData.getUsername();
    String password = userData.getPassword();

    if (password.equals(passwordField.getText())) {
      LOGGER.info("Attempting to delete account for user: " + username);

      try {
        URL url =
            new URL(
                "http://"
                    + ConstantsManager.HOST
                    + ":8081/api/user/delete?username="
                    + username
                    + "&password="
                    + password);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");

        int responseCode = connection.getResponseCode();
        LOGGER.info("Delete request response code: " + responseCode);

        if (responseCode == 200) {
          LOGGER.info("Account deleted successfully");
          StartScreenController ssc = changeScene("/FXML/start.fxml").getController();
          ssc.setStage(stage);
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
            LOGGER.severe(
                "Account deletion failed with code: "
                    + responseCode
                    + ", message: "
                    + errorMessage);
            showError(
                "Deletion failed. Response code: "
                    + responseCode
                    + (errorMessage.isEmpty() ? "" : "\nError: " + errorMessage));
          }
        }

      } catch (IOException ioe) {
        LOGGER.severe("IO Exception during deletion request: " + ioe.getMessage());
        showError("Connection error: " + ioe.getMessage());
      }
    } else {
      showError("Invalid Password!");
    }
  }

  /**
   * Displays an error message in a modal pop-up dialog using the JavaFX Alert class. The dialog is
   * of type ERROR, has no header, and only shows the provided message in its content area. This
   * method is typically used for displaying application errors to the end user in a user-friendly
   * manner.
   *
   * @param message The error message to be displayed in the dialog box.
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
