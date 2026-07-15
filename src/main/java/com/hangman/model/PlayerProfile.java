package com.hangman.model;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PlayerProfile {
    private static final String PROFILE_FILE = "profile.json";
    
    private String playerName = "Challenger";
    private int totalGamesPlayed;
    private int gamesWon;
    private int currentStreak;
    private int maxStreak;
    private int totalScore;
    private int coins = 100; // Starting coins
    private int level = 1;
    private int xp;

    public PlayerProfile() {}

    public static PlayerProfile load() {
        File file = new File(PROFILE_FILE);
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Gson gson = new Gson();
                PlayerProfile profile = gson.fromJson(reader, PlayerProfile.class);
                if (profile != null) {
                    return profile;
                }
            } catch (IOException e) {
                System.err.println("Error loading profile: " + e);
            }
        }
        return new PlayerProfile();
    }

    public void save() {
        try (FileWriter writer = new FileWriter(PROFILE_FILE)) {
            Gson gson = new Gson();
            gson.toJson(this, writer);
        } catch (IOException e) {
            System.err.println("Error saving profile: " + e);
        }
    }

    public void addXp(int amount) {
        this.xp += amount;
        int xpNeeded = getXpForNextLevel();
        while (this.xp >= xpNeeded) {
            this.xp -= xpNeeded;
            this.level++;
            this.coins += 50; // level-up bonus coins
            xpNeeded = getXpForNextLevel();
        }
    }

    public int getXpForNextLevel() {
        return this.level * 100;
    }

    public void recordGame(boolean won, int scoreEarned) {
        this.totalGamesPlayed++;
        if (won) {
            this.gamesWon++;
            this.currentStreak++;
            if (this.currentStreak > this.maxStreak) {
                this.maxStreak = this.currentStreak;
            }
            this.totalScore += scoreEarned;
            this.coins += 30; // base winning coins
            addXp(50);
        } else {
            this.currentStreak = 0;
            addXp(10); // minor experience for effort
        }
        save();
    }

    // Getters and Setters
    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
        save();
    }

    public int getTotalGamesPlayed() {
        return totalGamesPlayed;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public int getMaxStreak() {
        return maxStreak;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public int getCoins() {
        return coins;
    }

    public void deductCoins(int amount) {
        this.coins = Math.max(0, this.coins - amount);
        save();
    }

    public void addCoins(int amount) {
        this.coins += amount;
        save();
    }

    public int getLevel() {
        return level;
    }

    public int getXp() {
        return xp;
    }
}
