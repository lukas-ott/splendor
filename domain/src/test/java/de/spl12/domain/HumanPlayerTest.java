package de.spl12.domain;

import de.spl12.domain.Exceptions.CantAffordItemException;
import de.spl12.domain.Exceptions.DepletedResourceException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test suite for the HumanPlayer class.
 *
 * This test class verifies the functionality and behavior of the HumanPlayer class,
 * including initialization, managing resources, and gameplay actions.
 */
public class HumanPlayerTest {

    @Test
    public void testHumanPlayer() {
        User test_user = new User(0, "test_user", "1234", 21);
        HumanPlayer player = new HumanPlayer(0, test_user);

        assertEquals(test_user, player.getUser());
        try {
            assertEquals(0, player.getSessionPlayerNumber());
            assertEquals(0, player.getStoneInventory().get(StoneType.GREEN));
            assertEquals(0, player.getStoneInventory().get(StoneType.BLUE));
            assertEquals(0, player.getStoneInventory().get(StoneType.RED));
            assertEquals(0, player.getStoneInventory().get(StoneType.WHITE));
            assertEquals(0, player.getStoneInventory().get(StoneType.BLACK));
            assertEquals(0, player.getStoneInventory().get(StoneType.GOLD));
        } catch (Exception e) {
            fail("StoneInventory not initialized correctly");
        }
        assertTrue(player.getReservedCards().isEmpty());
        assertTrue(player.getOwnedCards().isEmpty());
        assertTrue(player.getNobles().isEmpty());
    }

    /**
     * Tests the {@code takeStone} method of the {@link HumanPlayer} class to ensure
     * that stones of specified types are correctly added to the player's inventory.
     *
     * The test verifies:
     * - That the quantity of each stone type in the inventory is updated correctly
     *   after invoking {@code takeStone}.
     * - That stone types not added remain at a quantity of zero.
     *
     * Preconditions:
     * - A {@link HumanPlayer} instance is initialized with an associated {@link User}.
     *
     * Test steps:
     * 1. Stones of various {@link StoneType} are added to the player's inventory using {@code takeStone}.
     * 2. The inventory is checked to confirm correct quantities for each stone type.
     *
     * Assertions:
     * - The inventory contains the expected count for each added stone type.
     * - The inventory reflects zero for stone types that were not added.
     */
    @Test
    public void testTakeStone() {
        User test_user = new User(0, "test_user", "1234", 21);
        HumanPlayer player = new HumanPlayer(0, test_user);
        player.takeStone(StoneType.GREEN);
        player.takeStone(StoneType.BLUE);
        player.takeStone(StoneType.RED);
        player.takeStone(StoneType.GREEN);
        player.takeStone(StoneType.BLACK);
        player.takeStone(StoneType.BLACK);
        player.takeStone(StoneType.BLACK);

        assertEquals(2,  player.getStoneInventory().get(StoneType.GREEN));
        assertEquals(1,  player.getStoneInventory().get(StoneType.BLUE));
        assertEquals(1,  player.getStoneInventory().get(StoneType.RED));
        assertEquals(0,  player.getStoneInventory().get(StoneType.WHITE));
        assertEquals(3,  player.getStoneInventory().get(StoneType.BLACK));
        assertEquals(0,  player.getStoneInventory().get(StoneType.GOLD));
    }

