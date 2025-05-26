package de.spl12.domain.moves;

import de.spl12.domain.AbstractPlayer;
import de.spl12.domain.Card;
import de.spl12.domain.GameState;
import de.spl12.domain.StoneType;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * MoveGenerator is a utility class that generates possible moves for the current game state. It
 * provides methods to generate moves for taking stones, reserving cards, and buying cards.
 *
 * @author luott
 */
public class MoveGenerator {

  /**
   * Calculates all possible legal moves for the current player based on the game state.
   *
   * @param gameState The current game state
   * @return A list of all possible AbstractMove instances the current player can perform
   */
  public static ArrayList<AbstractMove> generateAllPossibleMoves(GameState gameState) {
    ArrayList<AbstractMove> possibleMoves = new ArrayList<>();

    possibleMoves.addAll(generateTakeMoves(gameState));
    possibleMoves.addAll(generateReserveMoves(gameState));
    possibleMoves.addAll(generateBuyMoves(gameState));

    return possibleMoves;
  }

  /**
   * Generates all legal BuyMove instances for the current player, based on visible and reserved
   * cards they can afford.
   *
   * @param gameState The current game state
   * @return A collection of legal BuyMove objects
   */
  public static ArrayList<AbstractMove> generateBuyMoves(GameState gameState) {
    ArrayList<AbstractMove> moves = new ArrayList<>();
    AbstractPlayer player = gameState.getPlayers().get(gameState.getPlayersTurn());
    Map<StoneType, Integer> playerTokens = player.getStoneInventory();

    Map<StoneType, Integer> discounts = new EnumMap<>(StoneType.class);
    player.getOwnedCards().forEach(card -> {
      StoneType bonus = card.getBonus();
      discounts.put(bonus, discounts.getOrDefault(bonus, 0) + 1);
    });

    List<List<Card>> tableCards = List.of(gameState.getFirstCardDeck(),
        gameState.getSecondCardDeck(), gameState.getThirdCardDeck());

    for (List<Card> deck : tableCards) {
      for (Card card : deck) {
        if (card == null) {
          continue;
        }
        Map<StoneType, Integer> tokensUsed = getTokensUsedToBuy(card, playerTokens, discounts);
        if (tokensUsed != null) {
          moves.add(new BuyMove(tokensUsed, card));
        }
      }
    }

    for (Card reserved : player.getReservedCards()) {
      Map<StoneType, Integer> tokensUsed = getTokensUsedToBuy(reserved, playerTokens, discounts);
      if (tokensUsed != null) {
        moves.add(new BuyMove(tokensUsed, reserved));
      }
    }
    return moves;
  }


  /**
   * Generates all legal ReserveMove instances for the current player, taking into account token
   * limits and gold availability.
   *
   * @param gameState The current game state
   * @return A collection of legal ReserveMove objects
   */
  public static ArrayList<AbstractMove> generateReserveMoves(GameState gameState) {
    ArrayList<AbstractMove> moves = new ArrayList<>();
    AbstractPlayer player = gameState.getPlayers().get(gameState.getPlayersTurn());

    if (player.getReservedCards().size() >= 3) {
      return moves;
    }

    Map<StoneType, Integer> playerTokens = player.getStoneInventory();
    Map<StoneType, Integer> bank = gameState.getStonePool();

    boolean takeGold = bank.getOrDefault(StoneType.GOLD, 0) > 0;
    int totalTokens = player.getTotalStones();
    int tokensTaken = takeGold ? 1 : 0;
    int requiredReturn = Math.max(0, totalTokens + tokensTaken - 10);

    List<List<Card>> allDecks = List.of(gameState.getFirstCardDeck(), gameState.getSecondCardDeck(),
        gameState.getThirdCardDeck());

    for (List<Card> deck : allDecks) {
      for (Card card : deck) {
        if (requiredReturn == 0) {
          // No return needed – simple reserve move
          moves.add(new ReserveMove(card, takeGold, 0));
        } else {
          // Prepare full inventory including taken gold (if applicable)
          Map<StoneType, Integer> fullInventory = new EnumMap<>(StoneType.class);
          for (StoneType type : StoneType.values()) {
            int count = playerTokens.getOrDefault(type, 0);
            if (takeGold && type == StoneType.GOLD) {
              count += 1;
            }
            if (count > 0) {
              fullInventory.put(type, count);
            }
          }

          List<Map<StoneType, Integer>> returnCombinations = generateTokenReturnCombinations(
              fullInventory, requiredReturn);
          for (Map<StoneType, Integer> ret : returnCombinations) {
            ReserveMove move = new ReserveMove(card, takeGold, requiredReturn);
            ArrayList<StoneType> returnTokens = new ArrayList<>();
            for (Map.Entry<StoneType, Integer> e : ret.entrySet()) {
              for (int i = 0; i < e.getValue(); i++) {
                returnTokens.add(e.getKey());
              }
            }
            move.setTokensToReturn(returnTokens);
            moves.add(move);
          }
        }
      }
    }

    return moves;
  }


