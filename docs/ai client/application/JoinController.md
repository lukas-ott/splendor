# JoinController Documentation

## JoinController.java

Handles the user interface for joining an existing game lobby.

## Key Methods

### Initialization
- `initialize(URL location, ResourceBundle resources)`: Sets up the join game screen.

### Join Logic
- `joinGameLobby()`: Handles the process of joining a game by verifying the game code and submitting a join request.
- `cancelJoin()`: Cancels the join process and returns to the previous screen.

## Attributes

- `gameCodeField`: TextField for entering the game lobby code.
- `joinStatusLabel`: Displays messages related to join status or errors.
- `stage`: Current scene stage.
- `gameHandler`: Shared game logic handler.

## Usage in Game Flow

The `JoinController` is used when a player wants to join an existing game. The player inputs a code, which is validated before joining the lobby.

jan.philip.walter May 23. 2025