package de.spl12.server.application.game_logic;

import de.spl12.domain.Exceptions.SessionHandlingExceptions.SessionAlreadyFullException;
import de.spl12.domain.Exceptions.SessionHandlingExceptions.SessionAlreadyStartedException;
import de.spl12.domain.Exceptions.SessionHandlingExceptions.SessionNotFoundException;
import de.spl12.domain.Exceptions.SessionHandlingExceptions.SinglePlayerSessionException;
import de.spl12.domain.GameState;
import de.spl12.domain.messages.AbstractPackage;
import de.spl12.domain.messages.CreateSessionPackage;
import de.spl12.domain.messages.JoinSessionPackage;
import de.spl12.domain.messages.LeaveSessionPackage;
import de.spl12.domain.messages.PlayerActionPackage;
import de.spl12.domain.messages.GameOverPackage;
import de.spl12.domain.messages.PackageDecoder;
import de.spl12.domain.messages.PackageEncoder;
import jakarta.websocket.server.ServerEndpoint;
import jakarta.websocket.OnOpen;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.Session;
import jakarta.websocket.CloseReason;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.logging.Logger;

/**
 * WebSocket server endpoint that manages the game sessions and player actions.
 *
 * <p>This endpoint is responsible for handling incoming messages from clients, creating and
 * joining
 * sessions, updating game states, and broadcasting updates to all clients connected to a game
 * session.
 *
 * <p>Encodes and decodes custom packages using {@link PackageEncoder} and {@link PackageDecoder}.
 *
 * <p>Endpoint path: {@code /game}
 *
 * <p>Thread-safety is ensured via synchronized blocks around shared session resources.
 *
 * @author ennauman
 */
@ServerEndpoint(value = "/game", decoders = PackageDecoder.class, encoders = PackageEncoder.class)
public class GameServerEndpoint {

  /**
   * Logger for server-side events and diagnostics.
   */
  private static final Logger LOGGER = Logger.getLogger(GameServerEndpoint.class.getName());

  /**
   * A synchronized map of session IDs to their corresponding client sessions.
   */
  private static final Map<Integer, Map<String, Session>> SESSIONS =
      Collections.synchronizedMap(new HashMap<>());

  private static final String SERVER_IP = getLocalIpAddress();

  /**
   * Called when a new WebSocket connection is opened.
   *
   * @param session the newly opened session
   * @throws IOException if an I/O error occurs
   */
  @OnOpen
  public void onOpen(Session session) throws IOException {
    LOGGER.info("Player joined");
  }

  /**
   * Called when a WebSocket connection is closed.
   *
   * @param session the session that was closed
   * @throws IOException if an I/O error occurs
   */
  @OnClose
  public void onClose(Session session, CloseReason closeReason) throws IOException {
    LOGGER.info("Connection with the server was closed");
  }

