package de.spl12.client.application;

import de.spl12.client.utils.ConstantsManager;

/**
 * The App class serves as the entry point of the application. It initializes application
 * configurations (such as the host) and launches the graphical user interface (GUI) through the
 * StartScreen class.
 */
public class App {
  public static void main(String[] args) {
    if (args.length > 0) {
      ConstantsManager.HOST = args[0];
      System.out.println(ConstantsManager.HOST);
    }
    StartScreen.launchGui(args);
  }
}
