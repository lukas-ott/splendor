package de.spl12.ai_client.application;

import de.spl12.ai_client.utils.AiUser;
import de.spl12.domain.AbstractPlayer;
import de.spl12.domain.AiPlayer;
import de.spl12.domain.ChatMessage;
import de.spl12.domain.Exceptions.DepletedResourceException;
import de.spl12.domain.GameState;
import de.spl12.domain.HumanPlayer;
import de.spl12.domain.Noble;
import de.spl12.domain.StoneType;
import de.spl12.domain.messages.JoinSessionPackage;
import de.spl12.domain.messages.LeaveSessionPackage;
import de.spl12.domain.messages.PlayerActionPackage;
import de.spl12.domain.moves.AbstractMove;
import de.spl12.domain.moves.BuyMove;
import de.spl12.domain.moves.ReserveMove;
import de.spl12.domain.moves.TakeMove;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;


/**
 * The GameController class is responsible for managing the game state and player actions. It
 * handles joining game sessions, processing game updates, and sending player actions to the
 * server.
 * <p>
 * This class is a singleton, ensuring that only one instance exists throughout the application.
 */
public class GameController {

  private static final Logger LOGGER = Logger.getLogger(GameController.class.getName());

  private int turnNumber = 0;
  private int greetBackIn = Integer.MIN_VALUE;
  private boolean newGame = true;
  private static GameController INSTANCE;
  private GameState gameState;
  private AiPlayer player;
  private final GameClientEndpoint gameClientEndpoint;
  private LobbyController lobbyController;
  private JoinController joinController;
  private int sessionId;
  private Thread t;
  private boolean inSession;
  private boolean gameOver = false;

  private GameController() {
    this.gameClientEndpoint = new GameClientEndpoint();
    this.inSession = false;
  }

