package com.hangman.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class WordDB {
    private static final String LOCAL_DB_FILE = "words_database.json";
    private List<Word> words;

    public WordDB() {
        // Try local file first for persistence
        File localFile = new File(LOCAL_DB_FILE);
        if (localFile.exists()) {
            try (Reader reader = new FileReader(localFile, StandardCharsets.UTF_8)) {
                Gson gson = new Gson();
                words = gson.fromJson(reader, new TypeToken<List<Word>>() {}.getType());
                if (words != null) {
                    return;
                }
            } catch (Exception e) {
                System.err.println("Error reading local database: " + e);
            }
        }

        // Fallback to classpath resource
        try {
            var inputStream = getClass().getResourceAsStream("/resources/words_database.json");
            if (inputStream == null) {
                inputStream = getClass().getClassLoader().getResourceAsStream("resources/words_database.json");
            }
            if (inputStream == null) {
                throw new RuntimeException("Word database resource 'resources/words_database.json' not found!");
            }
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            words = gson.fromJson(reader, new TypeToken<List<Word>>() {}.getType());
            reader.close();
            
            // Save a local copy
            saveLocally();
        } catch (Exception e) {
            System.err.println("Error initializing WordDB: " + e);
            words = new ArrayList<>();
        }
    }

    public synchronized boolean addWord(Word word) {
        boolean exists = words.stream()
                .anyMatch(w -> w.getWord().equalsIgnoreCase(word.getWord()));
        if (!exists) {
            words.add(word);
            saveLocally();
            return true;
        }
        return false;
    }

    public synchronized void removeWord(String wordValue) {
        words.removeIf(w -> w.getWord().equalsIgnoreCase(wordValue.trim()));
        saveLocally();
    }

    private void saveLocally() {
        try (FileWriter writer = new FileWriter(LOCAL_DB_FILE, StandardCharsets.UTF_8)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(words, writer);
        } catch (Exception e) {
            System.err.println("Error saving database locally: " + e);
        }
    }

    public Word loadChallenge(Word.Difficulty difficulty) {
        if (words.isEmpty()) {
            return new Word("ERROR", "HANGMAN", "A classic word game.", Word.Difficulty.EASY);
        }
        
        List<Word> filtered = words.stream()
                .filter(w -> w.getDifficulty() == difficulty)
                .collect(Collectors.toList());
        
        if (filtered.isEmpty()) {
            filtered = words;
        }

        Random rand = new Random();
        return filtered.get(rand.nextInt(filtered.size()));
    }

    public List<Word> getAllWords() {
        return words;
    }
}
