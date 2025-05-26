package de.spl12.domain.moves;

import de.spl12.domain.StoneType;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a move in the game where a player takes tokens from the supply. This class is used to
 * encapsulate the details of the move, including the number of each type of token taken and whether
 * a return is required.
 *
 * @author luott
 */
public class TakeMove extends AbstractMove {

  private final Map<StoneType, Integer> tokens;

  public TakeMove(Map<StoneType, Integer> tokens, int requiredReturn) {
    this.tokens = new HashMap<>();
    this.tokens.put(StoneType.BLACK, tokens.getOrDefault(StoneType.BLACK, 0));
    this.tokens.put(StoneType.WHITE, tokens.getOrDefault(StoneType.WHITE, 0));
    this.tokens.put(StoneType.BLUE, tokens.getOrDefault(StoneType.BLUE, 0));
    this.tokens.put(StoneType.RED, tokens.getOrDefault(StoneType.RED, 0));
    this.tokens.put(StoneType.GREEN, tokens.getOrDefault(StoneType.GREEN, 0));
    this.setRequiredReturn(requiredReturn);
  }

  /**
   * Increments the count of the specified stone type in the tokens map.
   *
   * @param stoneType the type of stone whose count needs to be increased
   */
  public void incrementStoneType(StoneType stoneType) {
    this.tokens.put(stoneType, this.tokens.get(stoneType) + 1);
  }

  public Map<StoneType, Integer> getTokens() {
    return tokens;
  }

  @Override
  public String toString() {
    return "Move type: TakeMove, Tokens taken: " + tokens.toString() + super.toString();
  }
}