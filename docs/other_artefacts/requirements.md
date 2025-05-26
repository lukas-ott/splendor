# Use Case: UC1 – Manage Player Account

## UC1.1 Register & Login

### Characteristic Information
- **Goal in Context:** Allow users to register a new account and log in to access the system.
- **Scope:** Splendor Online Game System
- **Level:** User Goal
- **Preconditions:** The user has access to the application.
- **Success End Condition:** The user is successfully registered or logged in.
- **Failed End Condition:** The registration or login process fails due to errors.
- **Primary Actor:** Player
- **Trigger:** The user initiates the registration or login process.

### Main Success Scenario
1. The user opens the application and selects **Register** or **Login**.
2. The system prompts for required credentials (**username, password, and age**).
3. The user enters the requested information.
4. The system validates the input.
5. If **Register**:
   - The system checks for duplicate usernames.
   - The system verifies that the user is at least 12 years old.
   - If both checks pass, the account is created.
6. If **Login**:
   - The system verifies the provided credentials.
   - If correct, the user is logged in.
7. The system confirms successful registration or login.
8. The user is notified of success.

### Extensions
- **3a.** User provides incomplete or invalid data → The system displays an error message.
- **5a.** Duplicate username → The system prompts the user to enter a new username.
- **5b.** Age under 12 → The system displays a message: "Users must be at least 12 years old."
- **6a.** Incorrect login credentials → The system denies access.

---

## UC1.2 Manage Account

### Characteristic Information
- **Goal in Context:** Allow users to change their username, change their password, or delete their player account.
- **Scope:** Splendor Online Game System
- **Level:** User Goal
- **Preconditions:** The user must be logged in.
- **Success End Condition:** The user successfully changes their username, password, or deletes their account.
- **Failed End Condition:** The account update or deletion process fails.
- **Primary Actor:** Player
- **Trigger:** The user initiates an account management action.

### Main Success Scenario
1. The user is logged in and navigates to **Account Settings**.
2. The system displays the available actions: **Change Username**, **Change Password**, or **Delete Account**.
3. The user selects one of the actions.
4. The system asks for the required input and, if needed, requests credential confirmation.
5. The user enters the required information.
6. The system validates the input.
7. If the user selected **Change Username**:
   - The system updates the username.
8. If the user selected **Change Password**:
   - The system asks the user to enter the **current password** and the **new password**.
   - If the current password is correct, the system updates the password.
9. If the user selected **Delete Account**:
   - The system asks for confirmation.
   - If confirmed, the account is permanently deleted.
10. The system confirms the successful operation.
11. The user is notified of success.

### Extensions
- **3a.** User provides invalid data → The system displays an error message.
- **6a.** Attempt to delete account without authentication → The system denies the request.

---

# Use Case: UC2 – Play

### Characteristic Information
- **Goal in Context:** Enable a player (host) to start and conduct a round-based game session with human and/or AI players connected via network.
- **Scope:** Splendor Online Game System
- **Level:** User Goal
- **Preconditions:**
   - The player is logged in.
   - A lobby exists.
   - All required player slots are filled with connected clients (human or AI).
- **Success End Condition:** The game is played to completion and a winner is determined on all clients.
- **Failed End Condition:** The game session cannot be started or is disrupted.
- **Primary Actor:** Player (Host)
- **Trigger:** The host initiates the game start after all players (including AI) are connected.

---

### Main Success Scenario
1. The player selects **Multiplayer** from the main menu and creates a lobby.
2. Other human players connect via session code.
3. The host adds **AI clients**, which connect over the network like human players.
4. The host monitors the player list in the lobby.
5. Once all required player slots are filled, the host clicks **Start Game**.
6. All connected clients (human and AI) receive the game start command and initialize game state.
7. The game proceeds in turns:
   - Each client manages its own turn logic (AI or player interaction).
   - Game actions are sent to all clients to maintain sync.
8. After each turn, the clients evaluate whether a winning condition has been reached.
9. Once a client detects a winner:
   - It broadcasts the result to all players.
   - All clients display the game result.


---

### Extensions
- **4a.** Not all slots filled → Host cannot start the game; start button is not clickable.

---

### Sub-Variations
- **3b.** Mixed game: 2 human players + 2 remote AI.
- **7a.** AI clients use different difficulty models.

---

### Related Information
- **Priority:** High
- **Performance Target:** Game state must stay synchronized across all clients with <500ms delay.
- **Frequency:** Frequently used core feature.
- **Subordinate Use Cases:** UC2.1 Create Game Session, UC2.2 Join Game, UC2.3 Chat, UC2.4 Leave Game
- **Channel to Primary Actor:** Game UI
- **Secondary Actors:** AI Clients (via Network), Human Players
- **Channels to Secondary Actors:** WebSocket (game sync), REST API (match setup/reporting)

