# LoginScreenController Documentation

## LoginScreenController.java

Manages the login interface, allowing users to log in or proceed as guests.

## Key Methods

### Initialization
- `initialize(URL location, ResourceBundle resources)`: Sets up the login screen and prepares input fields.

### Login Functions
- `loginUser()`: Validates and processes user login.
- `loginAsGuest()`: Allows guest access without credentials.
- `navigateToRegister()`: Navigates to the registration screen.

## Attributes

- `usernameField`, `passwordField`: Input fields for user credentials.
- `errorLabel`: Displays login errors or messages.
- `stage`: JavaFX stage for the login screen.

## Usage in Game Flow

This controller is used at the beginning of the application, enabling users to authenticate or proceed as guests.

jan.philip.walter May 23. 2025