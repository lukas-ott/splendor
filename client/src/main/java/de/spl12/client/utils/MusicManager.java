package de.spl12.client.utils;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Manages background music for different parts of the application (e.g., start, lobby, game).
 * <p>
 * Provides methods to play and switch between different music tracks, control volume,
 * and toggle mute settings. Implements the Singleton pattern to ensure centralized audio control.
 * </p>
 *
 * <p>Music tracks are looped and adjusted dynamically according to user settings.</p>
 *
 * @author nmorali
 */


public class MusicManager {
  private static MusicManager instance;
  private MediaPlayer backgroundMusicPlayer;
  private MediaPlayer lobbyMusicPlayer;
  private MediaPlayer gameMusicPlayer;
  private BooleanProperty isMuted = new SimpleBooleanProperty(false);
  private double volume = 0.7;

  /**
   * Returns the singleton instance of the MusicManager.
   *
   * @return
   */
  public static MusicManager getInstance() {
    if (instance == null) {
      instance = new MusicManager();
    }
    return instance;
  }

  /**
   * Plays the starting background music for the application. This method ensures that any
   * previously playing lobby or game music is stopped before initializing and playing the starting
   * background music. If the background music player does not already exist, it is initialized with
   * the appropriate media resource. The music is set to loop indefinitely and its volume is
   * adjusted based on the global mute setting and volume level.
   *
   * <p>Behavior: - Stops any currently playing lobby or game music. - Initializes the background
   * music player if it does not exist. - Sets the background music to loop indefinitely. - Adjusts
   * the volume of the music based on mute and global volume levels. - Starts playing the background
   * music.
   */
  public void startingMusic() {
    if (lobbyMusicPlayer != null) {
      lobbyMusicPlayer.stop();
    }
    if (gameMusicPlayer != null) {
      gameMusicPlayer.stop();
    }
    if (backgroundMusicPlayer == null) {
      String path = getClass().getResource("/sounds/StartingMusic.mp3").toExternalForm();
      Media media = new Media(path);
      backgroundMusicPlayer = new MediaPlayer(media);
      backgroundMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
    }
    backgroundMusicPlayer.setVolume(isMuted.get() ? 0 : volume / 2);
    backgroundMusicPlayer.play();
  }

  /**
   * Plays the lobby music for the application. This method ensures that any previously playing
   * background or game music is stopped before playing the designated lobby music. If the lobby
   * music player does not already exist, it is initialized with the appropriate media resource. The
   * music is set to loop indefinitely and its volume is adjusted based on the application's mute
   * setting and volume level.
   *
   * <p>Behavior: - Stops any currently playing background or game music. - Initializes the lobby
   * music player if it does not exist. - Sets the lobby music to loop indefinitely. - Adjusts the
   * volume of the music based on the mute and volume settings. - Starts playing the lobby music.
   */
  public void lobbyMusic() {
    if (backgroundMusicPlayer != null) {
      backgroundMusicPlayer.stop();
    }
    if (gameMusicPlayer != null) {
      gameMusicPlayer.stop();
    }
    if (lobbyMusicPlayer == null) {
      String path = getClass().getResource("/sounds/LobbyMusic.mp3").toExternalForm();
      Media media = new Media(path);
      lobbyMusicPlayer = new MediaPlayer(media);
      lobbyMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
    }
    lobbyMusicPlayer.setVolume(isMuted.get() ? 0 : volume);
    lobbyMusicPlayer.play();
  }

  /**
   * Plays the game music for the application. This method ensures that any previously playing
   * background or lobby music is stopped before initializing and playing the game music. If the
   * game music player does not already exist, it is initialized with the appropriate media file.
   * The music is set to loop indefinitely, and its volume is adjusted based on the application's
   * mute setting and the global volume level.
   *
   * <p>Behavior: - Stops any currently playing background or lobby music. - Initializes the game
   * music player if it does not already exist. - Sets the game music to loop indefinitely. -
   * Adjusts the volume of the music based on mute and global volume settings. - Starts playing the
   * game music.
   */
  public void gameMusic() {
    if (backgroundMusicPlayer != null) {
      backgroundMusicPlayer.stop();
      lobbyMusicPlayer.stop();
    }
    if (gameMusicPlayer == null) {
      String path = getClass().getResource("/sounds/GameMusic.mp3").toExternalForm();
      Media media = new Media(path);
      gameMusicPlayer = new MediaPlayer(media);
      gameMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
    }
    gameMusicPlayer.setVolume(isMuted.get() ? 0 : volume / 3);
    gameMusicPlayer.play();
  }

  public void setVolume(double v) {
    if (backgroundMusicPlayer != null) {
      backgroundMusicPlayer.setVolume(v / 2);
    }
    if (lobbyMusicPlayer != null) {
      lobbyMusicPlayer.setVolume(v);
    }
    if (gameMusicPlayer != null) {
      gameMusicPlayer.setVolume(v / 3);
    }
    volume = v;
  }

  public double getVolume() {
    return volume;
  }

  public void setMuted(boolean m) {
    isMuted.set(m);
    if (backgroundMusicPlayer != null) {
      backgroundMusicPlayer.setMute(isMuted.get());
    }
    if (lobbyMusicPlayer != null) {
      lobbyMusicPlayer.setMute(isMuted.get());
    }
    if (gameMusicPlayer != null) {
      gameMusicPlayer.setMute(isMuted.get());
    }
  }

  public boolean isMuted() {
    return isMuted.get();
  }

  public BooleanProperty isMutedProperty() {
    return isMuted;
  }
}
