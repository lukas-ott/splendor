package de.spl12.domain.moves;

import de.spl12.domain.AbstractPlayer;
import de.spl12.domain.AiPlayer;
import de.spl12.domain.Card;
import de.spl12.domain.GameState;
import de.spl12.domain.StoneType;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Helper class for evaluating and selecting optimal AI moves in the Splendor game.
 *
 * <p>This class supports all AI difficulty levels by providing logic for:
 *
 * <ul>
 *   <li>Determining important token types from level-3 cards
 *   <li>Identifying good level-2 and level-1 cards based on frequency and cost
 *   <li>Deciding when reserving cards with gold is worthwhile
 *   <li>Scoring and ranking BuyMove and TakeMove actions
 *   <li>Finding high-value cards that opponents are likely to purchase soon
 * </ul>
 *
 * <p>Used by {@link de.spl12.domain.AiPlayer} to implement adaptive move decisions.
 *
 * @author lmelodia
 */
public class MoveCalculationHelper {

  /**
   * Computes the total required tokens and their frequency across all level-3 cards.
   *
   * @param gameState the current game state
   * @return a two-element array of maps:
   *     <ul>
   *       <li>[0] Token totals (StoneType → total cost across cards)
   *       <li>[1] Token frequency (StoneType → times it appears)
   *     </ul>
   */
  public static Map<StoneType, Integer>[] calculateImportantStonesTotalSum(GameState gameState) {
    Map<StoneType, Integer> totalSum = new HashMap<>();
    Map<StoneType, Integer> frequency = new HashMap<>();

    for (Card card : gameState.getThirdCardDeck()) {
      for (Map.Entry<StoneType, Integer> entry : card.getCost().entrySet()) {
        StoneType stone = entry.getKey();
        int amount = entry.getValue();
        totalSum.merge(stone, amount, Integer::sum);
        frequency.merge(stone, 1, Integer::sum);
      }
    }

    Map<StoneType, Integer>[] result = new Map[2];
    result[0] = totalSum;
    result[1] = frequency;
    return result;
  }

  /**
   * Finds valuable level-2 cards whose bonus types are frequently required in level-3 cards.
   *
   * @param gameState the current game state
   * @param totalSum token totals from level-3 cards
   * @param frequency token frequency from level-3 cards
   * @return a list of promising level-2 cards
   */
  public static ArrayList<Card> calculateGoodSecondDeckCards(
      GameState gameState, Map<StoneType, Integer> totalSum, Map<StoneType, Integer> frequency) {

    ArrayList<Card> goodCards = new ArrayList<>();
    List<StoneType> sortedStones =
        frequency.entrySet().stream()
            .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

    for (StoneType stone : sortedStones) {
      for (Card card : gameState.getSecondCardDeck()) {
        if (totalSum.containsKey(card.getBonus()) && card.getBonus() == stone) {
          goodCards.add(card);
        }
      }
      if (!goodCards.isEmpty()) {
        break;
      }
    }

    return goodCards;
  }

  /**
   * Identifies level-1 cards that match bonus types of good level-2 cards or highly demanded
   * tokens.
   *
   * @param gameState the current game state
   * @param totalSum token totals from level-3 cards
   * @param frequency token frequency from level-3 cards
   * @param goodSecondDeckCards list of good level-2 cards
   * @return a list of relevant level-1 cards
   */
  public static ArrayList<Card> calculateGoodFirstDeckCards(
      GameState gameState,
      Map<StoneType, Integer> totalSum,
      Map<StoneType, Integer> frequency,
      ArrayList<Card> goodSecondDeckCards) {

    ArrayList<Card> goodFirstDeckCards = new ArrayList<>();

    if (!goodSecondDeckCards.isEmpty()) {
      StoneType bonus = goodSecondDeckCards.get(0).getBonus();
      for (Card card : gameState.getFirstCardDeck()) {
        if (card != null && totalSum.containsKey(bonus) && card.getBonus() == bonus) {
          goodFirstDeckCards.add(card);
        }
      }
    }

    List<StoneType> sortedStones =
        frequency.entrySet().stream()
            .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

    for (StoneType stone : sortedStones) {
      if (goodFirstDeckCards.isEmpty()) {
        for (Card card : gameState.getFirstCardDeck()) {
          if (card != null && totalSum.containsKey(card.getBonus()) && card.getBonus() == stone) {
            goodFirstDeckCards.add(card);
          }
        }
      } else {
        break;
      }
    }

    return goodFirstDeckCards;
  }

