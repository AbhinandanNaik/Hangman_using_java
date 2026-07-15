package com.hangman.ui;

import java.awt.Color;

public class ThemeManager {
    public enum Theme {
        DARK_MODE,
        SYNTHWAVE,
        CYBERPUNK
    }

    private static Theme currentTheme = Theme.DARK_MODE;

    public static Theme getCurrentTheme() {
        return currentTheme;
    }

    public static void setTheme(Theme theme) {
        currentTheme = theme;
    }

    public static Color getBackgroundColor() {
        switch (currentTheme) {
            case SYNTHWAVE: return Color.decode("#200B3B");
            case CYBERPUNK: return Color.decode("#000814");
            case DARK_MODE:
            default: return Color.decode("#101820");
        }
    }

    public static Color getPrimaryColor() {
        switch (currentTheme) {
            case SYNTHWAVE: return Color.decode("#4D0E7C");
            case CYBERPUNK: return Color.decode("#001D3D");
            case DARK_MODE:
            default: return Color.decode("#14212D");
        }
    }

    public static Color getSecondaryColor() {
        switch (currentTheme) {
            case SYNTHWAVE: return Color.decode("#FF007F");
            case CYBERPUNK: return Color.decode("#00F0FF");
            case DARK_MODE:
            default: return Color.decode("#FCA311");
        }
    }

    public static Color getTextColor() {
        switch (currentTheme) {
            case SYNTHWAVE: return Color.decode("#00FFCC");
            case CYBERPUNK: return Color.decode("#FFD60A");
            case DARK_MODE:
            default: return Color.WHITE;
        }
    }

    public static Color getButtonTextColor() {
        return Color.WHITE;
    }


    public static Color getAccentColor() {
        switch (currentTheme) {
            case SYNTHWAVE: return Color.decode("#FF7700");
            case CYBERPUNK: return Color.decode("#FF0077");
            case DARK_MODE:
            default: return Color.decode("#E5E5E5");
        }
    }

    public static Color getCorrectColor() {
        switch (currentTheme) {
            case SYNTHWAVE: return Color.decode("#05FF9B");
            case CYBERPUNK: return Color.decode("#00FF66");
            case DARK_MODE:
            default: return new Color(34, 139, 34); // Forest green
        }
    }

    public static Color getIncorrectColor() {
        switch (currentTheme) {
            case SYNTHWAVE: return Color.decode("#FF2E93");
            case CYBERPUNK: return Color.decode("#FF0055");
            case DARK_MODE:
            default: return new Color(178, 34, 34); // Firebrick red
        }
    }
}
