# GameSessionManager Documentation

## GameSessionManager.java

Manages all active game sessions on the server.

## Key Methods

- `createSession()`: Creates a new game session and returns its ID.
- `getSession(String sessionId)`: Retrieves a session by ID.
- `removeSession(String sessionId)`: Deletes a session from the manager.

## Attributes

- `sessions`: Map of session IDs to GameSession instances.

## Usage in Game Flow

`GameSessionManager` is responsible for tracking and managing all running game sessions on the server.

jan.philip.walter May 23. 2025