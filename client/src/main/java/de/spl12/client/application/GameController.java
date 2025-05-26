package de.spl12.client.application;

import de.spl12.client.utils.ConstantsManager;
import de.spl12.domain.AbstractPlayer;
import de.spl12.domain.ChatMessage;
import de.spl12.domain.Exceptions.CantAffordItemException;
import de.spl12.domain.Exceptions.DepletedResourceException;
import de.spl12.domain.Exceptions.IllegalMoveCombinationException;
import de.spl12.domain.Exceptions.ActionNotPossibleException;
import de.spl12.domain.Exceptions.NotYourTurnException;
import de.spl12.domain.GameState;
import de.spl12.domain.HumanPlayer;
import de.spl12.domain.User;
import de.spl12.domain.Noble;
import de.spl12.domain.Card;
import de.spl12.domain.StoneType;
import de.spl12.domain.messages.CreateSessionPackage;
import de.spl12.domain.messages.GameOverPackage;
import de.spl12.domain.messages.JoinSessionPackage;
import de.spl12.domain.messages.PlayerActionPackage;
import de.spl12.domain.messages.LeaveSessionPackage;
import de.spl12.domain.moves.AbstractMove;
import de.spl12.domain.moves.BuyMove;
import de.spl12.domain.moves.ReserveMove;
import de.spl12.domain.moves.TakeMove;

import java.util.ArrayList;
import java.util.Map;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import javafx.application.Platform;

/**
 * Handles the actions, a user performs during a game, as well as incoming changes in the game state
 * via websockets. Calls the {@link GameScreenController} to update the UI each time.
 *
 * @author leon.kuersch
 */
public class GameController {

  private static final Logger LOGGER = Logger.getLogger(GameController.class.getName());

  private static GameController INSTANCE;
  private GameState gameState;
  private HumanPlayer player;
  private GameClientEndpoint gameClientEndpoint;
  private GameScreenController gameScreenController;
  private MultiplayerLobbyScreenController multiplayerLobbyScreenController;
  private MultiplayerLaunchScreenController multiplayerLaunchScreenController;
  private HomeScreenController homeScreenController;
  private int sessionId;
  private AbstractMove currentMove = null;
  private boolean selectedNoble = false;
  private Thread t;
  private boolean inSession;
  private String serverIP;

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

  public void resetGameState() {
    this.gameState.reset();
    this.commitAction();
  }

  /**
   * Creates a new game session.
   *
   * @param isMultiplayer
   */
  public void createGameSession(boolean isMultiplayer) {
    CreateSessionPackage createSessionPackage = new CreateSessionPackage(isMultiplayer);
    this.gameClientEndpoint.sendMessage(createSessionPackage);
  }

  /**
   * Sends a request to join a game session.
   *
   * <p>This method is used by the player to request joining a session. If no response is received
   * from the server within 10 seconds, it is treated as a failed attempt.
   *
   * @param sessionId the ID of the session to join
   */
  public void joinGameSession(int sessionId) {
    de.spl12.client.utils.UserData userData =
        de.spl12.client.utils.User.getInstance().getUserData();
    User user =
        new User(
            userData.getId(), userData.getUsername(), userData.getPassword(), userData.getAge());
    HumanPlayer humanPlayer = new HumanPlayer(-1, user);
    JoinSessionPackage joinSessionPackage =
        new JoinSessionPackage(humanPlayer, sessionId, JoinSessionPackage.JoinStatus.REQUESTED);
    this.gameClientEndpoint.sendMessage(joinSessionPackage);
    t =
        new Thread(
            () -> {
              try {
                Thread.sleep(10 * 1000);
                joinUnsuccessful("Received no response from the server - server can be down");
              } catch (InterruptedException e) {
                LOGGER.info(
                    "Received a response from the server about the joining request in time");
              }
            });
    t.start();
  }

  /**
   * Handles a successful join to a game session.
   *
   * <p>This method is called when the server approves the join request. It sets the human player
   * and session ID, and notifies the UI controller.
   *
   * @param player thehHuman player that successfully joined
   * @param sessionId the session ID that was joined
   */
  public void joinSuccessful(AbstractPlayer player, int sessionId, String serverIP) {
    t.interrupt();
    this.player = (HumanPlayer) player;
    this.sessionId = sessionId;
    this.serverIP = serverIP;
    if (this.multiplayerLaunchScreenController != null) {
      this.multiplayerLaunchScreenController.handleJoinSuccessful();
    } else if (this.homeScreenController != null) {
      this.homeScreenController.handleJoinSuccessful();
    }
    this.inSession = true;
    LOGGER.info("Join Successful");
  }

