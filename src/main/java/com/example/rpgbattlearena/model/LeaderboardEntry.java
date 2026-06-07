package com.example.rpgbattlearena.model;

public class LeaderboardEntry {
    private final String playerName;
    private final int level;
    private final int score;

    public LeaderboardEntry(String playerName, int level, int score) {
        this.playerName = playerName;
        this.level = level;
        this.score = score;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getLevel() {
        return level;
    }

    public int getScore() {
        return score;
    }
}
