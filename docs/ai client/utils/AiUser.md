# AiUser Documentation

## AiUser.java

Represents an AI-controlled user in the game, managing automated decisions and interactions.

## Key Methods

### Initialization
- `AiUser(String name)`: Constructor initializing the AI user with a specified name.

### AI Behavior
- `makeMove(GameState gameState)`: Determines the next move based on the current game state.
- `receiveUpdate(String message)`: Handles incoming messages or updates relevant to the AI.

## Attributes

- `name`: The identifier for the AI user.
- `strategy`: Defines the decision-making logic for this AI user.

## Usage in Game Flow

`AiUser` simulates a player controlled by the system. It participates in gameplay autonomously and interacts with the game via programmed logic.

jan.philip.walter May 23. 2025