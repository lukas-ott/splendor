# SingleplayerLaunchScreenController Documentation

## SingleplayerLaunchScreenController.java

Manages the setup interface for launching a singleplayer game.

## Key Methods

### Initialization
- `initialize(URL location, ResourceBundle resources)`: Sets up default values for difficulty selection.

### Game Start
- `startSingleplayerGame()`: Launches a new singleplayer game with selected options.
- `returnToStartScreen()`: Navigates back to the main start screen.

## Attributes

- `difficultySelector`: Dropdown or control for choosing difficulty.
- `stage`: The current application window stage.

## Usage in Game Flow

`SingleplayerLaunchScreenController` is used when the player chooses to play a solo game and wants to select difficulty and settings.

jan.philip.walter May 23. 2025