    /**
     * Tests the {@code returnStone} method of the {@link HumanPlayer} class to ensure proper
     * functionality when returning stones of specific {@link StoneType} back to the central
     * supply, and verifies inventory updates and exception handling.
     *
     * The test validates:
     * - That stones of specific types are successfully removed from the inventory using {@code returnStone}.
     * - That attempting to return a stone type with no available inventory throws a {@link DepletedResourceException}.
     * - That the player's stone inventory reflects accurate counts after the method calls.
     *
     * Preconditions:
     * - A {@code HumanPlayer} object is initialized with a mock {@link User}.
     * - Stones of various {@link StoneType} are pre-added to the player's inventory using {@code takeStone}.
     *
     * Test steps:
     * 1. Invoke {@code returnStone} on multiple stone types present in the player's inventory.
     * 2. Attempt to invoke {@code returnStone} on a stone type absent in the player's inventory.
     * 3. Verify proper exception handling for depleted resources.
     * 4. Confirm the inventory reflects the correct counts for each stone type after the operations.
     *
     * Assertions:
     * - No exceptions are thrown when returning stones of existing types in the inventory.
     * - A {@code DepletedResourceException} is thrown when returning a stone type not present in the inventory.
     * - The inventory's counts for each stone type are as expected after the method calls.
     */
    @Test
    public void testReturnStone() {
        User test_user = new User(0, "test_user", "1234", 21);
        HumanPlayer player = new HumanPlayer(0, test_user);
        player.takeStone(StoneType.GREEN);
        player.takeStone(StoneType.BLUE);
        player.takeStone(StoneType.RED);
        player.takeStone(StoneType.GREEN);
        player.takeStone(StoneType.BLACK);
        player.takeStone(StoneType.BLACK);
        player.takeStone(StoneType.BLACK);

        assertDoesNotThrow(() -> player.returnStone(StoneType.GREEN));
        assertDoesNotThrow(() -> player.returnStone(StoneType.RED));
        assertDoesNotThrow(() -> player.returnStone(StoneType.BLACK));

        assertThrows(DepletedResourceException.class, () -> player.returnStone(StoneType.WHITE));

        assertEquals(1,  player.getStoneInventory().get(StoneType.GREEN));
        assertEquals(1,  player.getStoneInventory().get(StoneType.BLUE));
        assertEquals(0,  player.getStoneInventory().get(StoneType.RED));
        assertEquals(0,  player.getStoneInventory().get(StoneType.WHITE));
        assertEquals(2,  player.getStoneInventory().get(StoneType.BLACK));
        assertEquals(0,  player.getStoneInventory().get(StoneType.GOLD));
    }

    /**
     * Tests the {@code getTotalStones} method of the {@link HumanPlayer} class to ensure
     * the total number of stones in the player's inventory is calculated correctly.
     *
     * The test validates:
     * - That the returned total accurately reflects the number of stones added to the inventory
     *   using the {@code takeStone} method.
     * - That the total updates correctly when stones are removed using the {@code returnStone} method.
     * - That no exceptions are thrown during valid operations for removing stones.
     *
     * Preconditions:
     * - A {@link HumanPlayer} instance is initialized with an associated {@link User}.
     * - Stones of various {@link StoneType} are added to the player's inventory prior to verification.
     *
     * Test steps:
     * 1. Add several stones of various {@link StoneType} to the player's inventory using {@code takeStone}.
     * 2. Confirm that the total stone count matches the expected value after adding the stones.
     * 3. Remove specific stones using {@code returnStone}, ensuring no exceptions are thrown.
     * 4. Verify that the total stone count updates correctly after the removal operations.
     *
     * Assertions:
     * - The total number of stones in the player's inventory is as expected after additions.
     * - The total number of stones in the player's inventory is as expected after removals.
     * - Calls to {@code returnStone} for valid stones do not throw any exceptions.
     */
    @Test
    public void testGetTotalStones() {
        User test_user = new User(0, "test_user", "1234", 21);
        HumanPlayer player = new HumanPlayer(0, test_user);
        player.takeStone(StoneType.GREEN);
        player.takeStone(StoneType.BLUE);
        player.takeStone(StoneType.RED);
        player.takeStone(StoneType.GREEN);
        player.takeStone(StoneType.BLACK);
        player.takeStone(StoneType.BLACK);
        player.takeStone(StoneType.BLACK);

        assertEquals(7,  player.getTotalStones());

        assertDoesNotThrow(() -> player.returnStone(StoneType.GREEN));
        assertDoesNotThrow(() -> player.returnStone(StoneType.RED));
        assertDoesNotThrow(() -> player.returnStone(StoneType.BLACK));

        assertEquals(4,   player.getTotalStones());
    }