  /**
   * Evaluates whether reserving a level-3 card using gold would likely help the player.
   *
   * @param gameState the current game state
   * @param player the AI player
   * @return true if reserving a level-3 card is considered beneficial
   */
  public static boolean calculateReserveToBuyLevel3WithGold(GameState gameState, AiPlayer player) {
    Map<StoneType, Integer> inventory = player.getStoneInventory();
    int totalCost = 0;
    int totalOwned = 0;

    for (Card card : gameState.getThirdCardDeck()) {
      for (Map.Entry<StoneType, Integer> entry : card.getCost().entrySet()) {
        if (entry.getKey() == StoneType.GOLD) {
          break;
        }
        totalCost += entry.getValue();
        totalOwned += inventory.get(entry.getKey());
      }
    }

    return totalCost > totalOwned && totalOwned > totalCost - 2;
  }

  /**
   * Evaluates whether reserving a level-2 card using gold would help the player.
   *
   * @param gameState the game state
   * @param player the AI player
   * @param goodSecondDeckCards relevant level-2 cards
   * @return true if reserving one of the level-2 cards is likely useful
   */
  public static boolean calculateReserveToBuyLevel2WithGold(
      GameState gameState, AiPlayer player, ArrayList<Card> goodSecondDeckCards) {

    Map<StoneType, Integer> inventory = player.getStoneInventory();
    int totalCost = 0;
    int totalOwned = 0;

    for (Card card : goodSecondDeckCards) {
      if (card == null) continue;
      for (Map.Entry<StoneType, Integer> entry : card.getCost().entrySet()) {
        totalCost += entry.getValue();
        totalOwned += inventory.get(entry.getKey());
      }
    }

    return totalCost > totalOwned && totalOwned > totalCost - 2;
  }

  /**
   * Sorts BuyMoves based on prestige points and bonus token importance.
   *
   * @param buyMoves list of potential buy moves
   * @param totalSum map of token importance
   * @param playerPoints the player's current score
   * @return a sorted list of BuyMoves, from best to worst
   */
  public static List<BuyMove> calculateBestBuyMovesSorted(
      List<? extends AbstractMove> buyMoves, Map<StoneType, Integer> totalSum, int playerPoints) {

    double pointWeight;
    double bonusWeight;

    if (playerPoints < 5) {
      pointWeight = 5;
      bonusWeight = 3;
    } else if (playerPoints < 10) {
      pointWeight = 10;
      bonusWeight = 1;
    } else {
      pointWeight = 15;
      bonusWeight = 1;
    }

    List<ScoredBuyMove> scored = new ArrayList<>();

    for (AbstractMove move : buyMoves) {
      if (!(move instanceof BuyMove buyMove)) continue;
      Card card = buyMove.getCard();
      if (card == null) continue;

      int points = card.getPrestigePoints();
      StoneType bonus = card.getBonus();
      int bonusImportance = totalSum.getOrDefault(bonus, 0);
      double score = (points * pointWeight) + (bonusImportance * bonusWeight);
      scored.add(new ScoredBuyMove(buyMove, score));
    }

    scored.sort(Comparator.comparingDouble(sb -> -sb.score));
    return scored.stream().map(sb -> sb.move).collect(Collectors.toList());
  }

  private static class ScoredBuyMove {
    BuyMove move;
    double score;

    ScoredBuyMove(BuyMove move, double score) {
      this.move = move;
      this.score = score;
    }
  }

