package de.spl12.client.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.spl12.client.utils.ConstantsManager;
import de.spl12.client.utils.SoundManager;
import de.spl12.client.utils.MusicManager;
import de.spl12.client.utils.UserStats;
import de.spl12.domain.AbstractPlayer;
import de.spl12.domain.AiPlayer;
import de.spl12.domain.HumanPlayer;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Comparator;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

public class GameLeaderboardScreenController extends Controller {

  @FXML private Pane settings;
  @FXML private ImageView leaderboard;

  @FXML private Pane player1_leaderboard;
  @FXML private Text player1_leaderboard_name;
  @FXML private Text player1_leaderboard_points;
  @FXML private Pane player2_leaderboard;
  @FXML private Text player2_leaderboard_name;
  @FXML private Text player2_leaderboard_points;
  @FXML private Pane player3_leaderboard;
  @FXML private Text player3_leaderboard_name;
  @FXML private Text player3_leaderboard_points;
  @FXML private Pane player4_leaderboard;
  @FXML private Text player4_leaderboard_name;
  @FXML private Text player4_leaderboard_points;

  @FXML private Slider soundSlider;
  @FXML private Slider musicSlider;
  @FXML private CheckBox soundMuteBox;
  @FXML private CheckBox musicMuteBox;

  @FXML private Text lobby_leaderboard;
  @FXML private Text round_leaderboard;
  private static final String BASE_URL = "http://" + ConstantsManager.HOST + ":8081";

