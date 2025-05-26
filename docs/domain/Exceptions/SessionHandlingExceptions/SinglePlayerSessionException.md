# SinglePlayerSessionException Documentation

## SinglePlayerSessionException.java

This class represents the custom exception `SinglePlayerSessionException` used in the game session logic. It extends `Exception` and is used to signal specific error conditions related to session handling in the game.

## Key Methods

### Constructor

`SinglePlayerSessionException(String message)`: Constructs a new `SinglePlayerSessionException` with the specified detail message.

## Usage in Game Flow

The `SinglePlayerSessionException` is thrown when a multiplayer action is attempted in a single-player session context. This helps to provide specific feedback and manage game session integrity effectively.

jan.philip.walter May 23. 2025
