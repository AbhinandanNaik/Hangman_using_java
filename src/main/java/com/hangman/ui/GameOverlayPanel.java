package com.hangman.ui;

import com.hangman.config.CommonConstants;
import com.hangman.util.CustomTools;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class GameOverlayPanel extends JPanel {
    private final GameplayPanel gameplayPanel;
    private final ScreenController controller;

    private JLabel titleLabel;
    private JLabel wordLabel;
    private JLabel rewardLabel;
    private JPanel cardPanel;

    private final Font titleFont;
    private final Font mediumFont;

    // Victory confetti animation variables
    private List<AnimationEngine.Particle> particles;
    private Timer confettiTimer;

    public GameOverlayPanel(GameplayPanel gameplayPanel, ScreenController controller) {
        this.gameplayPanel = gameplayPanel;
        this.controller = controller;

        setOpaque(false);
        setLayout(new GridBagLayout());
        setVisible(false); // Hidden by default

        titleFont = CustomTools.createFont(CommonConstants.FONT_PATH, 38f);
        mediumFont = CustomTools.createFont(CommonConstants.FONT_PATH, 18f);

        initComponents();
    }

    private void initComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;

        // Overlay Card
        cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(ThemeManager.getSecondaryColor(), 2, true),
                new EmptyBorder(25, 35, 25, 35)
        ));

        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.gridx = 0;
        cardGbc.fill = GridBagConstraints.HORIZONTAL;
        cardGbc.insets = new Insets(10, 0, 10, 0);

        titleLabel = new JLabel("VICTORY!", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        cardGbc.gridy = 0;
        cardPanel.add(titleLabel, cardGbc);

        wordLabel = new JLabel("Word: ANSWER", SwingConstants.CENTER);
        wordLabel.setFont(mediumFont);
        wordLabel.setForeground(Color.LIGHT_GRAY);
        cardGbc.gridy = 1;
        cardPanel.add(wordLabel, cardGbc);

        rewardLabel = new JLabel("+30 Coins, +50 XP", SwingConstants.CENTER);
        rewardLabel.setFont(mediumFont);
        cardGbc.gridy = 2;
        cardPanel.add(rewardLabel, cardGbc);

        // Retries & Main Menu panel
        JPanel actionPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        actionPanel.setOpaque(false);

        JButton playAgainBtn = createStyledButton("PLAY AGAIN", mediumFont, true);
        playAgainBtn.addActionListener(e -> {
            stopConfetti();
            setVisible(false);
            gameplayPanel.resetMatch();
        });

        JButton menuBtn = createStyledButton("BACK TO MAIN MENU", mediumFont, false);
        menuBtn.addActionListener(e -> {
            stopConfetti();
            setVisible(false);
            controller.showScreen("DASHBOARD");
        });

        actionPanel.add(playAgainBtn);
        actionPanel.add(menuBtn);

        cardGbc.gridy = 3;
        cardGbc.insets = new Insets(20, 0, 0, 0);
        cardPanel.add(actionPanel, cardGbc);

        add(cardPanel, gbc);
    }

    public void showResult(boolean won, String word, String rewardText) {
        titleLabel.setText(won ? "VICTORY!" : "GAME OVER!");
        titleLabel.setForeground(won ? ThemeManager.getCorrectColor() : ThemeManager.getIncorrectColor());
        wordLabel.setText("Word: " + word);
        rewardLabel.setText(rewardText);
        rewardLabel.setForeground(won ? ThemeManager.getCorrectColor() : ThemeManager.getIncorrectColor());
        
        cardPanel.setBackground(ThemeManager.getPrimaryColor());
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(ThemeManager.getSecondaryColor(), 2, true),
                new EmptyBorder(25, 35, 25, 35)
        ));
        
        setVisible(true);

        // Confetti trigger on victory
        if (won) {
            startConfetti();
        } else {
            stopConfetti();
        }

        repaint();
    }

    private void startConfetti() {
        particles = new ArrayList<>();
        int w = getWidth() > 0 ? getWidth() : 640;
        int h = getHeight() > 0 ? getHeight() : 840;
        for (int i = 0; i < 90; i++) {
            particles.add(new AnimationEngine.Particle(w, h));
        }

        if (confettiTimer != null) {
            confettiTimer.stop();
        }

        confettiTimer = new Timer(16, e -> {
            int width = getWidth() > 0 ? getWidth() : 640;
            int height = getHeight() > 0 ? getHeight() : 840;
            boolean active = false;
            for (AnimationEngine.Particle p : particles) {
                p.update(width, height);
                if (p.y < height) {
                    active = true;
                }
            }
            repaint();
            if (!active) {
                confettiTimer.stop();
            }
        });
        confettiTimer.start();
    }

    private void stopConfetti() {
        if (confettiTimer != null) {
            confettiTimer.stop();
            confettiTimer = null;
        }
        particles = null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(0, 0, 0, 190));
        g2.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g); // Draws subcomponents (card panel)
        if (particles != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (int i = 0; i < particles.size(); i++) {
                particles.get(i).draw(g2);
            }
            g2.dispose();
        }
    }

    private JButton createStyledButton(String text, Font font, boolean accent) {
        JButton btn = new JButton(text);
        btn.setFont(font);
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(Color.DARK_GRAY, 1, true));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(220, 40));

        Color normalBg = accent ? ThemeManager.getSecondaryColor() : ThemeManager.getBackgroundColor();
        Color hoverBg = normalBg.brighter();
        
        btn.setBackground(normalBg);
        btn.setForeground(accent ? Color.BLACK : ThemeManager.getTextColor());

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
                    btn.setBackground(normalBg);
                }
            }
        });

        return btn;
    }
}
