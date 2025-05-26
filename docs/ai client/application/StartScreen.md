# StartScreen Documentation

## StartScreen.java

Handles the initial user interface for the game application, providing navigation to login or guest access.

## Key Methods

### Initialization
- `initialize(URL location, ResourceBundle resources)`: Sets up the start screen interface.

### Navigation
- `startAsGuest()`: Begins the game as a guest player.
- `navigateToLogin()`: Navigates to the user login screen.
- `navigateToRegister()`: Navigates to the user registration screen.

## Attributes

- `stage`: The current application stage.
- `sceneController`: Scene manager for switching views.
- `gameHandler`: Shared handler for game state and networking.

## Usage in Game Flow

The `StartScreen` is the entry point of the game interface. It allows the user to log in, register, or continue as a guest to access the game features.

jan.philip.walter May 23. 2025