  /**
   * Handles incoming WebSocket messages from clients. Processes different types of packages: -
   * {@link CreateSessionPackage}: Starts a new game session. - {@link JoinSessionPackage}: Adds a
   * player to an existing session. - {@link LeaveSessionPackage}: Notifies all players and closes
   * the game session. - {@link PlayerActionPackage}: Updates game state and broadcasts changes. -
   * {@link GameOverPackage}: Sent if a game is over.
   *
   * @param msgPackage the incoming message package
   * @param session    the session from which the message was received
   * @throws IOException if message handling or broadcasting fails
   */
  @OnMessage
  public void onMessage(AbstractPackage msgPackage, Session session) throws IOException {
    LOGGER.info("received: " + msgPackage.getClass());
    switch (msgPackage) {
      case CreateSessionPackage msg -> {
        // handle the creation of a new game session on the server and add the additional session
        // (connection between server and client)
        // to our HashMap via a new Set of Sessions tied to the sessionId
        GameSession newGameSession =
            GameSessionManager.getInstance().handleCreateSession(msg.isMultiplayer());
        int sessionId = newGameSession.getSessionId();
        LOGGER.info("New session created under the Id: " + sessionId);
        // after the game session was created on the server we inform the client and send him the
        // sessionId
        msg.setCreatedSessionId(sessionId);
        msg.setServerIP(SERVER_IP);
        session.getAsyncRemote().sendObject(msg);
        LOGGER.info("Messaged the client that created the session with the SessionID" + sessionId);
      }
      case JoinSessionPackage msg -> {
        // handle the join on the server and add the additional session (connection between server and
        // client) to our HashMap
        GameSession gameSession = null;
        try {
          gameSession =
              GameSessionManager.getInstance().handleJoin(msg.getPlayer(), msg.getSessionId());
          int sessionId = gameSession.getSessionId();
          synchronized (SESSIONS) {
            if (SESSIONS.containsKey(sessionId)) {
              SESSIONS.get(sessionId).put(msg.getPlayer().getName(), session);
            } else {
              // if the first player joins a session
              Map<String, Session> sessions = new HashMap<>();
              sessions.put(msg.getPlayer().getName(), session);
              SESSIONS.put(sessionId, sessions);
            }
            LOGGER.info("Player joined session created under the Id: " + sessionId);
            JoinSessionPackage joinSessionPackage =
                new JoinSessionPackage(
                    msg.getPlayer(), sessionId, JoinSessionPackage.JoinStatus.SUCCESS);
            joinSessionPackage.setServerIP(SERVER_IP);
            session.getAsyncRemote().sendObject(joinSessionPackage);
            PlayerActionPackage msgWithNewGameState =
                new PlayerActionPackage(
                    msg.getPlayer(), msg.getSessionId(), gameSession.getGameState());
            for (Session s : SESSIONS.get(sessionId).values()) {
              s.getAsyncRemote().sendObject(msgWithNewGameState);
            }
          }
        } catch (SessionNotFoundException e) {
          msg.setJoinStatus(JoinSessionPackage.JoinStatus.SESSION_NOT_FOUND);
          session.getAsyncRemote().sendObject(msgPackage);
        } catch (SessionAlreadyFullException e) {
          msg.setJoinStatus(JoinSessionPackage.JoinStatus.SESSION_FULL);
          session.getAsyncRemote().sendObject(msgPackage);
        } catch (SessionAlreadyStartedException e) {
          msg.setJoinStatus(JoinSessionPackage.JoinStatus.SESSION_ALREADY_STARTED);
          session.getAsyncRemote().sendObject(msgPackage);
        } catch (SinglePlayerSessionException e) {
          msg.setJoinStatus(JoinSessionPackage.JoinStatus.SINGLE_PLAYER_SESSION);
          session.getAsyncRemote().sendObject(msgPackage);
        }

      }
      case LeaveSessionPackage msg -> {
        // handle the leave on the server and remove all session (connection between server and
        // client) from our HashMap
        GameSession gameSession =
            GameSessionManager.getInstance().handleLeave(msg.getPlayer(), msg.getSessionId());
        int sessionId = gameSession.getSessionId();
        // if the player leaving is the Host the session terminates too even if in Lobby
        if (gameSession.getGameState().isRunning()
            || gameSession.getPlayerCount() == 0
            || msg.getPlayer().getSessionPlayerNumber() == 0) {
          synchronized (SESSIONS) {
            for (Session s : SESSIONS.get(sessionId).values()) {
              s.getAsyncRemote().sendObject(msg);
            }
            SESSIONS.remove(sessionId);
          }
          LOGGER.info("Close session created under the Id: " + sessionId);
        } else {
          // if the game is not running (players are in the lobby), the game will not be terminated,
          // the new lobby/ game state will be distributed
          PlayerActionPackage msgWithNewGameState =
              new PlayerActionPackage(
                  msg.getPlayer(), msg.getSessionId(), gameSession.getGameState());
          synchronized (SESSIONS) {
            SESSIONS.get(sessionId).remove(msg.getPlayer().getName());
            for (Session s : SESSIONS.get(sessionId).values()) {
              s.getAsyncRemote().sendObject(msgWithNewGameState);
            }
          }
        }
      }
      case PlayerActionPackage msg -> {
        int sessionId = msg.getSessionId();
        GameState gameState =
            GameSessionManager.getInstance()
                .handlePlayerAction(msg.getSessionId(), msg.getGameState());
        PlayerActionPackage msgWithNewGameState =
            new PlayerActionPackage(msg.getPlayer(), msg.getSessionId(), gameState);
        synchronized (SESSIONS) {
          for (Session s : SESSIONS.get(sessionId).values()) {
            s.getAsyncRemote().sendObject(msgWithNewGameState);
          }
        }
      }
      case GameOverPackage msg -> {
        // this package only gets sent by the host
        GameSessionManager.getInstance().handleGameOver(msg.getSessionId());
        LOGGER.info("Game " + msg.getSessionId() + " is over");
      }
      default -> {
      }
    }
  }

  /**
   * Called when a WebSocket error occurs.
   *
   * @param session   the session where the error occurred
   * @param throwable the exception or error that was thrown
   */
  @OnError
  public void onError(Session session, Throwable throwable) {
    LOGGER.info("Error: " + throwable.getMessage());
  }

  /**
   * Returns the local IP address of the server.
   *
   * <p>
   *
   * @return the local IP address as a string
   * @author luott
   */
  private static String getLocalIpAddress() {
    try {
      Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
      while (interfaces.hasMoreElements()) {
        NetworkInterface iface = interfaces.nextElement();
        if (!iface.isUp() || iface.isLoopback()) {
          continue;
        }
        Enumeration<InetAddress> addresses = iface.getInetAddresses();
        while (addresses.hasMoreElements()) {
          InetAddress addr = addresses.nextElement();
          if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
            return addr.getHostAddress();
          }
        }
      }
    } catch (SocketException e) {
      throw new RuntimeException(e);
    }
    return "localhost";
  }
}
