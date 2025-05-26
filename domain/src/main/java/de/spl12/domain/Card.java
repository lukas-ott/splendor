package de.spl12.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a Card in the game state. Simply stores the corresponding values.
 *
 * @author leon.kuersch
 */
public class Card implements Serializable {
  private final Map<StoneType, Integer> cost;
  private final int prestigePoints;
  private final StoneType bonus;
  private final int stage;

  public Card(Map<StoneType, Integer> cost, StoneType bonus, int prestigePoints, int stage) {
    this.cost = cost;
    this.bonus = bonus;
    this.prestigePoints = prestigePoints;
    this.stage = stage;
  }

  /**
   * Constructs a Card object based on a string representation of its properties and its stage. The
   * properties string is expected to follow a specific format:
   * "{prestigePoints},{bonus},{stoneType1}={quantity1},{stoneType2}={quantity2},...".
   *
   * @param propsString a comma-separated string containing the card's prestige points, bonus stone
   *     type, and the cost in the format specified.
   * @param stage the stage of the card in the game.
   */
  public Card(String propsString, int stage) {
    String[] props = propsString.split(",");
    this.prestigePoints = Integer.parseInt(props[0]);
    this.bonus = StoneType.valueOf(props[1].toUpperCase());
    this.cost = new HashMap<>();
    this.stage = stage;

    for (int i = 2; i < props.length; i++) {
      String[] stoneCost = props[i].split("=");
      this.cost.put(StoneType.valueOf(stoneCost[0].toUpperCase()), Integer.parseInt(stoneCost[1]));
    }
  }

  public Map<StoneType, Integer> getCost() {
    return cost;
  }

  public int getPrestigePoints() {
    return prestigePoints;
  }

  public StoneType getBonus() {
    return bonus;
  }

  public int getStage() {
    return stage;
  }

  @Override
  public String toString() {
    return "Card: "
        + this.prestigePoints
        + ","
        + this.bonus.toString()
        + ","
        + this.cost.toString();
  }
}
