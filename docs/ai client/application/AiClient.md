# AiClient Documentation

## AiClient.java

This class represents the AI client logic in the game. It communicates with the game server and processes AI decisions.

## Key Methods

### Initialization
- `AiClient(String playerName)`: Constructor to initialize the AI client with a specific player name.

### Communication
- `connect()`: Establishes a connection to the game server.
- `onOpen(Session session)`: Called when the WebSocket connection is opened.
- `onMessage(String message)`: Handles incoming messages from the server.
- `sendMessage(String message)`: Sends a message to the server.
- `onClose(Session session, CloseReason closeReason)`: Called when the WebSocket connection is closed.
- `onError(Session session, Throwable throwable)`: Handles errors during WebSocket communication.

## Attributes

- `session`: Holds the WebSocket session.
- `playerName`: Stores the name of the AI player.

## Usage in Game Flow

The `AiClient` class is used to simulate an AI player within the game. It connects to the server, listens for messages, and responds with AI-generated decisions.

jan.philip.walter May 23. 2025