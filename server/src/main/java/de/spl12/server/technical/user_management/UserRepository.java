package de.spl12.server.technical.user_management;

import de.spl12.server.application.User;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Handles database operations related to users.
 *
 * @author luott
 */
public class UserRepository {

  private static final Logger LOG = Logger.getLogger(UserRepository.class.getName());
  private final Connection connection;

  public UserRepository(Connection connection) {
    this.connection = connection;
    try {
      this.connection.createStatement().executeQuery("SELECT COUNT(*) FROM users");
      LOG.info("Connected to database: " + connection.getMetaData().getURL());
    } catch (SQLException e) {
      LOG.log(Level.SEVERE, "Failed to load users");
    }
  }

  /**
   * Inserts a new user into the database.
   *
   * @param username Name of the user.
   * @param password Hashed password of the user.
   * @param age      Age of the user
   * @return {@code true} if the operation was successful, {@code false} otherwise.
   */
  public boolean createUser(String username, String password, int age) {
    String sql = "INSERT INTO users (username, password, age) VALUES (?, ?, ?)";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, username);
      stmt.setString(2, password);
      stmt.setInt(3, age);
      return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
      LOG.log(Level.SEVERE, "Error creating user: " + username, e);
      return false;
    }
  }

  /**
   * Retrieves a user by their username.
   *
   * @param username Name of the user.
   * @return An {@link Optional} containing the user if found, otherwise empty.
   */
  public Optional<User> getUserByName(String username) {
    String sql = "SELECT id, username, password, age FROM users WHERE username = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, username);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return Optional.of(mapResultSetToUser(rs));
        }
      }
    } catch (SQLException e) {
      LOG.log(Level.SEVERE, "Error retrieving user by username: " + username, e);
    }
    return Optional.empty();
  }

  /**
   * Retrieves a user by their user ID.
   *
   * @param userID ID of the user.
   * @return An {@link Optional} containing the user if found, otherwise empty.
   */
  public Optional<User> getUserByID(int userID) {
    String sql = "SELECT id, username, password, age FROM users WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, userID);
      try (ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
          return Optional.of(mapResultSetToUser(rs));
        }
      }
    } catch (SQLException e) {
      LOG.log(Level.SEVERE, "Error retrieving user by ID: " + userID, e);
    }
    return Optional.empty();
  }

  /**
   * Updates a user's password.
   *
   * @param userID      ID of the user.
   * @param newPassword The new hashed password.
   * @return {@code true} if the update was successful, {@code false} otherwise.
   */
  public boolean setPassword(int userID, String newPassword) {
    String sql = "UPDATE users SET password = ? WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, newPassword);
      stmt.setInt(2, userID);
      return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
      LOG.log(Level.SEVERE, "Error updating password for user ID: " + userID, e);
      return false;
    }
  }

  /**
   * Updates a user's username.
   *
   * @param userID      ID of the user.
   * @param newUsername The new username.
   * @return {@code true} if the update was successful, {@code false} otherwise.
   */
  public boolean setUsername(int userID, String newUsername) {
    String sql = "UPDATE users SET username = ? WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, newUsername);
      stmt.setInt(2, userID);
      return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
      LOG.log(Level.SEVERE, "Error updating username for user ID: " + userID, e);
      return false;
    }
  }

  /**
   * Maps a {@link ResultSet} to a {@link User} object.
   *
   * @param rs The result set containing user data.
   * @return A {@link User} object.
   * @throws SQLException if an error occurs while accessing the result set.
   */
  private User mapResultSetToUser(ResultSet rs) throws SQLException {
    return new User(
        rs.getInt("id"),
        rs.getString("username"),
        rs.getString("password"),
        rs.getInt("age")
    );
  }

  /**
   * Deletes a user from the database by their username.
   *
   * @param username The username of the user to delete.
   * @return {@code true} if the deletion was successful, {@code false} otherwise.
   */
  public boolean deleteUser(String username) {
    String sql = "DELETE FROM users WHERE username = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, username);
      return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
      LOG.log(Level.SEVERE, "Error deleting user by username: " + username, e);
      return false;
    }
  }

  /**
   * Deletes a user from the database by their user ID.
   *
   * @param userID The ID of the user to delete.
   * @return {@code true} if the deletion was successful, {@code false} otherwise.
   */
  public boolean deleteUserByID(int userID) {
    String sql = "DELETE FROM users WHERE id = ?";
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, userID);
      return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
      LOG.log(Level.SEVERE, "Error deleting user by ID: " + userID, e);
      return false;
    }
  }
}
