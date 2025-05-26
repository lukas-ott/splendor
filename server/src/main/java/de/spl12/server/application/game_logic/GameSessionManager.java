package de.spl12.server.application.game_logic;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import de.spl12.domain.AbstractPlayer;
import de.spl12.domain.HumanPlayer;
import de.spl12.domain.AiPlayer;
import de.spl12.domain.GameState;
import de.spl12.domain.StoneType;
import de.spl12.domain.Exceptions.SessionHandlingExceptions.SessionAlreadyFullException;
import de.spl12.domain.Exceptions.SessionHandlingExceptions.SessionAlreadyStartedException;
import de.spl12.domain.Exceptions.SessionHandlingExceptions.SessionNotFoundException;
import de.spl12.domain.Exceptions.SessionHandlingExceptions.SinglePlayerSessionException;
import de.spl12.server.application.user_management.UserHandler;
import de.spl12.server.application.user_management.UserStatsHandler;
import de.spl12.server.application.User;

/**
 * Singleton class responsible for managing all active {@link GameSession} instances.
 *
 * <p>It handles session creation, player joins and leaves, game starts, and in-game player
 * actions.
 * Sessions are stored in a synchronized map and are uniquely identified by an incrementing session
 * ID.
 *
 * <p>This class is used by the {@link GameServerEndpoint} to manage session-related logic.
 *
 * <p>Thread-safe access is ensured by synchronizing on the session map.
 *
 * @author ennauman
 */
public class GameSessionManager {

  /**
   * The maximum number of players allowed per game session.
   */
  private static final int MAX_PLAYER_COUNT = 4;

  /**
   * A synchronized map holding all active game sessions, indexed by their unique session IDs.
   */
  private static final Map<Integer, GameSession> GAME_SESSIONS =
      Collections.synchronizedMap(new HashMap<>());

  /**
   * Stores the last used session ID, used to assign unique IDs to new sessions. The Id should be
   * min a 4 digit number
   */
  private static int lastSessionId = 1000;

  /**
   * The singleton instance of the GameSessionManager.
   */
  private static GameSessionManager INSTANCE;

  /**
   * Private constructor to prevent instantiation (singleton).
   */
  private GameSessionManager() {
  }

