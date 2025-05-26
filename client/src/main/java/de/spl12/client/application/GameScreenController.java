package de.spl12.client.application;

import de.spl12.client.utils.ConstantsManager;
import de.spl12.client.utils.MusicManager;
import de.spl12.client.utils.SoundManager;
import de.spl12.domain.Card;
import de.spl12.domain.Noble;
import de.spl12.domain.StoneType;
import de.spl12.domain.ChatMessage;
import de.spl12.domain.AbstractPlayer;
import de.spl12.domain.Exceptions.NotYourTurnException;
import de.spl12.domain.Exceptions.IllegalMoveCombinationException;
import de.spl12.domain.Exceptions.DepletedResourceException;
import de.spl12.domain.Exceptions.ActionNotPossibleException;
import de.spl12.domain.Exceptions.CantAffordItemException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Controller for the in game screen of the Java FX application.
 * Handles actions of the user during the game or delegates them to the {@link GameController}.
 * Gets called to update the in game UI whenever a change in the game state occurs.
 *
 * @author leon.kuersch
 */
public class GameScreenController extends Controller {

  private static final Logger LOGGER = Logger.getLogger(GameScreenController.class.getName());

  @FXML private Text stage1count;
  @FXML private Text stage2count;
  @FXML private Text stage3count;

  @FXML private Button stage1stack;
  @FXML private Button stage2stack;
  @FXML private Button stage3stack;

  @FXML private Button stage1card1;
  @FXML private ImageView stage1card1_image;
  @FXML private Button stage1card2;
  @FXML private ImageView stage1card2_image;
  @FXML private Button stage1card3;
  @FXML private ImageView stage1card3_image;
  @FXML private Button stage1card4;
  @FXML private ImageView stage1card4_image;

  @FXML private Button stage2card1;
  @FXML private ImageView stage2card1_image;
  @FXML private Button stage2card2;
  @FXML private ImageView stage2card2_image;
  @FXML private Button stage2card3;
  @FXML private ImageView stage2card3_image;
  @FXML private Button stage2card4;
  @FXML private ImageView stage2card4_image;

  @FXML private Button stage3card1;
  @FXML private ImageView stage3card1_image;
  @FXML private Button stage3card2;
  @FXML private ImageView stage3card2_image;
  @FXML private Button stage3card3;
  @FXML private ImageView stage3card3_image;
  @FXML private Button stage3card4;
  @FXML private ImageView stage3card4_image;

  @FXML private AnchorPane rootPane;

  @FXML private Pane explanation_pane;

  @FXML private Button red_pouch;
  @FXML private Button blue_pouch;
  @FXML private Button green_pouch;
  @FXML private Button black_pouch;
  @FXML private Button white_pouch;
  @FXML private Button coin_pouch;

  @FXML private Text red_pouch_count;
  @FXML private Text blue_pouch_count;
  @FXML private Text green_pouch_count;
  @FXML private Text black_pouch_count;
  @FXML private Text white_pouch_count;
  @FXML private Text coin_pouch_count;

  @FXML private Pane nobles;
  @FXML private Button noble1;
  @FXML private ImageView noble1_image;
  @FXML private Button noble2;
  @FXML private ImageView noble2_image;
  @FXML private Button noble3;
  @FXML private ImageView noble3_image;
  @FXML private Button noble4;
  @FXML private ImageView noble4_image;
  @FXML private Button noble5;
  @FXML private ImageView noble5_image;

  @FXML private Pane player;
  @FXML private Text player_name;
  @FXML private Text player_points;
  @FXML private Text player_red;
  @FXML private Text player_blue;
  @FXML private Text player_green;
  @FXML private Text player_black;
  @FXML private Text player_white;
  @FXML private Text player_red_bonus;
  @FXML private Text player_blue_bonus;
  @FXML private Text player_green_bonus;
  @FXML private Text player_black_bonus;
  @FXML private Text player_white_bonus;

  @FXML private Text player_coins;
  @FXML private Button reserved1;
  @FXML private ImageView reserved1_image;
  @FXML private Button reserved2;
  @FXML private ImageView reserved2_image;
  @FXML private Button reserved3;
  @FXML private ImageView reserved3_image;

  @FXML private Button player_noble1;
  @FXML private ImageView player_noble1_image;
  @FXML private Button player_noble2;
  @FXML private ImageView player_noble2_image;
  @FXML private Button player_noble3;
  @FXML private ImageView player_noble3_image;
  @FXML private Button player_noble4;
  @FXML private ImageView player_noble4_image;

  @FXML private Button player_gem_button_blue;
  @FXML private Button player_gem_button_green;
  @FXML private Button player_gem_button_red;
  @FXML private Button player_gem_button_black;
  @FXML private Button player_gem_button_white;

  @FXML private Pane player1;
  @FXML private Text player1_name;
  @FXML private Text player1_points;
  @FXML private Text player1_red;
  @FXML private Text player1_blue;
  @FXML private Text player1_green;
  @FXML private Text player1_black;
  @FXML private Text player1_white;
  @FXML private Text player1_coins;
  @FXML private Text player1_red_bonus;
  @FXML private Text player1_blue_bonus;
  @FXML private Text player1_green_bonus;
  @FXML private Text player1_black_bonus;
  @FXML private Text player1_white_bonus;

  @FXML private Pane player2;
  @FXML private Text player2_name;
  @FXML private Text player2_points;
  @FXML private Text player2_red;
  @FXML private Text player2_blue;
  @FXML private Text player2_green;
  @FXML private Text player2_black;
  @FXML private Text player2_white;
  @FXML private Text player2_coins;
  @FXML private Text player2_red_bonus;
  @FXML private Text player2_blue_bonus;
  @FXML private Text player2_green_bonus;
  @FXML private Text player2_black_bonus;
  @FXML private Text player2_white_bonus;

  @FXML private Pane player3;
  @FXML private Text player3_name;
  @FXML private Text player3_points;
  @FXML private Text player3_red;
  @FXML private Text player3_blue;
  @FXML private Text player3_green;
  @FXML private Text player3_black;
  @FXML private Text player3_white;
  @FXML private Text player3_coins;
  @FXML private Text player3_red_bonus;
  @FXML private Text player3_blue_bonus;
  @FXML private Text player3_green_bonus;
  @FXML private Text player3_black_bonus;
  @FXML private Text player3_white_bonus;

  @FXML private Pane player4;
  @FXML private Text player4_name;
  @FXML private Text player4_points;
  @FXML private Text player4_red;
  @FXML private Text player4_blue;
  @FXML private Text player4_green;
  @FXML private Text player4_black;
  @FXML private Text player4_white;
  @FXML private Text player4_coins;
  @FXML private Text player4_red_bonus;
  @FXML private Text player4_blue_bonus;
  @FXML private Text player4_green_bonus;
  @FXML private Text player4_black_bonus;
  @FXML private Text player4_white_bonus;

  @FXML private Pane chat;
  @FXML private TextField messageField;

  @FXML private Pane settings;
  @FXML private Slider soundSlider;
  @FXML private Slider musicSlider;
  @FXML private CheckBox soundMuteBox;
  @FXML private CheckBox musicMuteBox;

  @FXML private ScrollPane chatHistory;
  @FXML private VBox chat_container;

  // Leaderboard
  @FXML private Pane leaderboard;
  @FXML Pane player1_leaderboard;
  @FXML Text player1_leaderboard_name;
  @FXML Pane player2_leaderboard;
  @FXML Text player2_leaderboard_name;
  @FXML Pane player3_leaderboard;
  @FXML Text player3_leaderboard_name;
  @FXML Pane player4_leaderboard;
  @FXML Text player4_leaderboard_name;

  // Alert Pane, when needing to return gems or select nobles
  @FXML private Pane alert;
  @FXML private Pane gems;
  @FXML private Rectangle tint;
  @FXML private Text alert_title;
  @FXML private Text alert_text;
  @FXML private Button settings_button;

  // Tooltip when hovering over cards
  @FXML private Text tooltip_title;
  @FXML private Pane card_bonus;
  @FXML private ImageView card_bonus_color;
  @FXML private Text card_prestige;
  @FXML private Text card_prestige1;
  @FXML private Pane card_cost_1;
  @FXML private ImageView card_cost_1_color;
  @FXML private Text card_cost_1_amount;
  @FXML private Pane card_cost_2;
  @FXML private ImageView card_cost_2_color;
  @FXML private Text card_cost_2_amount;
  @FXML private Pane card_cost_3;
  @FXML private ImageView card_cost_3_color;
  @FXML private Text card_cost_3_amount;
  @FXML private Pane card_cost_4;
  @FXML private ImageView card_cost_4_color;
  @FXML private Text card_cost_4_amount;
  @FXML private Pane card_cost_5;
  @FXML private ImageView card_cost_5_color;
  @FXML private Text card_cost_5_amount;
  @FXML private Pane primary_action;
  @FXML private Text primary_action_text;
  @FXML private Pane secondary_action;
  @FXML private Text secondary_action_text;
  @FXML private ImageView tooltip_pane;
  @FXML private Pane buy_sell_pane;
  @FXML private Text tooltip_text;
  @FXML private Text tooltip_cost;
  int offset = 0;
  private ColorAdjust grey_out = new ColorAdjust();

