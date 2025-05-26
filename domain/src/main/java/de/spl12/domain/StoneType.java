package de.spl12.domain;

import java.io.Serializable;

/**
 * Provides the possible types of stones in the game.
 *
 * @author leon.kuersch
 */
public enum StoneType implements Serializable {
  WHITE("white"),
  BLUE("blue"),
  GREEN("green"),
  RED("red"),
  BLACK("black"),
  GOLD("gold");

  String name;

  StoneType(String name) {
    this.name = name;
  }

  public String toString() {
    return this.name;
  }
}
