package de.spl12.domain.moves;

import de.spl12.domain.Card;
import de.spl12.domain.StoneType;

import java.util.Map;

/**
 * Represents a move in the game where a player buys a card. This class encapsulates the details of
 * the move, including the number of each type of token used for the purchase and the card being
 * bought.
 *
 * @author luott
 */
public class BuyMove extends AbstractMove {
  private final Map<StoneType, Integer> tokens;

  private final Card card;

  public BuyMove(Map<StoneType, Integer> tokens, Card card) {
    this.setRequiredReturn(0);
    this.card = card;
    this.tokens = Map.copyOf(tokens);
  }

  public Card getCard() {
    return card;
  }

  @Override
  public String toString() {
    return "Move type: BuyMove, Card bought: " + card.toString() + super.toString();
  }

  public Map<StoneType, Integer> getTokens() {
    return tokens;
  }
}
