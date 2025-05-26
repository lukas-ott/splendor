# Server
  - [ServerLauncher](docs/server/ServerLauncher.md) to launch the server environment.

- ## Application
  - [AiNameHelper](docs/server/application/AiNameHelper.md) to generate unique AI names.
  - [ServerApplication](docs/server/application/ServerApplication.md) to bootstrap the backend server.
  - [User](docs/server/application/User.md) to represent user accounts.

- ### Game logic
  - [GameServerEndpoint](docs/server/application/game_logic/GameServerEndpoint.md) to handle real-time communication between server and clients.
  - [GameSession](docs/server/application/game_logic/GameSession.md) to represent active game sessions.
  - [GameSessionManager](docs/server/application/game_logic/GameSessionManager.md) to manage multiple game sessions.

- ### User Management
  - [UserAPI](docs/server/application/user_management/UserAPI.md) to provide endpoints for user operations.
  - [UserHandler](docs/server/application/user_management/UserHandler.md) to implement business logic for user operations.
  - [UserStatsHandler](docs/server/application/user_management/UserStatsHandler.md) to handle user statistics updates and queries.
  - [WarmupResource](docs/server/application/user_management/WarmupResource.md) to verify server readiness.

- ## Technical
  - [DatabaseConnector](docs/server/technical/DatabaseConnector.md) to manage database connections.

- ### User Management
  - [PasswordHandler](docs/server/technical/user_management/PasswordHandler.md) to manage password encryption and verification.
  - [UserRepository](docs/server/technical/user_management/UserRepository.md) to interact with the user data source.
  - [UserStatsRepository](docs/server/technical/user_management/UserStatsRepository.md) to persist user statistics.


# AI Client

- ## Application
  - [AiClient](docs/ai%20client/application/AiClient.md) to simulate AI gameplay behavior.
  - [Controller](docs/ai%20client/application/Controller.md) base class for AI-related screens.
  - [GameClientEndpoint](docs/ai%20client/application/GameClientEndpoint.md) to connect AI clients to the server.
  - [GameController](docs/ai%20client/application/GameController.md) to control game logic and flow.
  - [JoinController](docs/ai%20client/application/JoinController.md) to join multiplayer lobbies.
  - [LobbyController](docs/ai%20client/application/LobbyController.md) to manage lobby interactions.
  - [StartScreen](docs/ai%20client/application/StartScreen.md) to initialize the AI game interface.

- ## Utils
  - [AiUser](docs/ai%20client/utils/AiUser.md) to manage AI user data.
  - [ConstantsManager](docs/ai%20client/utils/ConstantsManager.md) to store and retrieve AI constants.

# Client

- ## Application
  - [App](docs/client/application/App.md) to launch the GUI.
  - [ChangeNameScreenController](docs/client/application/ChangeNameScreenController.md) to change display name.
  - [ChangePasswordScreenController](docs/client/application/ChangePasswordScreenController.md) to update passwords.
  - [Controller](docs/client/application/Controller.md) base class for GUI logic.
  - [DeleteAccountScreenController](docs/client/application/DeleteAccountScreenController.md) to delete accounts.
  - [GameClientEndpoint](docs/client/application/GameClientEndpoint.md) to manage client-server messages.
  - [GameController](docs/client/application/GameController.md) to render game state and process actions.
  - [GameLeaderboardScreenController](docs/client/application/GameLeaderboardScreenController.md) to display leaderboard.
  - [GameScreenController](docs/client/application/GameScreenController.md) to handle core gameplay.
  - [HomeScreenController](docs/client/application/HomeScreenController.md) to navigate home features.
  - [LoginScreenController](docs/client/application/LoginScreenController.md) to handle login.
  - [MultiplayerLaunchScreenController](docs/client/application/MultiplayerLaunchScreenController.md) to choose MP mode.
  - [MultiplayerLobbyScreenController](docs/client/application/MultiplayerLobbyScreenController.md) to manage MP lobbies.
  - [ProfileScreenController](docs/client/application/ProfileScreenController.md) to access profile settings.
  - [RegisterScreenController](docs/client/application/RegisterScreenController.md) to register users.
  - [SettingsScreenController](docs/client/application/SettingsScreenController.md) to adjust settings.
  - [SingleplayerLaunchScreenController](docs/client/application/SingleplayerLaunchScreenController.md) to launch solo game.
  - [StartScreen](docs/client/application/StartScreen.md) to begin the application.
  - [StartScreenController](docs/client/application/StartScreenController.md) to manage initial navigation.

