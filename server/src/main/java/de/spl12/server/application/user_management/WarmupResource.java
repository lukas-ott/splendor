package de.spl12.server.application.user_management;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

/**
 * WarmupResource is a simple REST endpoint for warming up the server. It can be used to check if
 * the server is running and ready to accept requests.
 *
 * @author luott
 */
@Path("/test")
public class WarmupResource {

  @GET
  public Response warmup() {
    System.out.println("Warmup endpoint hit");
    return Response.ok().build();
  }
}
