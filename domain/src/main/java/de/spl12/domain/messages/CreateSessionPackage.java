package de.spl12.domain.messages;

/**
 * Message sent by a client to request the creation of a new game session.
 *
 * @author ennauman
 */
public class CreateSessionPackage extends AbstractPackage {

  private static final long serialVersionUID = 4879532929283948185L;

  private int createdSessionId;
  private String serverIP;

  private boolean isMultiplayer;

  public CreateSessionPackage(boolean isMultiplayer) {
    this.createdSessionId = -1;
    this.isMultiplayer = isMultiplayer;
  }

  public int getCreatedSessionId() {
    return createdSessionId;
  }

  public void setCreatedSessionId(int createdSessionId) {
    this.createdSessionId = createdSessionId;
  }

  public boolean isMultiplayer() {
    return isMultiplayer;
  }

  public void setIsMultiplayer(boolean isMultiplayer) {
    this.isMultiplayer = isMultiplayer;
  }

  public String getServerIP() {
    return serverIP;
  }

  public void setServerIP(String serverIP) {
    this.serverIP = serverIP;
  }
}
