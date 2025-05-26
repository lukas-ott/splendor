# SessionAlreadyStartedException Documentation

## SessionAlreadyStartedException.java

This class represents the custom exception `SessionAlreadyStartedException` used in the game session logic. It extends `Exception` and is used to signal specific error conditions related to session handling in the game.

## Key Methods

### Constructor

`SessionAlreadyStartedException(String message)`: Constructs a new `SessionAlreadyStartedException` with the specified detail message.

## Usage in Game Flow

The `SessionAlreadyStartedException` is thrown when an operation is attempted on a game session that has already started. This helps to provide specific feedback and manage game session integrity effectively.

jan.philip.walter May 23. 2025