  /**
   * Handles a failed attempt to join a game session.
   *
   * <p>This method is called if the join request times out or is rejected by the server.
   *
   * @param errorMsg the error message explaining the failure reason
   */
  public void joinUnsuccessful(String errorMsg) {
    t.interrupt();
    if (this.multiplayerLaunchScreenController != null) {
      this.multiplayerLaunchScreenController.handleJoinUnsuccessful(errorMsg);
    } else if (this.homeScreenController != null) {
      this.homeScreenController.handleJoinUnsuccessful(errorMsg);
    }
    LOGGER.info("Join Unsuccessful");
  }

  public void startGame() {
    this.gameState.startGame();
    this.commitAction();
  }

  public void updateGameState(GameState gameState) {
    boolean wasRunning = this.gameState != null && this.gameState.isRunning();
    this.gameState = gameState;
    // as the session player number can change due to someone with a lower number leaving we get our
    // player through the distinct name
    for (AbstractPlayer p : this.gameState.getPlayers()) {
      // human players get identified with their user id as the username can be a duplicate of
      // another
      if (p instanceof HumanPlayer humanPlayer) {
        if (humanPlayer.getUser().getId() == this.getPlayer().getUser().getId()) {
          this.player = humanPlayer;
        }
      }
    }
    if (this.gameState.isRunning()) {
      if (!wasRunning) {
        this.multiplayerLobbyScreenController.gameStarted();
      }
      if (this.gameScreenController != null) {
        this.gameScreenController.updateUI();
        this.gameScreenController.set_disable_game_buttons(false);
        Optional<AbstractPlayer> winner = this.checkWin();
        if (winner.isPresent()) {
          if (this.getPlayer().getSessionPlayerNumber() == 0) {
            // only the host sends this package
            this.gameClientEndpoint.sendMessage(
                new GameOverPackage(this.getPlayer(), this.sessionId));
          }
          // this has to happen after the database was updated through the GameOverPackage being
          // sent
          this.gameScreenController.switchToLeaderboard();
          LOGGER.info("Winner is " + winner.get().getName());
          return;
        }
      }
    }
    if (this.multiplayerLobbyScreenController != null) {
      this.multiplayerLobbyScreenController.updateUI();
    }
    if (this.isMyTurn()) {
      if (this.checkMoveComplete()) {
        if (this.currentMove != null && this.currentMove.getRequiredReturn() > 0) {
          LOGGER.info("Please return " + this.currentMove.getRequiredReturn() + " stone");
          Platform.runLater(
              () -> {
                this.gameScreenController.alert_return_gems(this.currentMove.getRequiredReturn());
              });
        } else if (this.checkIfNobleToSelect()) {
          LOGGER.info("Please select a noble");
          Platform.runLater(
              () -> {
                this.gameScreenController.alert_select_noble(
                    this.player.getNobleSelection(this.gameState.getNobleDeck()));
              });
        } else {
          this.currentMove = null;
          this.selectedNoble = false;
          this.gameState.nextTurn();
          this.commitAction();
        }
      }
    }
  }

  /**
   * Checks if the player has to select a noble to claim.
   *
   * @return
   */
  private boolean checkIfNobleToSelect() {
    if (this.selectedNoble) {
      return false;
    }
    List<Noble> obtainable = new ArrayList<>();
    for (Noble noble : this.gameState.getNobleDeck()) {
      if (this.player.checkNobleVisit(noble)) {
        obtainable.add(noble);
      }
    }
    if (obtainable.size() == 1) {
      this.player.obtainNoble(obtainable.getFirst());
      this.gameState.removeNoble(obtainable.getFirst());
    }
    return obtainable.size() >= 2;
  }

