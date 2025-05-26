package de.spl12.domain.messages;

import de.spl12.domain.AbstractPlayer;

/**
 * Message sent by a client to leave an existing game session.
 *
 * @author ennauman
 */
public class LeaveSessionPackage extends AbstractPackage {

  private static final long serialVersionUID = -1357513291307240382L;
  private final AbstractPlayer PLAYER;
  private final int SESSIONID;

  /**
   * Constructs a new {@code LeaveSessionPackage}.
   *
   * @param player the player leaving the session
   * @param sessionID the ID of the session to leave
   */
  public LeaveSessionPackage(AbstractPlayer player, int sessionID) {
    this.PLAYER = player;
    this.SESSIONID = sessionID;
  }

  public AbstractPlayer getPlayer() {
    return PLAYER;
  }

  public int getSessionId() {
    return SESSIONID;
  }
}
