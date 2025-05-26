# NotYourTurnException Documentation

## NotYourTurnException.java

This class represents the custom exception `NotYourTurnException` used in the game session logic. It extends `Exception` and is used to signal specific error conditions related to session handling in the game.

## Key Methods

### Constructor

`NotYourTurnException(String message)`: Constructs a new `NotYourTurnException` with the specified detail message.

## Usage in Game Flow

The `NotYourTurnException` is thrown when a player tries to make a move or perform an action outside their turn. This helps to provide specific feedback and manage game session integrity effectively.

jan.philip.walter May 23. 2025
