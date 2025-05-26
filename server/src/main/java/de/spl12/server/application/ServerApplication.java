package de.spl12.server.application;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Main application class for the server, defining the base URI for RESTful services. The
 * application path is set to "/api".
 *
 * @author luott
 */
@ApplicationPath("/api")
public class ServerApplication extends Application {

}