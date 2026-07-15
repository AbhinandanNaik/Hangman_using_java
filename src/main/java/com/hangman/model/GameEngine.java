package com.hangman.model;

import java.util.HashSet;
import java.util.Set;

public class GameEngine {
    private Word currentWord;
    private final Set<Character> guessedLetters;
    private int incorrectGuesses;
    private final int maxIncorrectGuesses = 6;
    private boolean hintUsed;
    private int score;

    public GameEngine(Word word) {
        this.guessedLetters = new HashSet<>();
        reset(word);
    }

    public void reset(Word word) {
        this.currentWord = word;
        this.guessedLetters.clear();
        this.incorrectGuesses = 0;
        this.hintUsed = false;
    }

    public boolean guessLetter(char letter) {
        char cleanLetter = Character.toUpperCase(letter);
        if (guessedLetters.contains(cleanLetter) || isGameOver()) {
            return false;
        }

        guessedLetters.add(cleanLetter);
        boolean correct = currentWord.getWord().toUpperCase().indexOf(cleanLetter) >= 0;
        if (!correct) {
            incorrectGuesses++;
        } else {
            // Add points for correct guess
            score += 10;
        }
        return correct;
    }

    public String getMaskedWord() {
        String original = currentWord.getWord().toUpperCase();
        StringBuilder masked = new StringBuilder();
        for (int i = 0; i < original.length(); i++) {
            char c = original.charAt(i);
            if (c == ' ') {
                masked.append(" ");
            } else if (guessedLetters.contains(c)) {
                masked.append(c);
            } else {
                masked.append("*");
            }
        }
        return masked.toString();
    }

    public String useHint() {
        if (!hintUsed) {
            hintUsed = true;
            // Subtract score penalty for using a hint
            score = Math.max(0, score - 15);
        }
        return currentWord.getHint();
    }

    public boolean isWon() {
        String original = currentWord.getWord().toUpperCase();
        for (int i = 0; i < original.length(); i++) {
            char c = original.charAt(i);
            if (c != ' ' && !guessedLetters.contains(c)) {
                return false;
            }
        }
        return true;
    }

    public boolean isGameOver() {
        return isWon() || incorrectGuesses >= maxIncorrectGuesses;
    }

    public Word getCurrentWord() {
        return currentWord;
    }

    public Set<Character> getGuessedLetters() {
        return guessedLetters;
    }

    public int getIncorrectGuesses() {
        return incorrectGuesses;
    }

    public int getMaxIncorrectGuesses() {
        return maxIncorrectGuesses;
    }

    public boolean isHintUsed() {
        return hintUsed;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addScore(int points) {
        this.score += points;
    }
}