    /**
     * Tests the {@code buyCard} method of the {@link HumanPlayer} class to verify the ability
     * to purchase cards when the player has sufficient resources and to confirm the appropriate
     * adjustments to the player's inventory and owned card list.
     *
     * The test evaluates:
     * - Successful card purchase when the player has enough stones of the required types.
     * - The inventory's stone count is properly reduced based on the card's resource cost.
     * - The purchased card is added to the player's owned card list.
     * - Unsuccessful card purchase when the player lacks sufficient resources.
     * - The inventory and owned card list remain unchanged in the case of unsuccessful purchases.
     *
     * Preconditions:
     * - A {@link HumanPlayer} instance is initialized with a {@link User}.
     * - One or more {@code Card} objects are initialized with specific costs and rewards.
     * - The player's inventory is populated with stones using {@code takeStone}.
     *
     * Test steps:
     * 1. Attempt to purchase a card with sufficient resources in inventory.
     * 2. Verify that the inventory reflects proper reductions based on the card's cost.
     * 3. Confirm that the purchased card is added to the owned card list.
     * 4. Repeat the process for additional cards with sufficient resources for purchase.
     * 5. Attempt to purchase a card with insufficient resources in inventory.
     * 6. Confirm that the inventory and owned card list remain unaffected for unsuccessful purchases.
     *
     * Assertions:
     * - The {@code buyCard} method returns {@code true} for successful purchases.
     * - The player's stone inventory is correctly reduced after a successful purchase.
     * - The player's owned card list contains the purchased card after a successful purchase.
     * - The {@code buyCard} method returns {@code false} for unsuccessful purchases.
     **/
    @Test
    public void testBuyCard() {
        User test_user = new User(0, "test_user", "1234", 21);
        HumanPlayer player = new HumanPlayer(0, test_user);
        HashMap<StoneType, Integer> cost = new HashMap<>(Map.of(
                StoneType.GREEN, 1,
                StoneType.BLUE, 1,
                StoneType.RED, 1
        ));
        Card card1 = new Card(cost, StoneType.RED, 2, 1);
        Card card2 = new Card(cost, StoneType.BLUE, 2, 1);
        Card card3 = new Card(cost, StoneType.BLUE, 2, 2);
        Card card4 = new Card(cost, StoneType.BLUE, 2, 3);
        player.takeStone(StoneType.GREEN);
        player.takeStone(StoneType.GREEN);
        player.takeStone(StoneType.BLUE);
        player.takeStone(StoneType.BLUE);
        player.takeStone(StoneType.RED);

        assertTrue(player.buyCard(card1));

        assertTrue(player.getOwnedCards().contains(card1));
        assertEquals(1,  player.getStoneInventory().get(StoneType.GREEN));
        assertEquals(1,  player.getStoneInventory().get(StoneType.BLUE));
        assertEquals(0,  player.getStoneInventory().get(StoneType.RED));

        assertTrue(player.buyCard(card2));
        assertTrue(player.getOwnedCards().contains(card2));

        assertEquals(0,  player.getStoneInventory().get(StoneType.GREEN));
        assertEquals(0,  player.getStoneInventory().get(StoneType.BLUE));
        assertEquals(0,  player.getStoneInventory().get(StoneType.RED));

        player.takeStone(StoneType.GREEN);
        player.takeStone(StoneType.BLUE);
        player.takeStone(StoneType.BLUE);
        player.takeStone(StoneType.RED);

        assertTrue(player.buyCard(card3));
        assertTrue(player.getOwnedCards().contains(card3));

        assertEquals(0,  player.getStoneInventory().get(StoneType.GREEN));
        assertEquals(2,  player.getStoneInventory().get(StoneType.BLUE));
        assertEquals(1,  player.getStoneInventory().get(StoneType.RED));

        assertFalse(player.buyCard(card4));
        assertFalse(player.getOwnedCards().contains(card4));
    }

