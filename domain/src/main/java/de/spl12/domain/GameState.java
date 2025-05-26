package de.spl12.domain;

import de.spl12.domain.Exceptions.DepletedResourceException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.List;
import java.util.Map;

/**
 * Represents the current state of the game.
 *
 * @author leon.kuersch
 */
public class GameState implements Serializable {
  private static final long serialVersionUID = 4338259086352890057L;
  private List<AbstractPlayer> players;
  private int playersTurn;
  private int startingPlayer;
  private boolean isRunning;

  private List<Card> firstCardDeck;
  private List<Card> firstCardStack;

  private List<Card> secondCardDeck;
  private List<Card> secondCardStack;

  private List<Card> thirdCardDeck;
  private List<Card> thirdCardStack;

  private List<Noble> nobleDeck;

  private Map<StoneType, Integer> stonePool;

  private Chat chat;
  private int turnNumber;

  private final String[] cards_level1 =
      new String[] {
        "0,black,green=1,red=3,black=1",
        "0,black,green=2,red=1",
        "0,black,green=3",
        "0,black,white=1,blue=1,green=1,red=1",
        "0,black,white=1,blue=2,green=1,red=1",
        "0,black,white=2,blue=2,red=1",
        "0,black,white=2,green=2",
        "0,blue,blue=1,green=3,red=1",
        "0,blue,green=2,black=2",
        "0,blue,white=1,black=2",
        "0,blue,white=1,green=1,red=1,black=1",
        "0,blue,white=1,green=1,red=2,black=1",
        "0,blue,white=1,green=2,red=2",
        "0,green,blue=1,red=2,black=2",
        "0,green,blue=2,red=2",
        "0,green,red=3",
        "0,green,white=1,blue=1,red=1,black=1",
        "0,green,white=1,blue=1,red=1,black=2",
        "0,green,white=1,blue=3,green=1",
        "0,green,white=2,blue=1",
        "0,red,blue=2,green=1",
        "0,red,white=1,blue=1,green=1,black=1",
        "0,red,white=1,red=1,black=3",
        "0,red,white=2,blue=1,green=1,black=1",
        "0,red,white=2,green=1,black=2",
        "0,red,white=2,red=2",
        "0,red,white=3",
        "0,white,blue=1,green=1,red=1,black=1",
        "0,white,blue=1,green=2,red=1,black=1",
        "0,white,blue=2,black=2",
        "0,white,blue=2,green=2,black=1",
        "0,white,blue=3",
        "0,white,red=2,black=1",
        "0,white,white=3,blue=1,black=1",
        "1,black,blue=4",
        "1,blue,black=3",
        "1,blue,red=4",
        "1,green,black=4",
        "1,red,white=4",
        "1,white,green=4"
      };

  private final String[] cards_level2 =
      new String[] {
        "1,black,white=3,blue=2,green=2",
        "1,black,white=3,green=3,black=2",
        "1,blue,blue=2,green=2,red=3",
        "1,blue,blue=3,green=3,black=3",
        "1,green,white=2,blue=3,black=2",
        "1,green,white=3,green=2,red=3",
        "1,red,blue=3,red=2,black=3",
        "1,red,white=2,red=2,black=3",
        "1,white,green=3,red=2,black=2",
        "1,white,white=2,blue=3,red=3",
        "2,black,blue=1,green=4,red=2",
        "2,black,green=5,red=3",
        "2,black,white=5",
        "2,blue,blue=5",
        "2,blue,white=2,red=1,black=4",
        "2,blue,white=5,blue=3",
        "2,green,blue=5,green=3",
        "2,green,green=5",
        "2,green,white=4,blue=2,black=1",
        "2,red,black=5",
        "2,red,white=1,blue=4,green=2",
        "2,red,white=3,black=5",
        "2,white,green=1,red=4,black=2",
        "2,white,red=5,black=3",
        "2,white,red=5",
        "3,black,black=6",
        "3,blue,blue=6",
        "3,green,green=6",
        "3,red,red=6",
        "3,white,white=6"
      };

  private final String[] cards_level3 =
      new String[] {
        "3,black,white=3,blue=3,green=5,red=3",
        "3,blue,white=3,green=3,red=3,black=5",
        "3,green,white=5,blue=3,red=3,black=3",
        "3,red,white=3,blue=5,green=3,black=3",
        "3,white,blue=3,green=3,red=5,black=3",
        "4,black,green=3,red=6,black=3",
        "4,black,red=7",
        "4,blue,white=6,blue=3,black=3",
        "4,blue,white=7",
        "4,green,blue=7",
        "4,green,white=3,blue=6,green=3",
        "4,red,blue=3,green=6,red=3",
        "4,red,green=7",
        "4,white,black=7",
        "4,white,white=3,black=7",
        "4,white,white=3,red=3,black=6",
        "5,black,red=7,black=3",
        "5,blue,white=7,blue=3",
        "5,green,blue=7,green=3",
        "5,red,green=7,red=3"
      };

