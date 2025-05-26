package de.spl12.domain.messages;

import java.io.Serializable;

/**
 * Base class for all message types exchanged between the client and server in the WebSocket
 * communication.
 *
 * <p>Implements {@link Serializable} to allow transmission via object streams. All specific message
 * types must extend this class.
 *
 * @author ennauman
 */
public class AbstractPackage implements Serializable {
  private static final long serialVersionUID = 8569896310620802956L;
}