  /**
   * Initializes the GameLeaderboardScreenController by setting up sound and music controls, binding
   * event listeners for volume and mute settings, populating player leaderboard data, and updating
   * the leaderboard display based on player scores.
   *
   * <p>This method performs the following: - Configures initial values for sound and music sliders
   * and checkboxes using SoundManager and MusicManager instances. - Adds listeners to the sliders
   * and mute checkboxes to dynamically update volume levels and mute states. - Disables sliders if
   * their corresponding mute checkbox is selected to reflect muting. - Sets player names on the
   * leaderboard using data from the GameController. - Retrieves player prestige scores and sorts
   * the leaderboard for display.
   *
   * @author nmorali
   */
  @FXML
  public void initialize() {
    soundManager = SoundManager.getInstance();
    musicManager = MusicManager.getInstance();

    soundSlider.setValue(soundManager.getVolume() * 100);
    soundMuteBox.setSelected(soundManager.isMuted());

    musicSlider.setValue(musicManager.getVolume() * 100);
    musicMuteBox.setSelected(musicManager.isMuted());

    // === EventHandler verbinden ===
    soundSlider
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              soundManager.setVolume(newVal.doubleValue() / 100);
            });

    musicSlider
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              musicManager.setVolume(newVal.doubleValue() / 100);
            });

    soundMuteBox
        .selectedProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              soundManager.setMuted(newVal);
              soundSlider.setDisable(newVal);
            });

    musicMuteBox
        .selectedProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              musicManager.setMuted(newVal);
              musicSlider.setDisable(newVal);
            });

    if (soundMuteBox.isSelected()) {
      soundSlider.setDisable(true);
    }
    if (musicMuteBox.isSelected()) {
      musicSlider.setDisable(true);
    }

    this.player1_leaderboard_name.setText(
        GameController.getInstance().getGameState().getPlayers().get(0).getName());
    this.player2_leaderboard_name.setText(
        GameController.getInstance().getGameState().getPlayers().get(1).getName());
    this.player3_leaderboard_name.setText(
        GameController.getInstance().getGameState().getPlayers().get(2).getName());
    this.player4_leaderboard_name.setText(
        GameController.getInstance().getGameState().getPlayers().get(3).getName());

    int player1_points =
        GameController.getInstance().getGameState().getPlayers().get(0).getPrestige();
    int player2_points =
        GameController.getInstance().getGameState().getPlayers().get(1).getPrestige();
    int player3_points =
        GameController.getInstance().getGameState().getPlayers().get(2).getPrestige();
    int player4_points =
        GameController.getInstance().getGameState().getPlayers().get(3).getPrestige();

    sortLeaderboard(player1_points, player2_points, player3_points, player4_points, "Round");
  }

  @FXML
  public void settings() {
    soundManager.playCancelSound();
    settings.setVisible(true);
  }

  /**
   * Updates the lobby leaderboard display with player information and game statistics.
   *
   * <p>This method performs the following actions: - Plays a click sound to indicate interaction. -
   * Updates the leaderboard panel image to show the current game leaderboard. - Changes the visual
   * appearance of the lobby leaderboard and round leaderboard text elements. - Retrieves and
   * displays the names of the players from the game's state. - Calculates and retrieves the number
   * of wins for each player based on their user ID, if the player is of type HumanPlayer. - Sorts
   * and updates the leaderboard display based on the calculated player statistics.
   */
  @FXML
  public void lobby_leaderboard() {
    soundManager.playClickSound();
    leaderboard.setImage(new Image("/images/panels/leaderboard_game.png"));
    lobby_leaderboard.setFill(Paint.valueOf("#461700"));
    round_leaderboard.setFill(Paint.valueOf("#220b00"));

    this.player1_leaderboard_name.setText(
        GameController.getInstance().getGameState().getPlayers().get(0).getName());
    this.player2_leaderboard_name.setText(
        GameController.getInstance().getGameState().getPlayers().get(1).getName());
    this.player3_leaderboard_name.setText(
        GameController.getInstance().getGameState().getPlayers().get(2).getName());
    this.player4_leaderboard_name.setText(
        GameController.getInstance().getGameState().getPlayers().get(3).getName());

    AbstractPlayer p1 = GameController.getInstance().getGameState().getPlayers().get(0);
    AbstractPlayer p2 = GameController.getInstance().getGameState().getPlayers().get(1);
    AbstractPlayer p3 = GameController.getInstance().getGameState().getPlayers().get(2);
    AbstractPlayer p4 = GameController.getInstance().getGameState().getPlayers().get(3);

    int player1_wins = 0;
    int player2_wins = 0;
    int player3_wins = 0;
    int player4_wins = 0;
    if (p1 instanceof HumanPlayer humanPlayer) {
      player1_wins = this.getUserWins(humanPlayer.getUser().getId());
    } else if (p1 instanceof AiPlayer aiPlayer && aiPlayer.getFakeUser() != null) {
      player1_wins = this.getUserWins(aiPlayer.getFakeUserId());
    }
    if (p2 instanceof HumanPlayer humanPlayer) {
      player2_wins = this.getUserWins(humanPlayer.getUser().getId());
    } else if (p2 instanceof AiPlayer aiPlayer && aiPlayer.getFakeUser() != null) {
      player2_wins = this.getUserWins(aiPlayer.getFakeUserId());
    }
    if (p3 instanceof HumanPlayer humanPlayer) {
      player3_wins = this.getUserWins(humanPlayer.getUser().getId());
    } else if (p3 instanceof AiPlayer aiPlayer && aiPlayer.getFakeUser() != null) {
      player3_wins = this.getUserWins(aiPlayer.getFakeUserId());
    }
    if (p4 instanceof HumanPlayer humanPlayer) {
      player4_wins = this.getUserWins(humanPlayer.getUser().getId());
    } else if (p4 instanceof AiPlayer aiPlayer && aiPlayer.getFakeUser() != null) {
      player4_wins = this.getUserWins(aiPlayer.getFakeUserId());
    }

    sortLeaderboard(player1_wins, player2_wins, player3_wins, player4_wins, "Game");
  }

  /**
   * Updates the round leaderboard display with player information and scores.
   *
   * <p>This method performs the following actions: - Plays a click sound to indicate interaction. -
   * Updates the leaderboard panel image to display the round leaderboard. - Modifies the visual
   * appearance of the lobby leaderboard and round leaderboard text elements. - Retrieves and
   * displays the names of the players from the game's current state. - Retrieves the prestige
   * points of each player from the game's current state. - Sorts and updates the leaderboard
   * display based on the retrieved player scores using the sortLeaderboard method.
   */
  @FXML
  public void round_leaderboard() {
    soundManager.playClickSound();
    leaderboard.setImage(new Image("/images/panels/leaderboard_round.png"));
    lobby_leaderboard.setFill(Paint.valueOf("#220b00"));
    round_leaderboard.setFill(Paint.valueOf("#461700"));

    this.player1_leaderboard_name.setText(
        GameController.getInstance().getGameState().getPlayers().get(0).getName());
    this.player2_leaderboard_name.setText(
        GameController.getInstance().getGameState().getPlayers().get(1).getName());
    this.player3_leaderboard_name.setText(
        GameController.getInstance().getGameState().getPlayers().get(2).getName());
    this.player4_leaderboard_name.setText(
        GameController.getInstance().getGameState().getPlayers().get(3).getName());

    int player1_points =
        GameController.getInstance().getGameState().getPlayers().get(0).getPrestige();
    int player2_points =
        GameController.getInstance().getGameState().getPlayers().get(1).getPrestige();
    int player3_points =
        GameController.getInstance().getGameState().getPlayers().get(2).getPrestige();
    int player4_points =
        GameController.getInstance().getGameState().getPlayers().get(3).getPrestige();

    sortLeaderboard(player1_points, player2_points, player3_points, player4_points, "Round");
  }

  /**
   * Fetches the number of games won by a user identified by their user ID. This method sends an
   * HTTP GET request to retrieve user statistics and parses the response to extract the number of
   * games won. If the request fails or invalid data is received, -1 is returned.
   *
   * @param userId The unique identifier of the user whose wins are to be fetched.
   * @return The number of games the user has won, or -1 in case of an error or invalid data.
   */
  private int getUserWins(int userId) {
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
          return -1;
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
          return stats.getGamesWon();
        }
      } else {
        System.err.println("HTTP error: " + responseCode);
        return -1;
      }
    } catch (Exception e) {
      System.err.println("Failed to fetch user stats: " + e.getMessage());
      e.printStackTrace();
      return -1;
    }
  }

  /**
   * Exits the current game session and redirects the player to the multiplayer lobby screen.
   *
   * <p>This method performs the following actions: - Plays a click sound effect to provide feedback
   * for the user interaction. - Resets the current game state using the {@code GameController}. -
   * Changes the scene to the multiplayer lobby screen by loading the relevant FXML file. -
   * Configures the lobby screen's controller by setting its stage and determining if the current
   * player is the host based on their session player number. - Updates the user interface of the
   * multiplayer lobby screen to reflect the current game state.
   */
  @FXML
  public void exit_game() {
    soundManager.playClickSound();
    GameController.getInstance().resetGameState();
    MultiplayerLobbyScreenController smsc =
        changeScene("/FXML/multiplayer_lobby.fxml").getController();
    smsc.setStage(stage);
    smsc.setHost(GameController.getInstance().getPlayer().getSessionPlayerNumber() == 0);
    smsc.updateUI();
  }

  @FXML
  public void back() {
    soundManager.playClickSound();
    settings.setVisible(false);
  }

  /**
   * Sorts and updates the leaderboard display based on player points or wins. The leaderboard is
   * rearranged in descending order of player scores. Depending on the specified type, either points
   * or wins are displayed next to each player's name.
   *
   * @param player1_points The points or wins of player 1.
   * @param player2_points The points or wins of player 2.
   * @param player3_points The points or wins of player 3.
   * @param player4_points The points or wins of player 4.
   * @param type Specifies whether the leaderboard displays "Round" (points) or "Wins".
   */
  public void sortLeaderboard(
      int player1_points, int player2_points, int player3_points, int player4_points, String type) {
    Pane[] players = {
      player1_leaderboard, player2_leaderboard, player3_leaderboard, player4_leaderboard
    };
    int[] player_points = {player1_points, player2_points, player3_points, player4_points};
    Map<Pane, Integer> point_map = new HashMap<>();
    for (int i = 0; i < players.length; i++) {
      point_map.put(players[i], player_points[i]);
    }

    List<Map.Entry<Pane, Integer>> entries = new ArrayList<>(point_map.entrySet());

    entries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

    int i = 0;
    for (Map.Entry<Pane, Integer> entry : entries) {
      entry.getKey().setTranslateY(80 * i);
      if (type.equals("Round")) {
        Text points = (Text) entry.getKey().getChildren().get(2);
        points.setText(entry.getValue() + " Points");
      } else {
        Text points = (Text) entry.getKey().getChildren().get(2);
        points.setText(entry.getValue() + " Wins");
      }
      i += 1;
    }
  }
}
