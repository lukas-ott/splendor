# ActionNotPossibleException Documentation

## ActionNotPossibleException.java

This class represents the custom exception `ActionNotPossibleException` used in the game session logic. It extends `Exception` and is used to signal specific error conditions related to session handling in the game.

## Key Methods

### Constructor

`ActionNotPossibleException(String message)`: Constructs a new `ActionNotPossibleException` with the specified detail message.

## Usage in Game Flow

The `ActionNotPossibleException` is thrown when the attempted game action is not allowed due to game rules or current state. This helps to provide specific feedback and manage game session integrity effectively.

jan.philip.walter May 23. 2025
