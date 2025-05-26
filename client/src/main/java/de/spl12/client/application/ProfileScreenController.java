package de.spl12.client.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.spl12.client.utils.ConstantsManager;
import de.spl12.client.utils.SoundManager;
import de.spl12.client.utils.User;
import de.spl12.client.utils.UserStats;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

/**
 * Controller for the profile screen.
 * <p>
 * Displays user stats like games played and won, favorite token, and
 * provides navigation to account management actions like password or username changes.
 * </p>
 *
 * @author nmorali
 */


public class ProfileScreenController extends Controller {

  @FXML private Text loginname;
  @FXML private Text gamesPlayed;
  @FXML private Text gamesWon;
  @FXML private Text favoriteToken;

  private String previous;
  private static final String BASE_URL = "http://" + ConstantsManager.HOST + ":8081";

  public void setPrevious(String s) {
    previous = s;
  }

  /**
   * Initializes the ProfileScreenController by setting up user-specific data, retrieving user
   * statistics, and updating the UI components accordingly. This method is invoked automatically
   * upon the controller's loading within an FXML context.
   *
   * <p>Functionality: - Initializes the SoundManager instance for sound-related operations. -
   * Displays the current logged-in user's username on the screen. - Fetches user statistics from
   * the server, including games played, games won, and the favorite token. - Updates the UI labels
   * with the retrieved user statistics. - Handles HTTP communication with the server, including
   * error handling for unexpected responses or exceptions.
   *
   * <p>Network Behavior: - Makes an HTTP GET request to retrieve user statistics from the endpoint
   * composed of the base URL and the user's unique ID. - Validates the server's content type to
   * ensure it is of JSON format. - Parses the JSON response into a UserStats object using the
   * ObjectMapper class from the Jackson library.
   *
   * <p>Error Scenarios: - Logs unexpected HTTP response codes or any exceptions encountered during
   * the network request. - Warns about unexpected or missing content types in the server response.
   */
  @FXML
  public void initialize() {
    soundManager = SoundManager.getInstance();
    loginname.setText("Logged in as: " + User.getInstance().getUserData().getUsername());

    int userId = User.getInstance().getUserData().getId();
    try {
      URL url = new URL(BASE_URL + "/api/user/stats/" + userId);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      conn.setConnectTimeout(5000);
      conn.setReadTimeout(5000);

      int responseCode = conn.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        String contentType = conn.getContentType();
        if (contentType == null || !contentType.contains("application/json")) {
          System.err.println("Unexpected content type: " + contentType);
          return;
        }

        try (InputStream inputStream = conn.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(reader)) {

          StringBuilder responseBuilder = new StringBuilder();
          String line;
          while ((line = bufferedReader.readLine()) != null) {
            responseBuilder.append(line);
          }

          ObjectMapper mapper = new ObjectMapper();
          UserStats stats = mapper.readValue(responseBuilder.toString(), UserStats.class);

          gamesPlayed.setText(String.valueOf(stats.getGamesPlayed()));
          gamesWon.setText(String.valueOf(stats.getGamesWon()));
          favoriteToken.setText(
              stats.getFavoriteToken() != null ? stats.getFavoriteToken() : "None");
        }
      } else {
        System.err.println("HTTP error: " + responseCode);
      }
    } catch (Exception e) {
      System.err.println("Failed to fetch user stats: " + e.getMessage());
      e.printStackTrace();
    }
  }

  @FXML
  public void back(ActionEvent event) {
    soundManager.playCancelSound();
    Controller smsc = changeScene(previous).getController();
    smsc.setStage(stage);
  }

  /**
   * Handles the password change action by transitioning to the change password screen and
   * initializing the corresponding controller.
   *
   * @param event The ActionEvent that triggers the password change process.
   */
  @FXML
  public void changePassword(ActionEvent event) {
    soundManager.playClickSound();
    ChangePasswordScreenController cpsc = changeScene("/FXML/change_password.fxml").getController();
    cpsc.setStage(stage);
    cpsc.setPrevious(previous);
  }

  /**
   * Handles the username change action by transitioning to the corresponding screen and
   * initializing the respective controller with necessary configurations. This method also plays a
   * click sound upon invocation.
   *
   * @param event The ActionEvent that triggers the username change process.
   */
  @FXML
  public void changeUsername(ActionEvent event) {
    soundManager.playClickSound();
    ChangeNameScreenController cnsc = changeScene("/FXML/change_name.fxml").getController();
    cnsc.setStage(stage);
    cnsc.setPrevious(previous);
  }

  /**
   * Handles the action of transitioning to the delete account screen. This method plays a click
   * sound, changes the current scene to the delete account FXML screen, and initializes the
   * DeleteAccountScreenController with necessary configurations including the current stage and
   * previous screen reference.
   *
   * @param event The ActionEvent triggered by the delete button press.
   */
  @FXML
  public void delete(ActionEvent event) {
    soundManager.playClickSound();
    DeleteAccountScreenController dasc = changeScene("/FXML/delete_account.fxml").getController();
    dasc.setStage(stage);
    dasc.setPrevious(previous);
  }

  /**
   * Handles the user logout action by transitioning the application to the start screen. This
   * method plays a click sound, changes the current scene to the start screen FXML, and initializes
   * the controller of the new screen with the current stage.
   *
   * @param event The ActionEvent triggered by the logout action.
   */
  @FXML
  public void logout(ActionEvent event) {
    soundManager.playClickSound();
    Controller ssc = changeScene("/FXML/start.fxml").getController();
    ssc.setStage(stage);
  }
}
