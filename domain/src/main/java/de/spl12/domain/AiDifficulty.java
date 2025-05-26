package de.spl12.domain;

/**
 * Enum representing the difficulty levels for AI players in the application.
 *
 * <p>These levels configure AI behavior and decision-making strategies:
 *
 * <ul>
 *   <li>{@link #EASY} – Basic and predictable logic
 *   <li>{@link #MEDIUM} – Moderate difficulty with some strategic thinking
 *   <li>{@link #HARD} – Optimized and competitive AI behavior
 * </ul>
 *
 * <p>Used throughout the game to select the appropriate AI logic.
 *
 * @author lmelodia
 */
public enum AiDifficulty {
  EASY("Easy"),
  MEDIUM("Medium"),
  HARD("Hard");

  private final String name;

  AiDifficulty(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return this.name;
  }
}
