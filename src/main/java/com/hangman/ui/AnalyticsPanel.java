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

public class AnalyticsPanel extends JPanel {
    private final ScreenController controller;
    private PlayerProfile profile;

    private JLabel titleLabel;
    private JLabel totalGamesLabel;
    private JLabel winsLabel;
    private JLabel winRatioLabel;
    private JLabel streakLabel;

    private JPanel statsCardPanel;
    private ChartCanvas chartCanvas;

    private Font fontMedium;
    private Font fontSmall;

    public AnalyticsPanel(ScreenController controller) {
        this.controller = controller;
        this.profile = PlayerProfile.load();

        this.fontMedium = CustomTools.createFont(CommonConstants.FONT_PATH, 18f);
        this.fontSmall = CustomTools.createFont(CommonConstants.FONT_PATH, 13f);

        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(25, 25, 25, 25));

        initComponents();
        applyTheme();
    }

    private void initComponents() {
        // Title
        titleLabel = new JLabel("PERFORMANCE ANALYTICS", SwingConstants.CENTER);
        titleLabel.setFont(CustomTools.createFont(CommonConstants.FONT_PATH, 32f));
        add(titleLabel, BorderLayout.NORTH);

        // Center split: stats summary text on left, charts on right
        JPanel centerGrid = new JPanel(new GridLayout(1, 2, 15, 0));
        centerGrid.setOpaque(false);

        // Left Side: Text cards
        statsCardPanel = new JPanel(new GridLayout(4, 1, 0, 15));
        statsCardPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.GRAY, 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        totalGamesLabel = new JLabel("Total Games: 0");
        winsLabel = new JLabel("Games Won: 0");
        winRatioLabel = new JLabel("Win Ratio: 0.0%");
        streakLabel = new JLabel("Max Win Streak: 0");

        totalGamesLabel.setFont(fontMedium);
        winsLabel.setFont(fontMedium);
        winRatioLabel.setFont(fontMedium);
        streakLabel.setFont(fontMedium);

        statsCardPanel.add(totalGamesLabel);
        statsCardPanel.add(winsLabel);
        statsCardPanel.add(winRatioLabel);
        statsCardPanel.add(streakLabel);
        centerGrid.add(statsCardPanel);

        // Right Side: Graphic canvas
        chartCanvas = new ChartCanvas();
        centerGrid.add(chartCanvas);

        add(centerGrid, BorderLayout.CENTER);

        // Footer button
        JButton backBtn = createStyledButton("BACK TO MAIN MENU", fontMedium, true);
        backBtn.setPreferredSize(new Dimension(200, 45));
        backBtn.addActionListener(e -> {
            SoundManager.playSound("resources/sounds/click.wav");
            controller.showScreen("DASHBOARD");
        });
        add(backBtn, BorderLayout.SOUTH);

        refreshData();
    }

    public void refreshData() {
        profile = PlayerProfile.load();
        
        int played = profile.getTotalGamesPlayed();
        int won = profile.getGamesWon();
        double ratio = played == 0 ? 0.0 : ((double) won / played) * 100.0;

        totalGamesLabel.setText("Total Matches: " + played);
        winsLabel.setText("Matches Won: " + won + " 🏆");
        winRatioLabel.setText(String.format("Win Ratio: %.1f%%", ratio));
        streakLabel.setText("Max Win Streak: " + profile.getMaxStreak() + " 🔥");

        chartCanvas.repaint();
        applyTheme();
    }

    private void applyTheme() {
        Color bg = ThemeManager.getBackgroundColor();
        Color fg = ThemeManager.getTextColor();
        Color primary = ThemeManager.getPrimaryColor();
        Color accent = ThemeManager.getSecondaryColor();

        setBackground(bg);
        titleLabel.setForeground(accent);

        statsCardPanel.setBackground(primary);
        statsCardPanel.setBorder(new LineBorder(accent, 1, true));

        totalGamesLabel.setForeground(fg);
        winsLabel.setForeground(fg);
        winRatioLabel.setForeground(fg);
        streakLabel.setForeground(fg);
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

    // Custom 2D Graphics Canvas
    private class ChartCanvas extends JPanel {
        public ChartCanvas() {
            setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(Color.GRAY, 1, true),
                    new EmptyBorder(10, 10, 10, 10)
            ));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color bg = ThemeManager.getPrimaryColor();
            Color borderAccent = ThemeManager.getSecondaryColor();
            setBackground(bg);
            
            // Draw background border manually
            g2.setColor(borderAccent);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

            int total = profile.getTotalGamesPlayed();
            int won = profile.getGamesWon();
            int lost = total - won;

            // Draw Win Ratio Donut Chart
            int size = Math.min(getWidth(), getHeight()) / 2 - 30;
            int cx = (getWidth() - size) / 2;
            int cy = (getHeight() - size) / 4 + 10;

            g2.setStroke(new BasicStroke(16f));
            
            if (total == 0) {
                // Default gray circle when no games are played
                g2.setColor(Color.DARK_GRAY);
                g2.drawOval(cx, cy, size, size);
                g2.setFont(fontSmall);
                g2.setColor(ThemeManager.getTextColor());
                g2.drawString("No Games Played", cx + size/4, cy + size/2 + 5);
            } else {
                int winAngle = (int) (((double) won / total) * 360.0);
                int lossAngle = 360 - winAngle;

                // Loss Arc
                g2.setColor(ThemeManager.getIncorrectColor());
                g2.drawArc(cx, cy, size, size, 90, lossAngle);

                // Win Arc
                g2.setColor(ThemeManager.getCorrectColor());
                g2.drawArc(cx, cy, size, size, 90 + lossAngle, winAngle);

                // Center percentage text
                g2.setFont(fontMedium.deriveFont(22f));
                g2.setColor(ThemeManager.getTextColor());
                String percent = String.format("%d%%", (int)(((double) won / total) * 100.0));
                FontMetrics fm = g2.getFontMetrics();
                int tx = cx + (size - fm.stringWidth(percent)) / 2;
                int ty = cy + (size + fm.getAscent() - fm.getLeading()) / 2;
                g2.drawString(percent, tx, ty);
            }

            // Legend indicators
            int lx = 20;
            int ly = getHeight() - 80;
            g2.setStroke(new BasicStroke(2f));

            g2.setColor(ThemeManager.getCorrectColor());
            g2.fillOval(lx, ly, 12, 12);
            g2.setFont(fontSmall);
            g2.setColor(ThemeManager.getTextColor());
            g2.drawString("Wins (" + won + ")", lx + 18, ly + 10);

            g2.setColor(ThemeManager.getIncorrectColor());
            g2.fillOval(lx + 120, ly, 12, 12);
            g2.drawString("Losses (" + lost + ")", lx + 138, ly + 10);
            
            // Graph Label
            g2.setFont(fontSmall.deriveFont(Font.BOLD));
            g2.drawString("Win Ratio Breakdowns", lx, cy - 15);
        }
    }
}
