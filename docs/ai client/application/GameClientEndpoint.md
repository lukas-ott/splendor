# GameClientEndpoint Documentation

## GameClientEndpoint.java

Handles the WebSocket communication between the game client and the server.

## Key Methods

### Communication
- `onOpen(Session session)`: Triggered when the WebSocket is connected.
- `onClose(Session session, CloseReason reason)`: Triggered when the WebSocket is closed.
- `onMessage(String message)`: Processes incoming messages from the server.
- `sendMessage(String message)`: Sends a message to the server.

## Attributes

- `session`: WebSocket session used for communication.
- `aiClient`: The associated AI client if any.
- `gameHandler`: Manages game state and interactions.

## Usage in Game Flow

`GameClientEndpoint` is a WebSocket endpoint that allows bidirectional communication between the game client and the game server. It's essential for real-time game updates.

jan.philip.walter May 23. 2025