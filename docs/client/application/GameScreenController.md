# GameScreenController Documentation

## GameScreenController.java

Manages the main game screen interface during an active game session.

## Key Methods

### Initialization
- `initialize(URL location, ResourceBundle resources)`: Initializes the screen and game components.

### Game Interaction
- `processMove()`: Processes a player's move.
- `updateScreen(GameState state)`: Updates the UI based on the current game state.
- `endGame()`: Handles end-of-game logic and transition.

## Attributes

- `boardPane`: UI component that renders the game board.
- `statusLabel`: Displays game status updates.
- `stage`: Stage representing the active game window.

## Usage in Game Flow

`GameScreenController` is the main game interface shown during active play. It updates as players make moves and reflects real-time game changes.

jan.philip.walter May 23. 2025