package de.spl12.server.technical.user_management;

import de.spl12.server.application.User;
import de.spl12.server.technical.DatabaseConnector;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UserRepository. These tests validate the core functionality for user
 * registration, login, password change, and user deletion.
 *
 * @author luott
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryTest {

  private Connection connection;
  private UserRepository userRepository;

  @BeforeAll
  void setUpDatabase() throws SQLException {
    String testDbUri = "jdbc:sqlite::memory:";
    DatabaseConnector dbConnector = DatabaseConnector.getInstance(testDbUri);
    connection = dbConnector.getConnection();
    connection.createStatement().execute(
        "CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password TEXT, age INTEGER)");
    userRepository = new UserRepository(connection);
  }

  @Test
  void testCreateAndRetrieveUser() {
    assertTrue(userRepository.createUser("testUser", "hashedPassword", 18));
    Optional<User> user = userRepository.getUserByName("testUser");
    assertTrue(user.isPresent());
    assertEquals("testUser", user.get().getUsername());
  }

  @Test
  void testGetUserByID() {
    userRepository.createUser("userById", "password123", 18);
    Optional<User> user = userRepository.getUserByName("userById");
    assertTrue(user.isPresent());
    int userId = user.get().getId();
    Optional<User> retrievedUser = userRepository.getUserByID(userId);
    assertTrue(retrievedUser.isPresent());
    assertEquals("userById", retrievedUser.get().getUsername());
  }

  @Test
  void testSetPassword() {
    userRepository.createUser("userPass", "oldPassword", 20);
    Optional<User> user = userRepository.getUserByName("userPass");
    assertTrue(user.isPresent());
    int userId = user.get().getId();
    assertTrue(userRepository.setPassword(userId, "newPassword"));
    Optional<User> updatedUser = userRepository.getUserByID(userId);
    assertTrue(updatedUser.isPresent());
    assertEquals("newPassword", updatedUser.get().getPassword());
  }

  @Test
  void testSetUsername() {
    userRepository.createUser("oldUsername", "password", 21);
    Optional<User> user = userRepository.getUserByName("oldUsername");
    assertTrue(user.isPresent());
    int userId = user.get().getId();
    assertTrue(userRepository.setUsername(userId, "newUsername"));
    Optional<User> updatedUser = userRepository.getUserByID(userId);
    assertTrue(updatedUser.isPresent());
    assertEquals("newUsername", updatedUser.get().getUsername());
  }

  @Test
  void testDeleteUserByUsername() {
    userRepository.createUser("deleteMe", "password", 22);
    assertTrue(userRepository.deleteUser("deleteMe"));
    Optional<User> user = userRepository.getUserByName("deleteMe");
    assertFalse(user.isPresent());
  }

  @Test
  void testDeleteUserByID() {
    userRepository.createUser("deleteById", "password", 23);
    Optional<User> user = userRepository.getUserByName("deleteById");
    assertTrue(user.isPresent());
    int userId = user.get().getId();
    assertTrue(userRepository.deleteUserByID(userId));
    Optional<User> deletedUser = userRepository.getUserByID(userId);
    assertFalse(deletedUser.isPresent());
  }

  @AfterAll
  void tearDownDatabase() throws SQLException {
    connection.close();
  }
}

