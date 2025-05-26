package de.spl12.domain.messages;

import de.spl12.domain.AbstractPlayer;

/**
 * A package sent when a game session ends, indicating the session ID and the player who caused or
 * triggered the game over event.
 *
 * @author ennauman
 */
public class GameOverPackage extends AbstractPackage {

  private static final long serialVersionUID = 5594173968309555210L;
  private final int SESSIONID;
  private final AbstractPlayer player;

  public GameOverPackage(AbstractPlayer player, int sessionId) {
    this.player = player;
    this.SESSIONID = sessionId;
  }

  public int getSessionId() {
    return this.SESSIONID;
  }

  public AbstractPlayer getPlayer() {
    return player;
  }
}