  private final String[] nobles =
      new String[] {
        "3,black=3,blue=3,white=3",
        "3,black=3,red=3,green=3",
        "3,black=3,red=3,white=3",
        "3,black=4,red=4",
        "3,black=4,white=4",
        "3,blue=4,green=4",
        "3,blue=4,white=4",
        "3,green=3,blue=3,red=3",
        "3,green=3,blue=3,white=3",
        "3,red=4,green=4"
      };

  public GameState() {
    this.isRunning = false;
    this.playersTurn = 0;
    this.turnNumber = 0;
    this.chat = new Chat();
    this.players = new ArrayList<>();
    this.firstCardDeck = new ArrayList<>();
    this.firstCardStack = new ArrayList<>();
    this.secondCardDeck = new ArrayList<>();
    this.secondCardStack = new ArrayList<>();
    this.thirdCardDeck = new ArrayList<>();
    this.thirdCardStack = new ArrayList<>();
    this.nobleDeck = new ArrayList<>();
    this.stonePool = this.createStonePool();
    this.shuffleAndPlaceCards();
    this.shuffleAndPlaceNobles();
  }

  /**
   * Shuffles and prepares the card stacks for each level in the game and places a specific number
   * of cards in the corresponding decks for gameplay.
   *
   * <p>The method performs the following steps: 1. Constructs card stacks for levels 1, 2, and 3
   * using the respective card property lists (cards_level1, cards_level2, cards_level3) and assigns
   * each card the appropriate stage. 2. Shuffles each stack to ensure randomness. 3. Removes a
   * predefined number of cards (four) from the top of each stack and places them into the
   * corresponding decks (firstCardDeck, secondCardDeck, thirdCardDeck) to initialize the gameplay
   * setup.
   */
  private void shuffleAndPlaceCards() {
    for (String cardProps : this.cards_level1) {
      this.firstCardStack.add(new Card(cardProps, 1));
    }
    Collections.shuffle(this.firstCardStack);

    for (String cardProps : this.cards_level2) {
      this.secondCardStack.add(new Card(cardProps, 2));
    }
    Collections.shuffle(this.secondCardStack);

    for (String cardProps : this.cards_level3) {
      this.thirdCardStack.add(new Card(cardProps, 3));
    }
    Collections.shuffle(this.thirdCardStack);

    for (int i = 0; i < 4; i++) {
      this.firstCardDeck.add(this.firstCardStack.removeFirst());
      this.secondCardDeck.add(this.secondCardStack.removeFirst());
      this.thirdCardDeck.add(this.thirdCardStack.removeFirst());
    }
  }

  /**
   * Shuffles the list of nobles and places the first five into the noble deck.
   *
   * <p>The method processes nobles as follows: 1. Iterates through the list of noble properties and
   * creates a list of Noble objects. 2. Randomly shuffles the list of Noble objects to ensure
   * randomness. 3. Takes the first five Noble objects from the shuffled list and adds them to the
   * noble deck.
   *
   * <p>This setup is crucial for initializing the game with a randomized subset of nobles to
   * provide a unique game experience for each game session.
   */
  private void shuffleAndPlaceNobles() {
    List<Noble> noblesToShuffle = new ArrayList<>();
    for (String nobleProps : this.nobles) {
      noblesToShuffle.add(new Noble(nobleProps));
    }
    Collections.shuffle(noblesToShuffle);
    for (int i = 0; i < 5; i++) {
      this.nobleDeck.add(noblesToShuffle.removeFirst());
    }
  }

  public void removeNoble(Noble noble) {
    this.nobleDeck.set(this.getNobleDeck().indexOf(noble), null);
  }

  /**
   * Creates and initializes a stone pool with predefined quantities for each stone type.
   *
   * <p>The stone pool consists of the following quantities: - GREEN, BLUE, RED, WHITE, BLACK: 7
   * each - GOLD: 5
   *
   * @return a map containing stone types as keys and their corresponding quantities as values
   */
  private Map<StoneType, Integer> createStonePool() {
    Map<StoneType, Integer> newStonePool = new HashMap<>();
    newStonePool.put(StoneType.GREEN, 7);
    newStonePool.put(StoneType.BLUE, 7);
    newStonePool.put(StoneType.RED, 7);
    newStonePool.put(StoneType.WHITE, 7);
    newStonePool.put(StoneType.BLACK, 7);
    newStonePool.put(StoneType.GOLD, 5);
    return newStonePool;
  }

