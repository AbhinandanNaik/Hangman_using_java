package com.hangman.util;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class SoundManager {
    private static boolean soundEnabled = true;

    public static boolean isSoundEnabled() {
        return soundEnabled;
    }

    public static void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
    }

    public static void playSound(String resourcePath) {
        if (!soundEnabled) return;

        new Thread(() -> {
            try {
                InputStream is = SoundManager.class.getResourceAsStream(resourcePath);
                if (is == null) {
                    is = SoundManager.class.getClassLoader().getResourceAsStream(resourcePath);
                }
                if (is != null) {
                    InputStream bufferedIn = new BufferedInputStream(is);
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(bufferedIn);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioIn);
                    clip.start();
                } else {
                    // Fallback to basic system beep if files are not loaded yet
                    java.awt.Toolkit.getDefaultToolkit().beep();
                }
            } catch (Exception e) {
                System.err.println("Error playing sound '" + resourcePath + "': " + e);
            }
        }).start();
    }
}