  /**
   * Generates all legal TakeMove instances for the current player, including both 3-different and
   * 2-same gem moves within token limits.
   *
   * @param gameState The current game state
   * @return A list of legal TakeMove objects
   */
  public static ArrayList<AbstractMove> generateTakeMoves(GameState gameState) {
    ArrayList<AbstractMove> moves = new ArrayList<>();
    AbstractPlayer player = gameState.getPlayers().get(gameState.getPlayersTurn());
    Map<StoneType, Integer> bank = gameState.getStonePool();
    Map<StoneType, Integer> playerTokens = player.getStoneInventory();

    int totalTokens = player.getTotalStones();

    // Filter colors with at least 1 in the bank (excluding GOLD)
    List<StoneType> availableColors = bank.keySet().stream()
        .filter(t -> t != StoneType.GOLD && bank.get(t) > 0).toList();

    // Handle 3-different-color move
    if (availableColors.size() >= 3) {
      for (int i = 0; i < availableColors.size(); i++) {
        for (int j = i + 1; j < availableColors.size(); j++) {
          for (int k = j + 1; k < availableColors.size(); k++) {
            Map<StoneType, Integer> gems = Map.of(availableColors.get(i), 1, availableColors.get(j),
                1, availableColors.get(k), 1);
            int tokensTaken = 3;
            int requiredReturn = Math.max(0, (totalTokens + tokensTaken) - 10);
            moves.addAll(createTakeMovesWithReturns(playerTokens, gems, requiredReturn));
          }
        }
      }
    }

    // Handle 2-of-same-color move
    for (StoneType color : availableColors) {
      if (bank.get(color) >= 4) {
        Map<StoneType, Integer> gems = Map.of(color, 2);
        int tokensTaken = 2;
        int requiredReturn = Math.max(0, (totalTokens + tokensTaken) - 10);
        moves.addAll(createTakeMovesWithReturns(playerTokens, gems, requiredReturn));
      }
    }
    return moves;
  }

  /**
   * Helper method. Creates TakeMove instances with the specified tokens and return requirements.
   *
   * @param currentInventory The player's current token inventory
   * @param taken            The tokens taken in the move
   * @param requiredReturn   The number of tokens that need to be returned
   * @return A list of TakeMove instances with the specified tokens and return requirements
   */
  private static List<AbstractMove> createTakeMovesWithReturns(
      Map<StoneType, Integer> currentInventory, Map<StoneType, Integer> taken, int requiredReturn) {
    List<AbstractMove> moves = new ArrayList<>();

    if (requiredReturn == 0) {
      moves.add(new TakeMove(taken, 0));
      return moves;
    }

    // Combine currentInventory + taken to get the total inventory for return selection
    Map<StoneType, Integer> fullInventory = new EnumMap<>(StoneType.class);
    for (StoneType type : StoneType.values()) {
      int count = currentInventory.getOrDefault(type, 0) + taken.getOrDefault(type, 0);
      if (count > 0) {
        fullInventory.put(type, count);
      }
    }

    List<Map<StoneType, Integer>> returnCombinations = generateTokenReturnCombinations(
        fullInventory, requiredReturn);

    for (Map<StoneType, Integer> ret : returnCombinations) {
      TakeMove move = new TakeMove(taken, requiredReturn);
      ArrayList<StoneType> returnTokens = new ArrayList<>();
      for (Map.Entry<StoneType, Integer> e : ret.entrySet()) {
        for (int i = 0; i < e.getValue(); i++) {
          returnTokens.add(e.getKey());
        }
      }
      move.setTokensToReturn(returnTokens);
      moves.add(move);
    }

    return moves;
  }

  private static List<Map<StoneType, Integer>> generateTokenReturnCombinations(
      Map<StoneType, Integer> inventory, int count) {
    List<Map<StoneType, Integer>> results = new ArrayList<>();
    backtrackReturnCombinations(new ArrayList<>(inventory.keySet()), inventory, 0, count,
        new EnumMap<>(StoneType.class), results);
    return results;
  }

  private static void backtrackReturnCombinations(List<StoneType> types,
      Map<StoneType, Integer> inventory, int index, int tokensLeft, Map<StoneType, Integer> current,
      List<Map<StoneType, Integer>> results) {
    if (tokensLeft == 0) {
      results.add(new EnumMap<>(current));
      return;
    }
    if (index >= types.size()) {
      return;
    }

    StoneType type = types.get(index);
    int maxAvailable = Math.min(tokensLeft, inventory.get(type));
    for (int i = 0; i <= maxAvailable; i++) {
      if (i > 0) {
        current.put(type, i);
      } else {
        current.remove(type);
      }
      backtrackReturnCombinations(types, inventory, index + 1, tokensLeft - i, current, results);
    }
  }


  /**
   * Determines if a card is affordable for a player and returns the exact token breakdown used to
   * pay for it.
   *
   * @param card         The card being considered for purchase
   * @param playerTokens The player's available tokens
   * @param discounts    The player's token discounts from owned cards
   * @return A token map indicating the exact tokens used to purchase the card, or null if not
   * affordable
   */
  protected static Map<StoneType, Integer> getTokensUsedToBuy(Card card,
      Map<StoneType, Integer> playerTokens, Map<StoneType, Integer> discounts) {
    Map<StoneType, Integer> cost = card.getCost();
    Map<StoneType, Integer> tokensUsed = new EnumMap<>(StoneType.class);
    int totalGoldNeeded = 0;

    for (StoneType type : StoneType.values()) {
      if (type == StoneType.GOLD) {
        continue;
      }

      int price = cost.getOrDefault(type, 0);
      int discount = discounts.getOrDefault(type, 0);
      int netCost = Math.max(0, price - discount);
      int tokens = playerTokens.getOrDefault(type, 0);

      if (tokens >= netCost) {
        if (netCost > 0) {
          tokensUsed.put(type, netCost);
        }
      } else {
        if (tokens > 0) {
          tokensUsed.put(type, tokens);
        }
        totalGoldNeeded += (netCost - tokens);
      }
    }

    int goldAvailable = playerTokens.getOrDefault(StoneType.GOLD, 0);
    if (goldAvailable < totalGoldNeeded) {
      return null;
    }

    if (totalGoldNeeded > 0) {
      tokensUsed.put(StoneType.GOLD, totalGoldNeeded);
    }
    return tokensUsed;
  }

}