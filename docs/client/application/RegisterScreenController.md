# RegisterScreenController Documentation

## RegisterScreenController.java

Handles the user interface and logic for registering a new account.

## Key Methods

### Initialization
- `initialize(URL location, ResourceBundle resources)`: Sets up the registration form.

### Registration
- `registerUser()`: Validates input and sends registration request.
- `cancelRegistration()`: Navigates back to the login screen.

## Attributes

- `usernameField`, `passwordField`, `emailField`: Input fields for new user data.
- `errorLabel`: Shows validation or server errors.
- `stage`: Current JavaFX stage.

## Usage in Game Flow

This controller is used when a user chooses to create a new account before starting to play.

jan.philip.walter May 23. 2025