  /**
   * Checks whether the current move performed by the player has been completed based on the game's
   * rules and conditions.
   *
   * <p>The method evaluates the type of move (e.g., TakeMove, BuyMove, or ReserveMove) and
   * validates the state of the move against the game's stone pool or available resource supply.
   *
   * <p>For TakeMove: - Checks if all available stones are taken. - Verifies the number and type of
   * stones taken to ensure compliance with rules. - Ensures that the move respects distinct stone
   * type requirements.
   *
   * <p>For BuyMove or ReserveMove: - These moves are always considered complete and valid.
   *
   * <p>If no move is currently active, or the conditions for move completion are not satisfied, it
   * returns false. If all conditions are met, the move is considered complete, and the method
   * returns true.
   *
   * @return true if the current move is complete and valid according to game rules, false
   *     otherwise.
   */
  private boolean checkMoveComplete() {
    if (this.currentMove == null) {
      return false;
    }
    if (this.currentMove instanceof TakeMove) {
      if (this.gameState.totalStonesAvailable() == 0) {
        return true;
      }
      int distinctStonesTaken = 0;
      for (StoneType stone : ((TakeMove) this.currentMove).getTokens().keySet()) {
        if (((TakeMove) this.currentMove).getTokens().get(stone) >= 2) {
          return true;
        }
        if (((TakeMove) this.currentMove).getTokens().get(stone) >= 1) {
          distinctStonesTaken++;
        }
      }
      if (distinctStonesTaken >= 3) {
        return true;
      }
      for (StoneType stoneType : this.gameState.getStonePool().keySet()) {
        if (this.gameState.getStonePool().get(stoneType) >= 1 && stoneType != StoneType.GOLD) {
          if (((TakeMove) this.currentMove).getTokens().get(stoneType) == 0) {
            return false;
          } else if (((TakeMove) this.currentMove).getTokens().get(stoneType) == 1
              && distinctStonesTaken == 1
              && this.gameState.getStonePool().get(stoneType) >= 3) {
            return false;
          }
        }
      }
    } else if (this.currentMove instanceof BuyMove) {
      return true;
    } else if (this.currentMove instanceof ReserveMove) {
      return true;
    }
    return true;
  }

  /**
   * Commits the player's current action by packaging relevant details and sending them to the game
   * server for processing.
   *
   * <p>This method encapsulates the player's current state, session ID, and the game state within a
   * {@link PlayerActionPackage} and transmits it using the provided game client endpoint. The
   * server is expected to handle the message and update the game state accordingly.
   *
   * <p>The method does not return any value and assumes that the action has already been validated
   * before invoking this method.
   */
  public void commitAction() {
    if (this.gameScreenController != null) {
      this.gameScreenController.set_disable_game_buttons(true);
      this.gameScreenController.close_alert();
    }
    PlayerActionPackage playerActionPackage =
        new PlayerActionPackage(this.player, this.sessionId, this.gameState);
    this.gameClientEndpoint.sendMessage(playerActionPackage);
  }

  /**
   * Checks if the game has ended and the winner has been determined.
   *
   * @return
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
   * Sends a chat message to the server.
   *
   * @param text
   */
  public void sendChatMessage(String text) {
    ChatMessage message = new ChatMessage(this.player.getUser().getUsername(), text);
    this.gameState.getChat().writeMessage(message);
    this.commitAction();
  }

  /**
   * Checks if the current player is the one whose turn it is.
   *
   * @return
   */
  public boolean isMyTurn() {
    return this.gameState.getPlayersTurn() == this.player.getSessionPlayerNumber();
  }

  /**
   * Handles the action of the player picking up a stone of the specified type. This method
   * validates the move based on the game's rules, including turn order, move combinations, and
   * resource availability. Updates the game state and player inventory if the action is valid, or
   * throws an exception otherwise.
   *
   * @param stoneType the type of stone the player attempts to pick up
   * @throws NotYourTurnException if the action is attempted out of turn
   * @throws DepletedResourceException if the requested stone type is unavailable
   * @throws IllegalMoveCombinationException if the action violates move combination rules
   */
  public void handleStonePickup(StoneType stoneType)
      throws NotYourTurnException, DepletedResourceException, IllegalMoveCombinationException {
    if (!this.isMyTurn()) {
      throw new NotYourTurnException();
    }
    if (!(this.currentMove instanceof TakeMove || this.currentMove == null)) {
      throw new IllegalMoveCombinationException("You cant take a stone now.");
    }
    if (this.currentMove == null) {
      this.currentMove =
          new TakeMove(Collections.emptyMap(), Math.max(this.player.getTotalStones() - 10, 0));
    }
    if (((TakeMove) this.currentMove).getTokens().get(stoneType) >= 1
        && this.gameState.getStonePool().get(stoneType) <= 2) {
      throw new IllegalMoveCombinationException(
          "There have to be at least 4 stones of this type for you to be allowed to take 2.");
    }
    for (StoneType stone : ((TakeMove) this.currentMove).getTokens().keySet()) {
      if (((TakeMove) this.currentMove).getTokens().get(stone) >= 2) {
        throw new IllegalMoveCombinationException("Already chose two stones of one type.");
      }
    }
    int distinctStoneTypes = 0;
    for (StoneType stone : ((TakeMove) this.currentMove).getTokens().keySet()) {
      if (((TakeMove) this.currentMove).getTokens().get(stone) >= 1) {
        distinctStoneTypes++;
      }
    }
    if (distinctStoneTypes >= 3) {
      throw new IllegalMoveCombinationException("Already took 3 different stones");
    }
    if (distinctStoneTypes >= 2 && ((TakeMove) this.currentMove).getTokens().get(stoneType) >= 1) {
      throw new IllegalMoveCombinationException(
          "Cant take 2 of one color if you already picked one of a different color.");
    }
    LOGGER.info("Take ");
    this.gameState.takeStone(stoneType);
    this.player.takeStone(stoneType);
    ((TakeMove) this.currentMove).incrementStoneType(stoneType);
    this.currentMove.setRequiredReturn(Math.max(this.player.getTotalStones() - 10, 0));
    LOGGER.info("Took Stone: " + this.player.getStoneInventory().toString());
    this.commitAction();
  }

