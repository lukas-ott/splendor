package de.spl12.ai_client.application;


import de.spl12.ai_client.utils.ConstantsManager;

/**
 * Main class for the AI client application.
 */
public class AiClient {
    public static void main(String[] args) {
        if (args.length > 0) {
            ConstantsManager.HOST = args[0];
            System.out.println(ConstantsManager.HOST);
        }
        StartScreen.launchGui(args);
    }
}