package de.spl12.client.utils;

public class UserStats {
    private int gamesPlayed;
    private int gamesWon;
    private String favoriteToken;

    public UserStats() {
    }

    /**
     * Constructor for UserStats class.
     * @param gamesPlayed
     * @param gamesWon
     * @param favoriteToken
     */
    public UserStats(int gamesPlayed, int gamesWon, String favoriteToken) {
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
        this.favoriteToken = favoriteToken;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public String getFavoriteToken() {
        return favoriteToken;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }

    public void setFavoriteToken(String favoriteToken) {
        this.favoriteToken = favoriteToken;
    }
}
