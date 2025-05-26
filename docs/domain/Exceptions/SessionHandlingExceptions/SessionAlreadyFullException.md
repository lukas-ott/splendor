# SessionAlreadyFullException Documentation

## SessionAlreadyFullException.java

This class represents the custom exception `SessionAlreadyFullException` used in the game session logic. It extends `Exception` and is used to signal specific error conditions related to session handling in the game.

## Key Methods

### Constructor

`SessionAlreadyFullException(String message)`: Constructs a new `SessionAlreadyFullException` with the specified detail message.

## Usage in Game Flow

The `SessionAlreadyFullException` is thrown when a player tries to join a session that has already reached its maximum number of participants. This helps to provide specific feedback and manage game session integrity effectively.

jan.philip.walter May 23. 2025
