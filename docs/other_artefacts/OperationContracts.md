# 🧾 Operation Contracts – Splendor Game System (Revised, with Proper Preconditions)

## 1. `register`

**Purpose:** Register a new player.

* **Cross References:** `UC1 User Management, UC1.1.1 Register(username, password, age)`
* **Preconditions:**

  * None
* **Postconditions:**

  * If `username` does not already exist and `age ≥ 10`:

    * A new user was created in the system.
    * A `validationMsg` was returned confirming success.
  * Else:

    * A `validationMsg` was returned with the relevant error.

---

## 2. `login`

**Purpose:** Log in an existing user.

* **Cross References:** `UC1 User Management, UC1.1.2 Login(username, password)`
* **Preconditions:**

  * None
* **Postconditions:**

  * If `username` exists and `password` is correct:

    * The user was marked as logged in.
    * A `validationMsg` was returned confirming success.
  * Else:

    * A `validationMsg` was returned indicating login failure.

---

## 3. `changePassword`

**Purpose:** Change a user's password.

* **Cross References:** `UC1 User Management, UC1.2.1 Change Password(username, newPassword)`
* **Preconditions:**

  * User is currently logged in.
* **Postconditions:**

  * If credentials were valid:

    * Password was updated in the system.
    * A `validationMsg` was returned confirming success.
  * Else:

    * A `validationMsg` was returned indicating failure.

---

## 4. `changeUsername`

**Purpose:** Change the username of a user.

* **Cross References:** `UC1 User Management, UC1.2.2 Change Username(currentUsername, newUsername)`
* **Preconditions:**

  * User is currently logged in.
* **Postconditions:**

  * If `newUsername` is not already taken:

    * Username was updated in the system.
    * A `validationMsg` was returned confirming success.
  * Else:

    * A `validationMsg` was returned indicating the name is already in use.

---

## 5. `deleteAccount`

**Purpose:** Delete a user account.

* **Cross References:** `UC1 User Management, UC1.2.3 Delete Account(username, password)`
* **Preconditions:**

  * User is currently logged in.
* **Postconditions:**

  * If credentials were valid and user confirmed deletion:

    * Account was deleted from the system.
    * A `validationMsg` was returned confirming success.
  * Else:

    * A `validationMsg` was returned indicating failure.

---

## 6. `createGameSession`

**Purpose:** Create a new game session.

* **Cross References:** `UC2 Play, UC2.1 Create Game Session`
* **Preconditions:**

  * User is currently logged in.
* **Postconditions:**

  * A new game session was created.
  * A `sessionId` was returned.
  * A `validationMsg` was returned confirming success.

---

## 7. `joinLobby`

**Purpose:** Join a game lobby.

* **Cross References:** `UC2 Play, UC2.2 Join Game`
* **Preconditions:**

  * User is currently logged in.
* **Postconditions:**

  * If a session with the given ID exists:

    * The user was added to the lobby.
    * A `validationMsg` was returned confirming success.
  * Else:

    * A `validationMsg` was returned indicating failure (e.g., invalid session ID).

---

## 8. `makeTurn(token?, reserveCard?, buyCard?)`

**Purpose:** Make a player turn.

* **Cross References:** `UC2 Play`
* **Preconditions:**

  * It is the player’s turn.
* **Postconditions:**

  * If the move was valid:

    * Game state was updated.
    * All players and the server were informed.
    * A `turnStatus` was returned confirming the move.
  * Else:

    * The move was rejected.
    * Player was prompted to try again.

---

## 9. `leaveGame`

**Purpose:** Leave the game.

* **Cross References:** `UC2 Play, UC2.4 Leave Game`
* **Preconditions:**

  * Player is currently in a game or lobby.
* **Postconditions:**

  * If the player was the host:

    * The session was closed.
  * Else:

    * The player left the session.
  * A `validationMsg` was returned confirming the outcome.

---

## 10. `saveGameStats(newGameState)`

**Purpose:** Save statistics after the game ended.

* **Cross References:** `UC2 Play`
* **Preconditions:**

  * Game is finished.
  * Player is the host.
* **Postconditions:**

  * Final statistics were saved to the system.

---

## 11. `selectGameMode(gameMode)`

**Purpose:** Select the game mode.

* **Cross References:** `UC2 Play`
* **Preconditions:**

  * User is currently logged in.
* **Postconditions:**

  * Game mode was set for the session.
  * A `validationMsg` was returned confirming the selection.

---

## 12. `sendMsg(msg)`

**Purpose:** Send a chat message.

* **Cross References:** `UC2 Play, UC2.3 Chat`
* **Preconditions:**

  * User is in a lobby or game session.
* **Postconditions:**

  * The message was delivered to all relevant players.

---

## 13. `informPlayersOfNewGameState`

**Purpose:** Inform players about a new game state.

* **Cross References:** `UC2 Play`
* **Preconditions:**

  * A valid game turn has been made.
* **Postconditions:**

  * All players were informed of the updated game state.

---

## 14. `checkForDuplicateUsername`

**Purpose:** Check whether a username is already taken.

* **Cross References:** `UC1 User Management`
* **Preconditions:**

  * None
* **Postconditions:**

  * `isDuplicate` was returned (`true` or `false`).

---

## 15. `verifyCredentials`

**Purpose:** Verify user credentials.

* **Cross References:** `UC1 User Management`
* **Preconditions:**

  * None
* **Postconditions:**

  * `areCorrect` was returned (`true` or `false`).

---
