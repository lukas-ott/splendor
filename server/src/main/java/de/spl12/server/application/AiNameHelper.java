package de.spl12.server.application;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utility class for generating random AI character names.
 *
 * <p>Names are drawn from a predefined fantasy-themed name pool to provide variety and immersion in
 * the AI experience.
 *
 * <p>Usage example:
 *
 * <pre>{@code
 * String name = AiNameHelper.getRandomName(ignoredNames);
 * }</pre>
 *
 * @author lmelodia
 */
public class AiNameHelper {

  /** A predefined list of fantasy-style names for AI clients. */
  private static final List<String> NAMES =
      List.of(
          "Elowen",
          "Seraphina",
          "Lyra",
          "Thalira",
          "Nymeria",
          "Kaelith",
          "Isolde",
          "Vaela",
          "Zyreth",
          "Aelira",
          "Mirelle",
          "Rowan",
          "Sylvaen",
          "Ysolde",
          "Neriah",
          "Virelle",
          "Celestine",
          "Tavira",
          "Amariel",
          "Elaris",
          "Theron",
          "Kaelen",
          "Dorian",
          "Rhydan",
          "Malrik",
          "Fenric",
          "Auren",
          "Balen",
          "Torvak",
          "Lucan",
          "Gareth",
          "Kairon",
          "Draven",
          "Eryndor",
          "Zephriel",
          "Vaelin",
          "Corwin",
          "Thorne",
          "Jarek",
          "Orien");

  /**
   * Returns a randomly selected name from the predefined list that is not in the ignored list.
   *
   * <p>If four or more names are already in use, returns a generic fallback name ("Bot").
   *
   * @param ignoredNames list of names to exclude from selection
   * @return a unique random AI name, or "Bot" if all preferred names are used
   */
  public static String getRandomName(List<String> ignoredNames) {
    if (ignoredNames.size() >= 4) {
      return "Bot";
    }

    String name;
    do {
      int randomIndex = ThreadLocalRandom.current().nextInt(NAMES.size());
      name = NAMES.get(randomIndex);
    } while (ignoredNames.contains(name));

    return name;
  }
}
