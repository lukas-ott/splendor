package de.spl12.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a Noble in the game state. Simply stores the corresponding values.
 *
 * @author leon.kuersch
 */
public class Noble implements Serializable {
  private final Map<StoneType, Integer> requirements;
  private final int prestigePoints;
  private String propsString;

  /**
   * Constructs a Noble object with a specified set of resource requirements and prestige points.
   *
   * @param cost a map defining the required quantities of each stone type needed for this noble.
   * @param prestigePoints the number of prestige points associated with this noble.
   */
  public Noble(Map<StoneType, Integer> cost, int prestigePoints) {
    this.requirements = cost;
    this.prestigePoints = prestigePoints;
  }

  /**
   * Constructs a Noble object based on a string representation of its properties. The properties
   * string is expected to follow a specific format:
   * "{prestigePoints},{stoneType1}={quantity1},{stoneType2}={quantity2},...".
   *
   * @param propsString a comma-separated string containing the noble's prestige points and required
   *     stone types with their respective quantities.
   */
  public Noble(String propsString) {
    this.propsString = propsString;
    String[] props = propsString.split(",");
    this.prestigePoints = Integer.parseInt(props[0]);
    this.requirements = new HashMap<>();

    for (int i = 1; i < props.length; i++) {
      String[] stoneCost = props[i].split("=");
      this.requirements.put(
          StoneType.valueOf(stoneCost[0].toUpperCase()), Integer.parseInt(stoneCost[1]));
    }
  }

  public Map<StoneType, Integer> getRequirements() {
    return requirements;
  }

  public int getPrestigePoints() {
    return prestigePoints;
  }

  public String getPropsString() {
    return propsString;
  }
}
