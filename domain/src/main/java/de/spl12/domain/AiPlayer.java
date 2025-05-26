package de.spl12.domain;

import static de.spl12.domain.moves.MoveCalculationHelper.calculateBestBuyMovesSorted;
import static de.spl12.domain.moves.MoveCalculationHelper.calculateBestTakeMove;
import static de.spl12.domain.moves.MoveCalculationHelper.calculateGoodSecondDeckCards;
import static de.spl12.domain.moves.MoveCalculationHelper.calculateImportantStonesTotalSum;
import static de.spl12.domain.moves.MoveCalculationHelper.calculateReserveToBuyLevel2WithGold;
import static de.spl12.domain.moves.MoveCalculationHelper.calculateReserveToBuyLevel3WithGold;
import static de.spl12.domain.moves.MoveCalculationHelper.findBlockableHighPointCard;
import static de.spl12.domain.moves.MoveGenerator.generateAllPossibleMoves;
import static de.spl12.domain.moves.MoveGenerator.generateBuyMoves;
import static de.spl12.domain.moves.MoveGenerator.generateReserveMoves;
import static de.spl12.domain.moves.MoveGenerator.generateTakeMoves;

import de.spl12.domain.moves.AbstractMove;
import de.spl12.domain.moves.BuyMove;
import de.spl12.domain.moves.ReserveMove;
import de.spl12.domain.moves.TakeMove;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents an AI-controlled player in the Splendor game.
 *
 * <p>This AI supports multiple difficulty levels:
 *
 * <ul>
 *   <li><b>EASY</b>: Chooses a random legal move
 *   <li><b>MEDIUM</b>: Prioritizes reserved and level-3 card purchases, makes strategic reserves,
 *       and selects optimized take moves
 *   <li><b>HARD</b>: Evaluates move scores dynamically, blocks opponents, and plays aggressively
 *       toward high-value targets
 * </ul>
 *
 * <p>The AI evaluates game state, player inventory, and card characteristics to determine the best
 * action based on its difficulty.
 *
 * @author lmelodia
 */
public class AiPlayer extends AbstractPlayer {

  @Serial private static final long serialVersionUID = 447468553448933070L;

  private final AiDifficulty difficulty;
  private User fakeUser;

  public AiPlayer(int sessionPlayerNumber, AiDifficulty difficulty) {
    super(sessionPlayerNumber);
    this.difficulty = difficulty;
    this.fakeUser = null;
  }