  /**
   * Reduces the quantity of a specified type of stone from the stone pool by 1. If no stones of the
   * specified type are available, an exception is thrown.
   *
   * @param stoneType the type of stone to be taken from the stone pool
   * @throws DepletedResourceException if there are no stones of the specified type left in the
   *     stone pool
   */
  public void takeStone(StoneType stoneType) throws DepletedResourceException {
    if (this.stonePool.get(stoneType) <= 0) {
      throw new DepletedResourceException("No Stones left of this type");
    }
    this.stonePool.put(stoneType, this.stonePool.get(stoneType) - 1);
  }

  /**
   * Advances the game to the next player's turn.
   *
   * <p>This method increments the current player's turn index, cycling back to the first player
   * after reaching the last player. Specifically, it calculates the next turn by incrementing the
   * `playersTurn` value and taking the modulus with 4 (assuming a 4-player game).
   *
   * <p>Additionally, it checks if the turn index has returned to the starting player. If so, the
   * turn number is incremented to reflect the progression to the next round.
   */
  public void nextTurn() {
    this.playersTurn = (this.playersTurn + 1) % 4;
    if (this.playersTurn - this.startingPlayer == 0) {
      this.turnNumber++;
    }
  }

  /**
   * Calculates and returns the total number of stones present in the stone pool. Iterates through
   * all stone types in the stone pool and sums up their respective quantities.
   *
   * @return the total number of stones available in the stone pool
   */
  public int totalStonesAvailable() {
    int amount = 0;
    for (StoneType stoneType : this.stonePool.keySet()) {
      amount += this.stonePool.get(stoneType);
    }
    return amount;
  }

  /**
   * Processes a payment by updating the stone pool with the given stone quantities. For each stone
   * type in the payment map, its corresponding quantity is added to the existing quantity in the
   * stone pool. If a stone type is not present in the pool, it is treated as having an initial
   * quantity of zero.
   *
   * @param payment a map where the key is the stone type and the value is the quantity of that
   *     stone type to be added to the stone pool
   */
  public void performPayment(Map<StoneType, Integer> payment) {
    for (StoneType stoneType : this.stonePool.keySet()) {
      this.stonePool.put(
          stoneType,
          this.stonePool.getOrDefault(stoneType, 0) + payment.getOrDefault(stoneType, 0));
    }
  }

  /**
   * Replaces a specified card in the appropriate card deck with a new card from the corresponding
   * stack. If the stack is empty, the card in the deck is replaced with null.
   *
   * @param card the card to be replaced; its associated stage determines the deck and stack used
   */
  public void replaceCard(Card card) {
    if (card.getStage() == 1) {
      int index = this.firstCardDeck.indexOf(card);
      if (index < 0) {
        return;
      }
      if (!this.firstCardStack.isEmpty()) {
        Card newCard = this.firstCardStack.removeFirst();
        this.firstCardDeck.set(index, newCard);
      } else {
        this.firstCardDeck.set(index, null);
      }
    } else if (card.getStage() == 2) {
      int index = this.secondCardDeck.indexOf(card);
      if (index < 0) {
        return;
      }
      if (!this.secondCardStack.isEmpty()) {
        Card newCard = this.secondCardStack.removeFirst();
        this.secondCardDeck.set(index, newCard);
      } else {
        this.secondCardDeck.set(index, null);
      }

    } else if (card.getStage() == 3) {
      int index = this.thirdCardDeck.indexOf(card);
      if (index < 0) {
        return;
      }
      if (!this.thirdCardStack.isEmpty()) {
        Card newCard = this.thirdCardStack.removeFirst();
        this.thirdCardDeck.set(index, newCard);
      } else {
        this.thirdCardDeck.set(index, null);
      }
    }
  }

  /**
   * Retrieves a random card from the specified card stack by removing the first card in the stack.
   * If the stack number is invalid or the stack is empty, an empty Optional is returned.
   *
   * @param stack the number of the stack from which to retrieve the card. Valid values are: 1 -
   *     first card stack, 2 - second card stack, 3 - third card stack.
   * @return an Optional containing the removed Card if available, otherwise an empty Optional.
   */
  public Optional<Card> getRandomCardFromStack(int stack) {
    List<Card> stackToTakeFrom = null;
    switch (stack) {
      case 1 -> stackToTakeFrom = this.firstCardStack;
      case 2 -> stackToTakeFrom = this.secondCardStack;
      case 3 -> stackToTakeFrom = this.thirdCardStack;
    }
    if (stackToTakeFrom != null && !stackToTakeFrom.isEmpty()) {
      return Optional.of(stackToTakeFrom.removeFirst());
    }
    return Optional.empty();
  }

