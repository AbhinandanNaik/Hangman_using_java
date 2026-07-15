package com.hangman.model;

public class Word {
    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    private String category;
    private String word;
    private String hint;
    private Difficulty difficulty;

    public Word() {}

    public Word(String category, String word, String hint, Difficulty difficulty) {
        this.category = category;
        this.word = word;
        this.hint = hint;
        this.difficulty = difficulty;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
}
