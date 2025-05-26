# LobbyController Documentation

## LobbyController.java

Manages the game lobby interface, including player display and start game functionality.

## Key Methods

### Initialization
- `initialize(URL location, ResourceBundle resources)`: Initializes the lobby screen and updates the player list.

### Lobby Management
- `refreshLobby()`: Refreshes the display of players in the lobby.
- `startGame()`: Starts the game session when all players are ready.
- `leaveLobby()`: Leaves the current lobby and navigates back.

## Attributes

- `playerListView`: List of players currently in the lobby.
- `startButton`: Button to initiate the game.
- `leaveButton`: Button to leave the lobby.
- `stage`: Current UI stage.
- `gameHandler`: Manages game-related logic and state.

## Usage in Game Flow

`LobbyController` is used after joining or creating a game lobby. It manages player readiness, displays the list of participants, and starts the game once initiated.

jan.philip.walter May 23. 2025