  /**
   * Handles the action of the player returning a stone of the specified type.
   *
   * @param stoneType
   * @throws NotYourTurnException
   * @throws DepletedResourceException
   */
  public void handleStoneReturn(StoneType stoneType)
      throws NotYourTurnException, DepletedResourceException {
    if (!this.isMyTurn()) {
      throw new NotYourTurnException();
    }
    if (this.checkMoveComplete()) {
      this.player.returnStone(stoneType);
      this.gameState.returnStone(stoneType);
      this.currentMove.setRequiredReturn(Math.max(this.player.getTotalStones() - 10, 0));
      this.commitAction();
    }
  }

  /**
   * Handles the action of the player claiming a noble.
   *
   * @param noble
   * @throws NotYourTurnException
   * @throws CantAffordItemException
   * @throws IllegalMoveCombinationException
   */
  public void handleNobleClaim(Noble noble)
      throws NotYourTurnException, CantAffordItemException, IllegalMoveCombinationException {
    if (!this.isMyTurn()) {
      throw new NotYourTurnException();
    }
    if (!this.checkMoveComplete()) {
      throw new IllegalMoveCombinationException(
          "You have to finish your turn before being visited by a noble.");
    }
    if (noble == null) {
      return;
    }
    if (!this.player.checkNobleVisit(noble)) {
      throw new CantAffordItemException(
          "You do not have enough stones to be visited by this noble.");
    }
    this.player.obtainNoble(noble);
    this.gameState.removeNoble(noble);
    this.selectedNoble = true;
    this.commitAction();
  }

  /**
   * Handles the action of the player purchasing a card.
   *
   * @param card
   * @throws NotYourTurnException
   * @throws IllegalMoveCombinationException
   */
  public void handleCardPurchase(Card card)
      throws NotYourTurnException, IllegalMoveCombinationException {
    if (!this.isMyTurn()) {
      throw new NotYourTurnException();
    }
    if (!(this.currentMove == null)) {
      throw new IllegalMoveCombinationException("You cant buy a card now.");
    }
    if (card == null) {
      return;
    }
    LOGGER.info("Buying card: " + card.toString());
    if (this.player.canAffordCard(card)) {
      Map<StoneType, Integer> payment = this.player.getPaymentForCard(card);
      this.player.buyCard(card);
      this.gameState.replaceCard(card);
      this.gameState.performPayment(payment);
      this.currentMove = new BuyMove(payment, card);
      this.commitAction();
    }
  }

  /**
   * Handles the action of purchasing a card that has been previously reserved.
   *
   * <p>This method validates the current game state and player's turn to ensure the purchase action
   * is legal. It checks for the player's ability to afford the reserved card and processes the
   * payment if the purchase is successful. If conditions are not met, appropriate exceptions are
   * thrown.
   *
   * @param card the reserved card the player intends to purchase
   * @throws NotYourTurnException if the action is attempted out of turn
   * @throws IllegalMoveCombinationException if the action violates game rules or timing
   */
  public void handleReservedCardPurchase(Card card)
      throws NotYourTurnException, IllegalMoveCombinationException {
    if (!this.isMyTurn()) {
      throw new NotYourTurnException();
    }
    if (!(this.currentMove == null)) {
      throw new IllegalMoveCombinationException("You cant buy a card now.");
    }
    if (card == null) {
      return;
    }
    LOGGER.info("Buying reserved card: " + card.toString());
    if (this.player.canAffordCard(card)) {
      Map<StoneType, Integer> payment = this.player.getPaymentForCard(card);
      if (this.player.buyReservedCard(card)) {
        this.gameState.performPayment(payment);
        this.currentMove = new BuyMove(payment, card);
        this.commitAction();
      }
    }
  }

