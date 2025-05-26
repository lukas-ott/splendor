package de.spl12.domain.messages;

import jakarta.websocket.DecodeException;
import jakarta.websocket.Decoder;
import jakarta.websocket.EndpointConfig;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Base64;

/**
 * Decoder class used in the WebSocket server endpoint to convert incoming Base64-encoded string
 * messages back into {@link AbstractPackage} objects.
 *
 * <p>This decoder is necessary because WebSocket messages are transmitted as text, while the actual
 * game logic expects serialized Java objects. Implements {@link Decoder.Text} for decoding text
 * messages.
 *
 * @see PackageEncoder
 * @see AbstractPackage
 * @author ennauman
 */
public class PackageDecoder implements Decoder.Text<AbstractPackage> {

  /**
   * Decodes a Base64-encoded string back into an {@link AbstractPackage} object.
   *
   * @param obj the Base64-encoded string representation of a serialized object
   * @return the decoded {@link AbstractPackage} object
   * @throws DecodeException if the string cannot be decoded into an {@link AbstractPackage}
   */
  @Override
  public AbstractPackage decode(String obj) throws DecodeException {
    try {
      byte[] data = Base64.getDecoder().decode(obj); // This can throw IllegalArgumentException
      try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
        return (AbstractPackage) ois.readObject();
      }
    } catch (IllegalArgumentException e) {
      throw new DecodeException(obj, "Invalid Base64 encoding", e); // Wrap in DecodeException
    } catch (IOException | ClassNotFoundException e) {
      throw new DecodeException(
          "", "Error when trying to convert the String to the AbstractPackage", e);
    }
  }

  /**
   * Determines whether the given string can be decoded.
   *
   * @param s the input string
   * @return true if the input is not null
   */
  @Override
  public boolean willDecode(String s) {
    return s != null;
  }

  @Override
  public void init(EndpointConfig endpointConfig) {
    // Custom initialization logic
  }

  @Override
  public void destroy() {
    // Close resources
  }
}
