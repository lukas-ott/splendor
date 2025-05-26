# GameController Documentation

## GameController.java

This class controls the core game interface, handling player interactions and game state updates.

## Key Methods

### Initialization
- `initialize(URL location, ResourceBundle resources)`: Initializes the game scene and components.

### Game Control
- `updateGameState(GameState gameState)`: Updates the game display based on the new state.
- `renderPlayerInfo()`: Displays player data such as name, tokens, and cards.
- `handleCardClick(Card card)`: Handles logic when a card is clicked.
- `endTurn()`: Sends a command to end the player's turn.

### Navigation
- `returnToLobby()`: Navigates back to the lobby screen.

## Attributes

- `playerNameLabel`: Displays the player’s name.
- `gameHandler`: Shared game handler for accessing game state and communication.
- `boardPane`: JavaFX pane for rendering the game board.
- `stage`: Current window stage.

## Usage in Game Flow

`GameController` is used during active gameplay. It updates the game state display, handles user inputs like card selections, and enables turn-based interaction with the game server.

jan.philip.walter May 23. 2025