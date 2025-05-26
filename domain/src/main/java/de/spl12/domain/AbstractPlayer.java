package de.spl12.domain;

import de.spl12.domain.Exceptions.DepletedResourceException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * AbstractPlayer implements the shared functionality of human or artificial players in a game
 * lobby, like some basic transactions with domain objects.
 *
 * @author leon.kuersch
 */
public abstract class AbstractPlayer implements Serializable {
  private static final long serialVersionUID = 8132866794109643691L;
  private int sessionPlayerNumber;
  protected String name;
  private Map<StoneType, Integer> stoneInventory;
  private List<Card> ownedCards;
  private List<Card> reservedCards;
  private List<Noble> nobles;
  private final int MAX_STONES = 10;
  private final int MAX_RESERVED_CARDS = 3;

  /**
   * Constructs an AbstractPlayer with a specific session player number. Initializes the player's
   * stone inventory, reserved cards, owned cards, and nobles.
   *
   * @param sessionPlayerNumber the player's unique session number
   */
  public AbstractPlayer(int sessionPlayerNumber) {
    this.sessionPlayerNumber = sessionPlayerNumber;
    this.stoneInventory = this.createStoneInventory();
    this.reservedCards = new ArrayList<>();
    this.ownedCards = new ArrayList<>();
    this.nobles = new ArrayList<>();
  }

  /**
   * Attempts to purchase the specified card by checking if the player can afford it and deducting
   * the necessary resources. If the purchase is successful, the card is added to the player's
   * collection of owned cards.
   *
   * @param card the card to be purchased
   * @return true if the card was successfully purchased, false otherwise
   */
  public boolean buyCard(Card card) {
    Map<StoneType, Integer> payment = this.getPaymentForCard(card);
    if (this.canAffordCard(card)) {
      this.spendStones(payment);
      this.getOwnedCards().add(card);
      return true;
    }
    return false;
  }

  /**
   * Determines whether the player can afford a given card based on their current stone inventory
   * and the cost of the card.
   *
   * @param card the card to be checked; must not be null
   * @return true if the player can afford the card, false otherwise
   */
  public boolean canAffordCard(Card card) {
    if (card == null) {
      return false;
    }
    Map<StoneType, Integer> payment = this.getPaymentForCard(card);
    boolean can_afford = true;
    for (StoneType type : payment.keySet()) {
      if (payment.get(type) > this.stoneInventory.get(type)) {
        can_afford = false;
      }
    }
    return can_afford;
  }

  /**
   * Calculates the payment required to purchase a given card, considering the player's current
   * stone inventory and any deficits that must be covered with gold. The method determines the
   * amount of each stone type to be used for payment and calculates the total deficit if the
   * player's inventory is insufficient.
   *
   * @param card the card for which the payment is to be calculated; must not be null
   * @return a map containing the amount of each stone type required for payment. The map includes
   *     all stone types in the card's cost and a special entry for StoneType.GOLD to represent the
   *     deficit.
   */
  public Map<StoneType, Integer> getPaymentForCard(Card card) {
    Map<StoneType, Integer> payment = new HashMap<>();
    int deficit = 0;
    Map<StoneType, Integer> cost = this.getCostAfterBonuses(card.getCost());
    for (StoneType type : cost.keySet()) {
      deficit += Math.max(cost.get(type) - this.getStoneInventory().get(type), 0);
      payment.put(type, Math.min(cost.get(type), this.getStoneInventory().get(type)));
    }
    payment.put(StoneType.GOLD, deficit);
    return payment;
  }

  /**
   * Adjusts the cost of each stone type by considering the bonuses a player has for each type. The
   * resulting cost for each stone type will be reduced by the corresponding bonus, with a minimum
   * value of zero.
   *
   * @param cost a map where the keys represent stone types and the values indicate their respective
   *     initial costs
   * @return a map where the keys represent stone types and the values indicate their adjusted costs
   *     after applying bonuses
   */
  private Map<StoneType, Integer> getCostAfterBonuses(Map<StoneType, Integer> cost) {
    Map<StoneType, Integer> newCost = new HashMap<>();
    for (StoneType type : cost.keySet()) {
      int bonus = this.getBonusForType(type);
      newCost.put(type, Math.max(0, cost.get(type) - bonus));
    }
    return newCost;
  }

  /**
   * Deducts the specified amounts of stones from the player's stone inventory.
   *
   * @param cost a map where the keys are the types of stones to deduct, and the values are the
   *     corresponding amounts to be deducted
   */
  private void spendStones(Map<StoneType, Integer> cost) {
    for (StoneType stoneType : cost.keySet()) {
      this.getStoneInventory()
          .put(stoneType, this.getStoneInventory().get(stoneType) - cost.get(stoneType));
    }
  }

  /**
   * Attempts to purchase a reserved card for the player. The card must be in the player's reserved
   * cards list and not already owned by the player. If the purchase is successful, the card is
   * removed from the reserved cards list.
   *
   * @param card the card to be purchased, which must be reserved by the player
   * @return true if the card was successfully purchased and removed from the reserved list, false
   *     otherwise
   */
  public boolean buyReservedCard(Card card) {
    if (!this.getReservedCards().contains(card) || this.getOwnedCards().contains(card)) {
      return false;
    }
    if (this.buyCard(card)) {
      this.getReservedCards().remove(card);
      return true;
    }
    return false;
  }