- ## Utils
  - [ConstantsManager](docs/client/utils/ConstantsManager.md) to handle shared configuration values.
  - [MusicManager](docs/client/utils/MusicManager.md) to manage background music.
  - [SoundManager](docs/client/utils/SoundManager.md) to manage sound effects.
  - [User](docs/client/utils/User.md) to hold user data.
  - [UserData](docs/client/utils/UserData.md) to track user settings.
  - [UserStats](docs/client/utils/UserStats.md) to track user statistics.

# Domain
- [AbstractPlayer](docs/domain/AbstractPlayer.md) is the common interface for all players
- [AiDifficulty](docs/domain/AiDifficulty.md) defines AI difficulty levels
- [AiPlayer](docs/domain/AiPlayer.md) represents an AI-controlled player
- [Card](docs/domain/Card.md) defines card data including cost and points
- [Chat](docs/domain/Chat.md) manages in-game chat logic
- [ChatMessage](docs/domain/ChatMessage.md) defines a single chat message
- [GameState](docs/domain/GameState.md) tracks the full current game state
- [HumanPlayer](docs/domain/HumanPlayer.md) represents a human player
- [Noble](docs/domain/Noble.md) defines nobles that grant bonus points
- [StoneType](docs/domain/StoneType.md) enumerates resource types
- [User](docs/domain/User.md) represents a registered player profile

## Exceptions
- [ActionNotPossibleException](docs/domain/Exceptions/ActionNotPossibleException.md) to signal when a game action is not permitted
- [CantAffordItemException](docs/domain/Exceptions/CantAffordItemException.md) to indicate a player lacks resources for a purchase
- [DepletedResourceException](docs/domain/Exceptions/DepletedResourceException.md) to signal that a targeted resource is no longer available
- [IllegalMoveCombinationException](docs/domain/Exceptions/IllegalMoveCombinationException.md) to signal an invalid combination of moves
- [NotYourTurnException](docs/domain/Exceptions/NotYourTurnException.md) to indicate that it's not the player's turn
- [TooManyStonesException](docs/domain/Exceptions/TooManyStonesException.md) to indicate that a player has exceeded the stone limit

### SessionHandlingExceptions
- [SessionAlreadyFullException](docs/domain/Exceptions/SessionHandlingExceptions/SessionAlreadyFullException.md) to signal when a session has reached its maximum capacity
- [SessionAlreadyStartedException](docs/domain/Exceptions/SessionHandlingExceptions/SessionAlreadyStartedException.md) to indicate that a session has already begun
- [SessionHandlingException](docs/domain/Exceptions/SessionHandlingExceptions/SessionHandlingException.md) to represent a general error in session handling
- [SessionNotFoundException](docs/domain/Exceptions/SessionHandlingExceptions/SessionNotFoundException.md) to signal that a requested session could not be found
- [SinglePlayerSessionException](docs/domain/Exceptions/SessionHandlingExceptions/SinglePlayerSessionException.md) to indicate an invalid action in a single-player session

## Messages
- [AbstractPackage](docs/domain/messages/AbstractPackage.md) is the base class for all communication packages
- [CreateSessionPackage](docs/domain/messages/CreateSessionPackage.md) to initiate a new session
- [GameOverPackage](docs/domain/messages/GameOverPackage.md) to send game-end data
- [JoinSessionPackage](docs/domain/messages/JoinSessionPackage.md) for session joining requests
- [LeaveSessionPackage](docs/domain/messages/LeaveSessionPackage.md) to signal player leaving
- [PackageDecoder](docs/domain/messages/PackageDecoder.md) to decode received packages
- [PackageEncoder](docs/domain/messages/PackageEncoder.md) to encode outgoing packages
- [PlayerActionPackage](docs/domain/messages/PlayerActionPackage.md) to transmit player actions

## Moves
- [AbstractMove](docs/domain/moves/AbstractMove.md) is the superclass for all move types
- [BuyMove](docs/domain/moves/BuyMove.md) defines logic for purchasing a card
- [MoveCalculationHelper](docs/domain/moves/MoveCalculationHelper.md) assists with move validation
- [MoveGenerator](docs/domain/moves/MoveGenerator.md) generates legal moves for a game state
- [ReserveMove](docs/domain/moves/ReserveMove.md) reserves a card and may give a joker
- [TakeMove](docs/domain/moves/TakeMove.md) allows a player to collect resources

Last updated: May 23, 2025
