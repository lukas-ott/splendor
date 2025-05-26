package de.spl12.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A test class for verifying the functionality and initialization of the GameState class.
 */
public class GameStateTest {
    @Test
    public void testInit() {
        GameState gs = new GameState();

        assertEquals(4, gs.getFirstCardDeck().size());
        assertEquals(4, gs.getSecondCardDeck().size());
        assertEquals(4, gs.getThirdCardDeck().size());

        assertEquals(36, gs.getFirstCardStack().size());
        assertEquals(26, gs.getSecondCardStack().size());
        assertEquals(16, gs.getThirdCardStack().size());
    }
}
