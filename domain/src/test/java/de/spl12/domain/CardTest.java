package de.spl12.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CardTest {

    /**
     * Tests the initialization of a {@link Card} object using a string representation
     * and verifies its properties using assertions.
     *
     * The test checks:
     * - The stage of the card.
     * - The prestige points of the card.
     * - The bonus stone type.
     * - The cost for various stone types, as defined in the string.
     * - That there is no cost for a stone type not included in the input string.
     */
    @Test
    public void testStringInit() {
        String propString = "3,black,white=3,blue=3,green=5,red=3";
        Card c = new Card(propString, 2);

        assertEquals(2, c.getStage());
        assertEquals(3, c.getPrestigePoints());
        assertEquals(StoneType.BLACK, c.getBonus());
        assertEquals(3, c.getCost().get(StoneType.WHITE));
        assertEquals(3, c.getCost().get(StoneType.BLUE));
        assertEquals(5, c.getCost().get(StoneType.GREEN));
        assertEquals(3, c.getCost().get(StoneType.RED));

        assertFalse(c.getCost().containsKey(StoneType.BLACK));

    }
}