    /**
     * Tests the {@code buyReservedCard} method of the {@link HumanPlayer} class to verify
     * the functionality of purchasing a card that has been previously reserved by the player.
     * This test ensures appropriate behavior when attempting to buy a reserved card under
     * various conditions and validates the relevant updates to the player's state.
     *
     * The test verifies:
     * - That the {@code buyReservedCard} method returns {@code false} when the player
     *   attempts to purchase a reserved card without having the required resources.
     * - That the {@code buyReservedCard} method returns {@code true} when the player
     *   successfully purchases a reserved card with sufficient resources.
     * - That the purchased card is added to the player's owned card list after a successful
     *   transaction.
     * - That the reserved card remains unaffected in the player's reserved cards list
     *   when the purchase is unsuccessful.
     *
     * Preconditions:
     * - A {@link HumanPlayer} object is initialized with an associated {@link User}.
     * - A {@link Card} object is initialized with specified resource costs and prestige rewards.
     * - The player's inventory is modified using the {@code takeStone} method, allowing
     *   the addition of resource stones required for the test.
     *
     * Test steps:
     * 1. Attempt to buy a reserved card without reserving it first, ensuring the method
     *    returns {@code false}.
     * 2. Reserve the card using the {@code reserveCard} method.
     * 3. Attempt to purchase the reserved card without enough resources; ensure the
     *    method returns {@code false}.
     * 4. Add sufficient resources to the player using {@code takeStone}.
     * 5. Attempt to buy the reserved card again; ensure the method now returns {@code true}.
     * 6. Verify that the reserved card is added to the player's owned card list after
     *    a successful purchase.
     *
     * Assertions:
     * - The {@code buyReservedCard} method returns {@code false} when attempting to
     *   purchase a non-reserved card or when insufficient resources are present.
     * - The {@code buyReservedCard} method returns {@code true} for successful purchases.
     * - The purchased card appears in the player's owned card list after a successful purchase.
     * - The reserved card list and owned card list are unaffected for unsuccessful purchases.
     * - The player's resource inventory is reduced accordingly after a successful purchase.
     */
    @Test
    public void testBuyReservedCard() {
        User test_user = new User(0, "test_user", "1234", 21);
        HumanPlayer player = new HumanPlayer(0, test_user);
        HashMap<StoneType, Integer> cost = new HashMap<>(Map.of(
                StoneType.GREEN, 1,
                StoneType.BLUE, 1,
                StoneType.RED, 1
        ));
        Card card1 = new Card(cost, StoneType.RED, 2, 2);
        player.takeStone(StoneType.GREEN);
        player.takeStone(StoneType.BLUE);
        player.takeStone(StoneType.RED);

        assertFalse(player.buyReservedCard(card1));

        player.reserveCard(card1);

        assertTrue(player.buyReservedCard(card1));
        assertTrue(player.getOwnedCards().contains(card1));
    }

    /**
     * Tests the ability of a HumanPlayer to fulfill the conditions required to obtain a Noble
     * and verifies if the Noble is correctly assigned to the player upon meeting these conditions.
     *
     * This test ensures that:
     * - A player cannot initially obtain a Noble without fulfilling the required conditions.
     * - A player can buy cards to satisfy the conditions needed to obtain a Noble.
     * - The `checkNobleVisit` method correctly identifies whether the player meets the
     *   requirements for a Noble.
     * - The `obtainNoble` method assigns the Noble to the player's collection if
     *   requirements are met.
     * - The obtained Nobles are correctly added to the player's list of Nobles.
     */
    @Test
    public void testObtainNoble() {
        User test_user = new User(0, "test_user", "1234", 21);
        HumanPlayer player = new HumanPlayer(0, test_user);
        HashMap<StoneType, Integer> cost = new HashMap<>();
        Card card1 = new Card(cost, StoneType.GREEN, 2, 2);
        Card card2 = new Card(cost, StoneType.BLUE, 2, 2);
        Card card3 = new Card(cost, StoneType.RED, 2, 3);

        HashMap<StoneType, Integer> requirements = new HashMap<>(Map.of(
                StoneType.GREEN, 1,
                StoneType.BLUE, 1,
                StoneType.RED, 1
        ));
        Noble noble1 = new Noble(requirements, 3);
        Noble noble2 = new Noble(requirements,3);

        assertFalse(player.checkNobleVisit(noble1));

        player.buyCard(card1);
        player.buyCard(card2);
        player.buyCard(card3);

        assertTrue(player.checkNobleVisit(noble1));
        assertTrue(player.obtainNoble(noble1));
        assertTrue(player.getNobles().contains(noble1));

        assertTrue(player.checkNobleVisit(noble2));
        assertTrue(player.obtainNoble(noble2));
        assertTrue(player.getNobles().contains(noble2));
    }