  /**
   * Returns a stone of the specified type to the stone pool by incrementing its quantity.
   *
   * @param stoneType the type of stone to be returned to the stone pool
   */
  public void returnStone(StoneType stoneType) {
    this.stonePool.put(stoneType, this.stonePool.get(stoneType) + 1);
  }

  public List<AbstractPlayer> getPlayers() {
    return players;
  }

  public void setPlayers(List<AbstractPlayer> players) {
    this.players = players;
  }

  public int getPlayersTurn() {
    return playersTurn;
  }

  public void setPlayersTurn(int playersTurn) {
    this.playersTurn = playersTurn;
  }

  public List<Card> getFirstCardDeck() {
    return firstCardDeck;
  }

  public void setFirstCardDeck(List<Card> firstCardDeck) {
    this.firstCardDeck = firstCardDeck;
  }

  public List<Card> getFirstCardStack() {
    return firstCardStack;
  }

  public void setFirstCardStack(List<Card> firstCardStack) {
    this.firstCardStack = firstCardStack;
  }

  public List<Card> getSecondCardDeck() {
    return secondCardDeck;
  }

  public void setSecondCardDeck(List<Card> secondCardDeck) {
    this.secondCardDeck = secondCardDeck;
  }

  public List<Card> getSecondCardStack() {
    return secondCardStack;
  }

  public void setSecondCardStack(List<Card> secondCardStack) {
    this.secondCardStack = secondCardStack;
  }

  public List<Card> getThirdCardDeck() {
    return thirdCardDeck;
  }

  public void setThirdCardDeck(List<Card> thirdCardDeck) {
    this.thirdCardDeck = thirdCardDeck;
  }

  public List<Card> getThirdCardStack() {
    return thirdCardStack;
  }

  public void setThirdCardStack(List<Card> thirdCardStack) {
    this.thirdCardStack = thirdCardStack;
  }

  public List<Noble> getNobleDeck() {
    return nobleDeck;
  }

  public void setNobleDeck(List<Noble> nobleDeck) {
    this.nobleDeck = nobleDeck;
  }

  public Map<StoneType, Integer> getStonePool() {
    return stonePool;
  }

  public void setStonePool(Map<StoneType, Integer> stonePool) {
    this.stonePool = stonePool;
  }

  public boolean isRunning() {
    return this.isRunning;
  }

  public void startGame() {
    this.isRunning = true;
    HumanPlayer youngest = (HumanPlayer) this.players.getFirst();
    for (AbstractPlayer player : this.players) {
      if (player instanceof HumanPlayer humanPlayer) {
        if (humanPlayer.getUser().getAge() < youngest.getUser().getAge()) {
          youngest = humanPlayer;
        }
      }
    }
    this.playersTurn = youngest.getSessionPlayerNumber();
    this.startingPlayer = this.playersTurn;
  }

  public Chat getChat() {
    return chat;
  }

  public int getTurnNumber() {
    return turnNumber;
  }

  public void reset() {
    this.isRunning = false;
    this.playersTurn = this.startingPlayer;
    this.turnNumber = 0;

    ArrayList<AbstractPlayer> newPlayers = new ArrayList<>();
    for (AbstractPlayer player : this.getPlayers()) {
      if (player instanceof HumanPlayer humanPlayer) {
        HumanPlayer newHumanPlayer =
            new HumanPlayer(humanPlayer.getSessionPlayerNumber(), humanPlayer.getUser());
        newPlayers.add(newHumanPlayer);
      } else if (player instanceof AiPlayer aiPlayer) {
        AiPlayer newAiPlayer =
            new AiPlayer(aiPlayer.getSessionPlayerNumber(), aiPlayer.getDifficulty());
        newAiPlayer.setFakeUser(aiPlayer.getFakeUser());
        newAiPlayer.setName(aiPlayer.getName());
        newPlayers.add(newAiPlayer);
      }
    }
    this.players = newPlayers;

    this.firstCardDeck = new ArrayList<>();
    this.firstCardStack = new ArrayList<>();
    this.secondCardDeck = new ArrayList<>();
    this.secondCardStack = new ArrayList<>();
    this.thirdCardDeck = new ArrayList<>();
    this.thirdCardStack = new ArrayList<>();
    this.nobleDeck = new ArrayList<>();
    this.stonePool = this.createStonePool();
    this.shuffleAndPlaceCards();
    this.shuffleAndPlaceNobles();
  }
}
