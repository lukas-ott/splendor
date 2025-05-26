package de.spl12.client.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.spl12.client.utils.ConstantsManager;
import de.spl12.client.utils.SoundManager;
import de.spl12.client.utils.User;
import de.spl12.client.utils.UserData;
import java.io.IOException;
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
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

/**
 * Controller for the user registration screen.
 * <p>
 * Handles input validation, registration via HTTP, and navigation to login/start screens.
 * Includes age restrictions and client-side error feedback.
 * </p>
 *
 * @author nmorali
 */

public class RegisterScreenController extends Controller {

  private static final Logger LOGGER = Logger.getLogger(RegisterScreenController.class.getName());

  @FXML private TextField usernameField;

  @FXML private PasswordField passwordField;

  @FXML private PasswordField confirmPasswordField;

  @FXML private AnchorPane rootPane;

  @FXML private TextField ageField;

  @FXML
  public void initialize() {
    soundManager = SoundManager.getInstance();
    rootPane.setOnKeyPressed(
        event -> {
          if (event.getCode() == KeyCode.ENTER) {
            confirm();
          }
        });

    ageField.setTextFormatter(
        new TextFormatter<String>(
            change -> {
              String newText = change.getControlNewText();
              if (newText.matches("\\d{0,2}")) {
                return change;
              }
              return null;
            }));
  }

  @FXML
  private void back(ActionEvent event) {
    soundManager.playCancelSound();
    Controller ssc = changeScene("/FXML/start.fxml").getController();
    ssc.setStage(stage);
  }

  /**
   * Handles the login button click action on the registration screen. This method performs the
   * following actions: 1. Plays a click sound using the sound manager. 2. Switches the current
   * scene to the login screen. 3. Sets the application's primary stage for the login screen
   * controller.
   *
   * @param event The ActionEvent triggered by the user's interaction with the login button.
   */
  @FXML
  private void handleLogin(ActionEvent event) {
    soundManager.playClickSound();
    LoginScreenController ssc = changeScene("/FXML/login.fxml").getController();
    ssc.setStage(stage);
  }

  /**
   * Confirms the registration process by validating input fields, sending a registration request to
   * the server, and handling the server's response. This method performs the following:
   *
   * <p>1. Validates the inputs provided by the user, ensuring that: - Password confirmation matches
   * the entered password. - Mandatory fields (username, password, age) are filled. - Age criteria
   * (10 years or older) is met.
   *
   * <p>2. Sends a registration HTTP POST request to the server with the user's details.
   *
   * <p>3. Processes the server's response, which includes: - Handling successful registration. -
   * Logging appropriate messages. - Displaying error messages in case of server-side issues or
   * invalid inputs.
   *
   * <p>4. Updates the user's information upon successful registration and navigates to the home
   * screen of the application.
   *
   * <p>Input validation: - Ensures non-empty fields for username, password, and age. - Confirms the
   * password and age requirements.
   *
   * <p>Server interaction: - Initiates a POST request to the server's registration endpoint. -
   * Handles response codes, specifically 200 for success and 404 for endpoint not found. - Captures
   * and logs unexpected errors or exceptions during the process.
   *
   * <p>Error handling: - Displays appropriate error messages to the user for invalid inputs,
   * unmatched passwords, or server-related errors.
   *
   * <p>Logs actions and exceptions to provide a traceable audit of the registration attempt.
   */
  @FXML
  private void confirm() {
    if (!passwordField.getText().equals(confirmPasswordField.getText())) {
      showError("Passwords do not match");
      return;
    }
    soundManager.playLoginSound();
    String username = usernameField.getText().trim();
    String password = passwordField.getText().trim();
    String age = ageField.getText().trim();

    LOGGER.info("Attempting to register user: " + username);

    if (username.isEmpty() || password.isEmpty() || age.isEmpty()) {
      showError("Username, password and age cannot be empty.");
      return;
    }

    if (Integer.parseInt(age) < 10) {
      showError("User must be 10 years or older to register.");
      return;
    }

    try {
      URL url = new URL("http://" + ConstantsManager.HOST + ":8081/api/user/register");
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      connection.setDoOutput(true);

      String params = "username=" + username + "&password=" + password + "&age=" + age;
      try (OutputStream os = connection.getOutputStream()) {
        os.write(params.getBytes(StandardCharsets.UTF_8));
      }

      int responseCode = connection.getResponseCode();
      LOGGER.info("Registration request response code: " + responseCode);

      if (responseCode == 200) {
        Scanner scanner = new Scanner(connection.getInputStream(), StandardCharsets.UTF_8);
        String response = scanner.useDelimiter("\\A").next();
        scanner.close();

        ObjectMapper objectMapper = new ObjectMapper();
        UserData userData = objectMapper.readValue(response, UserData.class);
        userData =
            new UserData(userData.getId(), userData.getUsername(), password, Integer.parseInt(age));
        User user = User.getInstance();
        user.setUserData(userData);

        LOGGER.info("Registration successful for user: " + username);
        HomeScreenController smsc = changeScene("/FXML/home.fxml").getController();
        smsc.setStage(stage);
      } else if (responseCode == 404) {
        LOGGER.severe("Endpoint not found (404): " + url.toString());
        showError("Server endpoint not found (404). Contact administrator.");
      } else {
        LOGGER.severe("Registration failed with code: " + responseCode);
        showError("Username already exists! Please choose a different username.");
      }
    } catch (IOException e) {
      LOGGER.severe("Exception during registration request: " + e.getMessage());
      showError("Server error: " + e.getMessage());
    }
  }

  /**
   * Displays an error message in a pop-up alert dialog. This method creates and shows an alert of
   * type ERROR with the specified message. The dialog does not have a header or additional details.
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
