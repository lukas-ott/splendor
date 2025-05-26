package de.spl12.domain;

import de.spl12.domain.messages.AbstractPackage;
import de.spl12.domain.messages.CreateSessionPackage;
import de.spl12.domain.messages.JoinSessionPackage;
import de.spl12.domain.messages.LeaveSessionPackage;
import de.spl12.domain.messages.PlayerActionPackage;
import de.spl12.domain.messages.GameOverPackage;
import de.spl12.domain.messages.PackageDecoder;
import jakarta.websocket.DecodeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 *  @author ennauman
 */
public class PackageDecoderTest {

  private PackageDecoder decoder;

  @BeforeEach
  void setUp() {
    decoder = new PackageDecoder();
  }

  /**
   * Tests the decoding functionality of the {@code PackageDecoder} for a {@link
   * CreateSessionPackage}.
   *
   * <p>The test involves the following steps: 1. Creation of an original {@link
   * CreateSessionPackage} instance with specific properties. 2. Encoding of the original package
   * into a Base64-encoded string using the {@code encode} method. 3. Decoding of the Base64-encoded
   * string back into an {@link AbstractPackage} using the {@code decoder.decode} method. 4.
   * Verifying that the decoded object is an instance of {@link CreateSessionPackage}.
   *
   * @throws Exception if there is an error during encoding, decoding, or type assertion.
   */
  @Test
  void testDecode_CreateSessionPackage() throws Exception {
    CreateSessionPackage original = new CreateSessionPackage(true);
    String encoded = encode(original);
    AbstractPackage decoded = decoder.decode(encoded);
    assertInstanceOf(CreateSessionPackage.class, decoded);
  }

  /**
   * Tests the decoding functionality of the {@code PackageDecoder} for a {@link GameOverPackage}.
   *
   * <p>The test performs the following steps: 1. Creates an instance of {@link GameOverPackage}
   * with predefined properties, including a player and a score. 2. Encodes the original {@link
   * GameOverPackage} instance into a Base64-encoded string using the {@code encode} method. 3.
   * Decodes the Base64-encoded string back into an {@link AbstractPackage} using the {@code
   * decoder.decode} method. 4. Validates that the decoded object is an instance of {@link
   * GameOverPackage}.
   *
   * @throws Exception if an error occurs during encoding, decoding, or type assertion.
   */
  @Test
  void testDecode_GameOverPackage() throws Exception {
    AbstractPlayer player = new HumanPlayer(1, new User(1, "Alice", "pwd123", 21));
    GameOverPackage original = new GameOverPackage(player, 123);
    String encoded = encode(original);
    AbstractPackage decoded = decoder.decode(encoded);
    assertInstanceOf(GameOverPackage.class, decoded);
  }

  /**
   * Tests the decoding functionality of the {@code PackageDecoder} for a {@link JoinSessionPackage}
   * initialized with a {@link HumanPlayer}.
   *
   * <p>This test verifies the following: 1. A {@link JoinSessionPackage} is created with a {@link
   * HumanPlayer}, session ID, and join status. 2. The package is serialized and encoded into a
   * Base64 string using the {@code encode} method. 3. The encoded string is decoded back into an
   * {@link AbstractPackage} using the {@code decoder.decode} method. 4. The decoded object is
   * verified to be an instance of {@link JoinSessionPackage}.
   *
   * @throws Exception if an error occurs during encoding, decoding, or type assertion.
   */
  @Test
  void testDecode_JoinSessionPackage_withHumanPlayer() throws Exception {
    JoinSessionPackage original =
        new JoinSessionPackage(
            new HumanPlayer(1, new User(1, "Alice", "pass", 21)),
            123,
            JoinSessionPackage.JoinStatus.REQUESTED);
    String encoded = encode(original);
    AbstractPackage decoded = decoder.decode(encoded);
    assertInstanceOf(JoinSessionPackage.class, decoded);
  }

  /**
   * Tests the decoding functionality of the {@code PackageDecoder} for a {@link JoinSessionPackage}
   * initialized with an {@link AiPlayer}.
   *
   * <p>This test ensures the following: 1. A {@link JoinSessionPackage} is created with an {@link
   * AiPlayer}, a session ID, and a join status. 2. The package is serialized and encoded into a
   * Base64-encoded string using the {@code encode} method. 3. The encoded string is decoded back
   * into an {@link AbstractPackage} using the {@code decoder.decode} method. 4. It is verified that
   * the decoded object is an instance of {@link JoinSessionPackage}.
   *
   * @throws Exception if there is an error during encoding, decoding, or type assertion.
   */
  @Test
  void testDecode_JoinSessionPackage_withAiPlayer() throws Exception {
    JoinSessionPackage original =
        new JoinSessionPackage(
            new AiPlayer(2, AiDifficulty.EASY), 124, JoinSessionPackage.JoinStatus.REQUESTED);
    String encoded = encode(original);
    AbstractPackage decoded = decoder.decode(encoded);
    assertInstanceOf(JoinSessionPackage.class, decoded);
  }

