package com.hangman.ui;

import com.hangman.config.CommonConstants;
import com.hangman.model.PlayerProfile;
import com.hangman.util.CustomTools;
import com.hangman.util.SoundManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DashboardPanel extends JPanel {
    private final ScreenController controller;
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JPanel statsContainer;
    
    private JLabel levelVal;
    private JLabel streakVal;
    private JLabel coinsVal;

    private Font titleFont;
    private Font subtitleFont;
    private Font buttonFont;

    public DashboardPanel(ScreenController controller) {
        this.controller = controller;
        this.titleFont = CustomTools.createFont(CommonConstants.FONT_PATH, 54f);
        this.subtitleFont = CustomTools.createFont(CommonConstants.FONT_PATH, 20f);
        this.buttonFont = CustomTools.createFont(CommonConstants.FONT_PATH, 22f);

        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(40, 40, 40, 40));
        initComponents();
        applyTheme();
    }

    private void initComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Title
        titleLabel = new JLabel("HANGMAN", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        add(titleLabel, gbc);

        // Subtitle
        subtitleLabel = new JLabel("Enterprise Edition", SwingConstants.CENTER);
        subtitleLabel.setFont(subtitleFont);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 40, 0);
        add(subtitleLabel, gbc);

        // Stats summary card container
        statsContainer = new JPanel(new GridLayout(1, 3, 10, 0));

        PlayerProfile profile = PlayerProfile.load();
        
        levelVal = new JLabel("Lv. " + profile.getLevel(), SwingConstants.CENTER);
        streakVal = new JLabel(profile.getCurrentStreak() + " 🔥", SwingConstants.CENTER);
        coinsVal = new JLabel(profile.getCoins() + " 🪙", SwingConstants.CENTER);

        statsContainer.add(createStatCard("LEVEL", levelVal));
        statsContainer.add(createStatCard("WIN STREAK", streakVal));
        statsContainer.add(createStatCard("COINS BAL", coinsVal));

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 40, 0);
        add(statsContainer, gbc);

        // Menu Actions Panel
        JPanel buttonsPanel = new JPanel(new GridLayout(4, 1, 0, 12));
        buttonsPanel.setOpaque(false);

        JButton playBtn = createMenuButton("QUICK PLAY");
        playBtn.addActionListener(e -> {
            SoundManager.playSound("resources/sounds/click.wav");
            controller.showScreen("GAMEPLAY");
        });

        JButton editorBtn = createMenuButton("WORD DATABASE EDITOR");
        editorBtn.addActionListener(e -> {
            SoundManager.playSound("resources/sounds/click.wav");
            controller.showScreen("EDITOR");
        });

        JButton statsBtn = createMenuButton("PERFORMANCE ANALYTICS");
        statsBtn.addActionListener(e -> {
            SoundManager.playSound("resources/sounds/click.wav");
            controller.showScreen("ANALYTICS");
        });

        JButton quitBtn = createMenuButton("QUIT GAME");
        quitBtn.addActionListener(e -> {
            SoundManager.playSound("resources/sounds/click.wav");
            System.exit(0);
        });

        buttonsPanel.add(playBtn);
        buttonsPanel.add(editorBtn);
        buttonsPanel.add(statsBtn);
        buttonsPanel.add(quitBtn);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 20, 0, 20);
        add(buttonsPanel, gbc);
    }

    private JPanel createStatCard(String labelText, JLabel valLabel) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setOpaque(false);
        
        JLabel nameLabel = new JLabel(labelText, SwingConstants.CENTER);
        nameLabel.setFont(subtitleFont.deriveFont(12f));
        nameLabel.setForeground(Color.LIGHT_GRAY);
        
        valLabel.setFont(subtitleFont.deriveFont(22f));
        
        card.add(nameLabel, BorderLayout.NORTH);
        card.add(valLabel, BorderLayout.CENTER);
        return card;
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(buttonFont);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 50));
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(ThemeManager.getSecondaryColor());
                btn.setForeground(Color.BLACK);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(ThemeManager.getPrimaryColor());
                btn.setForeground(ThemeManager.getButtonTextColor());
            }
        });
        return btn;
    }

    private void applyTheme() {
        Color bg = ThemeManager.getBackgroundColor();
        Color fg = ThemeManager.getTextColor();
        Color primary = ThemeManager.getPrimaryColor();
        Color accent = ThemeManager.getSecondaryColor();

        setBackground(bg);
        titleLabel.setForeground(accent);
        subtitleLabel.setForeground(fg);
        statsContainer.setBackground(primary);
        statsContainer.setBorder(new LineBorder(accent, 1, true));

        levelVal.setForeground(fg);
        streakVal.setForeground(fg);
        coinsVal.setForeground(fg);

        // Apply theme to children buttons recursively
        for (Component c : getComponents()) {
            if (c instanceof JPanel) {
                for (Component sub : ((JPanel) c).getComponents()) {
                    if (sub instanceof JButton) {
                        sub.setBackground(primary);
                        sub.setForeground(ThemeManager.getButtonTextColor());
                        ((JButton) sub).setBorder(new LineBorder(Color.DARK_GRAY, 1, true));
                    }
                }
            }
        }
    }

    public void refreshStats() {
        PlayerProfile profile = PlayerProfile.load();
        levelVal.setText("Lv. " + profile.getLevel());
        streakVal.setText(profile.getCurrentStreak() + " 🔥");
        coinsVal.setText(profile.getCoins() + " 🪙");
        applyTheme();
    }
}
