package de.spl12.server.application.game_logic;

import de.spl12.domain.AbstractPlayer;
import de.spl12.domain.AiPlayer;
import de.spl12.domain.GameState;
import java.util.ArrayList;

import static de.spl12.server.application.AiNameHelper.getRandomName;

/**
 * Represents a single game session on the server.
 *
 * <p>A session contains a unique ID, a list of players, and the current game state. It is
 * responsible for managing player participation and the lifecycle of the game.
 *
 * <p>Each session can be started and its game state can be updated or retrieved.
 *
 * <p>This class is typically managed by {@link GameSessionManager}.
 *
 * @author ennauman
 */
public class GameSession {

  /** The unique identifier for this game session. */
  private final int SESSION_ID;

  /** The current game state for this session. */
  private GameState gameState;

  private boolean isMultiplayer;

  /**
   * Constructs a new {@code GameSession} with the specified ID and initial game state.
   *
   * @param sessionID the unique session ID
   * @param gameState the initial game state
   */
  public GameSession(int sessionID, GameState gameState, boolean isMultiplayer) {
    this.SESSION_ID = sessionID;
    this.gameState = gameState;
    this.isMultiplayer = isMultiplayer;
  }

  /**
   * Returns the unique ID of this game session.
   *
   * @return the session ID
   */
  public int getSessionId() {
    return this.SESSION_ID;
  }

  /**
   * Returns the current game state.
   *
   * @return the current {@link GameState}
   */
  public GameState getGameState() {
    return this.gameState;
  }

  /**
   * Returns the number of players currently in this session.
   *
   * @return the number of players
   */
  public int getPlayerCount() {
    return this.gameState.getPlayers().size();
  }

  /**
   * Adds a player to this session.
   *
   * @param player the player to add
   */
  public void addPlayer(AbstractPlayer player) {
    player.setSessionPlayerNumber(this.getPlayerCount());
    System.out.println("Player number: " + player.getSessionPlayerNumber());
    if (player instanceof AiPlayer aiPlayer) {
      ArrayList<String> ignoredNames = new ArrayList<>();
      for (AbstractPlayer p : this.gameState.getPlayers()) {
        ignoredNames.add(p.getName());
      }
      aiPlayer.setName(
          getRandomName(ignoredNames) + " (" + aiPlayer.getDifficulty().toString() + ")");
    }
    System.out.println("Player name: " + player.getName());
    this.gameState.getPlayers().add(player);
  }

  /**
   * Removes a player from this session.
   *
   * @param player the player to remove
   */
  public void removePlayer(AbstractPlayer player) {
    this.gameState.getPlayers().remove(player.getSessionPlayerNumber());
    for (AbstractPlayer p : this.gameState.getPlayers()) {
      // update the new session player number so it corresponds to the index in the array list
      if (p.getSessionPlayerNumber() > player.getSessionPlayerNumber()) {
        p.setSessionPlayerNumber(p.getSessionPlayerNumber() - 1);
      }
    }
  }

  /**
   * Replaces the current game state with a new one.
   *
   * @param newGameState the updated game state
   */
  public void updateGameState(GameState newGameState) {
    this.gameState = newGameState;
  }

  public boolean isMultiplayer() {
    return this.isMultiplayer;
  }
}
