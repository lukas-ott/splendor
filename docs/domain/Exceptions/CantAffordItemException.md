# CantAffordItemException Documentation

## CantAffordItemException.java

This class represents the custom exception `CantAffordItemException` used in the game session logic. It extends `Exception` and is used to signal specific error conditions related to session handling in the game.

## Key Methods

### Constructor

`CantAffordItemException(String message)`: Constructs a new `CantAffordItemException` with the specified detail message.

## Usage in Game Flow

The `CantAffordItemException` is thrown when a player tries to purchase or acquire an item but lacks the necessary resources. This helps to provide specific feedback and manage game session integrity effectively.

jan.philip.walter May 23. 2025
