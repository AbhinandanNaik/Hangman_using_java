package com.hangman.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerProfileTest {
    private static final String PROFILE_FILE = "profile.json";

    @BeforeEach
    @AfterEach
    public void cleanup() {
        File file = new File(PROFILE_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void testProfileInitialization() {
        PlayerProfile profile = PlayerProfile.load();
        assertNotNull(profile);
        assertEquals("Challenger", profile.getPlayerName());
        assertEquals(1, profile.getLevel());
        assertEquals(100, profile.getCoins());
    }

    @Test
    public void testRecordWinAndLoot() {
        PlayerProfile profile = PlayerProfile.load();
        profile.recordGame(true, 50); // win game, score 50
        
        assertEquals(1, profile.getTotalGamesPlayed());
        assertEquals(1, profile.getGamesWon());
        assertEquals(1, profile.getCurrentStreak());
        assertEquals(130, profile.getCoins()); // 100 + 30 win coins
        assertEquals(50, profile.getXp());
    }

    @Test
    public void testLevelUp() {
        PlayerProfile profile = PlayerProfile.load();
        // Needs 100 XP to level up from Level 1
        profile.addXp(110);
        
        assertEquals(2, profile.getLevel());
        assertEquals(10, profile.getXp()); // 110 - 100
        assertEquals(150, profile.getCoins()); // 100 starting + 50 level up reward
    }
}