  /**
   * Checks if the player meets the requirements to attract a noble visit.
   *
   * @param noble the noble to check, containing the requirements needed for the visit
   * @return true if the player satisfies the noble's requirements, false otherwise
   */
  public boolean checkNobleVisit(Noble noble) {
    if (noble == null) {
      return false;
    }
    for (StoneType type : noble.getRequirements().keySet()) {
      if (this.getBonusForType(type) < noble.getRequirements().get(type)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Checks which nobles the player meets the requirements for. This is used to create the dialog
   * for the noble selection if 2 or more nobles could be claimed.
   *
   * @param nobles the deck of nobles in the game state
   * @return an array of booleans, each indicating whether the noble at that index can be claimed.
   */
  public boolean[] getNobleSelection(List<Noble> nobles) {
    boolean[] selection = new boolean[nobles.size()];
    int i = 0;
    for (Noble noble : nobles) {
      if (this.checkNobleVisit(noble)) {
        selection[i] = true;
      }
      i++;
    }
    return selection;
  }

  /**
   * Attempts to add a noble to the player's collection. If the noble is already present in the
   * player's list of nobles, the method returns false and does not add the noble again. Otherwise,
   * the noble is added to the list, and the method returns true.
   *
   * @param noble the noble to be added to the player's collection; must not be null
   * @return true if the noble was successfully added, false if the noble is already in the
   *     collection
   */
  public boolean obtainNoble(Noble noble) {
    if (this.nobles.contains(noble)) {
      return false;
    }
    this.getNobles().add(noble);
    return true;
  }

  /**
   * Attempts to reserve a card for the player. A card can only be reserved if the player has not
   * already reached the maximum limit of reserved cards.
   *
   * @param card the card to be reserved
   * @return true if the card was successfully reserved, false if the player already has the maximum
   *     allowed number of reserved cards
   */
  public boolean reserveCard(Card card) {
    if (this.getReservedCards().size() >= this.getMAX_RESERVED_CARDS()) {
      return false;
    }
    this.getReservedCards().add(card);
    return true;
  }

  /**
   * Adds one stone of the specified type to the player's stone inventory.
   *
   * @param stoneType the type of stone to be added to the player's inventory
   */
  public void takeStone(StoneType stoneType) {
    this.getStoneInventory().put(stoneType, this.getStoneInventory().get(stoneType) + 1);
  }

  /**
   * Decreases the count of the specified stone type in the player's inventory by one. Throws a
   * {@code DepletedResourceException} if there are no stones of the specified type left in the
   * player's inventory.
   *
   * @param stoneType the type of stone to be returned to the central supply; must not be null
   * @throws DepletedResourceException if the player has no stones of the specified type in their
   *     inventory
   */
  public void returnStone(StoneType stoneType) throws DepletedResourceException {
    if (this.getStoneInventory().get(stoneType) <= 0) {
      throw new DepletedResourceException("No Stone to return");
    }
    this.getStoneInventory().put(stoneType, this.getStoneInventory().get(stoneType) - 1);
  }

  /**
   * Calculates the total number of stones the player currently has in their inventory.
   *
   * @return the total count of all stones in the player's stone inventory
   */
  public int getTotalStones() {
    int sum = 0;
    for (StoneType type : this.getStoneInventory().keySet()) {
      sum += this.getStoneInventory().get(type);
    }
    return sum;
  }

  public int getPrestige() {
    int totalPrestige = 0;
    for (Card card : this.getOwnedCards()) {
      totalPrestige += card.getPrestigePoints();
    }
    for (Noble noble : this.getNobles()) {
      totalPrestige += noble.getPrestigePoints();
    }
    return totalPrestige;
  }

  /**
   * Initializes and creates a stone inventory with all stone types set to a default count of zero.
   *
   * @return a map where the keys represent each type of stone, and the values are initialized to
   *     zero
   */
  private Map<StoneType, Integer> createStoneInventory() {
    return new HashMap<>(
        Map.of(
            StoneType.GREEN, 0,
            StoneType.BLUE, 0,
            StoneType.RED, 0,
            StoneType.WHITE, 0,
            StoneType.BLACK, 0,
            StoneType.GOLD, 0));
  }

  public int getBonusForType(StoneType stoneType) {
    int bonus = 0;
    for (Card card : this.getOwnedCards()) {
      if (card.getBonus() == stoneType) {
        bonus++;
      }
    }
    return bonus;
  }

  public int getSessionPlayerNumber() {
    return sessionPlayerNumber;
  }

  public void setSessionPlayerNumber(int sessionPlayerNumber) {
    this.sessionPlayerNumber = sessionPlayerNumber;
  }

  public Map<StoneType, Integer> getStoneInventory() {
    return stoneInventory;
  }

  public List<Card> getReservedCards() {
    return reservedCards;
  }

  public List<Card> getOwnedCards() {
    return ownedCards;
  }

  public List<Noble> getNobles() {
    return nobles;
  }

  public int getMAX_STONES() {
    return MAX_STONES;
  }

  public int getMAX_RESERVED_CARDS() {
    return MAX_RESERVED_CARDS;
  }

  public String getName() {
    return name;
  }
}