  @FXML private Pane your_turn;

  GameController gameController;
  private int gems_z_index;
  private int nobles_z_index;

  PauseTransition delay = new PauseTransition(Duration.seconds(0.5));


  /**
   * Initializes the GameScreenController with all necessary configurations, event handlers, and UI
   * setups.
   *
   * <p>This method is automatically invoked when the FXML file for the Game Screen is loaded. It
   * sets up references to required controllers, initializes game music and sound managers,
   * configures UI elements, establishes event handlers for user interactions, and prepares the
   * default state of various components.
   *
   * <p>Specific actions include: - Setting up the GameController instance and binding it to the
   * current screen. - Configuring key press events to open the chat, close the chat, or change
   * scenes. - Setting visibility, styles, and tooltips for components such as the chat, card
   * buttons, and explanation pane. - Linking UI sliders for sound and music to their respective
   * managers and their mute states. - Handling button hover events and updating the UI for gameplay
   * elements like cards and gems. - Assigning random names to AI players using predefined name
   * arrays. - Ensuring the proper layout and styles for the chat history.
   *
   * <p>The method also ensures appropriate UI updates and interaction behavior based on the current
   * game state.
   *
   */
  @FXML
  public void initialize() {
    this.gameController = GameController.getInstance();
    this.gameController.setGameScreenController(this);
    rootPane.setOnKeyPressed(
        event -> {
          if (event.getCode() == KeyCode.T) {
            openChat();
          }
          if (event.getCode() == KeyCode.K) {
            GameLeaderboardScreenController glc =
                changeScene("/FXML/game_leaderboard.fxml").getController();
            glc.setStage(stage);
          }
        });
    chat.setOnKeyPressed(
        event -> {
          if (event.getCode() == KeyCode.ESCAPE) {
            closeChat();
          }
        });
    messageField.setOnKeyPressed(
        event -> {
          if (event.getCode() == KeyCode.ENTER) {
            sendMessage();
          }
        });
    chat.setVisible(false);
    soundManager = SoundManager.getInstance();
    musicManager = MusicManager.getInstance();
    musicManager.gameMusic();

    soundSlider.setValue(soundManager.getVolume() * 100);
    soundMuteBox.setSelected(soundManager.isMuted());

    musicSlider.setValue(musicManager.getVolume() * 100);
    musicMuteBox.setSelected(musicManager.isMuted());

    // === EventHandler verbinden ===
    soundSlider
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              soundManager.setVolume(newVal.doubleValue() / 100);
            });

    musicSlider
        .valueProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              musicManager.setVolume(newVal.doubleValue() / 100);
            });

    soundMuteBox
        .selectedProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              soundManager.setMuted(newVal);
              soundSlider.setDisable(newVal);
            });

    musicMuteBox
        .selectedProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              musicManager.setMuted(newVal);
              musicSlider.setDisable(newVal);
            });
    if (soundMuteBox.isSelected()) {
      soundSlider.setDisable(true);
    }
    if (musicMuteBox.isSelected()) {
      musicSlider.setDisable(true);
    }
    grey_out.setSaturation(-0.85);
    Button[] buttons = {
      stage1card1,
      stage1card2,
      stage1card3,
      stage1card4,
      stage2card1,
      stage2card2,
      stage2card3,
      stage2card4,
      stage3card1,
      stage3card2,
      stage3card3,
      stage3card4,
      noble1,
      noble2,
      noble3,
      noble4,
      noble5,
      stage1stack,
      stage2stack,
      stage3stack,
      red_pouch,
      white_pouch,
      green_pouch,
      black_pouch,
      blue_pouch,
      coin_pouch,
      player_gem_button_black,
      player_gem_button_blue,
      player_gem_button_green,
      player_gem_button_red,
      player_gem_button_white,
      reserved1,
      reserved2,
      reserved3
    };
    chatHistory.setStyle(
        "-fx-background-color: transparent;" + "-fx-background-insets: 0;" + "-fx-padding: 0;");
    Platform.runLater(
        () -> {
          Node viewport = chatHistory.lookup(".viewport");
          if (viewport != null) {
            viewport.setStyle("-fx-background-color: transparent;");
          }
        });
    chatHistory.setFitToWidth(true);
    chatHistory.setFitToHeight(false);
    settings.setVisible(false);
    this.gems_z_index = ((Pane) gems.getParent()).getChildren().indexOf(gems);
    this.nobles_z_index = ((Pane) nobles.getParent()).getChildren().indexOf(nobles);
    // Set Tooltip
    delay.setOnFinished(e -> explanation_pane.setVisible(true));
    for (Button button : buttons) {
      this.handleButtonHover(button);
    }
    this.updateUI();
  }

  /**
   * Updates the game user interface to reflect the current state of the game.
   *
   * <p>This method is executed on the JavaFX Application Thread and ensures that all visual
   * elements of the game UI are synchronized with the latest game data. It performs the following
   * operations:
   *
   * <p>- Sets up the display of development cards available in the game. - Updates the display of
   * cards reserved by the player. - Adjusts the visual representation of nobles available for
   * selection. - Updates the display of nobles currently visiting the player. - Loads and updates
   * the counts of available stones (gems) in the game pool. - Loads and updates the player's owned
   * stone counts. - Updates the display of bonus counts and their corresponding visuals. - Updates
   * player banners to reflect the current game state, including points and status. - Refreshes the
   * chat UI to show the latest messages.
   *
   * <p>Additionally, this method logs the UI update action for debugging or monitoring purposes.
   */
  public void updateUI() {
    Platform.runLater(
        () -> {
          this.setupCards();
          this.setupReservedCards();
          this.setupNobles();
          this.setupVisitingNobles();
          this.loadAvailableStoneCounts();
          this.loadOwnedStoneCounts();
          this.loadBonusCounts();
          this.updatePlayerBanners();
          this.loadChat();
          // TODO: Implement Noble/Gem Alert into Game Logic
          // this.alert_select_noble(new int[]{0, 1, 1, 0, 1});
          // this.alert_return_gems(14);
          LOGGER.info("updating ingame UI");
        });
  }

  /**
   * Updates the UI to display the current counts of each type of stone available in the game's
   * shared stone pool.
   *
   * <p>This method retrieves the counts of all stone types (RED, BLUE, GREEN, BLACK, WHITE, GOLD)
   * from the game's stone pool through the game controller and updates the respective text elements
   * to reflect these values within the user interface.
   *
   * <p>The stone counts are dynamically fetched from the current game state, ensuring that the
   * displayed data matches the most recent game conditions.
   *
   * <p>Associated UI elements updated: - Red stone count: `red_pouch_count` - Blue stone count:
   * `blue_pouch_count` - Green stone count: `green_pouch_count` - Black stone count:
   * `black_pouch_count` - White stone count: `white_pouch_count` - Gold coin count:
   * `coin_pouch_count`
   */
  private void loadAvailableStoneCounts() {
    this.red_pouch_count.setText(
        String.valueOf(this.gameController.getGameState().getStonePool().get(StoneType.RED)));
    this.blue_pouch_count.setText(
        String.valueOf(this.gameController.getGameState().getStonePool().get(StoneType.BLUE)));
    this.green_pouch_count.setText(
        String.valueOf(this.gameController.getGameState().getStonePool().get(StoneType.GREEN)));
    this.black_pouch_count.setText(
        String.valueOf(this.gameController.getGameState().getStonePool().get(StoneType.BLACK)));
    this.white_pouch_count.setText(
        String.valueOf(this.gameController.getGameState().getStonePool().get(StoneType.WHITE)));
    this.coin_pouch_count.setText(
        String.valueOf(this.gameController.getGameState().getStonePool().get(StoneType.GOLD)));
  }

  /**
   * Updates the UI to display the current counts of each type of stone owned by the player.
   *
   * <p>This method retrieves the counts for all stone types (RED, BLUE, GREEN, BLACK, WHITE, GOLD)
   * from the player's stone inventory through the game controller and updates the respective text
   * elements in the user interface to reflect these values. Each count is converted to a string and
   * set to the appropriate UI element.
   *
   * <p>The updated UI elements include: - Red stone count: `player_red` - Blue stone count:
   * `player_blue` - Green stone count: `player_green` - Black stone count: `player_black` - White
   * stone count: `player_white` - Gold coin count: `player_coins`
   *
   * <p>This ensures that the displayed data in the UI accurately represents the player's current
   * inventory in the game.
   */
  private void loadOwnedStoneCounts() {
    this.player_red.setText(
        String.valueOf(this.gameController.getPlayer().getStoneInventory().get(StoneType.RED)));
    this.player_blue.setText(
        String.valueOf(this.gameController.getPlayer().getStoneInventory().get(StoneType.BLUE)));
    this.player_green.setText(
        String.valueOf(this.gameController.getPlayer().getStoneInventory().get(StoneType.GREEN)));
    this.player_black.setText(
        String.valueOf(this.gameController.getPlayer().getStoneInventory().get(StoneType.BLACK)));
    this.player_white.setText(
        String.valueOf(this.gameController.getPlayer().getStoneInventory().get(StoneType.WHITE)));
    this.player_coins.setText(
        String.valueOf(this.gameController.getPlayer().getStoneInventory().get(StoneType.GOLD)));
  }

  /**
   * Updates the bonus count display for each stone type (red, blue, green, black, white). The bonus
   * values are retrieved from the player's bonus data for the respective stone types and set to the
   * corresponding text fields in the UI.
   */
  private void loadBonusCounts() {
    this.player_red_bonus.setText(
        "+" + this.gameController.getPlayer().getBonusForType(StoneType.RED));
    this.player_blue_bonus.setText(
        "+" + this.gameController.getPlayer().getBonusForType(StoneType.BLUE));
    this.player_green_bonus.setText(
        "+" + this.gameController.getPlayer().getBonusForType(StoneType.GREEN));
    this.player_black_bonus.setText(
        "+" + this.gameController.getPlayer().getBonusForType(StoneType.BLACK));
    this.player_white_bonus.setText(
        "+" + this.gameController.getPlayer().getBonusForType(StoneType.WHITE));
  }

  /**
   * Initializes and sets up the images and counters for the cards from three different card decks.
   *
   * <p>This method retrieves card data from the game state managed by the game controller. It
   * ensures the correct display of card stack sizes and card images for three different stages. The
   * method updates the stack counts for each stage and sets the image representations of the top
   * four cards in each deck, if available.
   *
   * <p>The card images are generated using the file paths built from the card objects. If a card at
   * a specific position is null, the corresponding image is set to null.
   */
  private void setupCards() {
    List<Card> firstDeck = this.gameController.getGameState().getFirstCardDeck();
    List<Card> secondDeck = this.gameController.getGameState().getSecondCardDeck();
    List<Card> thirdDeck = this.gameController.getGameState().getThirdCardDeck();

    this.stage1count.setText(
        String.valueOf(this.gameController.getGameState().getFirstCardStack().size()));
    this.stage2count.setText(
        String.valueOf(this.gameController.getGameState().getSecondCardStack().size()));
    this.stage3count.setText(
        String.valueOf(this.gameController.getGameState().getThirdCardStack().size()));

    this.stage1card1_image.setImage(
        (firstDeck.get(0) != null) ? new Image(this.buildPathForCard(firstDeck.get(0))) : null);
    this.stage1card2_image.setImage(
        (firstDeck.get(1) != null) ? new Image(this.buildPathForCard(firstDeck.get(1))) : null);
    this.stage1card3_image.setImage(
        (firstDeck.get(2) != null) ? new Image(this.buildPathForCard(firstDeck.get(2))) : null);
    this.stage1card4_image.setImage(
        (firstDeck.get(3) != null) ? new Image(this.buildPathForCard(firstDeck.get(3))) : null);

    this.stage2card1_image.setImage(
        (secondDeck.get(0) != null) ? new Image(this.buildPathForCard(secondDeck.get(0))) : null);
    this.stage2card2_image.setImage(
        (secondDeck.get(1) != null) ? new Image(this.buildPathForCard(secondDeck.get(1))) : null);
    this.stage2card3_image.setImage(
        (secondDeck.get(2) != null) ? new Image(this.buildPathForCard(secondDeck.get(2))) : null);
    this.stage2card4_image.setImage(
        (secondDeck.get(3) != null) ? new Image(this.buildPathForCard(secondDeck.get(3))) : null);

    this.stage3card1_image.setImage(
        (thirdDeck.get(0) != null) ? new Image(this.buildPathForCard(thirdDeck.get(0))) : null);
    this.stage3card2_image.setImage(
        (thirdDeck.get(1) != null) ? new Image(this.buildPathForCard(thirdDeck.get(1))) : null);
    this.stage3card3_image.setImage(
        (thirdDeck.get(2) != null) ? new Image(this.buildPathForCard(thirdDeck.get(2))) : null);
    this.stage3card4_image.setImage(
        (thirdDeck.get(3) != null) ? new Image(this.buildPathForCard(thirdDeck.get(3))) : null);
  }

  /**
   * Sets up the reserved cards displayed in the user interface.
   *
   * <p>This method retrieves the reserved cards of the current player through the game controller.
   * It updates the corresponding image views (reserved1_image, reserved2_image, reserved3_image) to
   * display the appropriate card images if reserved cards are present. If no card is reserved at a
   * specific position, it clears the image for that position.
   *
   * <p>The reserved cards are processed in order: 1. The first reserved card is set to
   * reserved1_image. 2. If a second reserved card exists, it is set to reserved2_image. 3. If a
   * third reserved card exists, it is set to reserved3_image.
   *
   * <p>If the reserved card list is empty or fewer than three cards exist, the corresponding image
   * views are set to null.
   */
  private void setupReservedCards() {
    List<Card> reserved = this.gameController.getPlayer().getReservedCards();

    if (!reserved.isEmpty()) {
      this.reserved1_image.setImage(new Image(this.buildPathForCard(reserved.getFirst())));
    } else {
      this.reserved1_image.setImage(null);
    }
    if (reserved.size() >= 2) {
      this.reserved2_image.setImage(new Image(this.buildPathForCard(reserved.get(1))));
    } else {
      this.reserved2_image.setImage(null);
    }
    if (reserved.size() >= 3) {
      this.reserved3_image.setImage(new Image(this.buildPathForCard(reserved.get(2))));
    } else {
      this.reserved3_image.setImage(null);
    }
  }

  /**
   * Sets up the noble images for the game by retrieving the noble deck from the current game state
   * and assigning corresponding images to the noble image components. If a noble does not exist at
   * a specific index in the noble deck, the respective image is set to null.
   *
   * <p>The method retrieves the noble deck using the gameController and attempts to assign images
   * for up to five noble slots by constructing the image path for each noble. The noble image
   * components are updated accordingly based on the retrieved nobles.
   */
  private void setupNobles() {
    List<Noble> nobles = this.gameController.getGameState().getNobleDeck();

    this.noble1_image.setImage(
        (nobles.get(0) != null) ? new Image(this.buildPathForNoble(nobles.get(0))) : null);
    this.noble2_image.setImage(
        (nobles.get(1) != null) ? new Image(this.buildPathForNoble(nobles.get(1))) : null);
    this.noble3_image.setImage(
        (nobles.get(2) != null) ? new Image(this.buildPathForNoble(nobles.get(2))) : null);
    this.noble4_image.setImage(
        (nobles.get(3) != null) ? new Image(this.buildPathForNoble(nobles.get(3))) : null);
    this.noble5_image.setImage(
        (nobles.get(4) != null) ? new Image(this.buildPathForNoble(nobles.get(4))) : null);
  }

  /**
   * Configures and updates the display for visiting nobles associated with the current player.
   *
   * <p>The method retrieves the list of nobles belonging to the player's game state and updates up
   * to four placeholder images to represent these nobles. The images are dynamically set based on
   * the size of the player's noble list and are cleared if there are fewer nobles than the number
   * of placeholders.
   *
   * <p>For each noble: - If a noble exists at the respective index, its associated image is loaded
   * and displayed. - If no noble exists for a given placeholder, the corresponding image is set to
   * null.
   *
   * <p>The noble images are determined by calling the `buildPathForNoble` method.
   */
  private void setupVisitingNobles() {
    List<Noble> myNobles = this.gameController.getPlayer().getNobles();
    if (!myNobles.isEmpty()) {
      this.player_noble1_image.setImage(new Image(this.buildPathForNoble(myNobles.getFirst())));
    } else {
      this.player_noble1_image.setImage(null);
    }
    if (myNobles.size() >= 2) {
      this.player_noble2_image.setImage(new Image(this.buildPathForNoble(myNobles.get(1))));
    } else {
      this.player_noble2_image.setImage(null);
    }
    if (myNobles.size() >= 3) {
      this.player_noble3_image.setImage(new Image(this.buildPathForNoble(myNobles.get(2))));
    } else {
      this.player_noble3_image.setImage(null);
    }
    if (myNobles.size() >= 4) {
      this.player_noble4_image.setImage(new Image(this.buildPathForNoble(myNobles.get(3))));
    } else {
      this.player_noble4_image.setImage(null);
    }
  }

  /**
   * Loads and displays chat messages in the chat view.
   *
   * <p>This method clears the current chat messages from the chat container and populates it with
   * the messages from the game's chat history. Each message is styled before being added to the
   * container for display.
   *
   * <p>The chat history scroll position is set to the bottom to ensure the most recent messages are
   * visible.
   *
   * <p>Functionality: - Removes existing Text nodes from the chat container. - Retrieves chat
   * messages from the game's chat history. - Creates and styles a Text node for each message. -
   * Adds the styled Text nodes to the chat container. - Sets the chat history scroll bar to the
   * bottom.
   */
  private void loadChat() {
    chat_container.getChildren().removeIf(node -> node instanceof Text);
    for (ChatMessage chatMessage : this.gameController.getGameState().getChat().getMessageList()) {
      Text text = new Text("[" + chatMessage.getSender() + "] " + chatMessage.getMessage());
      text.setStyle(
          "-fx-font-family: 'Songti SC'; -fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: #432700;");
      chat_container.getChildren().add(text);
    }
    chatHistory.setVvalue(1.0);
  }

  /**
   * Builds the file path for a given card based on its attributes such as stage, prestige points,
   * bonus, and associated costs.
   *
   * @param card the card object for which the file path needs to be created. If null, a default
   *     path for the back of a card is returned.
   * @return the constructed file path as a string, representing the image location of the given
   *     card.
   */
  private String buildPathForCard(Card card) {
    if (card == null) {
      return "images/game_tiles/development_cards/level_O/back.png";
    }
    String path = "/images/game_tiles/development_cards/";
    switch (card.getStage()) {
      case 1 -> path += "level_O/";
      case 2 -> path += "level_OO/";
      case 3 -> path += "level_OOO/";
    }
    path += card.getPrestigePoints() + "," + card.getBonus().toString();
    for (StoneType stoneType : StoneType.values()) {
      if (card.getCost().containsKey(stoneType)) {
        path += "," + stoneType.toString() + "=" + card.getCost().get(stoneType);
      }
    }
    return path + ".png";
  }

  /**
   * Constructs the file path for a given Noble object.
   *
   * @param noble the Noble object for which the file path is to be constructed. If null, a default
   *     path for the back of a noble tile is returned.
   * @return the file path as a string. Returns a default path if the noble is null, otherwise
   *     returns the path based on noble's properties.
   */
  private String buildPathForNoble(Noble noble) {
    if (noble == null) {
      return "/images/game_tiles/noble_tiles/back.png";
    }
    String path = "/images/game_tiles/noble_tiles/";
    path += noble.getPropsString();
    return path + ".png";
  }

  /**
   * Handles mouse events on card elements within the game interface. Depending on the type of mouse
   * button interaction, a card may be purchased or reserved. The method also triggers respective
   * sound effects and displays alerts for possible exceptions during the action.
   *
   * @param event the mouse event triggered by an interaction with the card element
   */
  @FXML
  public void cardEvent(MouseEvent event) {
    if (event.getButton() == MouseButton.PRIMARY) {
      soundManager.playBuyDevelopment();
      Object source = event.getSource();
      try {
        if (source.equals(stage1card1)) {
          this.gameController.handleCardPurchase(
              this.gameController.getGameState().getFirstCardDeck().get(0));
        } else if (source.equals(stage1card2)) {
          this.gameController.handleCardPurchase(
              this.gameController.getGameState().getFirstCardDeck().get(1));
        } else if (source.equals(stage1card3)) {
          this.gameController.handleCardPurchase(
              this.gameController.getGameState().getFirstCardDeck().get(2));
        } else if (source.equals(stage1card4)) {
          this.gameController.handleCardPurchase(
              this.gameController.getGameState().getFirstCardDeck().get(3));
        } else if (source.equals(stage2card1)) {
          this.gameController.handleCardPurchase(
              this.gameController.getGameState().getSecondCardDeck().get(0));
        } else if (source.equals(stage2card2)) {
          this.gameController.handleCardPurchase(
              this.gameController.getGameState().getSecondCardDeck().get(1));
        } else if (source.equals(stage2card3)) {
          this.gameController.handleCardPurchase(
              this.gameController.getGameState().getSecondCardDeck().get(2));
        } else if (source.equals(stage2card4)) {
          this.gameController.handleCardPurchase(
              this.gameController.getGameState().getSecondCardDeck().get(3));
        } else if (source.equals(stage3card1)) {
          this.gameController.handleCardPurchase(
              this.gameController.getGameState().getThirdCardDeck().get(0));
        } else if (source.equals(stage3card2)) {
          this.gameController.handleCardPurchase(
              this.gameController.getGameState().getThirdCardDeck().get(1));
        } else if (source.equals(stage3card3)) {
          this.gameController.handleCardPurchase(
              this.gameController.getGameState().getThirdCardDeck().get(2));
        } else if (source.equals(stage3card4)) {
          this.gameController.handleCardPurchase(
              this.gameController.getGameState().getThirdCardDeck().get(3));
        }
      } catch (NotYourTurnException e) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(e.getMessage());
        alert.show();
      } catch (IllegalMoveCombinationException e) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(e.getMessage());
        alert.show();
      }

    } else if (event.getButton() == MouseButton.SECONDARY) {
      soundManager.playGetGold();
      Object source = event.getSource();
      try {
        if (source.equals(stage1card1)) {
          this.gameController.handleCardReservation(
              this.gameController.getGameState().getFirstCardDeck().get(0));
        } else if (source.equals(stage1card2)) {
          this.gameController.handleCardReservation(
              this.gameController.getGameState().getFirstCardDeck().get(1));
        } else if (source.equals(stage1card3)) {
          this.gameController.handleCardReservation(
              this.gameController.getGameState().getFirstCardDeck().get(2));
        } else if (source.equals(stage1card4)) {
          this.gameController.handleCardReservation(
              this.gameController.getGameState().getFirstCardDeck().get(3));
        } else if (source.equals(stage2card1)) {
          this.gameController.handleCardReservation(
              this.gameController.getGameState().getSecondCardDeck().get(0));
        } else if (source.equals(stage2card2)) {
          this.gameController.handleCardReservation(
              this.gameController.getGameState().getSecondCardDeck().get(1));
        } else if (source.equals(stage2card3)) {
          this.gameController.handleCardReservation(
              this.gameController.getGameState().getSecondCardDeck().get(2));
        } else if (source.equals(stage2card4)) {
          this.gameController.handleCardReservation(
              this.gameController.getGameState().getSecondCardDeck().get(3));
        } else if (source.equals(stage3card1)) {
          this.gameController.handleCardReservation(
              this.gameController.getGameState().getThirdCardDeck().get(0));
        } else if (source.equals(stage3card2)) {
          this.gameController.handleCardReservation(
              this.gameController.getGameState().getThirdCardDeck().get(1));
        } else if (source.equals(stage3card3)) {
          this.gameController.handleCardReservation(
              this.gameController.getGameState().getThirdCardDeck().get(2));
        } else if (source.equals(stage3card4)) {
          this.gameController.handleCardReservation(
              this.gameController.getGameState().getThirdCardDeck().get(3));
        }
      } catch (NotYourTurnException e) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(e.getMessage());
        alert.show();
      } catch (IllegalMoveCombinationException e) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(e.getMessage());
        alert.show();
      } catch (DepletedResourceException e) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Something went wrong. There should have been gold.");
        alert.show();
      } catch (ActionNotPossibleException e) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(e.getMessage());
        alert.show();
      }
    }
  }

  /**
   * Handles the action of purchasing a reserved card when a reserved card UI element is clicked.
   * This method is triggered when a mouse event occurs on the reserved card elements. Verifies the
   * interaction and processes the purchase if valid.
   *
   * @param mouseEvent the mouse event triggered by the user's interaction, used to determine the
   *     source of the click and verify the button type (primary button)
   */
  @FXML
  public void reservedCardEvent(MouseEvent mouseEvent) {
    if (mouseEvent.getButton() == MouseButton.PRIMARY) {
      soundManager.playBuyDevelopment();
      Object source = mouseEvent.getSource();
      try {
        if (source.equals(reserved1)
            && !this.gameController.getPlayer().getReservedCards().isEmpty()) {
          this.gameController.handleReservedCardPurchase(
              this.gameController.getPlayer().getReservedCards().get(0));
        } else if (source.equals(reserved2)
            && this.gameController.getPlayer().getReservedCards().size() >= 2) {
          this.gameController.handleReservedCardPurchase(
              this.gameController.getPlayer().getReservedCards().get(1));
        } else if (source.equals(reserved3)
            && this.gameController.getPlayer().getReservedCards().size() >= 3) {
          this.gameController.handleReservedCardPurchase(
              this.gameController.getPlayer().getReservedCards().get(2));
        }
      } catch (NotYourTurnException e) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(e.getMessage());
        alert.show();
      } catch (IllegalMoveCombinationException e) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(e.getMessage());
        alert.show();
      }
    }
  }

  /**
   * Handles the reservation of a random card from a specified stack when triggered by a mouse
   * event, specifically a right-click action.
   *
   * @param event the MouseEvent that triggers this method; it should contain information about the
   *     source stack and the type of mouse button used during the event
   */
  @FXML
  public void reserveStack(MouseEvent event) {
    if (event.getButton() == MouseButton.SECONDARY) {
      LOGGER.info("Reserving random Card from Stack");
      soundManager.playGetGold();
      Object source = event.getSource();
      try {
        if (source.equals(stage1stack)) {
          Optional<Card> randomCard = this.gameController.getGameState().getRandomCardFromStack(1);
          if (randomCard.isPresent()) {
            this.gameController.handleCardReservation(randomCard.get());
          }
        } else if (source.equals(stage2stack)) {
          Optional<Card> randomCard = this.gameController.getGameState().getRandomCardFromStack(2);
          if (randomCard.isPresent()) {
            this.gameController.handleCardReservation(randomCard.get());
          }
        } else if (source.equals(stage3stack)) {
          Optional<Card> randomCard = this.gameController.getGameState().getRandomCardFromStack(3);
          if (randomCard.isPresent()) {
            this.gameController.handleCardReservation(randomCard.get());
          }
        }
      } catch (NotYourTurnException e) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(e.getMessage());
        alert.show();
      } catch (IllegalMoveCombinationException e) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(e.getMessage());
        alert.show();
      } catch (DepletedResourceException e) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Something went wrong. There should have been gold.");
        alert.show();
      } catch (ActionNotPossibleException e) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(e.getMessage());
        alert.show();
      }
    }
  }

  /**
   * Handles the purchase of gems based on the clicked button. Determines the type of gem to be
   * purchased and delegates the handling of the stone pickup to the game controller. Displays
   * informational alerts in case of errors or exceptions.
   *
   * @param event the event triggered when a button is clicked, used to identify the source of the
   *     action.
   */
  @FXML
  public void buyGems(Event event) {
    Button clickedButton = (Button) event.getSource();
    soundManager.playBuyGems();
    LOGGER.info("Buying " + clickedButton.getId());
    StoneType stone = null;
    switch (clickedButton.getId()) {
      case "red_pouch" -> {
        stone = StoneType.RED;
      }
      case "blue_pouch" -> {
        stone = StoneType.BLUE;
      }
      case "green_pouch" -> {
        stone = StoneType.GREEN;
      }
      case "black_pouch" -> {
        stone = StoneType.BLACK;
      }
      case "white_pouch" -> {
        stone = StoneType.WHITE;
      }
    }
    if (stone != null) {
      try {
        this.gameController.handleStonePickup(stone);
      } catch (NotYourTurnException e) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(e.getMessage());
        alert.show();
      } catch (DepletedResourceException e) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(e.getMessage());
        alert.show();
      } catch (IllegalMoveCombinationException e) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(e.getMessage());
        alert.show();
      }
    } else {
      LOGGER.info("stone was null");
    }
  }

  /**
   * Handles the return of a gem based on the button clicked by the player. Determines the type of
   * gem from the button ID and sends the stone type to the game controller for processing. If an
   * error occurs during the process, displays an alert with the appropriate error message.
   *
   * @param event The MouseEvent triggered when a gem button is clicked.
   */
  @FXML
  public void returnGem(MouseEvent event) {
    Button clickedButton = (Button) event.getSource();
    soundManager.playBuyGems();
    LOGGER.info("Trying to return " + clickedButton.getId());
    StoneType stone = null;
    switch (clickedButton.getId()) {
      case "player_gem_button_red" -> {
        stone = StoneType.RED;
      }
      case "player_gem_button_blue" -> {
        stone = StoneType.BLUE;
      }
      case "player_gem_button_green" -> {
        stone = StoneType.GREEN;
      }
      case "player_gem_button_black" -> {
        stone = StoneType.BLACK;
      }
      case "player_gem_button_white" -> {
        stone = StoneType.WHITE;
      }
    }
    if (stone != null) {
      try {
        this.gameController.handleStoneReturn(stone);
      } catch (NotYourTurnException e) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(e.getMessage());
        alert.show();
      } catch (DepletedResourceException e) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(e.getMessage());
        alert.show();
      }
    } else {
      LOGGER.info("stone was null");
    }
  }

  /**
   * Handles the action of purchasing a noble when triggered by a user event. This method determines
   * which noble is being claimed based on the event source, and delegates the claim handling to the
   * game controller. It also plays relevant sound effects and provides feedback if exceptions occur
   * during the process.
   *
   * @param event the event object triggered when a user interacts with a noble UI element (e.g.,
   *     button press or click), used to discern the source of the action
   */
  @FXML
  public void buyNoble(Event event) {
    soundManager.playBuyDevelopment();
    soundManager.playBuyNoble();

    Object source = event.getSource();
    try {
      if (source.equals(this.noble1)) {
        this.gameController.handleNobleClaim(
            this.gameController.getGameState().getNobleDeck().get(0));
      } else if (source.equals(this.noble2)) {
        this.gameController.handleNobleClaim(
            this.gameController.getGameState().getNobleDeck().get(1));
      } else if (source.equals(this.noble3)) {
        this.gameController.handleNobleClaim(
            this.gameController.getGameState().getNobleDeck().get(2));
      } else if (source.equals(this.noble4)) {
        this.gameController.handleNobleClaim(
            this.gameController.getGameState().getNobleDeck().get(3));
      } else if (source.equals(this.noble5)) {
        this.gameController.handleNobleClaim(
            this.gameController.getGameState().getNobleDeck().get(4));
      }
    } catch (NotYourTurnException e) {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setHeaderText(e.getMessage());
      alert.show();
    } catch (IllegalMoveCombinationException e) {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setHeaderText(e.getMessage());
      alert.show();
    } catch (CantAffordItemException e) {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setHeaderText(e.getMessage());
      alert.show();
    }
  }

  private void updatePlayerBanners() {
    List<AbstractPlayer> players = this.gameController.getGameState().getPlayers();

    // gameController.getPlayer().getName()

    // names
    this.player1_name.setText(players.get(0).getName());
    this.player2_name.setText(players.get(1).getName());
    this.player3_name.setText(players.get(2).getName());
    this.player4_name.setText(players.get(3).getName());

    // prestige points
    this.player1_points.setText(String.valueOf(players.get(0).getPrestige()));
    this.player2_points.setText(String.valueOf(players.get(1).getPrestige()));
    this.player3_points.setText(String.valueOf(players.get(2).getPrestige()));
    this.player4_points.setText(String.valueOf(players.get(3).getPrestige()));

    // red stones
    this.player1_red.setText("" + players.get(0).getStoneInventory().get(StoneType.RED));
    this.player1_red_bonus.setText("+" + players.get(0).getBonusForType(StoneType.RED));
    this.player2_red.setText("" + players.get(1).getStoneInventory().get(StoneType.RED));
    this.player2_red_bonus.setText("+" + players.get(1).getBonusForType(StoneType.RED));
    this.player3_red.setText("" + players.get(2).getStoneInventory().get(StoneType.RED));
    this.player3_red_bonus.setText("+" + players.get(2).getBonusForType(StoneType.RED));
    this.player4_red.setText("" + players.get(3).getStoneInventory().get(StoneType.RED));
    this.player4_red_bonus.setText("+" + players.get(3).getBonusForType(StoneType.RED));

    // blue stones
    this.player1_blue.setText("" + players.get(0).getStoneInventory().get(StoneType.BLUE));
    this.player1_blue_bonus.setText("+" + players.get(0).getBonusForType(StoneType.BLUE));
    this.player2_blue.setText("" + players.get(1).getStoneInventory().get(StoneType.BLUE));
    this.player2_blue_bonus.setText("+" + players.get(1).getBonusForType(StoneType.BLUE));
    this.player3_blue.setText("" + players.get(2).getStoneInventory().get(StoneType.BLUE));
    this.player3_blue_bonus.setText("+" + players.get(2).getBonusForType(StoneType.BLUE));
    this.player4_blue.setText("" + players.get(3).getStoneInventory().get(StoneType.BLUE));
    this.player4_blue_bonus.setText("+" + players.get(3).getBonusForType(StoneType.BLUE));

    // green stones
    this.player1_green.setText("" + players.get(0).getStoneInventory().get(StoneType.GREEN));
    this.player1_green_bonus.setText("+" + players.get(0).getBonusForType(StoneType.GREEN));
    this.player2_green.setText("" + players.get(1).getStoneInventory().get(StoneType.GREEN));
    this.player2_green_bonus.setText("+" + players.get(1).getBonusForType(StoneType.GREEN));
    this.player3_green.setText("" + players.get(2).getStoneInventory().get(StoneType.GREEN));
    this.player3_green_bonus.setText("+" + players.get(2).getBonusForType(StoneType.GREEN));
    this.player4_green.setText("" + players.get(3).getStoneInventory().get(StoneType.GREEN));
    this.player4_green_bonus.setText("+" + players.get(3).getBonusForType(StoneType.GREEN));

    // black stones
    this.player1_black.setText("" + players.get(0).getStoneInventory().get(StoneType.BLACK));
    this.player1_black_bonus.setText("+" + players.get(0).getBonusForType(StoneType.BLACK));
    this.player2_black.setText("" + players.get(1).getStoneInventory().get(StoneType.BLACK));
    this.player2_black_bonus.setText("+" + players.get(1).getBonusForType(StoneType.BLACK));
    this.player3_black.setText("" + players.get(2).getStoneInventory().get(StoneType.BLACK));
    this.player3_black_bonus.setText("+" + players.get(2).getBonusForType(StoneType.BLACK));
    this.player4_black.setText("" + players.get(3).getStoneInventory().get(StoneType.BLACK));
    this.player4_black_bonus.setText("+" + players.get(3).getBonusForType(StoneType.BLACK));

    // white stones
    this.player1_white.setText("" + players.get(0).getStoneInventory().get(StoneType.WHITE));
    this.player1_white_bonus.setText("+" + players.get(0).getBonusForType(StoneType.WHITE));
    this.player2_white.setText("" + players.get(1).getStoneInventory().get(StoneType.WHITE));
    this.player2_white_bonus.setText("+" + players.get(1).getBonusForType(StoneType.WHITE));
    this.player3_white.setText("" + players.get(2).getStoneInventory().get(StoneType.WHITE));
    this.player3_white_bonus.setText("+" + players.get(2).getBonusForType(StoneType.WHITE));
    this.player4_white.setText("" + players.get(3).getStoneInventory().get(StoneType.WHITE));
    this.player4_white_bonus.setText("+" + players.get(3).getBonusForType(StoneType.WHITE));

    // gold stones
    this.player1_coins.setText(
        String.valueOf(players.get(0).getStoneInventory().get(StoneType.GOLD)));
    this.player2_coins.setText(
        String.valueOf(players.get(1).getStoneInventory().get(StoneType.GOLD)));
    this.player3_coins.setText(
        String.valueOf(players.get(2).getStoneInventory().get(StoneType.GOLD)));
    this.player4_coins.setText(
        String.valueOf(players.get(3).getStoneInventory().get(StoneType.GOLD)));

    // highlight player, whose turn it is
    switch (this.gameController.getGameState().getPlayersTurn()) {
      case 0 -> {
        this.player1.setOpacity(1);
        this.player2.setOpacity(0.5);
        this.player3.setOpacity(0.5);
        this.player4.setOpacity(0.5);
      }
      case 1 -> {
        this.player1.setOpacity(0.5);
        this.player2.setOpacity(1);
        this.player3.setOpacity(0.5);
        this.player4.setOpacity(0.5);
      }
      case 2 -> {
        this.player1.setOpacity(0.5);
        this.player2.setOpacity(0.5);
        this.player3.setOpacity(1);
        this.player4.setOpacity(0.5);
      }
      case 3 -> {
        this.player1.setOpacity(0.5);
        this.player2.setOpacity(0.5);
        this.player3.setOpacity(0.5);
        this.player4.setOpacity(1);
      }
    }
    if (this.gameController.getGameState().getPlayersTurn()
            == gameController.getGameState().getPlayers().indexOf(gameController.getPlayer())
        && your_turn.getTranslateY() == -80) {
      TranslateTransition tt = new TranslateTransition(Duration.seconds(0.5), your_turn);
      tt.setFromY(-80); // Startposition
      tt.setToY(0); // Endposition
      tt.play();
    } else if (this.gameController.getGameState().getPlayersTurn()
            != gameController.getGameState().getPlayers().indexOf(gameController.getPlayer())
        && your_turn.getTranslateY() == 0) {
      TranslateTransition tt = new TranslateTransition(Duration.seconds(0.5), your_turn);
      tt.setFromY(0); // Startposition
      tt.setToY(-80); // Endposition
      tt.play();
    }
  }

  @FXML
  public void settings(Event event) {
    soundManager.playClickSound();
    settings.setVisible(true);
  }

  @FXML
  public void back(Event event) {
    soundManager.playCancelSound();
    settings.setVisible(false);
  }

  public void quit(Event event) {
    this.gameController.leaveGameSession();
    soundManager.playCancelSound();
    Controller c = changeScene("/FXML/home.fxml").getController();
    c.setStage(stage);
  }

  /**
   * Handles the scenario when another player quits during the game.
   *
   * <p>This method is triggered when any other player than the local leaves the game session
   * unexpectedly. It plays a cancellation sound, navigates back to the home screen, and displays an
   * informational alert to notify the user that the session is closed.
   *
   * <p>This code is executed on the JavaFX Application Thread.
   */
  public void handlePlayerQuit() {
    Platform.runLater(
        () -> {
          soundManager.playCancelSound();
          Controller c = changeScene("/FXML/home.fxml").getController();
          c.setStage(stage);
          showInfo("Some player left -> game session is closed");
        });
  }

  /**
   * Displays an informational alert dialog with the specified message.
   *
   * @param message the message to display in the information dialog
   */
  private void showInfo(String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Information");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  /**
   * Handles the event to open the chat interface. Makes the chat visible, clears any existing text
   * in the message field, plays a sound effect for opening the chat, and focuses on the message
   * input field.
   *
   * @param event the event that triggered this method, typically a user interaction.
   */
  @FXML
  public void openChat(Event event) {
    LOGGER.info("Opening Chat");
    chat.setVisible(true);
    messageField.clear();
    soundManager.playChatOpen();
    Platform.runLater(() -> messageField.requestFocus());
  }

  /**
   * Opens the chat interface and sets up necessary behaviors.
   *
   * <p>This method makes the chat window visible, plays a sound to indicate that the chat has
   * opened, and focuses on the message input field to allow immediate typing.
   */
  public void openChat() {
    LOGGER.info("Opening Chat");
    chat.setVisible(true);
    soundManager.playChatOpen();
    Platform.runLater(() -> messageField.requestFocus());
  }

  /**
   * Closes the chat interface. This method handles the event triggered when a user requests to
   * close the chat and performs necessary actions such as logging, playing a sound, and making the
   * chat interface invisible.
   *
   * @param event The event that triggers the chat close action.
   */
  @FXML
  public void closeChat(Event event) {
    LOGGER.info("Closing Chat");
    soundManager.playChatClose();
    chat.setVisible(false);
  }

  /**
   * Closes the chat interface and performs necessary cleanup operations.
   *
   * <p>This method performs the following actions: - Logs the chat close action. - Plays a sound
   * associated with closing the chat. - Sets the chat interface visibility to false.
   */
  public void closeChat() {
    LOGGER.info("Closing Chat");
    soundManager.playChatClose();
    chat.setVisible(false);
  }

  /**
   * Handles sending a chat message entered by the user. Retrieves the text from the message input
   * field, sends it to the associated game controller, and then clears the input field.
   */
  @FXML
  public void sendMessage() {
    String message = messageField.getText();
    this.gameController.sendChatMessage(message);
    messageField.clear();
  }

  /**
   * Displays an alert prompting the user to return gems when the maximum allowed number of gems is
   * exceeded.
   *
   * @param gem_count The current number of gems held by the player. If this value exceeds the
   *     allowable limit of ten gems, the alert message will prompt the user to return the excess
   *     gems.
   */
  @FXML
  public void alert_return_gems(int gem_count) {
    tint.toFront();
    gems.toFront();
    alert.toFront();
    alert.setLayoutX(555);
    alert.setLayoutY(450);
    tint.setVisible(true);
    alert.setVisible(true);
    alert_title.setText("Too Many Treasures...");
    alert_text.setText(
        "No hand may hold more than ten gems. Return "
            + gem_count
            + " of thy hoard to abide by the laws of the realm.");
    settings_button.toFront();
    settings.toFront();
  }

  @FXML
  public void alert_select_noble(boolean[] noble_ids) {
    Button[] noble_cards = {noble1, noble2, noble3, noble4, noble5};
    tint.toFront();
    alert.toFront();
    for (int i = 0; i < noble_ids.length; i++) {
      if (!noble_ids[i]) {
        noble_cards[i].setOpacity(0.5);
      }
    }
    nobles.toFront();
    alert.setLayoutX(555);
    alert.setLayoutY(260);
    tint.setVisible(true);
    alert.setVisible(true);
    alert_title.setText("Summons of the Court...");
    alert_text.setText(
        "Several noble houses await your favor. Choose wisely whom you would like to welcome to your court.");
    settings_button.toFront();
    settings.toFront();
  }

  @FXML
  public void close_alert() {
    Platform.runLater(
        () -> {
          ;
          chat.toFront();
          leaderboard.toFront();
          explanation_pane.toFront();
          settings.toFront();
          tint.setVisible(false);
          alert.setVisible(false);
          Button[] noble_cards = {noble1, noble2, noble3, noble4, noble5};
          for (Button i : noble_cards) {
            i.setOpacity(1);
          }
        });
  }

  @FXML
  public void openLeaderboard() {
    soundManager.playChatOpen();
    leaderboard.toFront();
    leaderboard.setVisible(true);
    leaderboard();
  }

  @FXML
  public void closeLeaderboard() {
    soundManager.playChatClose();
    leaderboard.setVisible(false);
  }

  /**
   * Updates the leaderboard by dynamically setting the names and scores of the players and sorting
   * them based on their performance.
   *
   * <p>The method retrieves player details including names and prestige points from the game state
   * and updates the leaderboard UI accordingly. Finally, it sorts the players in a ranked order
   * based on their prestige points through the `sortLeaderboard` method.
   */
  @FXML
  public void leaderboard() {
    this.player1_leaderboard_name.setText(
        GameController.getInstance().getGameState().getPlayers().get(0).getName());
    this.player2_leaderboard_name.setText(
        GameController.getInstance().getGameState().getPlayers().get(1).getName());
    this.player3_leaderboard_name.setText(
        GameController.getInstance().getGameState().getPlayers().get(2).getName());
    this.player4_leaderboard_name.setText(
        GameController.getInstance().getGameState().getPlayers().get(3).getName());

    int player1_points =
        GameController.getInstance().getGameState().getPlayers().get(0).getPrestige();
    int player2_points =
        GameController.getInstance().getGameState().getPlayers().get(1).getPrestige();
    int player3_points =
        GameController.getInstance().getGameState().getPlayers().get(2).getPrestige();
    int player4_points =
        GameController.getInstance().getGameState().getPlayers().get(3).getPrestige();

    sortLeaderboard(player1_points, player2_points, player3_points, player4_points, "Round");
  }

  /**
   * Sorts the leaderboard based on player points in descending order and updates the UI
   * accordingly.
   *
   * @param player1_points Points scored by Player 1.
   * @param player2_points Points scored by Player 2.
   * @param player3_points Points scored by Player 3.
   * @param player4_points Points scored by Player 4.
   * @param type The type of sorting (not currently utilized in this method logic).
   */
  public void sortLeaderboard(
      int player1_points, int player2_points, int player3_points, int player4_points, String type) {
    Pane[] players = {
      player1_leaderboard, player2_leaderboard, player3_leaderboard, player4_leaderboard
    };
    int[] player_points = {player1_points, player2_points, player3_points, player4_points};
    Map<Pane, Integer> point_map = new HashMap<>();
    for (int i = 0; i < players.length; i++) {
      point_map.put(players[i], player_points[i]);
    }

    List<Map.Entry<Pane, Integer>> entries = new ArrayList<>(point_map.entrySet());

    entries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

    int i = 0;
    for (Map.Entry<Pane, Integer> entry : entries) {
      entry.getKey().setTranslateY(80 * i);
      Text points = (Text) entry.getKey().getChildren().get(2);
      points.setText(entry.getValue() + " Points");
      i += 1;
    }
  }

  /**
   * Switches the current scene to the leaderboard screen.
   *
   * <p>This method utilizes the JavaFX Platform.runLater to ensure that the scene change is
   * performed on the JavaFX application thread. It changes the scene to the "game_leaderboard.fxml"
   * UI and sets the stage for its corresponding controller.
   */
  public void switchToLeaderboard() {
    Platform.runLater(
        () -> {
          Controller c = changeScene("/FXML/game_leaderboard.fxml").getController();
          c.setStage(stage);
        });
  }

  /**
   * Handles the hover event on a development card by updating the tooltip and UI elements to
   * display relevant information about the card.
   *
   * @param path the file path of the card image, which contains information encoded in its file
   *     name such as card level, bonus color, prestige points, and gem costs
   */
  private void developmentCardHovered(String path) {
    String level = null;
    Matcher levelMatcher = Pattern.compile("level_([A-Z]+)").matcher(path);
    if (levelMatcher.find()) {
      level = levelMatcher.group(1); // z. B. "OOO"
      level = level.replace("O", "I");
    }
    if (path.substring(path.lastIndexOf("/") + 1, path.length() - 4).split(",").length > 1) {
      // Development Card Front
      tooltip_pane.setFitWidth(237);
      tooltip_pane.setFitHeight(212);
      tooltip_cost.setText("You Pay:");
      primary_action.setVisible(true);
      primary_action.setTranslateX(0);
      primary_action.setTranslateY(0);
      primary_action_text.setText("Buy Card");
      secondary_action_text.setText("Reserve Card");
      secondary_action.setVisible(true);
      secondary_action.setTranslateX(0);
      secondary_action.setTranslateY(0);
      card_bonus.setVisible(true);
      buy_sell_pane.setVisible(true);
      tooltip_text.setVisible(false);
      // Detect card Level
      tooltip_title.setText("Stage " + level + " Development Card");
      // Detect card Prestige Points
      String filename = path.substring(path.lastIndexOf("/") + 1, path.length() - 4);
      String[] parts = filename.split(",", 3);
      int prestigePoints = Integer.parseInt(parts[0]);
      if (prestigePoints > 0) {
        card_prestige.setTranslateX(0);
        card_prestige.setVisible(true);
        card_prestige.setText("+" + prestigePoints);
        card_prestige1.setTranslateX(0);
        card_prestige1.setVisible(true);
      } else {
        card_prestige.setVisible(false);
        card_prestige1.setVisible(false);
      }
      // Detect card Costs
      String bonusColor = parts[1];
      card_bonus_color.setImage(new Image("/images/gems/bonus_" + bonusColor + ".png"));
      String gemCostsRaw = parts[2];
      Pane[] card_costs = {card_cost_1, card_cost_2, card_cost_3, card_cost_4, card_cost_5};
      for (Pane card_cost : card_costs) {
        card_cost.setVisible(false);
      }
      ImageView[] cost_colors = {
        card_cost_1_color,
        card_cost_2_color,
        card_cost_3_color,
        card_cost_4_color,
        card_cost_5_color
      };
      for (ImageView imageView : cost_colors) {
        imageView.setEffect(null);
      }
      Text[] cost_amounts = {
        card_cost_1_amount,
        card_cost_2_amount,
        card_cost_3_amount,
        card_cost_4_amount,
        card_cost_5_amount
      };
      filename = filename.replaceAll("%3d", "=");
      Card duplicate = new Card(filename, -1);

      Map<StoneType, Integer> payment =
          this.gameController.getPlayer().getPaymentForCard(duplicate);
      Map<StoneType, Integer> cost = duplicate.getCost();
      if (!this.gameController.getPlayer().canAffordCard(duplicate)) {
        tooltip_cost.setText("You Need:");
        for (ImageView imageView : cost_colors) {
          imageView.setEffect(grey_out);
        }
        int n = 0;
        for (Map.Entry<StoneType, Integer> entry : cost.entrySet()) {
          if (entry.getValue() > 0) {
            cost_colors[n].setImage(
                new Image("/images/gems/" + entry.getKey().toString() + "_gem.png"));
            cost_amounts[n].setText(entry.getValue() + "");
            card_costs[n].setVisible(true);
            n++;
          }
        }
      } else {
        int n = 0;
        for (Map.Entry<StoneType, Integer> entry : payment.entrySet()) {
          if (entry.getValue() > 0) {
            cost_colors[n].setImage(
                new Image("/images/gems/" + entry.getKey().toString() + "_gem.png"));
            cost_amounts[n].setText(entry.getValue() + "");
            card_costs[n].setVisible(true);
            n++;
          }
        }
      }
    } else {
      // Development Card Back
      tooltip_pane.setFitWidth(237);
      tooltip_pane.setFitHeight(154);
      primary_action.setVisible(false);
      secondary_action.setVisible(true);
      secondary_action.setTranslateX(-47);
      secondary_action.setTranslateY(-55);
      secondary_action_text.setText("Reserve Card");
      buy_sell_pane.setVisible(false);
      tooltip_text.setVisible(true);
      tooltip_text.setText(
          "If you don't like any of the open cards, you can choose to reserve a random card from the stack.");
      tooltip_title.setText("Stage " + level + " Card Deck");
    }
  }

  /**
   * Handles the UI update and display logic when a noble tile is hovered over. Populates the
   * tooltip pane with relevant information about the noble card, including its prestige points and
   * the cost to acquire it, based on the provided file path.
   *
   * @param path the file path of the noble card image, containing encoded information about the
   *     card's prestige and cost in its filename
   */
  private void nobleTileHovered(String path) {
    tooltip_pane.setFitWidth(182);
    tooltip_pane.setFitHeight(185);
    tooltip_title.setText("Noble Card");
    card_bonus.setVisible(false);
    tooltip_cost.setText("You need:");
    secondary_action.setVisible(false);
    primary_action.setVisible(false);
    primary_action_text.setText("Buy Noble");
    buy_sell_pane.setVisible(true);
    tooltip_text.setVisible(false);
    card_prestige.setTranslateX(-47);
    card_prestige1.setTranslateX(-39);
    // Detect Card Prestige
    String filename = path.substring(path.lastIndexOf("/") + 1, path.length() - 4);
    String[] parts = filename.split(",", 2);
    card_prestige.setText("+ " + parts[0]);
    card_prestige.setVisible(true);
    card_prestige1.setVisible(true);
    String gemCostsRaw = parts[1];
    Pane[] card_costs = {card_cost_1, card_cost_2, card_cost_3, card_cost_4, card_cost_5};
    for (Pane card_cost : card_costs) {
      card_cost.setVisible(false);
    }
    // Detect Card Cost
    ImageView[] cost_colors = {
      card_cost_1_color, card_cost_2_color, card_cost_3_color, card_cost_4_color, card_cost_5_color
    };
    for (ImageView imageView : cost_colors) {
      imageView.setEffect(null);
    }
    Text[] cost_amounts = {
      card_cost_1_amount,
      card_cost_2_amount,
      card_cost_3_amount,
      card_cost_4_amount,
      card_cost_5_amount
    };
    String[] costParts = gemCostsRaw.split(",");
    for (int n = 0; n < costParts.length; n++) {
      String[] keyVal = costParts[n].split("%3d");
      if (keyVal.length == 2) {
        cost_colors[n].setImage(new Image("/images/gems/" + keyVal[0] + "_gem.png"));
        cost_amounts[n].setText(keyVal[1]);
        card_costs[n].setVisible(true);
      }
    }
  }

  /**
   * Handles the hover event for the gem pouch, updating the tooltip and UI elements to provide
   * contextual information about the gem pile.
   *
   * @param path the string representing the path or identifier of the gem pouch, used to determine
   *     the specific gem type and update the tooltip title appropriately
   */
  private void gemPouchHovered(String path) {
    tooltip_pane.setFitWidth(237);
    tooltip_pane.setFitHeight(154);
    primary_action.setTranslateX(47);
    primary_action.setTranslateY(-55);
    primary_action.setVisible(true);
    secondary_action.setVisible(false);
    primary_action_text.setText("Take Gem");
    buy_sell_pane.setVisible(false);
    tooltip_text.setVisible(true);
    tooltip_text.setText(
        "If you don't buy or reserve a card, you can decide to take 3 gems of different colors or 2 gems of the same color each turn.");
    String[] color_raw = path.split("_");
    // Detect Gem Pouch Color
    tooltip_title.setText(
        color_raw[color_raw.length - 1].substring(0, 1).toUpperCase()
            + color_raw[color_raw.length - 1].substring(
                1, color_raw[color_raw.length - 1].length() - 4)
            + " Gem Pile");
  }

  /**
   * Handles the hover event for a gem pile, updating the tooltip and UI elements to show relevant
   * information and actions associated with the selected gem pile.
   *
   * @param path The file path indicating the selected gem pile, which contains information about
   *     the pile's color and type.
   */
  private void gemPileHovered(String path) {
    tooltip_pane.setFitWidth(237);
    tooltip_pane.setFitHeight(130);
    primary_action.setTranslateX(35);
    primary_action.setTranslateY(-80);
    primary_action.setVisible(true);
    secondary_action.setVisible(false);
    String[] color_raw = path.split("_");
    color_raw = color_raw[1].split("/");
    // Detect Gem Pile Color
    primary_action_text.setText(
        "Return "
            + color_raw[color_raw.length - 1].substring(0, 1).toUpperCase()
            + color_raw[color_raw.length - 1].substring(1)
            + " Gem");
    buy_sell_pane.setVisible(false);
    tooltip_text.setVisible(true);
    tooltip_text.setText(
        "If you have more than 10 gems at the end of a round, you must return gems.");
    tooltip_title.setText("Your Gems");
    offset = -((int) tooltip_pane.getFitHeight() + 10);
  }

  /**
   * Configures the tooltip pane and its related UI components to display information when hovering
   * over the coin pile. This method updates the tooltip layout and makes specific UI elements
   * visible or hidden to show details about the coin pile.
   *
   * <p>The tooltip explains the functionality of the coin pile: as long as coins remain, the player
   * will receive a coin for each reserved card. These coins can be used as wildcard gems.
   */
  private void coinsHovered() {
    tooltip_pane.setFitWidth(237);
    tooltip_pane.setFitHeight(130);
    primary_action.setVisible(false);
    secondary_action.setVisible(false);
    buy_sell_pane.setVisible(false);
    tooltip_text.setVisible(true);
    tooltip_title.setText("Coin Pile");
    tooltip_text.setText(
        "As long as there are coins left, you will receive a coin for each reserved card, which you can use as a gem wildcard.");
  }

  /**
   * Identifies the type of element currently being hovered over based on its image URL. This method
   * analyzes the URL of the image associated with the provided ImageView to determine the category
   * of the element (e.g., development card, noble tile, gem pouch, etc.).
   *
   * @param element the ImageView representing the element to be analyzed
   * @return an array of Strings where the first element indicates the type of the element (e.g.,
   *     "development_card", "noble_tile", "gem_pouch", etc.) and the second element contains the
   *     URL of the image; returns ["unknown", "n/a"] if no image is present
   */
  private String[] identifyHoveredElement(ImageView element) {
    String tileType = "unknown";
    String path = "n/a";
    Image image = element.getImage();
    if (image != null) {
      path = image.getUrl();
      tileType =
          path.contains("development_cards")
              ? "development_card"
              : path.contains("noble_tiles")
                  ? "noble_tile"
                  : path.contains("gem_pouches")
                      ? "gem_pouch"
                      : path.contains("coins")
                          ? "coins"
                          : path.contains("gem_piles") ? "gem_pile" : "unknown";
    }
    return new String[] {tileType, path};
  }

  /**
   * Handles the hover behavior for a given button, including tooltip positioning and
   * context-specific actions based on the type of the hovered element.
   *
   * @param button the button to which hover behavior is being added
   */
  private void handleButtonHover(Button button) {
    button.setOnMouseMoved(
        event -> {
          explanation_pane.setLayoutX(event.getSceneX() + 10);
          explanation_pane.setLayoutY(event.getSceneY() + offset);
        });
    button.setOnMouseEntered(
        event -> {
          offset = 10;
          explanation_pane.setTranslateY(0);
          explanation_pane.setLayoutX(event.getSceneX() + 10);
          explanation_pane.setLayoutY(event.getSceneY() + offset);
          // Detect Card Type
          ImageView card = (ImageView) button.getGraphic();
          String[] typeAndPath = this.identifyHoveredElement(card);
          String tileType = typeAndPath[0];
          String path = typeAndPath[1];
          if (button.getId().toString().contains("reserved")) {
            tileType = "reserved";
          }
          // Development Cards
          switch (tileType) {
            case "development_card" -> {
              this.developmentCardHovered(path);
            }
            case "reserved" -> {
              if (!path.equals("n/a")) {
                explanation_pane.setTranslateY(-220);
                this.developmentCardHovered(path);
              } else {
                return;
              }
            }
            case "noble_tile" -> {
              this.nobleTileHovered(path);
            }
            case "gem_pouch" -> {
              this.gemPouchHovered(path);
            }
            case "gem_pile" -> {
              this.gemPileHovered(path);
            }
            case "coins" -> {
              this.coinsHovered();
            }
          }
          delay.playFromStart();
        });
    button.setOnMouseExited(
        event -> {
          explanation_pane.setVisible(false);
          delay.stop();
        });
  }

  public void set_disable_game_buttons(boolean disable) {
    Button[] buttons = {
      stage1card1,
      stage1card2,
      stage1card3,
      stage1card4,
      stage2card1,
      stage2card2,
      stage2card3,
      stage2card4,
      stage3card1,
      stage3card2,
      stage3card3,
      stage3card4,
      noble1,
      noble2,
      noble3,
      noble4,
      noble5,
      stage1stack,
      stage2stack,
      stage3stack,
      red_pouch,
      white_pouch,
      green_pouch,
      black_pouch,
      blue_pouch,
      coin_pouch,
      player_gem_button_black,
      player_gem_button_blue,
      player_gem_button_green,
      player_gem_button_red,
      player_gem_button_white,
      reserved1,
      reserved2,
      reserved3
    };
    for (Button button : buttons) {
      Platform.runLater(
          () -> {
            button.setDisable(disable);
            button.setOpacity(1);
          });
    }
  }
}
