package de.spl12.server.technical.user_management;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Handles password hashing and verification using SHA-256 with salt and pepper.
 */
public class PasswordHandler {

    private static final int SALT_LENGTH = 16;
    private static final String PEPPER = "RobertKratzIsAnAwesomeTutor";

    /**
     * Generates a random salt using SecureRandom.
     *
     * @return a Base64 encoded salt
     */
    private static String generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hashes a plaintext password using SHA-256 with a generated salt and pepper.
     *
     * @param password the plaintext password to hash
     * @return the hashed password concatenated with the salt (Base64 encoded)
     * @throws IllegalArgumentException if the password is null or empty
     * @throws RuntimeException if the SHA-256 algorithm is not available
     */
    public static String hashPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        String salt = generateSalt();

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((password + salt + PEPPER).getBytes());
            String hashedPassword = Base64.getEncoder().encodeToString(hash);
            return hashedPassword + ";" + salt;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Compares a plaintext password with a hashed password using SHA-256, salt, and pepper.
     *
     * @param password the plaintext password to compare
     * @param storedData the previously hashed password and salt concatenated with ';'
     * @return true if the password matches the hashed password, false otherwise
     * @throws IllegalArgumentException if the stored data format is invalid or the password is null/empty
     * @throws RuntimeException if the SHA-256 algorithm is not available
     */
    public static boolean comparePassword(String password, String storedData) {
        if (storedData == null
                || !storedData.contains(";")
                || password == null
                || password.isBlank()) {
            throw new IllegalArgumentException(
                    "Invalid stored password format or password is null/empty");
        }

        String[] hashPasswordAndSalt = storedData.split(";");
        String hashedPassword = hashPasswordAndSalt[0];
        String salt = hashPasswordAndSalt[1];

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((password + salt + PEPPER).getBytes());
            String calculatedHash = Base64.getEncoder().encodeToString(hash);
            return calculatedHash.equals(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
