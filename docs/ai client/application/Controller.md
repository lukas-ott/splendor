# Controller Documentation

## Controller.java

This abstract class provides a common base for all scene controllers in the game.

## Key Methods

### Navigation
- `setStage(Stage stage)`: Sets the JavaFX stage for the controller.
- `getStage()`: Returns the current stage.
- `setGameHandler(GameHandler gameHandler)`: Sets the game handler used throughout the game.
- `getGameHandler()`: Returns the game handler instance.

## Attributes

- `stage`: The primary stage on which the controller operates.
- `gameHandler`: The shared game handler managing game state and flow.

## Usage in Game Flow

The `Controller` class serves as the base for all other controllers in the application. It provides stage and game handler management to facilitate consistent scene transitions and shared state.

jan.philip.walter May 23. 2025