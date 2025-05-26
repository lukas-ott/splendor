package de.spl12.server.application.user_management;

import de.spl12.server.application.User;
import de.spl12.server.technical.DatabaseConnector;
import de.spl12.server.technical.user_management.UserRepository;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UserHandler. These tests validate the core functionality for user
 * registration, login, password change, and user deletion.
 *
 * @author luott
 */
class UserHandlerTest {

  private static Connection connection;
  private static UserHandler userHandler;

  @BeforeAll
  static void setupDatabase() throws SQLException {
    DatabaseConnector dbConnector = DatabaseConnector.getInstance("jdbc:sqlite::memory:");
    connection = dbConnector.getConnection();
    UserRepository userRepository = new UserRepository(connection);
    userHandler = new UserHandler(userRepository);

    try (Statement stmt = connection.createStatement()) {
      stmt.execute("DROP TABLE IF EXISTS users");
      stmt.execute(
          "CREATE TABLE users (id INTEGER PRIMARY KEY, username TEXT UNIQUE, password TEXT, age INTEGER)");
    }
  }

  @AfterAll
  static void teardownDatabase() throws SQLException {
    if (connection != null && !connection.isClosed()) {
      connection.close();
    }
  }

  @BeforeEach
  void cleanDatabase() throws SQLException {
    try (Statement stmt = connection.createStatement()) {
      stmt.execute("DELETE FROM users");
    }
  }

  @Test
  void testRegisterUser() {
    Optional<User> user = userHandler.handleRegister("testUser", "testPass", 12);
    assertTrue(user.isPresent());
    assertEquals("testUser", user.get().getUsername());
  }

  @Test
  void testLoginSuccess() {
    userHandler.handleRegister("validUser", "securePass", 12);
    Optional<User> login = userHandler.handleLogin("validUser", "securePass");
    assertTrue(login.isPresent());
  }

  @Test
  void testLoginFailure() {
    userHandler.handleRegister("invalidUser", "correctPass", 12);
    Optional<User> login = userHandler.handleLogin("invalidUser", "wrongPass");
    assertFalse(login.isPresent());
  }

  @Test
  void testChangePassword() {
    userHandler.handleRegister("user123", "oldPass", 12);
    Optional<User> update = userHandler.handleUpdatePassword("user123", "oldPass", "newPass");
    assertTrue(update.isPresent());
    assertFalse(userHandler.handleLogin("user123", "oldPass").isPresent());
    assertTrue(userHandler.handleLogin("user123", "newPass").isPresent());
  }

  @Test
  void testDeleteUser() {
    userHandler.handleRegister("deleteMe", "pass", 12);
    assertTrue(userHandler.handleDelete("deleteMe", "pass").isPresent());
    assertFalse(userHandler.handleLogin("deleteMe", "pass").isPresent());
  }
}
