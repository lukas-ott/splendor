# DepletedResourceException Documentation

## DepletedResourceException.java

This class represents the custom exception `DepletedResourceException` used in the game session logic. It extends `Exception` and is used to signal specific error conditions related to session handling in the game.

## Key Methods

### Constructor

`DepletedResourceException(String message)`: Constructs a new `DepletedResourceException` with the specified detail message.

## Usage in Game Flow

The `DepletedResourceException` is thrown when an action targets a resource that has already been used up or is no longer available. This helps to provide specific feedback and manage game session integrity effectively.

jan.philip.walter May 23. 2025
