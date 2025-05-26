package de.spl12.server.technical.user_management;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.spl12.server.technical.DatabaseConnector;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

/**
 * Unit tests for the {@link UserStatsRepository}.
 *
 * <p>Validates correct behavior when aggregating user statistics from the {@code game_results}
 * table.
 *
 * @author lmelodia
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserStatsRepositoryTest {

  private Connection connection;
  private UserStatsRepository statsRepository;

  @BeforeAll
  void setUpDatabase() throws SQLException {
    String testDbUri = "jdbc:sqlite::memory:";
    DatabaseConnector dbConnector = DatabaseConnector.getInstance(testDbUri);
    connection = dbConnector.getConnection();
    statsRepository = new UserStatsRepository(connection);

    try (Statement stmt = connection.createStatement()) {
      stmt.execute("PRAGMA foreign_keys = ON");
      stmt.execute("DROP TABLE IF EXISTS game_results");
      stmt.execute("DROP TABLE IF EXISTS users");

      stmt.execute(
          "CREATE TABLE users ("
              + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
              + "username TEXT UNIQUE, "
              + "password TEXT)");

      stmt.execute(
          """
              CREATE TABLE game_results (
                  game_id TEXT,
                  player_id INTEGER,
                  tokens_played_green INTEGER DEFAULT 0,
                  tokens_played_blue INTEGER DEFAULT 0,
                  tokens_played_red INTEGER DEFAULT 0,
                  tokens_played_black INTEGER DEFAULT 0,
                  tokens_played_white INTEGER DEFAULT 0,
                  tokens_played_gold INTEGER DEFAULT 0,
                  cards_bought INTEGER DEFAULT 0,
                  placement INTEGER,
                  PRIMARY KEY (game_id, player_id),
                  FOREIGN KEY (player_id) REFERENCES users(id) ON DELETE CASCADE
              )
              """);
    }
  }

  @BeforeEach
  void resetDatabase() throws SQLException {
    try (Statement stmt = connection.createStatement()) {
      stmt.execute("DELETE FROM game_results");
      stmt.execute("DELETE FROM users");

      stmt.execute("INSERT INTO users (id, username, password) VALUES (1, 'testUser', 'password')");

      stmt.execute(
          """
              INSERT INTO game_results (
                  game_id, player_id, tokens_played_green, tokens_played_blue,
                  tokens_played_red, tokens_played_black, tokens_played_white, tokens_played_gold,
                  cards_bought, placement)
              VALUES
                  ('game1', 1, 5, 2, 0, 1, 3, 0, 4, 1),
                  ('game2', 1, 1, 1, 2, 0, 2, 1, 2, 2)
              """);
    }
  }

  @Test
  void testAggregatedStatsForExistingUser() {
    Map<String, Object> stats = statsRepository.getAggregatedStats(1);

    assertEquals(2, stats.get("gamesPlayed"));
    assertEquals(1, stats.get("gamesWon"));
    assertEquals("Green", stats.get("favoriteToken"));
  }

  @Test
  void testAggregatedStatsForNonExistingUser() {
    Map<String, Object> stats = statsRepository.getAggregatedStats(999);
    assertEquals(0, stats.get("gamesPlayed"));
  }

  @AfterAll
  void tearDownDatabase() throws SQLException {
    if (connection != null && !connection.isClosed()) {
      connection.close();
    }
  }
}
