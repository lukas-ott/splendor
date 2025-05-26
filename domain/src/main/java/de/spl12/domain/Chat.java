package de.spl12.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the state of the chat in a game session
 *
 * @author leon.kuersch
 */
public class Chat implements Serializable {
  private List<ChatMessage> messageList;

  public Chat() {
    this.messageList = new ArrayList<>();
  }

  public void writeMessage(ChatMessage message) {
    this.messageList.add(message);
  }

  public List<ChatMessage> getMessageList() {
    return messageList;
  }
}