    /**
     * Tests the functionality of the `reserveCard` method of the `HumanPlayer` class.
     *
     * This test verifies that:
     * - A card can be successfully reserved by a player if the maximum reserve limit has not been reached.
     * - The player's reserved card list size increases appropriately when a card is successfully reserved.
     * - When the maximum reserve limit (3 cards) is reached, the method prevents reserving additional cards and
     *   the reserved card list size remains unchanged.
     *
     * Assertions:
     * - The method should return true when reserving a card is successful.
     * - The size of the reserved card list should reflect the number of successfully reserved cards.
     * - Once the maximum reserve limit is reached, the method should return false and the reserved card list
     *   should not exceed the limit.
     */
    @Test
    public void testReserveCard() {
        User test_user = new User(0, "test_user", "1234", 21);
        HumanPlayer player = new HumanPlayer(0, test_user);
        HashMap<StoneType, Integer> cost = new HashMap<>(Map.of(
                StoneType.GREEN, 1,
                StoneType.BLUE, 1,
                StoneType.RED, 1
        ));
        Card card1 = new Card(cost, StoneType.BLACK, 2, 2);
        Card card2 = new Card(cost, StoneType.BLUE, 2, 2);
        Card card3 = new Card(cost, StoneType.BLUE, 2, 2);
        Card card4 = new Card(cost, StoneType.BLUE, 2, 2);

        assertTrue(player.reserveCard(card1));
        assertEquals(1,  player.getReservedCards().size());
        assertTrue(player.reserveCard(card2));
        assertEquals(2,  player.getReservedCards().size());
        assertTrue(player.reserveCard(card3));
        assertEquals(3,  player.getReservedCards().size());
        assertFalse(player.reserveCard(card4));
        assertEquals(3,  player.getReservedCards().size());
    }

    /**
     * Tests the {@code getPrestige} method of the {@code HumanPlayer} class.
     *
     * Verifies the following scenarios:
     * - Initial prestige of the player is zero.
     * - Prestige increases correctly when the player purchases cards with prestige points.
     * - Prestige increases correctly when the player obtains nobles with prestige points.
     *
     * Preconditions:
     * - A {@code HumanPlayer} object is created with an initial prestige of zero.
     * - Two cards and two nobles with defined prestige points are used for testing.
     *
     * Assertions:
     * - Ensure the initial prestige is zero.
     * - Verify the prestige is updated correctly after:
     *   1. Buying a card that grants prestige points.
     *   2. Obtaining a noble that grants prestige points.
     */
    @Test
    public void testGetPrestige() {
        User test_user = new User(0, "test_user", "1234", 21);
        HumanPlayer player = new HumanPlayer(0, test_user);
        HashMap<StoneType, Integer> cost = new HashMap<>();
        Card card1 = new Card(cost, StoneType.RED, 2, 2);
        Card card2 = new Card(cost, StoneType.BLUE, 5, 3);
        Noble noble1 = new Noble(cost, 3);
        Noble noble2 = new Noble(cost, 3);

        assertEquals(0, player.getPrestige());
        player.buyCard(card1);
        assertEquals(2, player.getPrestige());
        player.buyCard(card2);
        assertEquals(7, player.getPrestige());
        player.obtainNoble(noble1);
        assertEquals(10, player.getPrestige());
        player.obtainNoble(noble2);
        assertEquals(13, player.getPrestige());
    }
}
