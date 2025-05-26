package de.spl12.domain.messages;

import de.spl12.domain.AbstractPlayer;
import de.spl12.domain.GameState;

/**
 * Message sent by a player to indicate an action taken in the game, typically resulting in an
 * updated {@link GameState}.
 *
 * @author ennauman
 */
public class PlayerActionPackage extends AbstractPackage {

  private static final long serialVersionUID = -7754364073646983534L;
  private final AbstractPlayer PLAYER;
  private final int SESSIONID;
  private final GameState GAMESTATE;

  /**
   * Constructs a new {@code PlayerActionPackage}.
   *
   * @param player the player who took the action
   * @param sessionID the ID of the game session
   * @param gameState the updated game state after the action
   */
  public PlayerActionPackage(AbstractPlayer player, int sessionID, GameState gameState) {
    this.PLAYER = player;
    this.SESSIONID = sessionID;
    this.GAMESTATE = gameState;
  }

  public AbstractPlayer getPlayer() {
    return PLAYER;
  }

  public int getSessionId() {
    return SESSIONID;
  }

  public GameState getGameState() {
    return this.GAMESTATE;
  }
}
