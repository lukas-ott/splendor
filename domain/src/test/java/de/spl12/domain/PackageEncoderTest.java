package de.spl12.domain;

import de.spl12.domain.messages.AbstractPackage;
import de.spl12.domain.messages.CreateSessionPackage;
import de.spl12.domain.messages.JoinSessionPackage;
import de.spl12.domain.messages.LeaveSessionPackage;
import de.spl12.domain.messages.PlayerActionPackage;
import de.spl12.domain.messages.GameOverPackage;
import de.spl12.domain.messages.PackageEncoder;
import jakarta.websocket.EncodeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 *  @author ennauman
 */
public class PackageEncoderTest {

  private PackageEncoder encoder;

  @BeforeEach
  void setUp() {
    encoder = new PackageEncoder();
  }

  @Test
  void testEncode_CreateSessionPackage() throws Exception {
    CreateSessionPackage pkg = new CreateSessionPackage(true);
    assertRoundTrip(pkg, CreateSessionPackage.class);
  }

  @Test
  void testEncode_GameOverPackage() throws Exception {
    AbstractPlayer player = new HumanPlayer(1, new User(1, "Alice", "pwd123", 21));
    GameOverPackage pkg = new GameOverPackage(player, 123);
    assertRoundTrip(pkg, GameOverPackage.class);
  }

  @Test
  void testEncode_JoinSessionPackage_withHumanPlayer() throws Exception {
    AbstractPlayer player = new HumanPlayer(1, new User(1, "Alice", "pwd123", 21));
    JoinSessionPackage pkg =
        new JoinSessionPackage(player, 123, JoinSessionPackage.JoinStatus.REQUESTED);
    assertRoundTrip(pkg, JoinSessionPackage.class);
  }

  @Test
  void testEncode_JoinSessionPackage_withAiPlayer() throws Exception {
    AbstractPlayer player = new AiPlayer(2, AiDifficulty.HARD);
    JoinSessionPackage pkg =
        new JoinSessionPackage(player, 124, JoinSessionPackage.JoinStatus.REQUESTED);
    assertRoundTrip(pkg, JoinSessionPackage.class);
  }

  @Test
  void testEncode_LeaveSessionPackage_withHumanPlayer() throws Exception {
    AbstractPlayer player = new HumanPlayer(1, new User(1, "Bob", "secret", 21));
    LeaveSessionPackage pkg = new LeaveSessionPackage(player, 456);
    assertRoundTrip(pkg, LeaveSessionPackage.class);
  }

  @Test
  void testEncode_LeaveSessionPackage_withAiPlayer() throws Exception {
    AbstractPlayer player = new AiPlayer(3, AiDifficulty.MEDIUM);
    LeaveSessionPackage pkg = new LeaveSessionPackage(player, 457);
    assertRoundTrip(pkg, LeaveSessionPackage.class);
  }

  @Test
  void testEncode_PlayerActionPackage_withHumanPlayer() throws Exception {
    AbstractPlayer player = new HumanPlayer(4, new User(2, "Charlie", "pass", 21));
    PlayerActionPackage pkg = new PlayerActionPackage(player, 789, new GameState());
    assertRoundTrip(pkg, PlayerActionPackage.class);
  }

  @Test
  void testEncode_PlayerActionPackage_withAiPlayer() throws Exception {
    AbstractPlayer player = new AiPlayer(5, AiDifficulty.EASY);
    PlayerActionPackage pkg = new PlayerActionPackage(player, 790, new GameState());
    assertRoundTrip(pkg, PlayerActionPackage.class);
  }

  @Test
  void testEncode_nullPackage_throwsEncodeException() {
    assertThrows(EncodeException.class, () -> encoder.encode(null));
  }

  // Utility to test encode/decode round-trip
  // we do not test the equals method
  private void assertRoundTrip(AbstractPackage input, Class<?> expectedType) throws Exception {
    String encoded = encoder.encode(input);
    assertNotNull(encoded);
    byte[] decodedBytes = Base64.getDecoder().decode(encoded);

    try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(decodedBytes))) {
      Object deserialized = ois.readObject();
      assertEquals(expectedType, deserialized.getClass());
    }
  }
}
