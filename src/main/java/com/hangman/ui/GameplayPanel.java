package com.hangman.ui;

import com.hangman.config.CommonConstants;
import com.hangman.model.GameEngine;
import com.hangman.model.PlayerProfile;
import com.hangman.model.Word;
import com.hangman.model.WordDB;
import com.hangman.util.CustomTools;
import com.hangman.util.SoundManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameplayPanel extends JPanel {
    private final ScreenController controller;
    private final WordDB wordDB;
    private GameEngine gameEngine;
    private PlayerProfile playerProfile;
    private Word.Difficulty currentDifficulty = Word.Difficulty.EASY;

    // Layering controls for overlays
    private JLayeredPane layeredPane;
    private GameOverlayPanel overlayPanel;

    // GUI components
    private JLabel hangmanImage;
    private JLabel categoryLabel;
    private JLabel hiddenWordLabel;
    private JLabel levelLabel;
    private JLabel streakLabel;
    private JLabel coinsLabel;
    private JProgressBar xpProgressBar;
    private JProgressBar timerProgress; // Round countdown bar
    
    private JPanel headerPanel;
    private JPanel mainPanel;
    private JPanel keyboardPanel;
    private JButton[] letterButtons;
    private JButton hintButton;
    private JComboBox<Word.Difficulty> difficultySelector;
    private JComboBox<ThemeManager.Theme> themeSelector;
    private JButton soundToggleBtn;

    private Font customFontTitle;
    private Font customFontMedium;
    private Font customFontSmall;

    // Timer variables
    private int timeLeftDeciSeconds = 300; // 30.0 seconds
    private Timer roundTimer;

    public GameplayPanel(ScreenController controller) {
        this.controller = controller;
        setLayout(new BorderLayout());

        // Load profile and database
        playerProfile = PlayerProfile.load();
        wordDB = new WordDB();
        
        // Start engine with an easy word
        Word startingWord = wordDB.loadChallenge(currentDifficulty);
        gameEngine = new GameEngine(startingWord);

        // Load custom fonts
        customFontTitle = CustomTools.createFont(CommonConstants.FONT_PATH, 44f);
        customFontMedium = CustomTools.createFont(CommonConstants.FONT_PATH, 18f);
        customFontSmall = CustomTools.createFont(CommonConstants.FONT_PATH, 13f);

        initComponents();
        setupKeyBindings();
        setupRoundTimer();
        applyTheme();
        
        // Start the countdown on launch
        roundTimer.start();
    }

    private void initComponents() {
        // Base layered pane wrapper
        layeredPane = new JLayeredPane();

        // Main layout panel
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // 1. Header panel (Dashboard)
        headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.GRAY, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(2, 5, 2, 5);

        levelLabel = new JLabel("Lv. " + playerProfile.getLevel());
        levelLabel.setFont(customFontMedium);
        
        xpProgressBar = new JProgressBar(0, playerProfile.getXpForNextLevel());
        xpProgressBar.setValue(playerProfile.getXp());
        xpProgressBar.setStringPainted(true);
        xpProgressBar.setFont(customFontSmall);

        streakLabel = new JLabel("Streak: " + playerProfile.getCurrentStreak() + " 🔥");
        streakLabel.setFont(customFontMedium);
        streakLabel.setHorizontalAlignment(SwingConstants.CENTER);

        coinsLabel = new JLabel("Coins: " + playerProfile.getCoins() + " 🪙");
        coinsLabel.setFont(customFontMedium);
        coinsLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        // First row of header
        gbc.gridy = 0;
        gbc.gridx = 0; gbc.weightx = 0.2; headerPanel.add(levelLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 0.5; headerPanel.add(xpProgressBar, gbc);
        gbc.gridx = 2; gbc.weightx = 0.3; headerPanel.add(coinsLabel, gbc);

        // Second row of header: settings and stats
        gbc.gridy = 1;
        gbc.gridx = 0; gbc.weightx = 0.3; headerPanel.add(streakLabel, gbc);

        JPanel controlsSubPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        controlsSubPanel.setOpaque(false);

        themeSelector = new JComboBox<>(ThemeManager.Theme.values());
        themeSelector.setFont(customFontSmall);
        themeSelector.setSelectedItem(ThemeManager.getCurrentTheme());
        themeSelector.addActionListener(e -> {
            ThemeManager.setTheme((ThemeManager.Theme) themeSelector.getSelectedItem());
            applyTheme();
            controller.repaint();
            controller.revalidate();
            SoundManager.playSound("resources/sounds/click.wav");
        });
        controlsSubPanel.add(new JLabel("Theme:"));
        controlsSubPanel.add(themeSelector);

        soundToggleBtn = createStyledButton("🔊", customFontSmall, true);
        soundToggleBtn.setPreferredSize(new Dimension(45, 25));
        soundToggleBtn.addActionListener(e -> {
            boolean current = SoundManager.isSoundEnabled();
            SoundManager.setSoundEnabled(!current);
            soundToggleBtn.setText(SoundManager.isSoundEnabled() ? "🔊" : "🔇");
        });
        controlsSubPanel.add(soundToggleBtn);

        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 0.7;
        headerPanel.add(controlsSubPanel, gbc);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // 2. Center Content Pane (Hangman Graphic + Clues)
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints centerGbc = new GridBagConstraints();
        centerGbc.gridx = 0;
        centerGbc.fill = GridBagConstraints.BOTH;
        centerGbc.weightx = 1.0;

        // Hangman Image container
        hangmanImage = CustomTools.loadImage(CommonConstants.IMAGE_PATH);
        hangmanImage.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel imgContainer = new JPanel(new BorderLayout());
        imgContainer.setOpaque(false);
        imgContainer.add(hangmanImage, BorderLayout.CENTER);
        
        centerGbc.gridy = 0;
        centerGbc.weighty = 0.5;
        centerPanel.add(imgContainer, centerGbc);

        // Timer Progress Bar
        timerProgress = new JProgressBar(0, 300);
        timerProgress.setValue(300);
        timerProgress.setStringPainted(false);
        timerProgress.setBorder(BorderFactory.createEmptyBorder());
        timerProgress.setPreferredSize(new Dimension(0, 6)); // thin styling

        centerGbc.gridy = 1;
        centerGbc.weighty = 0.02;
        centerPanel.add(timerProgress, centerGbc);

        // Category Tag
        categoryLabel = new JLabel(gameEngine.getCurrentWord().getCategory());
        categoryLabel.setFont(customFontMedium);
        categoryLabel.setHorizontalAlignment(SwingConstants.CENTER);
        categoryLabel.setBorder(new EmptyBorder(5, 10, 5, 10));

        centerGbc.gridy = 2;
        centerGbc.weighty = 0.05;
        centerPanel.add(categoryLabel, centerGbc);

        // Hidden Word Display
        hiddenWordLabel = new JLabel(formatMaskedWord(gameEngine.getMaskedWord()));
        hiddenWordLabel.setFont(customFontTitle);
        hiddenWordLabel.setHorizontalAlignment(SwingConstants.CENTER);

        centerGbc.gridy = 3;
        centerGbc.weighty = 0.15;
        centerPanel.add(hiddenWordLabel, centerGbc);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // 3. Lower Control / Input Panel (Keyboard + Extra Buttons)
        JPanel footerPanel = new JPanel(new BorderLayout(5, 5));
        footerPanel.setOpaque(false);

        keyboardPanel = new JPanel(new GridLayout(4, 7, 4, 4));
        keyboardPanel.setOpaque(false);

        letterButtons = new JButton[26];
        for (char c = 'A'; c <= 'Z'; c++) {
            final String letterStr = Character.toString(c);
            JButton btn = createStyledButton(letterStr, customFontMedium, false);
            btn.addActionListener(e -> handleLetterGuess(letterStr, btn));
            letterButtons[c - 'A'] = btn;
            keyboardPanel.add(btn);
        }

        // Functional control buttons in keyboard grid
        hintButton = createStyledButton("HINT", customFontMedium, true);
        hintButton.addActionListener(e -> handleHintRequest());
        keyboardPanel.add(hintButton);

        JButton resetBtn = createStyledButton("RESET", customFontMedium, true);
        resetBtn.addActionListener(e -> resetMatch());
        keyboardPanel.add(resetBtn);

        footerPanel.add(keyboardPanel, BorderLayout.CENTER);

        // Options bar (Difficulty selection & exit)
        JPanel optionsBar = new JPanel(new BorderLayout(5, 5));
        optionsBar.setOpaque(false);
        optionsBar.setBorder(new EmptyBorder(5, 0, 0, 0));

        JPanel diffPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        diffPanel.setOpaque(false);
        diffPanel.add(new JLabel("Difficulty:"));
        
        difficultySelector = new JComboBox<>(Word.Difficulty.values());
        difficultySelector.setFont(customFontSmall);
        difficultySelector.setSelectedItem(currentDifficulty);
        difficultySelector.addActionListener(e -> {
            currentDifficulty = (Word.Difficulty) difficultySelector.getSelectedItem();
            resetMatch();
        });
        diffPanel.add(difficultySelector);
        optionsBar.add(diffPanel, BorderLayout.WEST);

        JButton quitBtn = createStyledButton("BACK TO MENU", customFontMedium, true);
        quitBtn.setPreferredSize(new Dimension(170, 32));
        quitBtn.addActionListener(e -> {
            SoundManager.playSound("resources/sounds/click.wav");
            playerProfile.save();
            roundTimer.stop();
            controller.showScreen("DASHBOARD");
        });
        optionsBar.add(quitBtn, BorderLayout.EAST);

        footerPanel.add(optionsBar, BorderLayout.SOUTH);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        // Overlay dialog
        overlayPanel = new GameOverlayPanel(this, controller);

        // Register components in layered pane
        layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(overlayPanel, JLayeredPane.MODAL_LAYER);

        // Ensure bounds resize dynamically with window dimensions
        layeredPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                mainPanel.setBounds(0, 0, layeredPane.getWidth(), layeredPane.getHeight());
                overlayPanel.setBounds(0, 0, layeredPane.getWidth(), layeredPane.getHeight());
            }
        });

        add(layeredPane, BorderLayout.CENTER);
    }

    private void setupKeyBindings() {
        for (char c = 'A'; c <= 'Z'; c++) {
            final String letter = Character.toString(c);
            final int index = c - 'A';
            String actionKey = "guess_" + letter;

            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                    KeyStroke.getKeyStroke(c), actionKey
            );
            getActionMap().put(actionKey, new AbstractAction() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (letterButtons[index].isEnabled() && !overlayPanel.isVisible() && GameplayPanel.this.isShowing()) {
                        handleLetterGuess(letter, letterButtons[index]);
                    }
                }
            });
        }
    }

    private void setupRoundTimer() {
        roundTimer = new Timer(100, e -> {
            if (overlayPanel.isVisible() || !GameplayPanel.this.isShowing()) {
                roundTimer.stop();
                return;
            }
            timeLeftDeciSeconds--;
            timerProgress.setValue(timeLeftDeciSeconds);

            if (timeLeftDeciSeconds < 100) {
                timerProgress.setForeground(ThemeManager.getIncorrectColor());
            } else {
                timerProgress.setForeground(ThemeManager.getSecondaryColor());
            }

            if (timeLeftDeciSeconds <= 0) {
                roundTimer.stop();
                SoundManager.playSound("resources/sounds/gameover.wav");
                handleMatchEnd(false);
            }
        });
    }

    private void handleLetterGuess(String character, JButton button) {
        button.setEnabled(false);
        boolean correct = gameEngine.guessLetter(character.charAt(0));
        
        if (correct) {
            button.setBackground(ThemeManager.getCorrectColor());
            hiddenWordLabel.setText(formatMaskedWord(gameEngine.getMaskedWord()));
            SoundManager.playSound("resources/sounds/correct.wav");
            
            if (gameEngine.isWon()) {
                SoundManager.playSound("resources/sounds/win.wav");
                handleMatchEnd(true);
            }
        } else {
            button.setBackground(ThemeManager.getIncorrectColor());
            CustomTools.updateImage(hangmanImage, "resources/" + (gameEngine.getIncorrectGuesses() + 1) + ".png");
            SoundManager.playSound("resources/sounds/incorrect.wav");
            
            // Screenshake physical feedback on input errors
            AnimationEngine.shake(hangmanImage);
            
            if (gameEngine.isGameOver()) {
                SoundManager.playSound("resources/sounds/gameover.wav");
                handleMatchEnd(false);
            }
        }
    }

    private void handleHintRequest() {
        if (playerProfile.getCoins() < 15) {
            JOptionPane.showMessageDialog(controller, 
                    "You need at least 15 coins to purchase a hint!", 
                    "Insufficient Funds", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (gameEngine.isHintUsed()) {
            JOptionPane.showMessageDialog(controller, 
                    "Hint: " + gameEngine.getCurrentWord().getHint(), 
                    "Active Clue", 
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        playerProfile.deductCoins(15);
        coinsLabel.setText("Coins: " + playerProfile.getCoins() + " 🪙");
        String clue = gameEngine.useHint();
        SoundManager.playSound("resources/sounds/click.wav");

        JOptionPane.showMessageDialog(controller, 
                "Clue: " + clue + "\n(15 coins deducted)", 
                "Purchased Clue", 
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleMatchEnd(boolean won) {
        roundTimer.stop();

        // Calculate time reward bonuses for fast speeds!
        int timeBonus = won ? (timeLeftDeciSeconds / 10) : 0;
        gameEngine.addScore(timeBonus);
        
        playerProfile.recordGame(won, gameEngine.getScore());
        refreshStats();

        String rewardText = won ? 
                String.format("+30 Coins, +50 XP (Time Bonus: +%d pts)", timeBonus) : 
                "Streak Reset 💔";

        overlayPanel.showResult(won, gameEngine.getCurrentWord().getWord(), rewardText);
    }

    public void resetMatch() {
        Word challenge = wordDB.loadChallenge(currentDifficulty);
        gameEngine.reset(challenge);
        
        // Reset controls
        hiddenWordLabel.setText(formatMaskedWord(gameEngine.getMaskedWord()));
        categoryLabel.setText(gameEngine.getCurrentWord().getCategory());
        CustomTools.updateImage(hangmanImage, CommonConstants.IMAGE_PATH);

        for (JButton btn : letterButtons) {
            btn.setEnabled(true);
            btn.setBackground(ThemeManager.getPrimaryColor());
        }

        // Reset timer
        timeLeftDeciSeconds = 300;
        timerProgress.setValue(300);
        timerProgress.setForeground(ThemeManager.getSecondaryColor());
        
        if (roundTimer != null) {
            roundTimer.restart();
        }
    }

    public void refreshStats() {
        playerProfile = PlayerProfile.load();
        levelLabel.setText("Lv. " + playerProfile.getLevel());
        xpProgressBar.setMaximum(playerProfile.getXpForNextLevel());
        xpProgressBar.setValue(playerProfile.getXp());
        streakLabel.setText("Streak: " + playerProfile.getCurrentStreak() + " 🔥");
        coinsLabel.setText("Coins: " + playerProfile.getCoins() + " 🪙");
    }

    private void applyTheme() {
        Color bg = ThemeManager.getBackgroundColor();
        Color fg = ThemeManager.getTextColor();
        Color primary = ThemeManager.getPrimaryColor();
        Color accent = ThemeManager.getSecondaryColor();

        mainPanel.setBackground(bg);
        headerPanel.setBackground(primary);
        headerPanel.setBorder(new LineBorder(accent, 1, true));

        levelLabel.setForeground(fg);
        streakLabel.setForeground(fg);
        coinsLabel.setForeground(fg);
        categoryLabel.setForeground(fg);
        categoryLabel.setBackground(primary);
        categoryLabel.setOpaque(true);
        hiddenWordLabel.setForeground(fg);

        xpProgressBar.setForeground(accent);
        xpProgressBar.setBackground(bg);

        timerProgress.setBackground(bg);
        timerProgress.setForeground(accent);

        // Update keyboard buttons
        for (JButton btn : letterButtons) {
            if (btn.isEnabled()) {
                btn.setBackground(primary);
                btn.setForeground(ThemeManager.getButtonTextColor());
            }
        }

        hintButton.setBackground(accent);
        hintButton.setForeground(Color.BLACK);
    }

    private JButton createStyledButton(String text, Font font, boolean accent) {
        JButton btn = new JButton(text);
        btn.setFont(font);
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(Color.DARK_GRAY, 1, true));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Color normalBg = accent ? ThemeManager.getSecondaryColor() : ThemeManager.getPrimaryColor();
        Color hoverBg = normalBg.brighter();
        
        btn.setBackground(normalBg);
        btn.setForeground(accent ? Color.BLACK : ThemeManager.getButtonTextColor());

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(hoverBg);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(accent ? ThemeManager.getSecondaryColor() : ThemeManager.getPrimaryColor());
                }
            }
        });

        return btn;
    }

    private String formatMaskedWord(String maskedWord) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < maskedWord.length(); i++) {
            char c = maskedWord.charAt(i);
            if (c == '*') {
                sb.append("_ ");
            } else if (c == ' ') {
                sb.append("   ");
            } else {
                sb.append(c).append(" ");
            }
        }
        return sb.toString().trim();
    }
}