  /**
   * Returns the singleton instance of the {@code GameSessionManager}.
   *
   * @return the singleton instance
   */
  public static GameSessionManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new GameSessionManager();
    }
    return INSTANCE;
  }

  /**
   * Creates a new game session with a unique session ID and an initial {@link GameState}.
   *
   * @return the new {@link GameSession}
   */
  public GameSession handleCreateSession(boolean isMultiplayer) {
    lastSessionId++;
    GameSession newGameSession = new GameSession(lastSessionId, new GameState(), isMultiplayer);
    synchronized (GAME_SESSIONS) {
      GAME_SESSIONS.put(lastSessionId, newGameSession);
    }
    return newGameSession;
  }

  /**
   * Attempts to add a player to an existing session. The session must not be full or already
   * running. It registers a fake user if the player is an AI.
   *
   * @param player    the player attempting to join
   * @param sessionId the ID of the session to join
   * @return the session if join was successful
   */
  public GameSession handleJoin(AbstractPlayer player, int sessionId)
      throws SessionNotFoundException,
      SessionAlreadyFullException,
      SessionAlreadyStartedException,
      SinglePlayerSessionException {
    synchronized (GAME_SESSIONS) {
      if (!GAME_SESSIONS.containsKey(sessionId)) {
        throw new SessionNotFoundException("" + sessionId);
      }
      GameSession gameSession = GAME_SESSIONS.get(sessionId);

      if (gameSession.getPlayerCount() == MAX_PLAYER_COUNT) {
        throw new SessionAlreadyFullException("");
      }
      if (gameSession.getGameState().isRunning()) {
        throw new SessionAlreadyStartedException("");
      }
      if (!gameSession.isMultiplayer()
          && player instanceof HumanPlayer
          && !gameSession.getGameState().getPlayers().isEmpty()) {
        throw new SinglePlayerSessionException("");
      }
      gameSession.addPlayer(player);
      if (player instanceof AiPlayer aiPlayer) {
        UserHandler userHandler = new UserHandler();
        Optional<User> optionalUser = userHandler.handleRegister(aiPlayer.getName(), "pwd", 99);
        if (optionalUser.isPresent()) {
          User fakeUser = optionalUser.get();
          aiPlayer.setFakeUser(
              new de.spl12.domain.User(
                  fakeUser.getId(), fakeUser.getUsername(), fakeUser.getPassword(), 99));
        } else {
          optionalUser = userHandler.handleLogin(aiPlayer.getName(), "pwd");
          if (optionalUser.isPresent()) {
            User fakeUser = optionalUser.get();
            aiPlayer.setFakeUser(
                new de.spl12.domain.User(
                    fakeUser.getId(), fakeUser.getUsername(), fakeUser.getPassword(), 99));
          }
        }
      }
      return gameSession;
    }
  }

  /**
   * Handles a player leaving the session. The session is terminated for all players. It deletes the
   * player if it is an AI from the data base and all AIs if the session is terminated.
   *
   * @param sessionId the ID of the session to leave
   * @return the removed session
   */
  public GameSession handleLeave(AbstractPlayer player, int sessionId) {
    // if one player leaves every player should leave and the game ends unfinished
    // the connection is closed in the onMessage method on the client endpoint
    // if the game has not started yet, the game will not be terminated
    synchronized (GAME_SESSIONS) {
      GameSession gameSession = GAME_SESSIONS.get(sessionId);
      UserHandler userHandler = new UserHandler();
      UserStatsHandler userStatsHandler = new UserStatsHandler();
      if (gameSession.getGameState().isRunning()
          || gameSession.getPlayerCount() == 1
          || player.getSessionPlayerNumber() == 0) {
        GAME_SESSIONS.remove(sessionId);
        for (AbstractPlayer p : gameSession.getGameState().getPlayers()) {
          if (p instanceof AiPlayer aiPlayer) {
            if (aiPlayer.getFakeUser() != null) {
              userStatsHandler.handleDeleteAllEntriesForUser(aiPlayer.getFakeUserId());
              userHandler.handleDelete(
                  aiPlayer.getFakeUser().getUsername(), aiPlayer.getFakeUser().getPassword());
              aiPlayer.setFakeUser(null);
            }
          }
        }
      } else {
        if (player instanceof AiPlayer aiPlayer) {
          if (aiPlayer.getFakeUser() != null) {
            userStatsHandler.handleDeleteAllEntriesForUser(aiPlayer.getFakeUserId());
            userHandler.handleDelete(
                aiPlayer.getFakeUser().getUsername(), aiPlayer.getFakeUser().getPassword());
            aiPlayer.setFakeUser(null);
          }
        }
        gameSession.removePlayer(player);
      }
      return gameSession;
    }
  }

  /**
   * Updates the game state of the session in response to a player's in-game action.
   *
   * @param sessionId    the ID of the session to update
   * @param newGameState the new game state after the action
   * @return the new {@link GameState}
   */
  public GameState handlePlayerAction(int sessionId, GameState newGameState) {
    synchronized (GAME_SESSIONS) {
      GameSession gameSession = GAME_SESSIONS.get(sessionId);
      gameSession.updateGameState(newGameState);
      return gameSession.getGameState();
    }
  }

  /**
   * Handles the logic executed when a game session ends.
   *
   * <p>Collects final statistics for each {@link HumanPlayer} in the session, including: user ID,
   * number of each token type, cards bought, and final placement. These results are then passed to
   * {@link UserStatsHandler}.
   *
   * @param sessionId the ID of the session that has ended
   */
  public void handleGameOver(int sessionId) {
    UserStatsHandler userStatsHandler = new UserStatsHandler();
    GameState gameState = GAME_SESSIONS.get(sessionId).getGameState();

    ArrayList<Integer> playerIdsList = new ArrayList<>();
    ArrayList<Integer> tokensGreensList = new ArrayList<>();
    ArrayList<Integer> tokensBlueList = new ArrayList<>();
    ArrayList<Integer> tokensRedList = new ArrayList<>();
    ArrayList<Integer> tokensBlackList = new ArrayList<>();
    ArrayList<Integer> tokensWhiteList = new ArrayList<>();
    ArrayList<Integer> tokensGoldList = new ArrayList<>();
    ArrayList<Integer> cardsBoughtList = new ArrayList<>();
    ArrayList<Integer> placementList = new ArrayList<>();

    for (AbstractPlayer p : gameState.getPlayers()) {
      if (p instanceof HumanPlayer humanPlayer) {
        playerIdsList.add(humanPlayer.getUser().getId());
      } else if (p instanceof AiPlayer aiPlayer && aiPlayer.getFakeUser() != null) {
        playerIdsList.add(aiPlayer.getFakeUserId());
      }
      tokensGreensList.add(p.getStoneInventory().get(StoneType.GREEN));
      tokensBlueList.add(p.getStoneInventory().get(StoneType.BLUE));
      tokensRedList.add(p.getStoneInventory().get(StoneType.RED));
      tokensBlackList.add(p.getStoneInventory().get(StoneType.BLACK));
      tokensWhiteList.add(p.getStoneInventory().get(StoneType.WHITE));
      tokensGoldList.add(p.getStoneInventory().get(StoneType.GOLD));
      cardsBoughtList.add(p.getOwnedCards().size());
      placementList.add(getPosition(p, gameState.getPlayers()));
    }
    int[] playerIds = playerIdsList.stream().mapToInt(Integer::intValue).toArray();
    int[] tokensGreens = tokensGreensList.stream().mapToInt(Integer::intValue).toArray();
    int[] tokensBlue = tokensBlueList.stream().mapToInt(Integer::intValue).toArray();
    int[] tokensRed = tokensRedList.stream().mapToInt(Integer::intValue).toArray();
    int[] tokensBlack = tokensBlackList.stream().mapToInt(Integer::intValue).toArray();
    int[] tokensWhite = tokensWhiteList.stream().mapToInt(Integer::intValue).toArray();
    int[] tokensGold = tokensGoldList.stream().mapToInt(Integer::intValue).toArray();
    int[] cardsBought = cardsBoughtList.stream().mapToInt(Integer::intValue).toArray();
    int[] placement = placementList.stream().mapToInt(Integer::intValue).toArray();

    userStatsHandler.handleInsertGameResult(
        playerIds,
        tokensGreens,
        tokensBlue,
        tokensRed,
        tokensBlack,
        tokensWhite,
        tokensGold,
        cardsBought,
        placement);
  }

  /**
   * Calculates the final placement (rank) of a player based on prestige points.
   *
   * <p>The rank is determined by the number of players with more prestige points, where a smaller
   * number indicates a better rank (1 = highest).
   *
   * @param player  the player whose position is being calculated
   * @param players the list of all players in the game session
   * @return the final placement (rank) of the player
   */
  private int getPosition(AbstractPlayer player, List<AbstractPlayer> players) {
    // number of players with more points
    int numPlayersHigherPoints = 0;
    for (AbstractPlayer p : players) {
      if (p.getPrestige() > player.getPrestige()) {
        numPlayersHigherPoints++;
      }
    }
    return 1 + numPlayersHigherPoints;
  }
}
