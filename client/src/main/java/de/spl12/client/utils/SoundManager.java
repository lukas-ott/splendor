package de.spl12.client.utils;

import javafx.scene.media.AudioClip;

/**
 * Manages all sound effects used across the application.
 * <p>
 * Implements the Singleton design pattern to ensure a single instance
 * of audio control for click sounds, chat events, purchases, and more.
 * Provides methods to play specific sound effects and to adjust mute and volume settings.
 * </p>
 *
 * <p>Audio clips are loaded at initialization from resource paths and played conditionally based on mute status.</p>
 *
 * @author nmorali
 */


public class SoundManager {
  private static SoundManager instance;
  private boolean isMuted = false;

  private double volume = 0.5;

  private final AudioClip clickSound;
  private final AudioClip cancelSound;
  private final AudioClip loginSound;
  private final AudioClip startSound;
  private final AudioClip chatOpen;
  private final AudioClip chatClose;
  private final AudioClip buyDevelopment;
  private final AudioClip buyGems;
  private final AudioClip buyNoble;
  private final AudioClip getGold;

  /**
   * Private constructor for the SoundManager class. Initializes all audio clips used within the
   * SoundManager for various sound events. This includes sounds for user interactions such as
   * button clicks, chat events, and purchasing events. Ensures the sound clips are loaded from
   * their respective resources at runtime.
   *
   * <p>This constructor is private as SoundManager follows the Singleton design pattern, and
   * instances of this class should only be accessed through the {@link #getInstance()} method.
   */
  private SoundManager() {
    clickSound = new AudioClip(getClass().getResource("/sounds/ButtonClick.wav").toExternalForm());
    cancelSound =
        new AudioClip(getClass().getResource("/sounds/ButtonCancel.wav").toExternalForm());
    loginSound = new AudioClip(getClass().getResource("/sounds/ButtonLogin.wav").toExternalForm());
    startSound = new AudioClip(getClass().getResource("/sounds/ButtonStart.wav").toExternalForm());
    chatOpen = new AudioClip(getClass().getResource("/sounds/openChat.wav").toExternalForm());
    chatClose = new AudioClip(getClass().getResource("/sounds/closeChat.wav").toExternalForm());
    buyDevelopment =
        new AudioClip(getClass().getResource("/sounds/buyDevelopment.wav").toExternalForm());
    buyGems = new AudioClip(getClass().getResource("/sounds/buyGems.wav").toExternalForm());
    buyNoble = new AudioClip(getClass().getResource("/sounds/buyNoble.wav").toExternalForm());
    getGold = new AudioClip(getClass().getResource("/sounds/getGold.wav").toExternalForm());
  }

  /**
   * Returns the singleton instance of the SoundManager class. If the instance does not already
   * exist, this method initializes it. This ensures that only one instance of SoundManager is
   * created and used throughout the application, following the Singleton design pattern.
   *
   * @return the singleton instance of SoundManager
   */
  public static SoundManager getInstance() {
    if (instance == null) {
      instance = new SoundManager();
    }
    return instance;
  }

  public void playClickSound() {
    if (!isMuted) clickSound.play();
  }

  public void playCancelSound() {
    if (!isMuted) cancelSound.play();
  }

  public void playLoginSound() {
    if (!isMuted) loginSound.play();
  }

  public void playStartSound() {
    if (!isMuted) startSound.play();
  }

  public void playChatOpen() {
    if (!isMuted) chatOpen.play();
  }

  public void playChatClose() {
    if (!isMuted) chatClose.play();
  }

  public void playBuyDevelopment() {
    if (!isMuted) buyDevelopment.play();
  }

  public void playBuyGems() {
    if (!isMuted) buyGems.play();
  }

  public void playBuyNoble() {
    if (!isMuted & !buyNoble.isPlaying()) buyNoble.play();
  }

  public void playGetGold() {
    if (!isMuted) getGold.play();
  }

  public void setVolume(double v) {
    volume = v;
    clickSound.setVolume(v);
    cancelSound.setVolume(v);
    loginSound.setVolume(v);
    startSound.setVolume(v);
    chatOpen.setVolume(v);
    chatClose.setVolume(v);
    buyDevelopment.setVolume(v);
    buyGems.setVolume(v);
    buyNoble.setVolume(v);
    getGold.setVolume(v);
  }

  public double getVolume() {
    return volume;
  }

  public void setMuted(boolean m) {
    isMuted = m;
  }

  public boolean isMuted() {
    return isMuted;
  }
}
