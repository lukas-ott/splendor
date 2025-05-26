package de.spl12.server.application.user_management;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import de.spl12.server.application.User;
import de.spl12.server.technical.DatabaseConnector;
import de.spl12.server.technical.user_management.UserRepository;
import de.spl12.server.technical.user_management.UserStatsRepository;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for the {@link UserAPI} REST endpoints.
 *
 * <p>This class uses JerseyTest to simulate HTTP requests and verify the full lifecycle of user
 * management operations:
 *
 * <ul>
 *   <li>Registration
 *   <li>Duplicate user check
 *   <li>Login (success and failure)
 *   <li>Password and username updates
 *   <li>Account deletion
 *   <li>Game result insertion
 *   <li>Aggregated gameplay statistics retrieval
 * </ul>
 *
 * <p>An in-memory SQLite database is used to isolate tests and ensure no persistent state. Database
 * schema is reset before each test.
 *
 * @see UserHandler
 * @see UserStatsHandler
 * @see UserRepository
 * @see UserStatsRepository
 * @see UserAPI
 * @author lmelodia
 */
class UserAPITest extends JerseyTest {

  private static Connection connection;
  private static UserRepository userRepository;
  private static UserHandler userHandler;
  private static UserStatsRepository userStatsRepository;
  private static UserStatsHandler userStatsHandler;

  @BeforeAll
  static void setupDatabase() throws SQLException {
    DatabaseConnector dbConnector = DatabaseConnector.getInstance("jdbc:sqlite::memory:");
    connection = dbConnector.getConnection();
    userRepository = new UserRepository(connection);
    userHandler = new UserHandler(userRepository);
    userStatsRepository = new UserStatsRepository(connection);
    userStatsHandler = new UserStatsHandler(userStatsRepository);

    try (Statement stmt = connection.createStatement()) {
      stmt.execute("DROP TABLE IF EXISTS users"); // Clear old schema
      stmt.execute("DROP TABLE IF EXISTS game_results");
      stmt.execute(
          "CREATE TABLE users (id INTEGER PRIMARY KEY, username TEXT UNIQUE, password TEXT, age INTEGER)");
      stmt.execute(
          "CREATE TABLE game_results (game_id TEXT, player_id INTEGER, tokens_played_green INTEGER DEFAULT 0, tokens_played_blue INTEGER DEFAULT 0, tokens_played_red INTEGER DEFAULT 0, tokens_played_black INTEGER DEFAULT 0, tokens_played_white INTEGER DEFAULT 0, tokens_played_gold INTEGER DEFAULT 0, cards_bought INTEGER DEFAULT 0, placement INTEGER, PRIMARY KEY (game_id, player_id), FOREIGN KEY (player_id) REFERENCES users(id) ON DELETE CASCADE)");
    }
  }

