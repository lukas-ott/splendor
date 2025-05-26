package de.spl12.domain.messages;

import jakarta.websocket.EncodeException;
import jakarta.websocket.Encoder;
import jakarta.websocket.EndpointConfig;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Base64;

/**
 * Encoder class used in the WebSocket server endpoint to convert {@link AbstractPackage} objects
 * into Base64-encoded string messages.
 *
 * <p>This encoder allows binary Java objects to be transmitted as text over WebSocket. It is the
 * counterpart to {@link PackageDecoder}, which performs the inverse operation. Implements {@link
 * Encoder.Text} for encoding text messages.
 *
 * @see PackageDecoder
 * @see AbstractPackage
 * @author ennauman
 */
public class PackageEncoder implements Encoder.Text<AbstractPackage> {

  /**
   * Encodes an {@link AbstractPackage} object into a Base64-encoded string.
   *
   * @param abstractPackage the object to encode
   * @return a Base64 string representation of the serialized object
   * @throws EncodeException if an error occurs during encoding
   */
  @Override
  public String encode(AbstractPackage abstractPackage) throws EncodeException {
    if (abstractPackage == null) {
      throw new EncodeException(
          abstractPackage, "Error when trying to convert the GameStateUpdate to String");
    }
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos)) {
      oos.writeObject(abstractPackage);
      return Base64.getEncoder().encodeToString(baos.toByteArray());
    } catch (IOException e) {
      throw new EncodeException(
          abstractPackage, "Error when trying to convert the GameStateUpdate to String");
    }
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