  /**
   * Handles the action of reserving a card during a player's turn. This method validates the action
   * based on the current game rules, ensuring the player can reserve the card, has not exceeded the
   * reserved card limit, and it is their turn to play. Additionally, it manages the gold coin
   * allocation if available and updates the current game state accordingly.
   *
   * @param card the card to be reserved by the player
   * @throws ActionNotPossibleException if the player exceeds the reserved card limit
   * @throws NotYourTurnException if the action is attempted out of the player's turn
   * @throws IllegalMoveCombinationException if reserving the card violates game rules or existing
   *     move combinations
   */
  public void handleCardReservation(Card card) throws ActionNotPossibleException {
    if (!this.isMyTurn()) {
      throw new NotYourTurnException();
    }
    if (!(this.currentMove == null)) {
      throw new IllegalMoveCombinationException("You cant reserve a card now.");
    }
    if (card == null) {
      return;
    }
    LOGGER.info("Reserving Card: " + card.toString());
    if (this.player.reserveCard(card)) {
      this.gameState.replaceCard(card);
      boolean gotGoldCoin = false;
      if (this.gameState.getStonePool().get(StoneType.GOLD) >= 1) {
        this.player.takeStone(StoneType.GOLD);
        this.gameState.takeStone(StoneType.GOLD);
        gotGoldCoin = true;
      }
      this.currentMove =
          new ReserveMove(card, gotGoldCoin, Math.max(this.player.getTotalStones() - 10, 0));
      this.commitAction();
    } else {
      throw new ActionNotPossibleException("You cant have more than 3 reserved cards.");
    }
  }

  public HumanPlayer getPlayer() {
    return this.player;
  }

  public boolean isGameOver() {
    return false;
  }

  public GameState getGameState() {
    return this.gameState;
  }

  public void setGameState(GameState gameState) {
    this.gameState = gameState;
  }

  public void setGameScreenController(GameScreenController gameScreenController) {
    this.gameScreenController = gameScreenController;
  }

  public void setMultiplayerLobbyScreenController(
      MultiplayerLobbyScreenController multiplayerLobbyScreenController) {
    this.multiplayerLobbyScreenController = multiplayerLobbyScreenController;
  }

  public int getSessionId() {
    return this.sessionId;
  }

  public String getServerIP() {
    return this.serverIP;
  }

  public boolean isNobleSelected() {
    return this.selectedNoble;
  }

  public void setNobleSelected(boolean selectedNoble) {
    this.selectedNoble = selectedNoble;
  }

  /**
   * Sends a message to leave the current game session.
   *
   * <p>This method is called when the local player decides to leave the game. It sends a {@link
   * LeaveSessionPackage} to the server to notify about the leaving.
   */
  public void leaveGameSession() {
    if (this.inSession) {
      LeaveSessionPackage leaveSessionPackage =
          new LeaveSessionPackage(this.getPlayer(), this.getSessionId());
      this.gameClientEndpoint.sendMessage(leaveSessionPackage);
      this.inSession = false;
    }
  }

  /**
   * Handles an incoming leave notification from another player or the local player.
   *
   * <p>This method is triggered when any player (including the local one) leaves the session. If a
   * non-local player leaves while the game is running or the host exits, it cancels the game. Does
   * not change the scene if the local player initiated the leave, as UI changes are already
   * handled. The websocket connection is still present. The players only do not belong to the game
   * session anymore.
   *
   * @param leaveSessionPackage the leave notification package received from the server
   */
  public void handleLeave(LeaveSessionPackage leaveSessionPackage) {
    if (leaveSessionPackage.getPlayer() != this.getPlayer()) {
      if (this.gameState.isRunning()) {
        this.gameScreenController.handlePlayerQuit();
        // if the player leaving is the Host the session gets terminated even if in Lobby
      } else if (leaveSessionPackage.getPlayer().getSessionPlayerNumber() == 0) {
        this.multiplayerLobbyScreenController.handleHostExit();
      }
    }
  }

  public MultiplayerLaunchScreenController getMultiplayerLaunchScreenController() {
    return multiplayerLaunchScreenController;
  }

  public void setMultiplayerLaunchScreenController(
      MultiplayerLaunchScreenController multiplayerLaunchScreenController) {
    this.multiplayerLaunchScreenController = multiplayerLaunchScreenController;
  }

  public HomeScreenController getHomeScreenController() {
    return homeScreenController;
  }

  public void setHomeScreenController(HomeScreenController homeScreenController) {
    this.homeScreenController = homeScreenController;
  }
}