  @AfterAll
  static void teardownDatabase() {
    try {
      if (connection != null && !connection.isClosed()) {
        connection.close();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    File testDatabase = new File("data/splendorTest.db");
    if (testDatabase.exists()) {
      boolean deleted = testDatabase.delete();
      if (!deleted) {
        System.err.println("Failed to delete test database!");
        testDatabase.deleteOnExit(); // Ensure deletion on JVM exit
      }
    }
  }

  @Override
  protected Application configure() {
    return new ResourceConfig().register(new UserAPI(userHandler, userStatsHandler));
  }

  @BeforeEach
  void cleanDatabase() throws SQLException {
    try (Statement stmt = connection.createStatement()) {
      stmt.execute("DELETE FROM users");
      stmt.execute("DELETE FROM game_results");
    }
  }

  @Test
  void testRegisterUser() {
    Form form = new Form();
    form.param("username", "LukasOtt20");
    form.param("password", "großerJunge007!!!");
    form.param("age", "20");

    Response response = target("user/register").request().post(Entity.form(form));

    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
  }

  @Test
  void testRegisterDuplicateUserFails() {
    Form form = new Form();
    form.param("username", "Enrico_HD");
    form.param("password", "!EselFreund150!");
    form.param("age", "21");
    target("user/register").request().post(Entity.form(form));

    Response response = target("user/register").request().post(Entity.form(form));

    assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
  }

  @Test
  void testLoginSuccess() {
    userHandler.handleRegister("Noyan", "StadsIstSoGeil", 22);

    Form form = new Form();
    form.param("username", "Noyan");
    form.param("password", "StadsIstSoGeil");

    Response response = target("user/login").request().post(Entity.form(form));
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
  }

  @Test
  void testLoginFailure() {
    Form form = new Form();
    form.param("username", "Leon");
    form.param("password", "S_A_P_Fan117");

    Response response = target("user/login").request().post(Entity.form(form));
    assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
  }

  @Test
  void testDeleteUser() throws SQLException {
    userHandler.handleRegister("xXLeonardoLPXx", "SoleilPause100", 23);

    Response response =
        target("user/delete")
            .queryParam("username", "xXLeonardoLPXx")
            .queryParam("password", "SoleilPause100")
            .request()
            .delete();

    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

    Form loginForm = new Form();
    loginForm.param("username", "xXLeonardoLPXx");
    loginForm.param("password", "SoleilPause100");

    Response loginResponse = target("user/login").request().post(Entity.form(loginForm));

    assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), loginResponse.getStatus());

    try (Statement stmt = connection.createStatement()) {
      var resultSet =
          stmt.executeQuery("SELECT COUNT(*) FROM users WHERE username = 'xXLeonardoLPXx'");
      resultSet.next();
      int count = resultSet.getInt(1);
      assertEquals(0, count, "User should be deleted from the database.");
    }
  }

  @Test
  void testChangePassword() {
    userHandler.handleRegister("UltraJanWalter1000", "IchBinEinMockUpGott", 24);

    Form form = new Form();
    form.param("username", "UltraJanWalter1000");
    form.param("password", "IchBinEinMockUpGott");
    form.param("newPassword", "IchBinDerMockUpGott!!!");

    Response response = target("user/change-password").request().put(Entity.form(form));

    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
  }

  @Test
  void testChangeUsername() {
    userHandler.handleRegister("megaboy26", "testpassword123", 25);

    Form form = new Form();
    form.param("oldUsername", "megaboy26");
    form.param("newUsername", "supercoolguy120");
    form.param("password", "testpassword123");

    Response response = target("user/change-username").request().put(Entity.form(form));

    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
  }

  @Test
  void testGetAggregatedStats() {
    userHandler.handleRegister("testUser", "testPassword", 26);
    Optional<User> userOpt = userRepository.getUserByName("testUser");
    if (userOpt.isPresent()) {
      User user = userOpt.get();
      int[] playerIds = {user.getId()};
      int[] tokensGreen = {1};
      int[] tokensBlue = {2};
      int[] tokensRed = {3};
      int[] tokensBlack = {4};
      int[] tokensWhite = {5};
      int[] tokensGold = {6};
      int[] cardsBought = {7};
      int[] placement = {1};

      userStatsHandler.handleInsertGameResult(
          playerIds,
          tokensGreen,
          tokensBlue,
          tokensRed,
          tokensBlack,
          tokensWhite,
          tokensGold,
          cardsBought,
          placement);
    }

    int userId = userOpt.get().getId();
    Response response = target("user/stats/" + userId).request().get();

    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
  }

  @Test
  void testInsertGameResult() {
    userHandler.handleRegister("testUser", "testPassword", 27);
    Optional<User> userOpt = userRepository.getUserByName("testUser");
    if (userOpt.isPresent()) {
      User user = userOpt.get();

      UserAPI.GameResultDTO dto = new UserAPI.GameResultDTO();
      dto.gameId = "game1";
      dto.playerId = new int[] {user.getId()};
      dto.tokensGreen = new int[] {1};
      dto.tokensBlue = new int[] {2};
      dto.tokensRed = new int[] {3};
      dto.tokensBlack = new int[] {4};
      dto.tokensWhite = new int[] {5};
      dto.tokensGold = new int[] {6};
      dto.cardsBought = new int[] {7};
      dto.placement = new int[] {1};

      Response response =
          target("user/insert-game-result")
              .request()
              .post(Entity.entity(dto, MediaType.APPLICATION_JSON));

      assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    } else {
      fail("User not found");
    }
  }
}