  public static GameController getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new GameController();
    }
    return INSTANCE;
  }

  /**
   * Sends a request to join a game session.
   * <p>
   * This method is used to request joining a session. If no response is received from the server
   * within 10 seconds, it is treated as a failed attempt.
   *
   * @param sessionId the ID of the session to join
   */
  public void joinGameSession(int sessionId) {
    AiPlayer aiPlayer = new AiPlayer(-1, AiUser.getInstance().getDifficulty());
    JoinSessionPackage joinSessionPackage = new JoinSessionPackage(aiPlayer, sessionId,
        JoinSessionPackage.JoinStatus.REQUESTED);
    this.gameClientEndpoint.sendMessage(joinSessionPackage);
    t = new Thread(() -> {
      try {
        Thread.sleep(10 * 1000);
        joinUnsuccessful("Received no response from the server - server can be down");
      } catch (InterruptedException e) {
        LOGGER.info("Received a response from the server about the joining request in time");
      }
    });
    t.start();
  }

  /**
   * Handles a successful join to a game session.
   * <p>
   * This method is called when the server approves the join request. It sets the AI player and
   * session ID, and notifies the UI controller.
   *
   * @param player    the AI player that successfully joined
   * @param sessionId the session ID that was joined
   */
  public void joinSuccessful(AbstractPlayer player, int sessionId) {
    t.interrupt();
    this.player = (AiPlayer) player;
    this.sessionId = sessionId;
    this.joinController.handleJoinSuccessful();
    this.inSession = true;
    LOGGER.info("Join Successful");
    AiUser.getInstance().setName(this.player.getName());
  }

  /**
   * Handles a failed attempt to join a game session.
   * <p>
   * This method is called if the join request times out or is rejected by the server.
   *
   * @param errorMsg the error message explaining the failure reason
   */
  public void joinUnsuccessful(String errorMsg) {
    t.interrupt();
    this.joinController.handleJoinUnsuccessful(errorMsg);
    LOGGER.info("Join Unsuccessful");
  }

  /**
   * Updates the game state and processes player actions.
   * <p>
   * This method is called when the game state changes, such as when it's the player's turn. It
   * handles player moves, including taking stones, reserving cards, and buying cards. It also
   * initiates the greeting response if a player sends a greeting message.
   *
   * @param gameState the updated game state
   */
  public void updateGameState(GameState gameState) {
    this.gameState = gameState;
    if (this.greetBackIn == Integer.MIN_VALUE) {
      initializeReplyQueue();
    }
    Optional<AbstractPlayer> winner = this.checkWin();
    if (winner.isEmpty()) {
      this.gameOver = false;
    }

    if (this.newGame && this.gameState.getTurnNumber() == 0) {
      this.turnNumber = 0;
      this.newGame = false;
    }
    if (this.gameState.getTurnNumber() == 1) {
      this.newGame = true;
    }

    // as the session player number can change due to someone with a lower number leaving we get our player through the distinct name
    for (AbstractPlayer p : this.gameState.getPlayers()) {
      // within AI players the names are distinct
      if (p instanceof AiPlayer aiPlayer) {
        if (aiPlayer.getName().equals(this.getPlayer().getName())) {
          this.player = aiPlayer;
        }
      }
    }

    String lastMessage = this.gameState.getChat().getMessageList().getLast().getMessage()
        .toLowerCase();
    String[] greetings = {"hello", "hi", "hey", "greetings", "howdy"};
    if (Arrays.asList(greetings).contains(lastMessage)) {
      this.sendReply();
    }

    // Stop playing if game is over
    if (this.gameState.isRunning()) {
      if (this.lobbyController != null) {
        this.lobbyController.updateUI();
        winner = this.checkWin();
        if (winner.isPresent()) {
          LOGGER.info("Winner is " + winner.get().getName());
          this.gameOver = true;
          this.newGame = true;
          this.turnNumber = 0;
          return;
        }
      }
    }

    if (this.isMyTurn() && this.turnNumber == gameState.getTurnNumber()) {
      this.turnNumber++;
      try {
        Thread.sleep((2000));
        AbstractMove move = this.player.decideMove(this.gameState);

        if (move instanceof TakeMove takeMove) {
          Map<StoneType, Integer> tokens = takeMove.getTokens();
          for (Map.Entry<StoneType, Integer> entry : tokens.entrySet()) {
            StoneType stoneType = entry.getKey();
            int count = entry.getValue();
            for (int i = 0; i < count; i++) {
              this.player.takeStone(stoneType);
              this.gameState.takeStone(stoneType);
              commitAction();
              sleepRandomAmountOfTime();
            }
          }
        } else if (move instanceof ReserveMove reserveMove) {
          this.player.reserveCard(reserveMove.getCard());
          this.gameState.replaceCard(reserveMove.getCard());
          if (reserveMove.gotGoldCoin()) {
            this.player.takeStone(StoneType.GOLD);
            gameState.takeStone(StoneType.GOLD);
          }
          commitAction();
          sleepRandomAmountOfTime();
        } else if (move instanceof BuyMove buyMove) {
          this.gameState.performPayment(buyMove.getTokens());
          this.player.buyCard(buyMove.getCard());
          this.gameState.replaceCard(buyMove.getCard());
          commitAction();
          sleepRandomAmountOfTime();
        }

        // Return tokens if more than 10
        for (StoneType stoneToReturn : move.getTokensToReturn()) {
          this.player.returnStone(stoneToReturn);
          this.gameState.returnStone(stoneToReturn);
          commitAction();
          sleepRandomAmountOfTime();
        }

        // check nobles
        List<Noble> obtainable = new ArrayList<>();
        for (Noble noble : this.gameState.getNobleDeck()) {
          if (this.player.checkNobleVisit(noble)) {
            obtainable.add(noble);
          }
        }
        if (!obtainable.isEmpty()) {
          Noble noble = obtainable.getFirst();
          gameState.removeNoble(noble);
          this.player.obtainNoble(noble);
          commitAction();
          sleepRandomAmountOfTime();
        }
        sleepRandomAmountOfTime();
        gameState.nextTurn();
        this.commitAction();
      } catch (DepletedResourceException e) {
        LOGGER.info(
            "Something went very wrong! DepletedResourceException in AI Client: " + e.getMessage());
      } catch (Exception e) {
        LOGGER.info("Exception in AI Client: " + e.getMessage());
      }
    }
  }


  /**
   * Simulates "thinking" time between 250 and 750 milliseconds.
   * <p>
   * This method is used to simulate a delay in the AI player's actions, making it appear more
   * human-like in its decision-making process.
   */
  private void sleepRandomAmountOfTime() {
    double sleepTime = Math.random() * 500 + 250;
    try {
      Thread.sleep((long) sleepTime);
    } catch (InterruptedException e) {
      LOGGER.info("InterruptedException in AI Client: " + e.getMessage());
    }
  }

  /**
   * Sends a greeting reply to the chat.
   * <p>
   * This method is called when a human player sends a greeting message, and it's the bot's turn to
   * respond. The AI-Bots have a predefined reply queue to prevent race conditions. It randomly
   * selects a greeting from a predefined list and sends it to the chat.
   */
  public void sendReply() {
    if (this.greetBackIn == 0) {
      String[] replies = {"Hello", "Hi", "Hey", "Greetings", "Howdy"};
      try {
        Thread.sleep((long) (Math.random() * 500 + 250)); // simulate some delay
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      ChatMessage message = new ChatMessage(this.player.getName(),
          replies[(int) (Math.random() * replies.length)]);
      this.gameState.getChat().writeMessage(message);
      this.commitAction();
    }
    this.greetBackIn--;
  }

  /**
   * Initializes the reply queue for greeting responses.
   * <p>
   * This method sets the initial value for the greetBackIn (number of "greeting"-messages before
   * replying) variable based on the player's session number. It ensures that the AI player responds
   * to greetings in a turn based manner.
   */
  private void initializeReplyQueue() {
    this.greetBackIn = this.player.getSessionPlayerNumber();
    for (int i = 0; i < this.gameState.getPlayers().size(); i++) {
      AbstractPlayer p = this.gameState.getPlayers().get(i);
      if (p instanceof HumanPlayer
          && p.getSessionPlayerNumber() < this.player.getSessionPlayerNumber()) {
        this.greetBackIn -= 1;
      }
    }
  }

  /**
   * Sends a message to leave the current game session.
   * <p>
   * This method is called when the local player decides to leave the game. It sends a
   * {@link LeaveSessionPackage} to the server to notify about the leaving.
   */
  public void leaveGameSession() {
    if (this.inSession) {
      LeaveSessionPackage leaveSessionPackage = new LeaveSessionPackage(this.getPlayer(),
          this.getSessionId());
      this.gameClientEndpoint.sendMessage(leaveSessionPackage);
      this.inSession = false;
    }
  }

  /**
   * Handles an incoming leave notification from another player or the local player.
   * <p>
   * This method is triggered when any player (including the local one) leaves the session. If a
   * non-local player leaves while the game is running or the host exits, it cancels the game. Does
   * not change the scene if the local player initiated the leave, as UI changes are already
   * handled. The websocket connection is still present. The players only do not belong to the game
   * session anymore.
   *
   * @param leaveSessionPackage the leave notification package received from the server
   */
  public void handleLeave(LeaveSessionPackage leaveSessionPackage) {
    if (leaveSessionPackage.getPlayer() != this.getPlayer()) {
      if (this.gameState.isRunning()
          || leaveSessionPackage.getPlayer().getSessionPlayerNumber() == 0) {
        this.lobbyController.handlePlayerExit();
      }
    }
  }

  /**
   * Checks if any player in the game has achieved the victory condition. A player wins if their
   * prestige points are equal to or greater than 15.
   *
   * @return an {@code Optional} containing the winning {@code AbstractPlayer}, or
   * {@code Optional.empty()} if no player has won yet.
   */
  public Optional<AbstractPlayer> checkWin() {
    for (AbstractPlayer player : this.gameState.getPlayers()) {
      if (player.getPrestige() >= 15) {
        return Optional.of(player);
      }
    }
    return Optional.empty();
  }

  /**
   * Sends the player's action to the server.
   * <p>
   * This method is called after a player performs an action (e.g., taking stones, reserving cards,
   * etc.). It creates a {@link PlayerActionPackage} and sends it to the server to update the game
   * state.
   */
  public void commitAction() {
    // we need this check because the ai client can make multiple move in one turn
    // even after the partial turn finished the game
    Optional<AbstractPlayer> winner = this.checkWin();
    if (!this.gameOver && winner.isPresent()) {
      this.gameOver = true;
      PlayerActionPackage playerActionPackage = new PlayerActionPackage(this.player, this.sessionId,
          this.gameState);
      this.gameClientEndpoint.sendMessage(playerActionPackage);
    }
    if (!this.gameOver) {
      PlayerActionPackage playerActionPackage = new PlayerActionPackage(this.player, this.sessionId,
          this.gameState);
      this.gameClientEndpoint.sendMessage(playerActionPackage);
    }
  }

  public boolean isMyTurn() {
    return this.gameState.getPlayersTurn() == this.player.getSessionPlayerNumber();
  }

  public AiPlayer getPlayer() {
    return this.player;
  }

  public GameState getGameState() {
    return this.gameState;
  }

  public void setLobbyController(LobbyController lobbyController) {
    this.lobbyController = lobbyController;
  }

  public void setJoinController(JoinController joinController) {
    this.joinController = joinController;
  }

  public int getSessionId() {
    return this.sessionId;
  }
}
