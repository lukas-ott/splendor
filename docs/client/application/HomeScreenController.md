# HomeScreenController Documentation

## HomeScreenController.java

Manages the home screen interface after a user logs in.

## Key Methods

### Initialization
- `initialize(URL location, ResourceBundle resources)`: Loads user data and home screen elements.

### Navigation
- `navigateToGame()`: Takes the user to the game screen.
- `navigateToLeaderboard()`: Takes the user to the leaderboard.
- `navigateToSettings()`: Opens user settings like profile or password change.

## Attributes

- `welcomeLabel`: Label displaying welcome message.
- `stage`: The window stage for the home screen.

## Usage in Game Flow

`HomeScreenController` is the central hub after login, providing access to the game, settings, and leaderboard.

jan.philip.walter May 23. 2025