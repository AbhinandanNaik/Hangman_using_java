package com.hangman.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

public class WordDBMutationTest {
    private static final String LOCAL_DB_FILE = "words_database.json";
    private File backupFile;

    @BeforeEach
    public void setup() {
        File current = new File(LOCAL_DB_FILE);
        if (current.exists()) {
            backupFile = new File("words_database.json.bak");
            if (backupFile.exists()) {
                backupFile.delete();
            }
            current.renameTo(backupFile);
        }
    }

    @AfterEach
    public void teardown() {
        File current = new File(LOCAL_DB_FILE);
        if (current.exists()) {
            current.delete();
        }
        if (backupFile != null && backupFile.exists()) {
            backupFile.renameTo(new File(LOCAL_DB_FILE));
        }
    }

    @Test
    public void testDatabaseMutations() {
        WordDB db = new WordDB();
        int initialSize = db.getAllWords().size();

        // 1. Add new word
        Word newWord = new Word("TECH", "COMPILER", "Translates source code into machine targets.", Word.Difficulty.HARD);
        assertTrue(db.addWord(newWord), "Should successfully add unique word");
        assertEquals(initialSize + 1, db.getAllWords().size());

        // 2. Prevent duplicates
        assertFalse(db.addWord(newWord), "Should reject duplicate word entry");

        // 3. Remove word
        db.removeWord("COMPILER");
        assertEquals(initialSize, db.getAllWords().size());
    }
}
