package de.spl12.server.application.user_management;

import de.spl12.server.application.User;
import de.spl12.server.technical.DatabaseConnector;
import de.spl12.server.technical.user_management.PasswordHandler;
import de.spl12.server.technical.user_management.UserRepository;
import java.util.Optional;

/**
 * Handles all core user operations such as login, registration, deletion, password updates, and
 * username changes. Serves as the application-level logic between the API and the data layer.
 *
 * <p>Passwords are securely managed via the {@link PasswordHandler}, and data access is handled
 * through the {@link UserRepository}.
 *
 * <p>Usage example:
 *
 * <pre>{@code
 * UserHandler handler = new UserHandler();
 * Optional<User> user = handler.handleLogin("username", "password");
 * }</pre>
 *
 * @author lmelodia
 */
public class UserHandler {

  private final UserRepository userRepository;

  /** Constructs a UserHandler with the default database connection. */
  public UserHandler() {
    this.userRepository = new UserRepository(DatabaseConnector.getInstance().getConnection());
  }

  /**
   * Constructs a UserHandler with a specified repository. Useful for testing.
   *
   * @param userRepository the repository used for user operations
   */
  public UserHandler(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Attempts to authenticate a user using the provided credentials.
   *
   * @param username the user's username
   * @param password the user's plain-text password
   * @return an {@code Optional<User>} if authentication is successful; otherwise empty
   */
  public Optional<User> handleLogin(String username, String password) {
    return userRepository
        .getUserByName(username)
        .filter(user -> PasswordHandler.comparePassword(password, user.getPassword()));
  }

  /**
   * Registers a new user, hashing the password and persisting the user if the username is unique.
   *
   * @param username the desired username
   * @param password the plain-text password
   * @param age the user's age
   * @return an {@code Optional<User>} if registration is successful; otherwise empty
   */
  public Optional<User> handleRegister(String username, String password, int age) {
    if (userRepository.getUserByName(username).isPresent()) {
      return Optional.empty(); // Username already taken
    }

    String hashedPassword = PasswordHandler.hashPassword(password);

    if (!userRepository.createUser(username, hashedPassword, age)) {
      return Optional.empty(); // User creation failed
    }

    return userRepository.getUserByName(username);
  }

  /**
   * Deletes a user from the system after validating credentials.
   *
   * @param username the user's username
   * @param password the user's plain-text password
   * @return an {@code Optional<User>} containing the deleted user if successful; otherwise empty
   */
  public Optional<User> handleDelete(String username, String password) {
    Optional<User> userOptional =
        userRepository
            .getUserByName(username)
            .filter(user -> PasswordHandler.comparePassword(password, user.getPassword()));

    if (userOptional.isPresent() && userRepository.deleteUser(username)) {
      return userOptional;
    }

    return Optional.empty();
  }

  /**
   * Updates a user's password after validating the current one.
   *
   * @param username the user's username
   * @param password the current password
   * @param newPassword the new password
   * @return an Optional containing the updated User if successful
   */
  public Optional<User> handleUpdatePassword(String username, String password, String newPassword) {
    return userRepository
        .getUserByName(username)
        .filter(user -> PasswordHandler.comparePassword(password, user.getPassword()))
        .filter(
            user ->
                userRepository.setPassword(user.getId(), PasswordHandler.hashPassword(newPassword)))
        .flatMap(user -> userRepository.getUserByName(username));
  }

  /**
   * Changes a user's username after validating their password.
   *
   * @param oldUsername the user's current username
   * @param newUsername the new username to be set
   * @param password the user's current plain-text password
   * @return an {@code Optional<User>} with the updated user if successful; otherwise empty
   */
  public Optional<User> handleChangeUsername(
      String oldUsername, String newUsername, String password) {
    return userRepository
        .getUserByName(oldUsername)
        .filter(user -> PasswordHandler.comparePassword(password, user.getPassword()))
        .filter(user -> userRepository.getUserByName(newUsername).isEmpty())
        .filter(user -> userRepository.setUsername(user.getId(), newUsername))
        .flatMap(user -> userRepository.getUserByName(newUsername));
  }
}
