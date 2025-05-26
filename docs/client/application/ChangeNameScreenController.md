# ChangeNameScreenController Documentation

## ChangeNameScreenController.java

Handles user interactions on the change name screen.

## Key Methods

### Initialization
- `initialize(URL location, ResourceBundle resources)`: Initializes the screen and sets initial values.

### Name Change
- `changeName()`: Validates input and sends a request to change the user’s name.
- `cancelChange()`: Cancels the name change process and navigates back.

## Attributes

- `nameField`: Text field where the user inputs the new name.
- `errorLabel`: Label to show validation or server error messages.
- `stage`: JavaFX stage representing the current window.

## Usage in Game Flow

The `ChangeNameScreenController` is used in the settings or profile management flow. It allows users to change their display name and handles validation and error feedback.

jan.philip.walter May 23. 2025