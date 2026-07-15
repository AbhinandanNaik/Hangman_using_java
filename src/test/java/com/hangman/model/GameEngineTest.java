package com.hangman.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameEngineTest {
    private Word word;
    private GameEngine engine;

    @BeforeEach
    public void setUp() {
        word = new Word("ANIMALS", "PANDA BEAR", "Native Chinese black and white bear.", Word.Difficulty.EASY);
        engine = new GameEngine(word);
    }

    @Test
    public void testInitialState() {
        assertEquals("PANDA BEAR", engine.getCurrentWord().getWord());
        assertEquals("***** ****", engine.getMaskedWord());
        assertEquals(0, engine.getIncorrectGuesses());
        assertFalse(engine.isWon());
        assertFalse(engine.isGameOver());
    }

    @Test
    public void testCorrectGuesses() {
        assertTrue(engine.guessLetter('P'));
        assertEquals("P**** ****", engine.getMaskedWord());
        assertEquals(0, engine.getIncorrectGuesses());

        assertTrue(engine.guessLetter('a')); // test case insensitivity
        assertEquals("PA**A **A*", engine.getMaskedWord());
    }

    @Test
    public void testIncorrectGuesses() {
        assertFalse(engine.guessLetter('X'));
        assertEquals(1, engine.getIncorrectGuesses());
        assertEquals("***** ****", engine.getMaskedWord());
    }

    @Test
    public void testWinState() {
        // "PANDA BEAR"
        engine.guessLetter('P');
        engine.guessLetter('A');
        engine.guessLetter('N');
        engine.guessLetter('D');
        engine.guessLetter('B');
        engine.guessLetter('E');
        engine.guessLetter('R');

        assertTrue(engine.isWon());
        assertTrue(engine.isGameOver());
    }

    @Test
    public void testLossState() {
        engine.guessLetter('Q');
        engine.guessLetter('W');
        engine.guessLetter('E'); // correct, since 'E' is in BEAR
        engine.guessLetter('T');
        engine.guessLetter('Y');
        engine.guessLetter('U');
        engine.guessLetter('I');

        assertEquals(6, engine.getIncorrectGuesses());
        assertTrue(engine.isGameOver());
        assertFalse(engine.isWon());
    }
}
