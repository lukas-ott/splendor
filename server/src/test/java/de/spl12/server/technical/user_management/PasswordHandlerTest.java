package de.spl12.server.technical.user_management;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the PasswordHandler class.
 *
 * <p>Tests hashing and comparison logic, including salt and pepper handling.
 *
 * <p>Ensures:
 *
 * <ul>
 *   <li>Passwords are hashed
 *   <li>Hashes vary due to random salt
 *   <li>Correct password matches stored hash
 *   <li>Incorrect password does not match stored hash
 * </ul>
 *
 * @author lmelodia
 */
public class PasswordHandlerTest {

  @Test
  public void testHashPasswordNotNull() {
    String password = "test123!!";
    String hashedPassword = PasswordHandler.hashPassword(password);
    assertNotNull(hashedPassword, "Hashed password should not be null");
  }

  @Test
  public void testHashPasswordDifferentSalt() {
    String password = "test123!!";
    String hash1 = PasswordHandler.hashPassword(password);
    String hash2 = PasswordHandler.hashPassword(password);
    assertNotEquals(hash1, hash2, "Hashes should be different due to different salts");
  }

  @Test
  public void testComparePasswordSuccess() {
    String password = "test123!!";
    String hashedPassword = PasswordHandler.hashPassword(password);
    assertTrue(
        PasswordHandler.comparePassword(password, hashedPassword),
        "Password should match the hash");
  }

  @Test
  public void testComparePasswordFailure() {
    String password = "test123!!";
    String wrongPassword = "test321!!";
    String hashedPassword = PasswordHandler.hashPassword(password);
    assertFalse(
        PasswordHandler.comparePassword(wrongPassword, hashedPassword),
        "Wrong password should not match the hash");
  }
}
