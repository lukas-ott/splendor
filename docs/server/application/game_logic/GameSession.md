# GameSession Documentation

## GameSession.java

Represents a single instance of a game session, holding state and player data.

## Key Methods

- `addPlayer(Session session, String playerName)`: Adds a player to the session.
- `broadcast(String message)`: Sends a message to all players in the session.
- `removePlayer(Session session)`: Removes a player from the session.
- `handleMessage(Session session, String message)`: Processes incoming messages.

## Attributes

- `players`: Map of player sessions to player names.
- `sessionId`: Unique identifier for the game session.

## Usage in Game Flow

`GameSession` is used to manage the lifecycle and state of an active game session, including communication between participants.

jan.philip.walter May 23. 2025