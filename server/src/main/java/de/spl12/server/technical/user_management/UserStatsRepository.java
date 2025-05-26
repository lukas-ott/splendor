package de.spl12.server.technical.user_management;

import de.spl12.server.technical.DatabaseConnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles database operations related to user statistics and game results. Connects to the
 * game_results table.
 *
 * <p>Provides methods to insert game results, retrieve aggregated statistics, and clear or delete
 * user-specific data.
 *
 * @author lmelodia
 */
public class UserStatsRepository {

  private static final Logger LOG = Logger.getLogger(UserStatsRepository.class.getName());

  private final Connection connection;

  /**
   * Constructs a UserStatsRepository using a provided database connection.
   *
   * @param connection the JDBC connection to use
   */
  public UserStatsRepository(Connection connection) {
    this.connection = connection;
  }

  /** Constructs a UserStatsRepository with the default database connection. */
  public UserStatsRepository() {
    this.connection = DatabaseConnector.getInstance().getConnection();
  }

  /** Clears all entries in the user statistics repository. */
  public void clearUserStatsRepository() {
    try (Statement stmt = connection.createStatement()) {
      stmt.executeUpdate("DELETE FROM game_results;");
    } catch (SQLException e) {
      LOG.log(Level.SEVERE, "Failed to delete game_results", e);
    }
  }

  /**
   * Inserts a new game result entry for one or more players.
   *
   * @param playerIds IDs of the players
   * @param tokensGreen number of green tokens played
   * @param tokensBlue number of blue tokens played
   * @param tokensRed number of red tokens played
   * @param tokensBlack number of black tokens played
   * @param tokensWhite number of white tokens played
   * @param tokensGold number of gold tokens played
   * @param cardsBought number of cards bought
   * @param placement final placement (1 = win, 2+ = lose)
   * @return true if all entries were inserted successfully
   */
  public boolean insertGameResult(
      int[] playerIds,
      int[] tokensGreen,
      int[] tokensBlue,
      int[] tokensRed,
      int[] tokensBlack,
      int[] tokensWhite,
      int[] tokensGold,
      int[] cardsBought,
      int[] placement) {

    String gameId = UUID.randomUUID().toString();

    for (int i = 0; i < playerIds.length; i++) {
      String sql =
          "INSERT INTO game_results ("
              + "game_id, player_id, tokens_played_green, tokens_played_blue, "
              + "tokens_played_red, tokens_played_black, tokens_played_white, tokens_played_gold, "
              + "cards_bought, placement) "
              + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

      try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setString(1, gameId);
        stmt.setInt(2, playerIds[i]);
        stmt.setInt(3, tokensGreen[i]);
        stmt.setInt(4, tokensBlue[i]);
        stmt.setInt(5, tokensRed[i]);
        stmt.setInt(6, tokensBlack[i]);
        stmt.setInt(7, tokensWhite[i]);
        stmt.setInt(8, tokensGold[i]);
        stmt.setInt(9, cardsBought[i]);
        stmt.setInt(10, placement[i]);

        if (stmt.executeUpdate() == 0) {
          LOG.log(
              Level.WARNING,
              "No rows inserted for game ID: " + gameId + " and player ID: " + playerIds[i]);
          return false;
        }

        LOG.log(
            Level.INFO,
            "Inserted game result for game ID: " + gameId + " and player ID: " + playerIds[i]);

      } catch (SQLException e) {
        LOG.log(
            Level.SEVERE,
            "Error inserting game result for game ID: "
                + gameId
                + " and player ID: "
                + playerIds[i],
            e);
        return false;
      }
    }
    return true;
  }

  /**
   * Aggregates total games played, games won, and favorite token color for a given user.
   *
   * @param userId the user ID to fetch stats for
   * @return a map with keys "gamesPlayed", "gamesWon", and "favoriteToken"
   */
  public Map<String, Object> getAggregatedStats(int userId) {
    Map<String, Object> stats = new HashMap<>();
    stats.put("gamesPlayed", 0);
    stats.put("gamesWon", 0);
    stats.put("favoriteToken", "-");

    String sql =
        "SELECT "
            + "COUNT(*) AS games_played, "
            + "SUM(CASE WHEN placement = 1 THEN 1 ELSE 0 END) AS games_won, "
            + "SUM(tokens_played_green) AS green, "
            + "SUM(tokens_played_blue) AS blue, "
            + "SUM(tokens_played_red) AS red, "
            + "SUM(tokens_played_black) AS black, "
            + "SUM(tokens_played_white) AS white, "
            + "SUM(tokens_played_gold) AS gold "
            + "FROM game_results WHERE player_id = ?";

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, userId);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          int gamesPlayed = rs.getInt("games_played");

          if (gamesPlayed > 0) {
            int gamesWon = rs.getInt("games_won");

            Map<String, Integer> tokenCounts = new HashMap<>();
            tokenCounts.put("Green", rs.getInt("green"));
            tokenCounts.put("Blue", rs.getInt("blue"));
            tokenCounts.put("Red", rs.getInt("red"));
            tokenCounts.put("Black", rs.getInt("black"));
            tokenCounts.put("White", rs.getInt("white"));
            tokenCounts.put("Gold", rs.getInt("gold"));

            String favoriteToken =
                tokenCounts.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("None");

            stats.put("gamesPlayed", gamesPlayed);
            stats.put("gamesWon", gamesWon);
            stats.put("favoriteToken", favoriteToken);
          }
        }
      }
    } catch (SQLException e) {
      LOG.log(Level.SEVERE, "Error aggregating stats for user ID: " + userId, e);
      throw new RuntimeException(e);
    }

    return stats;
  }

  /**
   * Deletes all game results for a specific user.
   *
   * @param userId the ID of the user
   * @return true if one or more results were deleted
   */
  public boolean deleteGameResultsByUserId(int userId) {
    String sql = "DELETE FROM game_results WHERE player_id = ?";

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, userId);
      int affectedRows = stmt.executeUpdate();

      LOG.log(Level.INFO, "Deleted " + affectedRows + " game result(s) for user ID: " + userId);
      return affectedRows > 0;

    } catch (SQLException e) {
      LOG.log(Level.SEVERE, "Error deleting game results for user ID: " + userId, e);
      return false;
    }
  }
}
