# MultiplayerLobbyScreenController Documentation

## MultiplayerLobbyScreenController.java

Manages the multiplayer lobby interface, where players wait before starting the game.

## Key Methods

### Initialization
- `initialize(URL location, ResourceBundle resources)`: Loads lobby data and player list.

### Lobby Management
- `startGame()`: Starts the multiplayer game session.
- `leaveLobby()`: Allows the user to leave the lobby.

## Attributes

- `playerList`: Displays current players in the lobby.
- `startButton`, `leaveButton`: Control the game start and exit.
- `stage`: The current window stage.

## Usage in Game Flow

This controller is used while waiting for all players to be ready before starting a multiplayer game.

jan.philip.walter May 23. 2025