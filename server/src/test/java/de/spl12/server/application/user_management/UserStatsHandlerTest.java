package de.spl12.server.application.user_management;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.spl12.server.technical.DatabaseConnector;
import de.spl12.server.technical.user_management.UserStatsRepository;
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
 * Unit tests for {@link UserStatsHandler}.
 *
 * <p>Verifies correct aggregation, insertion, and behavior with non-existent users.
 *
 * <ul>
 *   <li>Database is created in-memory (SQLite)
 *   <li>Foreign key constraints are enforced
 *   <li>Each test works on a reset database state
 * </ul>
 *
 * @author lmelodia
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserStatsHandlerTest {

  private Connection connection;
  private UserStatsRepository statsRepository;
  private UserStatsHandler statsHandler;

  @BeforeAll
  void setupDatabase() throws SQLException {
    DatabaseConnector dbConnector = DatabaseConnector.getInstance("jdbc:sqlite::memory:");
    connection = dbConnector.getConnection();
    statsRepository = new UserStatsRepository(connection);
    statsHandler = new UserStatsHandler(statsRepository);

    try (Statement stmt = connection.createStatement()) {
      stmt.execute("PRAGMA foreign_keys = ON");
      stmt.execute("DROP TABLE IF EXISTS game_results");
      stmt.execute("DROP TABLE IF EXISTS users");

      stmt.execute(
          "CREATE TABLE users (id INTEGER PRIMARY KEY, username TEXT UNIQUE, password TEXT)");
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

  @AfterAll
  void teardownDatabase() throws SQLException {
    if (connection != null && !connection.isClosed()) {
      connection.close();
    }
  }

  @BeforeEach
  void resetDatabase() throws SQLException {
    try (Statement stmt = connection.createStatement()) {
      stmt.execute("DELETE FROM game_results");
      stmt.execute("DELETE FROM users");

      stmt.execute("INSERT INTO users (id, username, password) VALUES (1, 'testUser', 'password')");
      stmt.execute(
          "INSERT INTO users (id, username, password) VALUES (2, 'testUser2', 'password')");
      stmt.execute(
          """
                    INSERT INTO game_results (
                        game_id, player_id, tokens_played_green, tokens_played_blue,
                        tokens_played_red, tokens_played_black, tokens_played_white, tokens_played_gold,
                        cards_bought, placement
                    ) VALUES
                        ('game1', 1, 5, 2, 0, 1, 3, 0, 4, 1),
                        ('game2', 1, 1, 1, 2, 0, 2, 1, 2, 2)
                    """);
    }
  }

  @Test
  void testAggregatedStatsForExistingUser() {
    Map<String, Object> stats = statsHandler.handleGetAggregatedStatsForUser(1);

    assertNotNull(stats);
    assertEquals(2, stats.get("gamesPlayed"));
    assertEquals(1, stats.get("gamesWon"));
    assertEquals("Green", stats.get("favoriteToken"));
  }

  @Test
  void testAggregatedStatsForNonexistentUserReturnsDefaults() {
    Map<String, Object> stats = statsHandler.handleGetAggregatedStatsForUser(999);

    assertNotNull(stats);
    assertEquals(0, stats.get("gamesPlayed"));
    assertEquals(0, stats.get("gamesWon"));
    assertEquals("-", stats.get("favoriteToken"));
  }

  @Test
  void testInsertGameResultAndValidateAggregation() {
    int[] playerIds = {1, 2};
    int[] tokensGreen = {5, 3};
    int[] tokensBlue = {2, 1};
    int[] tokensRed = {0, 4};
    int[] tokensBlack = {1, 0};
    int[] tokensWhite = {3, 2};
    int[] tokensGold = {0, 1};
    int[] cardsBought = {4, 2};
    int[] placement = {1, 2};

    boolean success =
        statsHandler.handleInsertGameResult(
            playerIds,
            tokensGreen,
            tokensBlue,
            tokensRed,
            tokensBlack,
            tokensWhite,
            tokensGold,
            cardsBought,
            placement);

    assertTrue(success, "Insertion should return true");

    Map<String, Object> stats = statsHandler.handleGetAggregatedStatsForUser(2);
    assertNotNull(stats);
    assertEquals(1, stats.get("gamesPlayed"));
  }
}
