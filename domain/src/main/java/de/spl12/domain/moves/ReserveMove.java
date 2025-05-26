package de.spl12.domain.moves;

import de.spl12.domain.Card;

/**
 * Represents a move in the game where a player reserves a card. This class extends the AbstractMove
 * class and contains information about the reserved card, whether the player received a gold coin,
 * and if the card requires a return.
 *
 * @author luott
 */
public class ReserveMove extends AbstractMove {

  private final Card card;
  private final boolean gotGoldCoin;

  public ReserveMove(Card card, boolean gotGoldCoin, int requiredReturn) {
    this.card = card;
    this.gotGoldCoin = gotGoldCoin;
    this.setRequiredReturn(requiredReturn);
  }

  public Card getCard() {
    return card;
  }

  public boolean gotGoldCoin() {
    return gotGoldCoin;
  }

  @Override
  public String toString() {
    return "Move type: ReserveMove, Card reserved: " + card.toString() + super.toString();
  }
}
