# ChangePasswordScreenController Documentation

## ChangePasswordScreenController.java

Manages the user interface for changing account passwords.

## Key Methods

### Initialization
- `initialize(URL location, ResourceBundle resources)`: Prepares the password change screen for user interaction.

### Password Handling
- `changePassword()`: Processes password change by validating inputs and sending a request to the server.
- `cancelChange()`: Cancels the operation and returns to the previous screen.

## Attributes

- `currentPasswordField`: Field for entering the current password.
- `newPasswordField`: Field for entering the new password.
- `errorLabel`: Displays error or success messages.
- `stage`: JavaFX stage on which the UI is rendered.

## Usage in Game Flow

This controller is part of the account management flow, allowing users to securely change their password.

jan.philip.walter May 23. 2025