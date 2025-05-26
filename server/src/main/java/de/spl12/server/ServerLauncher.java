package de.spl12.server;

import de.spl12.server.application.game_logic.GameServerEndpoint;
import de.spl12.server.application.user_management.UserStatsHandler;
import de.spl12.server.technical.DatabaseConnector;
import jakarta.websocket.server.ServerContainer;
import org.apache.tomcat.websocket.server.WsSci;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URI;
import java.util.Enumeration;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class to launch the server application.
 * <p>
 * This class initializes the Tomcat server, sets up the WebSocket and REST API endpoints, and
 * starts the server to listen for incoming connections.
 * <p>
 * It also handles database connection initialization and warm-up requests for the REST API.
 *
 * @author luott and ennauman
 */
public class ServerLauncher {

  private static final int PORT = 8081;
  private static final Logger LOG = Logger.getLogger(ServerLauncher.class.getName());

  public static void main(String[] args) {
    DatabaseConnector.getInstance();

    // Register shutdown hook to close the database connection on JVM exit
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      DatabaseConnector connector = DatabaseConnector.getInstance();
      connector.close();
    }));

    Tomcat tomcat = new Tomcat();
    tomcat.setPort(PORT);
    tomcat.setHostname("0.0.0.0");

    File baseDir = new File(System.getProperty("java.io.tmpdir"));
    LOG.info("Base directory for Tomcat: " + baseDir.getAbsolutePath());
    Context ctxRESTAPI = tomcat.addContext("", baseDir.getAbsolutePath());
    Context ctxWS = tomcat.addWebapp("/ws", baseDir.getAbsolutePath());

    ResourceConfig config = new ResourceConfig()
        .packages("de.spl12.server.application.user_management")
        .register(org.glassfish.jersey.jackson.JacksonFeature.class);
    LOG.info("Tomcat connector port: " + tomcat.getConnector().getPort());

    try {
      // Register WebSocket initializer
      ctxWS.addServletContainerInitializer(new WsSci(), Set.of());

      // Start Tomcat
      tomcat.start();

      // Register Jersey servlet
      ServletContainer jerseyServlet = new ServletContainer(config);
      Tomcat.addServlet(ctxRESTAPI, "jersey", jerseyServlet).setLoadOnStartup(1);
      ctxRESTAPI.addServletMappingDecoded("/api/*", "jersey");
      ServerContainer serverContainer = (ServerContainer) ctxWS.getServletContext()
          .getAttribute("jakarta.websocket.server.ServerContainer");
      serverContainer.setDefaultMaxTextMessageBufferSize(65536);
      serverContainer.setDefaultMaxBinaryMessageBufferSize(65536);
      serverContainer.addEndpoint(GameServerEndpoint.class);
      // Warm-up request to trigger REST/Jackson initialization
      warmUpRestAPI();

      String localIp = getLocalIpAddress();
      LOG.info("Local IP address: " + localIp);

      UserStatsHandler userStatsHandler = new UserStatsHandler();
      userStatsHandler.handleClearUserStatsRepository();

      tomcat.getServer().await();

    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Error starting Tomcat", e);
    }
  }

  /**
   * Sends a warm-up request to the REST API to trigger initialization of the Jersey and Jackson
   * components.
   * <p>
   * This method attempts to send a GET request to the REST API endpoint multiple times until it
   * receives a successful response or exhausts the retry limit.
   */
  private static void warmUpRestAPI() {
    int retries = 10;
    while (retries-- > 0) {
      try {
        URL url = new URI("http://localhost:" + PORT + "/api/test").toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(1000);
        conn.setReadTimeout(1000);

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
          try (var reader = new java.io.BufferedReader(
              new java.io.InputStreamReader(conn.getInputStream()))) {
            while (reader.readLine() != null) {
              // Read response to trigger deserialization
            }
          }
          LOG.info("REST API warm-up successful.");
          break;
        } else {
          LOG.warning("REST API warm-up got non-200 response: " + responseCode);
        }
      } catch (Exception e) {
        LOG.warning("REST API warm-up attempt failed: " + e.getMessage());
      }

      try {
        Thread.sleep(300);
      } catch (InterruptedException ignored) {
      }
    }
  }


  /**
   * Returns the local IP address of the server.
   * <p>
   *
   * @return the local IP address as a string
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
