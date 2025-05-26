package de.spl12.ai_client.application;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;
import de.spl12.ai_client.utils.ConstantsManager;
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
 * WebSocket client endpoint that handles communication between the AI client and the game server.
 *
 * <p>This class manages the WebSocket connection lifecycle and interprets incoming message packages
 * to:
 *
 * <ul>
 *   <li>Join or create game sessions
 *   <li>Process join results and handle errors
 *   <li>Handle session exits and player actions
 * </ul>
 *
 * The client connects to a server hosted at: {@code ws://<host>:8081/ws/game}.
 *
 * <p>It uses Jakarta WebSocket API with custom encoders and decoders for game message packages.
 *
 * @see PackageDecoder
 * @see PackageEncoder
 * @see AbstractPackage
 * @see GameController
 * @author ennauman
 */
@ClientEndpoint(decoders = PackageDecoder.class, encoders = PackageEncoder.class)
public class GameClientEndpoint {

  /** Logger instance for logging WebSocket client activity. */
  private static final Logger LOGGER = Logger.getLogger(GameClientEndpoint.class.getName());

  /** The WebSocket session used to send and receive messages. */
  private final Session session;

  /**
   * Constructs a new {@code GameClientEndpoint} and connects it to the WebSocket game server.
   *
   * <p>If the connection fails, a {@link RuntimeException} is thrown.
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
   * Called when the WebSocket connection is successfully opened.
   *
   * @param session the opened WebSocket session
   * @throws IOException if an I/O error occurs
   */
  @OnOpen
  public void onOpen(Session session) throws IOException {
    LOGGER.info("WebSocket connection opened.");
  }

  /**
   * Called when the WebSocket connection is closed.
   *
   * @param closeReason the reason the connection was closed
   * @param session the session that was closed
   * @throws IOException if an I/O error occurs
   */
  @OnClose
  public void onClose(CloseReason closeReason, Session session) throws IOException {
    LOGGER.info("Connection with the server was closed - Reason: " + closeReason.getReasonPhrase());
  }

  /**
   * Called when a message (package) is received from the server.
   *
   * <p>The message is interpreted based on its type and dispatched to the {@link GameController}.
   * Handles:
   *
   * <ul>
   *   <li>Session creation and join responses
   *   <li>Session exit notifications
   *   <li>Player action/game state updates
   * </ul>
   *
   * @param msgPackage the received message package
   * @param session the active WebSocket session
   * @throws Exception if any error occurs during message handling
   */
  @OnMessage
  public void onMessage(AbstractPackage msgPackage, Session session) throws Exception {
    LOGGER.info("Received: " + msgPackage.getClass());

    if (msgPackage instanceof CreateSessionPackage msg) {
      int sessionId = msg.getCreatedSessionId();
      GameController.getInstance().joinGameSession(sessionId);
      LOGGER.info("Created new session with ID: " + sessionId);

    } else if (msgPackage instanceof JoinSessionPackage msg) {
      switch (msg.getJoinStatus()) {
        case JoinSessionPackage.JoinStatus.SUCCESS -> {
          GameController.getInstance().joinSuccessful(msg.getPlayer(), msg.getSessionId());
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
        case JoinSessionPackage.JoinStatus.REQUESTED -> {
          GameController.getInstance()
              .joinUnsuccessful("Something went wrong while joining the session");
          LOGGER.info("Something went wrong while joining the session");
        }
      }

    } else if (msgPackage instanceof LeaveSessionPackage msg) {
      GameController.getInstance().handleLeave(msg);
      LOGGER.info("Received leave session package — game was canceled");

    } else if (msgPackage instanceof PlayerActionPackage msg) {
      GameController.getInstance().updateGameState(msg.getGameState());
      LOGGER.info("Received updated game state");
    }
  }

  /**
   * Sends a message (package) to the server asynchronously.
   *
   * @param msgPackage the package to be sent to the server
   */
  public void sendMessage(AbstractPackage msgPackage) {
    LOGGER.info("Sending message — session open: " + this.session.isOpen());
    this.session.getAsyncRemote().sendObject(msgPackage);
    LOGGER.info("Sent message of type: " + msgPackage.getClass().getName());
  }
}
