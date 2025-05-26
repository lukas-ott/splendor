package de.spl12.server.technical;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Handles database connections for the Splendor server. Uses SQLite as the database engine.
 *
 * @author luott
 */
public class DatabaseConnector {

  private static final Logger LOG = Logger.getLogger(DatabaseConnector.class.getName());
  private static volatile DatabaseConnector instance;

  private final String dbUri;
  private final Connection connection;

  private DatabaseConnector(String dbUri) {
    this.dbUri = dbUri;
    this.connection = connect();
  }

  private Connection connect() {
    try {
      Class.forName("org.sqlite.JDBC");
      Connection conn = DriverManager.getConnection(dbUri);
      LOG.info("Connected to database: " + dbUri);
      return conn;
    } catch (ClassNotFoundException | SQLException e) {
      LOG.log(Level.SEVERE, "Database connection error: " + e.getMessage(), e);
      throw new RuntimeException("Failed to connect to database", e);
    }
  }

  /**
   * Returns the singleton instance of DatabaseConnector. If the instance is not created yet, it
   * initializes it with the default database URI.
   *
   * @return the singleton instance of DatabaseConnector
   */
  public static DatabaseConnector getInstance() {
    String dbPath = new File("server/data/splendor.db").exists() ? new File(
        "server/data/splendor.db").getAbsolutePath()
        : new File("data/splendor.db").getAbsolutePath();
    return getInstance("jdbc:sqlite:" + dbPath);
  }

  /**
   * Returns the singleton instance of DatabaseConnector. If the instance is not created yet, it
   * initializes it with the provided database URI.
   *
   * @param dbUri the database URI
   * @return the singleton instance of DatabaseConnector
   */
  public static DatabaseConnector getInstance(String dbUri) {
    if (instance == null) {
      synchronized (DatabaseConnector.class) {
        if (instance == null) {
          instance = new DatabaseConnector(dbUri);
        }
      }
    }
    return instance;
  }

  /**
   * Returns a valid database connection. If the current connection is not valid, it attempts to
   * reconnect.
   *
   * @return a valid database connection
   */
  public Connection getConnection() {
    try {
      return connection.isValid(2) ? connection : connect();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to retrieve a valid database connection", e);
    }
  }

  public void close() {
    try {
      if (connection != null && !connection.isClosed()) {
        connection.close();
        LOG.info("Database connection closed.");
        instance = null;
      }
    } catch (SQLException e) {
      LOG.log(Level.SEVERE, "Error closing database connection", e);
    }
  }
}
