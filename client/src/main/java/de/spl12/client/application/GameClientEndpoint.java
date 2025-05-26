package de.spl12.client.application;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;
import de.spl12.client.utils.ConstantsManager;
import de.spl12.domain.messages.AbstractPackage;
import de.spl12.domain.messages.CreateSessionPackage;
import de.spl12.domain.messages.JoinSessionPackage;
import de.spl12.domain.messages.LeaveSessionPackage;
import de.spl12.domain.messages.PlayerActionPackage;
import de.spl12.domain.messages.PackageDecoder;
import de.spl12.domain.messages.PackageEncoder;
import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.OnOpen;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnClose;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.WebSocketContainer;
import jakarta.websocket.Session;
import jakarta.websocket.CloseReason;

/**
 * WebSocket client endpoint that handles communication between the client/player and the game
 * server.
 *
 * <p>It manages WebSocket lifecycle events (connect, disconnect, message) and interprets incoming
 * packages to update game state or handle session termination.
 *
 * <p>This client connects to a WebSocket server hosted at ws://localhost:8081/ws/game.
 *
 * @author ennauman
 */
@ClientEndpoint(decoders = PackageDecoder.class, encoders = PackageEncoder.class)
public class GameClientEndpoint {

  /** Logger instance for logging client events and errors. */
  private static final Logger LOGGER = Logger.getLogger(GameClientEndpoint.class.getName());

  /** The WebSocket session representing the connection to the server. */
  private final Session session;

  /**
   * Initializes and connects the WebSocket client to the game server. Throws a {@link
   * RuntimeException} if the connection fails.
   */
  public GameClientEndpoint() {
    try {
      WebSocketContainer container = ContainerProvider.getWebSocketContainer();
      this.session =
          container.connectToServer(
              this, new URI("ws://" + ConstantsManager.HOST + ":8081/ws/game"));
      LOGGER.info("Successfully connected to server");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Called when a WebSocket connection is established.
   *
   * @param session the session that was opened
   * @throws IOException if an I/O error occurs
   */
  @OnOpen
  public void onOpen(Session session) throws IOException {
    LOGGER.info("You connected successful");
  }

  /**
   * Called when the WebSocket connection is closed.
   *
   * @param session the session that was closed
   * @throws IOException if an I/O error occurs
   */
  @OnClose
  public void onClose(Session session, CloseReason closeReason) throws IOException {
    LOGGER.info("Connection with the server was closed - Reason: " + closeReason.getReasonPhrase());
  }

  /**
   * Called when a message (package) is received from the server. Handles different types of
   * packages such as player actions and leave session signals.
   *
   * @param msgPackage the received message package
   * @param session the current WebSocket session
   * @throws Exception if any exception occurs while processing the message
   */
  @OnMessage
  public void onMessage(AbstractPackage msgPackage, Session session) throws Exception {
    LOGGER.info("received: " + msgPackage.getClass().toString());
    if (msgPackage instanceof CreateSessionPackage msg) {
      int sessionId = msg.getCreatedSessionId();
      GameController.getInstance().joinGameSession(sessionId);
      LOGGER.info("Created new session under the id: " + sessionId);
    } else if (msgPackage instanceof JoinSessionPackage msg) {
      switch (msg.getJoinStatus()) {
        case JoinSessionPackage.JoinStatus.SUCCESS -> {
          GameController.getInstance()
              .joinSuccessful(msg.getPlayer(), msg.getSessionId(), msg.getServerIP());
          LOGGER.info(
              "Joined session "
                  + msg.getSessionId()
                  + " with playerSessionId "
                  + GameController.getInstance().getPlayer().getSessionPlayerNumber());
        }
        case JoinSessionPackage.JoinStatus.SESSION_FULL -> {
          GameController.getInstance().joinUnsuccessful("This session is already full");
          LOGGER.info("This session is already full");
        }
        case JoinSessionPackage.JoinStatus.SESSION_ALREADY_STARTED -> {
          GameController.getInstance().joinUnsuccessful("This session has already started");
          LOGGER.info("This session has already started");
        }
        case JoinSessionPackage.JoinStatus.SESSION_NOT_FOUND -> {
          GameController.getInstance().joinUnsuccessful("This session was not found");
          LOGGER.info("This session was not found");
        }
        case JoinSessionPackage.JoinStatus.SINGLE_PLAYER_SESSION -> {
          GameController.getInstance()
              .joinUnsuccessful("This session is a a single player session");
        }
        case JoinSessionPackage.JoinStatus.REQUESTED -> {
          GameController.getInstance()
              .joinUnsuccessful("Something went wrong while joining session");
          LOGGER.info("Something went wrong while joining session");
        }
      }
    } else if (msgPackage instanceof LeaveSessionPackage msg) {
      GameController.getInstance().handleLeave(msg);
      LOGGER.info("Somebody left");
    } else if (msgPackage instanceof PlayerActionPackage msg) {
      GameController.getInstance().updateGameState(msg.getGameState());
    }
  }

  /**
   * Sends a message (package) to the server asynchronously.
   *
   * @param msgPackage the package to send
   */
  public void sendMessage(AbstractPackage msgPackage) {
    LOGGER.info(String.valueOf(this.session.isOpen()));
    this.session.getAsyncRemote().sendObject(msgPackage);
    LOGGER.info("Sent message of type " + msgPackage.getClass().getName());
  }
}
