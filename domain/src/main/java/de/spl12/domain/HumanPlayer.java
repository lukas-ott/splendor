package de.spl12.domain;

/**
 * Represents a player in a game session that is controlled by an actual human user.
 *
 * @author leon.kuersch
 */
public class HumanPlayer extends AbstractPlayer {
  private static final long serialVersionUID = 7139526888241612521L;
  private final User user;

  /**
   * Constructs a HumanPlayer object associated with a specific session player number and a user
   * account. The player's name is initialized based on the user's username.
   *
   * @param sessionPlayerNumber the unique identifier for the player within a game session
   * @param user the user account associated with the human player
   */
  public HumanPlayer(int sessionPlayerNumber, User user) {
    super(sessionPlayerNumber);
    this.user = user;
    this.name = user.getUsername();
  }

  public User getUser() {
    return user;
  }
}
