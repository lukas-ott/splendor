# GameServerEndpoint Documentation

## GameServerEndpoint.java

WebSocket endpoint that handles real-time communication between the server and game clients.

## Key Methods

- `onOpen(Session session)`: Called when a client connects to the server.
- `onMessage(String message, Session session)`: Handles messages received from clients.
- `onClose(Session session, CloseReason reason)`: Handles client disconnection.
- `onError(Session session, Throwable throwable)`: Handles errors during communication.

## Attributes

- `sessionMap`: Tracks active client sessions.
- `gameSessionManager`: Manages all active game sessions.

## Usage in Game Flow

`GameServerEndpoint` facilitates real-time communication between players and the game logic on the server.

jan.philip.walter May 23. 2025