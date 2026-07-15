package com.hangman;

import com.formdev.flatlaf.FlatDarkLaf;
import com.hangman.ui.ScreenController;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        // Setup FlatLaf look and feel
        FlatDarkLaf.setup();
        
        SwingUtilities.invokeLater(() -> {
            new ScreenController().setVisible(true);
        });
    }
}