---

## UC2.1 Create Game Session

### Characteristic Information
- **Goal in Context:** Allow players to start a new game session by selecting a mode (Singleplayer or Multiplayer), and proceed accordingly.
- **Scope:** Splendor Online Game System
- **Level:** Subfunction
- **Preconditions:** The player is logged in.
- **Success End Condition:** A new game session is successfully created or joined, and the player enters the lobby.
- **Failed End Condition:** The session creation or join process fails.
- **Primary Actor:** Player
- **Trigger:** The player selects **Singleplayer** or **Multiplayer** from the main menu.

### Main Success Scenario
1. The player selects **Singleplayer** or **Multiplayer** from the main menu.
2. If **Singleplayer** is selected:
   - The system creates a new session with AI players.
   - The player is sent directly to the game lobby.
3. If **Multiplayer** is selected:
   - The system displays options: **Create Lobby** or **Enter Game Code to Join**.
   - The player selects an option.
4. If the player selects **Create Lobby**:
   - The system creates a new multiplayer session and assigns the player as host.
   - The system displays the lobby with a session code.
5. If the player enters a **session code** to join:
   - The system validates the code.
   - If valid, the player is added to the existing lobby.
6. The game lobby is displayed.

### Extensions
- **5a.** Invalid or expired session code → The system displays an error.
- **5b.** Game lobby is full → The system denies the join request and informs the player.

---

## UC2.2 Join Game

### Characteristic Information
- **Goal in Context:** Allow players to join an existing multiplayer game session using a session code.
- **Scope:** Splendor Online Game System
- **Level:** Subfunction
- **Preconditions:**
   - The player is logged in.
   - A valid game session exists that is accepting new players.
- **Success End Condition:** The player successfully joins the game lobby.
- **Failed End Condition:** The join attempt fails (e.g., invalid code, full session).
- **Primary Actor:** Player
- **Trigger:** The player selects **Multiplayer** → **Join Game** and enters a session code.

### Main Success Scenario
1. The player selects **Multiplayer** from the main menu.
2. The system displays options: **Create Lobby** or **Join Game**.
3. The player selects **Join Game**.
4. The system prompts the player to enter a session code.
5. The player enters the session code.
6. The system validates the session code.
7. If the code is valid and the session is joinable:
   - The player is added to the game lobby.
   - The system updates the lobby status.
8. The player sees the game lobby and waits for the game to begin.

### Extensions
- **5a.** The player enters an empty or malformed code → The system prompts the user to try again.
- **6a.** Invalid or expired session code → The system displays an error message.
- **6b.** The game session is already full → The system informs the player and denies the join request.
- **6c.** The session is already in progress → The system denies the join request and suggests creating a new session.
- **7a.** Connection to the game server fails → The system displays a network error and offers a retry.

---

## UC2.3 Chat

### Characteristic Information
- **Goal in Context:** Enable players to send and receive text messages in the game lobby and during gameplay.
- **Scope:** Splendor Online Game System
- **Preconditions:** The player is in a lobby or game session.
- **Success End Condition:** Messages are successfully sent and received.
- **Failed End Condition:** Messages are not transmitted.
- **Primary Actor:** Player
- **Trigger:** The player types a message.

### Main Success Scenario
1. The player opens the chat interface.
2. The player types a message.
3. The player presses enter.
4. The system displays the message in the chat.

### Extensions
- **3a.** The player types "hello" → AI clients respond with a greeting in the chat.

---

## UC2.4 Leave Game

### Characteristic Information
- **Goal in Context:** Allow players (human or AI) to exit a game session, either in the lobby or during gameplay.
- **Scope:** Splendor Online Game System
- **Level:** Subfunction
- **Preconditions:** The player is either in the game lobby or an active game session.
- **Success End Condition:** The player is removed from the session or the game ends if applicable.
- **Failed End Condition:** The player is not removed due to a system error.
- **Primary Actor:** Player
- **Trigger:** The player chooses to leave the game or disconnects unexpectedly.

### Main Success Scenario
1. The player is in a game lobby or an active game session.
2. The player selects **Leave Game** or disconnects.
3. The system detects the leave request or disconnection.
4. If the player is in the **lobby**:
   - The player is removed from the lobby.
5. If the player is in an **active game session**:
   - The system ends the game session.
   - All players are notified that the game has ended.
6. The system returns the player to the main menu.

### Extensions
- **2a.** The player disconnects due to a network error → The system handles it the same as a leave action.
- **4a.** The lobby becomes empty → The system deletes the lobby.
- **4b.** The **host leaves the lobby** → The system immediately deletes the lobby and notifies remaining players.
- **5a.** An AI leaves during gameplay → The system also ends the session, just like with human players.
- **6a.** The system fails to remove the player → An error message is shown, and retry is prompted.
