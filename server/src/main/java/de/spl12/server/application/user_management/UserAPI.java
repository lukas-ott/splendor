package de.spl12.server.application.user_management;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import de.spl12.server.application.User;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * REST API for user management, handling authentication and user modifications.
 *
 * @author luott
 */
@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
public class UserAPI {

  private static final Logger LOGGER = Logger.getLogger(UserAPI.class.getName());
  private final UserHandler userHandler;
  private final UserStatsHandler userStatsHandler;

  // Standard constructor
  public UserAPI() {
    this.userHandler = new UserHandler();
    this.userStatsHandler = new UserStatsHandler();
  }

  // Constructor for dependency injection (test only)
  public UserAPI(UserHandler userHandler, UserStatsHandler userStatsHandler) {
    this.userHandler = userHandler;
    this.userStatsHandler = userStatsHandler;
  }

  /**
   * Handles user login.
   *
   * @param username The username of the user.
   * @param password The password of the user.
   * @return HTTP response containing user data if successful, otherwise UNAUTHORIZED.
   */
  @POST
  @Path("/login")
  public Response login(@FormParam("username") String username,
      @FormParam("password") String password) {
    Optional<User> user = userHandler.handleLogin(username, password);
    if (user.isPresent()) {
      return Response.ok(user.get()).build();
    }
    return Response.status(Response.Status.UNAUTHORIZED).build();
  }

  /**
   * Handles user registration.
   *
   * @param username The desired username.
   * @param password The desired password.
   * @return HTTP response containing user data if successful, otherwise BAD_REQUEST.
   */
  @POST
  @Path("/register")
  public Response register(@FormParam("username") String username,
      @FormParam("password") String password, @FormParam("age") int age) {
    Optional<User> user = userHandler.handleRegister(username, password, age);
    if (user.isPresent()) {
      return Response.ok(user.get()).build();
    }
    return Response.status(Response.Status.BAD_REQUEST).build();
  }

  /**
   * Handles the deletion of a user from the system after verifying the provided credentials.
   *
   * @param username The username of the user to be deleted.
   * @param password The password of the user used to authenticate the request.
   * @return HTTP response indicating the outcome of the operation: - OK if the user was
   * successfully deleted. - BAD_REQUEST if the username or password is missing. - UNAUTHORIZED if
   * the credentials are invalid. - INTERNAL_SERVER_ERROR if an unexpected error occurs.
   */
  @DELETE
  @Path("/delete")
  public Response delete(@QueryParam("username") String username,
      @QueryParam("password") String password) {
    LOGGER.info("Delete request received for user: " + username);

    if (username == null || username.isEmpty()) {
      LOGGER.warning("Delete failed: Username is null or empty");
      return Response.status(Response.Status.BAD_REQUEST).entity("Username is required").build();
    }

    if (password == null || password.isEmpty()) {
      LOGGER.warning("Delete failed: Password is null or empty");
      return Response.status(Response.Status.BAD_REQUEST).entity("Password is required").build();
    }

    try {
      Optional<User> user = userHandler.handleDelete(username, password);
      if (user.isPresent()) {
        LOGGER.info("User deleted successfully: " + username);
        return Response.ok().build();
      } else {
        LOGGER.warning("Delete failed: User authentication failed for: " + username);
        return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid username or password")
            .build();
      }
    } catch (Exception e) {
      LOGGER.severe("Delete failed: An unexpected error occurred: " + e.getMessage());
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity("An unexpected error occurred").build();
    }
  }

  /**
   * Handles password change requests.
   *
   * @param username    The username of the user.
   * @param password    The current password of the user.
   * @param newPassword The new password to be set.
   * @return HTTP response indicating success or failure.
   */
  @PUT
  @Path("/change-password")
  public Response changePassword(@FormParam("username") String username,
      @FormParam("password") String password,
      @FormParam("newPassword") String newPassword) {
    Optional<User> user = userHandler.handleUpdatePassword(username, password, newPassword);
    if (user.isPresent()) {
      return Response.ok().build();
    }
    return Response.status(Response.Status.UNAUTHORIZED).build();
  }

  /**
   * Handles username change requests.
   *
   * @param oldUsername The current username of the user.
   * @param newUsername The new username to be set.
   * @param password    The current password to authenticate the request.
   * @return HTTP response indicating success or failure.
   */
  @PUT
  @Path("/change-username")
  public Response changeUsername(@FormParam("oldUsername") String oldUsername,
      @FormParam("newUsername") String newUsername,
      @FormParam("password") String password) {
    Optional<User> user = userHandler.handleChangeUsername(oldUsername, newUsername, password);
    if (user.isPresent()) {
      return Response.ok().build();
    }
    return Response.status(Response.Status.UNAUTHORIZED).build();
  }

  /**
   * Retrieves aggregated gameplay statistics for a user.
   *
   * @param userId The ID of the user.
   * @return HTTP response with aggregated statistics or NOT FOUND.
   */
  @GET
  @Path("/stats/{userId}")
  public Response getAggregatedStats(@PathParam("userId") String userId) {
    try {
      int id = Integer.parseInt(userId);
      Map<String, Object> stats = userStatsHandler.handleGetAggregatedStatsForUser(id);
      return Response.ok(stats).build();
    } catch (NumberFormatException e) {
      return Response.status(Response.Status.BAD_REQUEST).entity("Invalid user ID").build();
    }
  }

  /**
   * Inserts a game result into the database.
   *
   * @param result The game result data transfer object containing game result details.
   * @return HTTP response indicating success or failure.
   */
  @POST
  @Path("/insert-game-result")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response insertGameResult(GameResultDTO result) {
    boolean success = userStatsHandler.handleInsertGameResult(
        result.playerId, result.tokensGreen, result.tokensBlue, result.tokensRed,
        result.tokensBlack, result.tokensWhite, result.tokensGold, result.cardsBought,
        result.placement);

    if (success) {
      return Response.ok().build();
    }
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity("Failed to insert game result").build();
  }

  public static class GameResultDTO {

    public String gameId;
    public int[] playerId;
    public int[] tokensGreen;
    public int[] tokensBlue;
    public int[] tokensRed;
    public int[] tokensBlack;
    public int[] tokensWhite;
    public int[] tokensGold;
    public int[] cardsBought;
    public int[] placement;
  }
}

