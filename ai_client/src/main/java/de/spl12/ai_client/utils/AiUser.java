package de.spl12.ai_client.utils;

import de.spl12.domain.AiDifficulty;

/**
 * Singleton class that stores information about the current AI user.
 *
 * <p>This includes the AI's name, selected lobby code, and difficulty level.
 * The singleton pattern ensures that the same instance is shared across the application.</p>
 *
 * <p>Use {@link #getInstance()} to access the singleton instance.</p>
 *
 * <pre>{@code
 * AiUser user = AiUser.getInstance();
 * user.setName("Elowen");
 * }</pre>
 */
public class AiUser {

    private String name;
    private String lobbyCode;
    private AiDifficulty difficulty;

    /** Private constructor to prevent external instantiation. */
    private AiUser() {
        this.difficulty = AiDifficulty.MEDIUM;
    }

    /** Inner static helper class for thread-safe lazy initialization. */
    private static class SingletonHelper {
        private static final AiUser INSTANCE = new AiUser();
    }

    /**
     * Returns the singleton instance of {@code AiUser}.
     *
     * @return the single {@code AiUser} instance
     */
    public static AiUser getInstance() {
        return SingletonHelper.INSTANCE;
    }

    /**
     * Gets the AI user's name.
     *
     * @return the name of the AI user
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the current lobby code.
     *
     * @return the lobby code as a string
     */
    public String getLobbyCode() {
        return lobbyCode;
    }

    /**
     * Gets the AI difficulty level.
     *
     * @return the {@link AiDifficulty} of the AI
     */
    public AiDifficulty getDifficulty() {
        return difficulty;
    }

    /**
     * Sets the AI user's name.
     *
     * @param name the name to assign
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the lobby code the AI is connected to.
     *
     * @param lobbyCode the lobby code
     */
    public void setLobbyCode(String lobbyCode) {
        this.lobbyCode = lobbyCode;
    }

    /**
     * Sets the difficulty level of the AI.
     *
     * @param difficulty the {@link AiDifficulty} to assign
     */
    public void setDifficulty(AiDifficulty difficulty) {
        this.difficulty = difficulty;
    }
}
