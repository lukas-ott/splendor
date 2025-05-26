# DeleteAccountScreenController Documentation

## DeleteAccountScreenController.java

Handles the user interface for deleting an account.

## Key Methods

### Initialization
- `initialize(URL location, ResourceBundle resources)`: Sets up the screen and confirms identity requirements.

### Account Deletion
- `deleteAccount()`: Validates user input and sends a deletion request to the server.
- `cancelDeletion()`: Cancels the process and returns to the home or settings screen.

## Attributes

- `confirmationField`: Input field for confirming deletion.
- `errorLabel`: Shows feedback for errors or confirmations.
- `stage`: Current JavaFX window stage.

## Usage in Game Flow

`DeleteAccountScreenController` is used when a user decides to permanently delete their account. It validates confirmation and handles the server request securely.

jan.philip.walter May 23. 2025