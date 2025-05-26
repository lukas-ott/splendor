# StartScreenController Documentation

## StartScreenController.java

Displays the initial screen with main menu options.

## Key Methods

### Initialization
- `initialize(URL location, ResourceBundle resources)`: Prepares the UI and main menu elements.

### Navigation
- `navigateToLogin()`: Navigates to the login screen.
- `navigateToSingleplayer()`: Opens the singleplayer game setup.
- `navigateToMultiplayer()`: Opens the multiplayer mode screen.
- `navigateToSettings()`: Opens the application settings.

## Attributes

- `stage`: JavaFX stage for the main start screen.

## Usage in Game Flow

`StartScreenController` is the first interaction point of the game application. It offers navigation to all major areas like login, singleplayer, multiplayer, and settings.

jan.philip.walter May 23. 2025