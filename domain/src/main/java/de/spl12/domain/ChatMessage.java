package de.spl12.domain;

import java.io.Serializable;

/**
 * Represents a single message in the {@link Chat}.
 *
 * @author leon.kuersch
 */
public class ChatMessage implements Serializable {
  private String username;
  private String message;

  public ChatMessage(String username, String message) {
    this.username = username;
    this.message = message;
  }

  public String getSender() {
    return this.username;
  }

  public String getMessage() {
    return message;
  }

  public String getUsername() {
    return username;
  }
}