  /**
   * Tests the decoding functionality of the {@code PackageDecoder} for a {@link
   * LeaveSessionPackage} initialized with a {@link HumanPlayer}.
   *
   * <p>The test performs the following steps: 1. Creates an original {@link LeaveSessionPackage}
   * with a {@link HumanPlayer} and a session ID. 2. Encodes the original package into a
   * Base64-encoded string using the {@code encode} method. 3. Decodes the encoded string back into
   * an {@link AbstractPackage} using the {@code decoder.decode} method. 4. Validates that the
   * decoded object is an instance of {@link LeaveSessionPackage}.
   *
   * @throws Exception if an error occurs during encoding, decoding, or type assertion.
   */
  @Test
  void testDecode_LeaveSessionPackage_withHumanPlayer() throws Exception {
    LeaveSessionPackage original =
        new LeaveSessionPackage(new HumanPlayer(3, new User(2, "Bob", "secret", 21)), 456);
    String encoded = encode(original);
    AbstractPackage decoded = decoder.decode(encoded);
    assertInstanceOf(LeaveSessionPackage.class, decoded);
  }

  /** Tests the decoding functionality of */
  @Test
  void testDecode_LeaveSessionPackage_withAiPlayer() throws Exception {
    LeaveSessionPackage original = new LeaveSessionPackage(new AiPlayer(4, AiDifficulty.HARD), 457);
    String encoded = encode(original);
    AbstractPackage decoded = decoder.decode(encoded);
    assertInstanceOf(LeaveSessionPackage.class, decoded);
  }

  /**
   * Tests the decoding functionality of the {@code PackageDecoder} for a {@link
   * PlayerActionPackage} initialized with a {@link HumanPlayer}.
   *
   * <p>This test verifies the following: 1. A {@link PlayerActionPackage} is created with a {@link
   * HumanPlayer}, session ID, and {@link GameState}. 2. The package is serialized and encoded into
   * a Base64-encoded string using the {@code encode} method. 3. The encoded string is decoded back
   * into an {@link AbstractPackage} using the {@code decoder.decode} method. 4. It is verified that
   * the decoded object is an instance of {@link PlayerActionPackage}.
   *
   * @throws Exception if an error occurs during encoding, decoding, or type assertion.
   */
  @Test
  void testDecode_PlayerActionPackage_withHumanPlayer() throws Exception {
    PlayerActionPackage original =
        new PlayerActionPackage(
            new HumanPlayer(5, new User(3, "Charlie", "pwd", 21)), 789, new GameState());
    String encoded = encode(original);
    AbstractPackage decoded = decoder.decode(encoded);
    assertInstanceOf(PlayerActionPackage.class, decoded);
  }

  /**
   * Tests the decoding functionality of the {@code PackageDecoder} for a {@link
   * PlayerActionPackage} initialized with an {@link AiPlayer}.
   *
   * <p>This test verifies the following steps: 1. A {@link PlayerActionPackage} is created with an
   * {@link AiPlayer}, session ID, and {@link GameState}. 2. The package is serialized and encoded
   * into a Base64-encoded string using the {@code encode} method. 3. The encoded string is decoded
   * back into an {@link AbstractPackage} using the {@code decoder.decode} method. 4. It is
   * validated that the decoded object is an instance of {@link PlayerActionPackage}.
   *
   * @throws Exception if an error occurs during encoding, decoding, or type assertion.
   */
  @Test
  void testDecode_PlayerActionPackage_withAiPlayer() throws Exception {
    PlayerActionPackage original =
        new PlayerActionPackage(new AiPlayer(6, AiDifficulty.MEDIUM), 790, new GameState());
    String encoded = encode(original);
    AbstractPackage decoded = decoder.decode(encoded);
    assertInstanceOf(PlayerActionPackage.class, decoded);
  }

  /**
   * Verifies that the {@code decoder.decode} method throws a {@link DecodeException} when provided
   * with an invalid Base64-encoded string.
   *
   * <p>The test ensures the following: - An invalid Base64-encoded string is passed to the {@code
   * decoder.decode} method. - The method throws a {@link DecodeException}, indicating that the
   * decoding process failed.
   */
  @Test
  void testDecode_invalidBase64_throwsDecodeException() {
    assertThrows(DecodeException.class, () -> decoder.decode("invalid-base64-string"));
  }

  /**
   * Tests the behavior of the {@code willDecode} method when provided with a {@code null} input.
   *
   * <p>This test ensures the following: - If the input string to {@code willDecode} is {@code
   * null}, the method returns {@code false}.
   *
   * <p>This verifies that the {@code willDecode} method correctly identifies non-decodable input.
   */
  @Test
  void testWillDecode_withNull_returnsFalse() {
    assertFalse(decoder.willDecode(null));
  }

  /**
   * Tests the {@code willDecode} method with a valid input string.
   *
   * <p>This test verifies that when the {@code willDecode} method is provided with a valid,
   * non-null input string, it returns {@code true}.
   *
   * <p>It ensures the correct behavior of the {@code willDecode} method for decodable inputs.
   */
  @Test
  void testWillDecode_withValidString_returnsTrue() {
    assertTrue(decoder.willDecode("some-string"));
  }

  // Utility method to serialize and encode an AbstractPackage
  private String encode(AbstractPackage input) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
      oos.writeObject(input);
    }
    return Base64.getEncoder().encodeToString(baos.toByteArray());
  }
}
