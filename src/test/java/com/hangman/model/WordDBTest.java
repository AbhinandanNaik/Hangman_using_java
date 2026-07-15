package com.hangman.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class WordDBTest {
    private WordDB wordDB;

    @BeforeEach
    public void setUp() {
        wordDB = new WordDB();
    }

    @Test
    public void testDatabaseLoad() {
        List<Word> allWords = wordDB.getAllWords();
        assertNotNull(allWords);
        assertFalse(allWords.isEmpty(), "Word list should load correctly and not be empty");
    }

    @Test
    public void testLoadChallengeByDifficulty() {
        Word easyWord = wordDB.loadChallenge(Word.Difficulty.EASY);
        assertNotNull(easyWord);
        assertEquals(Word.Difficulty.EASY, easyWord.getDifficulty());

        Word hardWord = wordDB.loadChallenge(Word.Difficulty.HARD);
        assertNotNull(hardWord);
        assertEquals(Word.Difficulty.HARD, hardWord.getDifficulty());
    }
}
