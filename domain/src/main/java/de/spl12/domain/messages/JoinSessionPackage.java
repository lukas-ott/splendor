package de.spl12.domain.messages;

import de.spl12.domain.AbstractPlayer;

/**
 * Message sent by a client to join an existing game session.
 *
 * @author ennauman
 */
public class JoinSessionPackage extends AbstractPackage {

  private static final long serialVersionUID = 1172985714829804181L;

  private final AbstractPlayer PLAYER;
  private final int SESSIONID;
  private String SERVER_IP;
  private JoinStatus joinStatus;

  /**
   * Constructs a new {@code JoinSessionPackage}.
   *
   * @param player the player attempting to join the session
   * @param sessionID the ID of the session to join
   */
  public JoinSessionPackage(AbstractPlayer player, int sessionID, JoinStatus joinStatus) {
    this.PLAYER = player;
    this.SESSIONID = sessionID;
    this.joinStatus = joinStatus;
  }

  public enum JoinStatus {
    REQUESTED,
    SUCCESS,
    SESSION_FULL,
    SESSION_NOT_FOUND,
    SESSION_ALREADY_STARTED,
    SINGLE_PLAYER_SESSION
  }

  public AbstractPlayer getPlayer() {
    return PLAYER;
  }

  public int getSessionId() {
    return SESSIONID;
  }

  public JoinStatus getJoinStatus() {
    return joinStatus;
  }

  public void setJoinStatus(JoinStatus joinStatus) {
    this.joinStatus = joinStatus;
  }

  public void setServerIP(String serverIP) {
    this.SERVER_IP = serverIP;
  }

  public String getServerIP() {
    return SERVER_IP;
  }
}