  public AiDifficulty getDifficulty() {
    return difficulty;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setFakeUser(User fakeUser) {
    this.fakeUser = fakeUser;
  }

  public User getFakeUser() {
    return this.fakeUser;
  }

  public int getFakeUserId() {
    return this.fakeUser.getId();
  }

  /**
   * Determines the next move based on the AI difficulty.
   *
   * @param gameState the current game state
   * @return the chosen move
   */
  public AbstractMove decideMove(GameState gameState) {
    return switch (this.difficulty) {
      case EASY -> decideMoveEasy(gameState);
      case MEDIUM -> decideMoveMedium(gameState);
      case HARD -> decideMoveHard(gameState);
    };
  }

  /** EASY difficulty: returns a random BuyMove if available, otherwise a random legal move. */
  public AbstractMove decideMoveEasy(GameState gameState) {
    List<AbstractMove> buyMoves = generateBuyMoves(gameState);
    if (!buyMoves.isEmpty()) {
      return buyMoves.get(ThreadLocalRandom.current().nextInt(buyMoves.size()));
    }
    List<AbstractMove> allMoves = generateAllPossibleMoves(gameState);
    return allMoves.get(ThreadLocalRandom.current().nextInt(allMoves.size()));
  }

  /**
   * MEDIUM difficulty: prioritizes reserved and level-3 cards, then strategic reserves, then best
   * take.
   */
  private AbstractMove decideMoveMedium(GameState gameState) {
    Map<StoneType, Integer>[] stats = calculateImportantStonesTotalSum(gameState);
    Map<StoneType, Integer> totalSum = stats[0];

    List<AbstractMove> buyMoves = generateBuyMoves(gameState);
    List<BuyMove> reserved =
        buyMoves.stream()
            .filter(m -> m instanceof BuyMove)
            .map(m -> (BuyMove) m)
            .filter(b -> getReservedCards().contains(b.getCard()))
            .toList();

    List<BuyMove> reservedSorted = calculateBestBuyMovesSorted(reserved, totalSum, getPrestige());
    for (BuyMove move : reservedSorted) {
      if (gameState.getThirdCardDeck().contains(move.getCard())) {
        return move;
      }
    }

    List<BuyMove> allSorted = calculateBestBuyMovesSorted(buyMoves, totalSum, getPrestige());
    for (BuyMove move : allSorted) {
      if (gameState.getThirdCardDeck().contains(move.getCard())) {
        return move;
      }
    }

    if (!allSorted.isEmpty()) {
      return buyMoves.getFirst();
    }

    for (AbstractMove move : generateReserveMoves(gameState)) {
      if (calculateReserveToBuyLevel3WithGold(gameState, this)) {
        return move;
      }
    }

    List<AbstractMove> takeMoves = generateTakeMoves(gameState);
    if (!takeMoves.isEmpty()) {
      return calculateBestTakeMove(
          takeMoves,
          totalSum,
          getStoneInventory(),
          this,
          gameState.getPlayers(),
          gameState.getStonePool());
    }

    return generateAllPossibleMoves(gameState).getFirst();
  }

  /** HARD difficulty: uses score-based evaluation, opponent blocking, and reservation strategy. */
  public AbstractMove decideMoveHard(GameState gameState) {
    Map<StoneType, Integer>[] stats = calculateImportantStonesTotalSum(gameState);
    Map<StoneType, Integer> totalSum = stats[0];
    Map<StoneType, Integer> frequency = stats[1];

    List<BuyMove> buyMoves = new ArrayList<>();
    for (AbstractMove move : generateBuyMoves(gameState)) {
      if (move instanceof BuyMove buyMove) {
        buyMoves.add(buyMove);
      }
    }

    List<BuyMove> reserved =
        buyMoves.stream().filter(b -> getReservedCards().contains(b.getCard())).toList();

    if (!reserved.isEmpty()) {
      return calculateBestBuyMovesSorted(reserved, totalSum, getPrestige()).getFirst();
    }

    if (!buyMoves.isEmpty()) {
      return calculateBestBuyMovesSorted(buyMoves, totalSum, getPrestige()).get(0);
    }

    boolean danger =
        gameState.getPlayers().stream().anyMatch(p -> p != this && p.getPrestige() >= 10);
    if (danger) {
      Card block = findBlockableHighPointCard(gameState);
      if (block != null) {
        for (AbstractMove move : generateReserveMoves(gameState)) {
          if (move instanceof ReserveMove reserveMove && block.equals(reserveMove.getCard())) {
            return reserveMove;
          }
        }
      }
    }

    List<Card> goodLevel2 = calculateGoodSecondDeckCards(gameState, totalSum, frequency);
    for (AbstractMove move : generateReserveMoves(gameState)) {
      if (!(move instanceof ReserveMove reserveMove)) continue;
      Card card = reserveMove.getCard();
      if (card == null) continue;

      if (calculateReserveToBuyLevel3WithGold(gameState, this)
          && gameState.getThirdCardDeck().contains(card)) {
        return reserveMove;
      }

      if (calculateReserveToBuyLevel2WithGold(gameState, this, new ArrayList<>(goodLevel2))
          && gameState.getSecondCardDeck().contains(card)) {
        return reserveMove;
      }
    }

    List<AbstractMove> takeThree =
        generateTakeMoves(gameState).stream()
            .filter(
                m ->
                    m instanceof TakeMove takeMove
                        && takeMove.getTokens().values().stream().mapToInt(Integer::intValue).sum()
                            == 3)
            .toList();

    if (!takeThree.isEmpty()) {
      return calculateBestTakeMove(
          takeThree,
          totalSum,
          getStoneInventory(),
          this,
          gameState.getPlayers(),
          gameState.getStonePool());
    }

    List<AbstractMove> allTakeMoves = generateTakeMoves(gameState);
    if (!allTakeMoves.isEmpty()) {
      return calculateBestTakeMove(
          allTakeMoves,
          totalSum,
          getStoneInventory(),
          this,
          gameState.getPlayers(),
          gameState.getStonePool());
    }

    return generateAllPossibleMoves(gameState).getFirst();
  }
}
