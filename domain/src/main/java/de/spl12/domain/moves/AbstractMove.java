package de.spl12.domain.moves;

import de.spl12.domain.Noble;
import de.spl12.domain.StoneType;

import java.util.ArrayList;

/**
 * Represents a move in the game. This class serves as a base class for different types of moves,
 * such as TakeMove and ReserveMove. Used to create lists of multiple move types in the
 * MoveGenerator class.
 *
 * @author luott
 */
public abstract class AbstractMove {

  private int requiredReturn = 0;
  private ArrayList<StoneType> tokensToReturn = new ArrayList<>();
  private Noble visitingNoble;

  public int getRequiredReturn() {
    return requiredReturn;
  }

  public void setRequiredReturn(int requiredReturn) {
    this.requiredReturn = requiredReturn;
  }

  public ArrayList<StoneType> getTokensToReturn() {
    return tokensToReturn;
  }

  public void setTokensToReturn(ArrayList<StoneType> tokensToReturn) {
    this.tokensToReturn = tokensToReturn;
  }

  public void setVisitingNoble(Noble visitingNoble) {
    this.visitingNoble = visitingNoble;
  }

  public Noble getVisitingNoble() {
    return visitingNoble;
  }
}