  /**
   * Identifies a high-prestige card that another player may soon purchase.
   *
   * @param gameState the current game state
   * @return a blockable high-point card or null if none found
   */
  public static Card findBlockableHighPointCard(GameState gameState) {
    int myIndex = gameState.getPlayersTurn();
    List<List<Card>> tableDecks =
        List.of(
            gameState.getFirstCardDeck(),
            gameState.getSecondCardDeck(),
            gameState.getThirdCardDeck());

    for (int i = 0; i < gameState.getPlayers().size(); i++) {
      if (i == myIndex) continue;
      AbstractPlayer opponent = gameState.getPlayers().get(i);

      Map<StoneType, Integer> discounts = new EnumMap<>(StoneType.class);
      opponent.getOwnedCards().forEach(c -> discounts.merge(c.getBonus(), 1, Integer::sum));

      Map<StoneType, Integer> tokens = opponent.getStoneInventory();

      for (List<Card> deck : tableDecks) {
        for (Card card : deck) {
          if (card == null || card.getPrestigePoints() <= 3) continue;
          Map<StoneType, Integer> used = MoveGenerator.getTokensUsedToBuy(card, tokens, discounts);
          if (used != null) {
            return card;
          }
        }
      }
    }

    return null;
  }

  /**
   * Calculates token types that the player still needs for their reserved cards.
   *
   * @param player the player to analyze
   * @return map of needed StoneType to remaining quantity
   */
  private static Map<StoneType, Integer> calculateTargetStoneNeeds(AbstractPlayer player) {
    Map<StoneType, Integer> needs = new EnumMap<>(StoneType.class);

    for (Card card : player.getReservedCards()) {
      if (card == null) continue;
      for (Map.Entry<StoneType, Integer> entry : card.getCost().entrySet()) {
        int owned = player.getStoneInventory().getOrDefault(entry.getKey(), 0);
        int bonus =
            (int)
                player.getOwnedCards().stream().filter(c -> c.getBonus() == entry.getKey()).count();
        int covered = owned + bonus;
        int remaining = Math.max(entry.getValue() - covered, 0);
        needs.merge(entry.getKey(), remaining, Integer::sum);
      }
    }

    return needs;
  }

  /**
   * Selects the most valuable TakeMove based on token demand, scarcity, urgency, and strategy.
   *
   * @param takeMoves all available take moves
   * @param totalSum map of token importance from level-3 cards
   * @param inventory the current token inventory of the player
   * @param player the player making the decision
   * @param players all players in the game
   * @param stonePool current available tokens in the central pool
   * @return the best TakeMove or null if none are suitable
   */
  public static TakeMove calculateBestTakeMove(
      List<AbstractMove> takeMoves,
      Map<StoneType, Integer> totalSum,
      Map<StoneType, Integer> inventory,
      AbstractPlayer player,
      List<AbstractPlayer> players,
      Map<StoneType, Integer> stonePool) {

    TakeMove best = null;
    double bestScore = Double.NEGATIVE_INFINITY;

    final double returnPenaltyFactor = 1.5;
    final double scarcityFactor = 2.0;
    final double urgencyFactor = 1.2;
    final double opponentPenaltyFactor = 0.5;
    final double targetNeedWeight = 3.5;

    Map<StoneType, Integer> oppInventory = new EnumMap<>(StoneType.class);
    for (AbstractPlayer p : players) {
      for (Map.Entry<StoneType, Integer> e : p.getStoneInventory().entrySet()) {
        oppInventory.merge(e.getKey(), e.getValue(), Integer::sum);
      }
    }

    Map<StoneType, Integer> targetNeeds = calculateTargetStoneNeeds(player);

    for (AbstractMove move : takeMoves) {
      if (!(move instanceof TakeMove takeMove)) continue;
      double score = 0;

      for (Map.Entry<StoneType, Integer> entry : takeMove.getTokens().entrySet()) {
        StoneType type = entry.getKey();
        int count = entry.getValue();

        int importance = totalSum.getOrDefault(type, 0);
        int current = inventory.getOrDefault(type, 0);
        int poolLeft = stonePool.getOrDefault(type, 0);
        int opponentCount = oppInventory.getOrDefault(type, 0) - current;
        int targetNeed = targetNeeds.getOrDefault(type, 0);

        score += importance * count;
        score += urgencyFactor * count / (current + 1.0);
        if (poolLeft < 3) score += scarcityFactor * (3 - poolLeft) * count;
        score -= opponentPenaltyFactor * opponentCount * count;
        score += targetNeedWeight * targetNeed * count;
      }

      score -= returnPenaltyFactor * takeMove.getRequiredReturn();

      if (score > bestScore) {
        bestScore = score;
        best = takeMove;
      }
    }

    return best;
  }
}
