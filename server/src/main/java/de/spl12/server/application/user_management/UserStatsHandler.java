package de.spl12.server.application.user_management;

import de.spl12.server.technical.user_management.UserStatsRepository;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles user statistics operations such as retrieving aggregated statistics and inserting or
 * deleting game result entries. Acts as a bridge between the application layer and the
 * UserStatsRepository.
 *
 * <p>Used by the REST API to manage and access user gameplay statistics.
 *
 * @author lmelodia
 */
public class UserStatsHandler {

  private static final Logger LOG = Logger.getLogger(UserStatsHandler.class.getName());

  private final UserStatsRepository statsRepository;

  /**
   * Constructs the handler using a specific stats repository instance.
   *
   * @param statsRepository the repository used to manage statistics
   */
  public UserStatsHandler(UserStatsRepository statsRepository) {
    this.statsRepository = statsRepository;
  }

  /** Constructs the handler with a default repository instance. */
  public UserStatsHandler() {
    this.statsRepository = new UserStatsRepository();
  }

  /** Clears all stored user statistics. Called once when the server starts. */
  public void handleClearUserStatsRepository() {
    statsRepository.clearUserStatsRepository();
  }

  /**
   * Aggregates statistics for a given user, including:
   *
   * <ul>
   *   <li>Total games played
   *   <li>Total games won
   *   <li>Favorite token color (based on usage)
   * </ul>
   *
   * @param userId the ID of the user
   * @return a map of statistics, or an empty map if aggregation fails
   */
  public Map<String, Object> handleGetAggregatedStatsForUser(int userId) {
    try {
      return statsRepository.getAggregatedStats(userId);
    } catch (Exception e) {
      LOG.log(Level.SEVERE, "Error aggregating stats for user ID: " + userId, e);
      return Collections.emptyMap();
    }
  }

  /**
   * Inserts a full game result into the statistics repository.
   *
   * @param playerIds array of player IDs
   * @param tokensGreens green tokens used per player
   * @param tokensBlue blue tokens used per player
   * @param tokensRed red tokens used per player
   * @param tokensBlack black tokens used per player
   * @param tokensWhite white tokens used per player
   * @param tokensGold gold tokens used per player
   * @param cardsBought number of cards bought per player
   * @param placement placement per player (1 = win)
   * @return true if insertion was successful
   */
  public boolean handleInsertGameResult(
      int[] playerIds,
      int[] tokensGreens,
      int[] tokensBlue,
      int[] tokensRed,
      int[] tokensBlack,
      int[] tokensWhite,
      int[] tokensGold,
      int[] cardsBought,
      int[] placement) {
    return statsRepository.insertGameResult(
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
   * Deletes all game results for a specific user.
   *
   * @param userId the ID of the user whose entries should be deleted
   * @return true if any rows were deleted; false otherwise
   */
  public boolean handleDeleteAllEntriesForUser(int userId) {
    return statsRepository.deleteGameResultsByUserId(userId);
  }
}
